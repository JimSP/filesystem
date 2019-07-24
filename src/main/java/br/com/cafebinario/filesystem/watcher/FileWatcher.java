
package br.com.cafebinario.filesystem.watcher;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import lombok.SneakyThrows;

public final class FileWatcher implements Closeable {

    private static final int QTY_THREADS = 10;

    public static FileWatcher of() {
        return new FileWatcher();
    }

    public static FileWatcher of(final ExecutorService executorService) {
        return new FileWatcher(executorService);
    }

    private AtomicBoolean started = new AtomicBoolean(Boolean.TRUE);

    private final Map<WatchKey, Path> keys = Collections.synchronizedMap(new HashMap<>());
    private final ExecutorService executorService;

    private final Map<WatchKeyOriginAndEvent, List<BiConsumer<WatcherEvent, String>>> fileConsumerMapRegister = Collections
            .synchronizedMap(new HashMap<>());

    private final Map<WatchKeyOriginAndEvent, WatchServiceWalkAndRegisterWrapper> watchServiceMap = Collections
            .synchronizedMap(new HashMap<>());

    private FileWatcher(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    private FileWatcher() {
        this.executorService = Executors.newFixedThreadPool(QTY_THREADS);
    }

    public void registerConsumerCreateEvent(final Path origin,
            final BiConsumer<WatcherEvent, String> fileConsumer) {
        this.registerConsumerWithoutWalkAndRegisterDirectories(origin, fileConsumer,
                StandardWatchEventKinds.ENTRY_CREATE);
    }

    public void registerConsumerModifyEvent(final Path origin,
            final BiConsumer<WatcherEvent, String> fileConsumer) {
        this.registerConsumerWithoutWalkAndRegisterDirectories(origin, fileConsumer,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public void registerConsumerDeleteEvent(final Path origin,
            final BiConsumer<WatcherEvent, String> fileConsumer) {
        this.registerConsumerWithoutWalkAndRegisterDirectories(origin, fileConsumer,
                StandardWatchEventKinds.ENTRY_DELETE);
    }

    public void registerConsumerOverflowEvent(final Path origin,
            final BiConsumer<WatcherEvent, String> fileConsumer) {
        this.registerConsumerWithoutWalkAndRegisterDirectories(origin, fileConsumer,
                StandardWatchEventKinds.OVERFLOW);
    }

    public void registerConsumerAllEvents(final Path origin,
            final BiConsumer<WatcherEvent, String> fileConsumer) {
        this.registerConsumerWithoutWalkAndRegisterDirectories(origin, fileConsumer,
                StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public void registerConsumerWithWalkAndRegisterDirectories(final Path origin,
            final BiConsumer<WatcherEvent, String> fileConsumer, final Kind<?>... events) {
        this.registerConsumer(origin, fileConsumer, Boolean.TRUE, events);
    }

    public void registerConsumerWithoutWalkAndRegisterDirectories(final Path origin,
            final BiConsumer<WatcherEvent, String> fileConsumer, final Kind<?>... events) {
        this.registerConsumer(origin, fileConsumer, Boolean.FALSE, events);
    }

    @SneakyThrows
    public void registerConsumer(final Path origin,
            final BiConsumer<WatcherEvent, String> fileConsumer,
            final Boolean walkAndRegisterDirectories, final Kind<?>... events) {
        if (Files.notExists(origin)) {
            Files.createDirectories(origin);
        }

        if (!Files.isDirectory(origin)) {
            throw new IllegalArgumentException(
                    origin.toAbsolutePath().toString() + " is not a directory.");
        }

        Arrays.asList(events) //
                .stream() //
                .forEach(event -> sendNotify(origin, fileConsumer, walkAndRegisterDirectories,
                        event));
    }

    @SneakyThrows
    private void sendNotify(final Path origin, final BiConsumer<WatcherEvent, String> fileConsumer,
            final Boolean walkAndRegisterDirectories, final Kind<?> event) {

        final WatchService watchService = origin.getFileSystem().newWatchService();
        final WatchKeyOriginAndEvent watchKeyOriginAndEvent = new WatchKeyOriginAndEvent(
                origin.toAbsolutePath().toString(), event);

        watchServiceMap.put(watchKeyOriginAndEvent,
                new WatchServiceWalkAndRegisterWrapper(watchService, walkAndRegisterDirectories));

        walkAndRegisterDirectories(watchService, origin, event);

        if (fileConsumerMapRegister.containsKey(watchKeyOriginAndEvent)) {
            fileConsumerMapRegister.get(watchKeyOriginAndEvent).add(fileConsumer);
        } else {
            final List<BiConsumer<WatcherEvent, String>> list = new ArrayList<>();
            list.add(fileConsumer);
            fileConsumerMapRegister.put(watchKeyOriginAndEvent, list);
        }
    }

    public boolean unregister(final File origin,
            final BiConsumer<WatcherEvent, String> fileConsumer, final Kind<?> event) {

        final List<BiConsumer<WatcherEvent, String>> consumerList = fileConsumerMapRegister
                .get(new WatchKeyOriginAndEvent(origin.getAbsolutePath(), event));
        if (consumerList != null) {
            return consumerList.remove(fileConsumer);
        }

        return false;
    }

    public void start() {
        started.set(Boolean.TRUE);
        executorService.submit(() -> {
            while (started.get()) {
                processEvent();
            }
        });
    }

    private void processEvent() {
        watchServiceMap //
                .entrySet() //
                .stream() //
                .forEach(entry -> executorService.execute(() -> {
                    final WatchServiceWalkAndRegisterWrapper wrapper = entry.getValue();
                    pooling(wrapper);
                }));
    }

    @SneakyThrows
    private void pooling(final WatchServiceWalkAndRegisterWrapper wrapper) {
        final WatchKey key = wrapper.getWatchService().take();
        final Path dir = createPath(key);
        poolingEvents(wrapper.getWatchService(), key, dir, wrapper.getWalkAndRegisterDirectories());
        checkValid(key);
    }

    @SneakyThrows
    private Path createPath(final WatchKey key) {
        return keys.get(key);
    }

    private void checkValid(final WatchKey key) {
        final boolean valid = key.reset();
        if (!valid) {
            keys.remove(key);
        }
    }

    @SuppressWarnings("unchecked")
    private void poolingEvents(final WatchService watchService, final WatchKey key, final Path dir,
            final Boolean walkAndRegisterDirectories) {
        key.pollEvents() //
                .forEach(event -> {
                    final Kind<?> kind = event.kind();
                    final Path name = ((WatchEvent<Path>) event).context();
                    final Path child = Paths
                            .get(dir.toString() + File.separatorChar + name.toString());

                    dispacherEvent(dir.toFile().getAbsolutePath(), kind, child);

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE && walkAndRegisterDirectories
                            && Files.isDirectory(child)) {
                        walkAndRegisterDirectories(watchService, child);
                    }
                });
    }

    private void dispacherEvent(final String origin, final Kind<?> kind, final Path child) {
        final WatcherEvent watcherEvent = new WatcherEvent(origin, kind, child);
        final WatchKeyOriginAndEvent watchKeyOriginAndEvent = new WatchKeyOriginAndEvent(origin,
                kind);

        fileConsumerMapRegister //
                .get(watchKeyOriginAndEvent) //
                .parallelStream() //
                .forEach(action -> {
                    final String uuid = UUID.randomUUID().toString();
                    action.accept(watcherEvent, uuid);
                });
    }

    private void registerDirectory(final WatchService watchService, final Path dir,
            final Kind<?>... events) throws IOException {
        final WatchKey key = dir.register(watchService, events);
        keys.put(key, dir);
    }

    @SneakyThrows(value = { IOException.class })
    private void walkAndRegisterDirectories(final WatchService watchService, final Path path,
            final Kind<?>... events) {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path dir,
                    final BasicFileAttributes attrs) throws IOException {
                registerDirectory(watchService, dir, events);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void close() throws IOException {
        started.set(Boolean.FALSE);
    }
}
