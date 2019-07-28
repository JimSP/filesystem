
package br.com.cafebinario.filesystem.watchers;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FileWatcher implements Closeable {

	private static final int QTY_THREADS = 5;

	public static FileWatcher of(final FileSystem fileSystem) {
		return new FileWatcher(fileSystem);
	}

	public static FileWatcher of(final FileSystem fileSystem, final Integer qtyThreads) {
		return new FileWatcher(fileSystem, qtyThreads);
	}

	public static FileWatcher of(final FileSystem fileSystem, final ExecutorService executorService) {
		return new FileWatcher(fileSystem, executorService);
	}

	private AtomicBoolean started = new AtomicBoolean(Boolean.TRUE);

	private final ExecutorService executorService;

	private final WatchService watchService;

	private final Map<Path, KeyConsumerEntry> pathConsumerMap = Collections.synchronizedMap(new HashMap<>());

	@SneakyThrows
	private FileWatcher(final FileSystem fileSystem, final ExecutorService executorService) {
		this.executorService = executorService;
		this.watchService = fileSystem.newWatchService();
	}

	private FileWatcher(final FileSystem fileSystem) {
		this(fileSystem, Executors.newFixedThreadPool(QTY_THREADS));
	}

	private FileWatcher(final FileSystem fileSystem, final Integer qtyThreads) {
		this(fileSystem, Executors.newFixedThreadPool(qtyThreads));
	}

	public void start() {

		log.info("m:start");

		started.set(Boolean.TRUE);

		executorService.submit(() -> {

			while (isRunning()) {

				pooling();
			}
		});
	}

	@SneakyThrows
	public void register(final Path path, final BiConsumer<WatcherEvent, String> fileConsumer) {

		log.info("m:register, path={}", path);

		final WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
				StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY,
				StandardWatchEventKinds.OVERFLOW);

		pathConsumerMap.put(path, KeyConsumerEntry
				.builder()
				.watchKey(watchKey)
				.consumer(fileConsumer)
				.build());
	}

	public void unregiter(final Path path) {

		log.info("m:unregiter, path={}", path);

		pathConsumerMap.remove(path);
	}

	@SneakyThrows
	private void pooling() {

		log.info("m:pooling");

		watchService.take().pollEvents().stream()
				.forEach(action -> executorService.submit(() -> dispacherEvent(action)));
	}

	private void dispacherEvent(final WatchEvent<?> wathEvent) {

		log.info("m:dispacherEvent, wathEvent:{}", wathEvent);

		try {

			pathConsumerMap.entrySet().stream().forEach(keyConsumerEntry -> {
				log.info("m:dispacherEvent, step:accept, wathEvent:{}", wathEvent);

				final BiConsumer<WatcherEvent, String> consumer = keyConsumerEntry
						.getValue()
						.getConsumer();

				consumer.accept(WatcherEvent
						.builder()
						.event(wathEvent
								.kind()
								.name())
						.path(wathEvent
								.context()
								.toString())
						.build(), keyConsumerEntry
						.getKey()
						.toString());
			});

		} catch (Exception e) {
			log.error("m:dispacherEvent", e);
		}
	}

	public Boolean isRunning() {

		return started.get();
	}

	@Override
	public void close() throws IOException {

		log.info("m:close");

		started.set(Boolean.FALSE);

		watchService.close();

		executorService.shutdownNow();
	}
}
