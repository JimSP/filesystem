package br.com.cafebinario.filesystem.services;

import static br.com.cafebinario.filesystem.functions.Contains.contains;
import static br.com.cafebinario.filesystem.functions.Contains.containsData;
import static br.com.cafebinario.filesystem.functions.IndexOf.indexOf;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

import br.com.cafebinario.filesystem.HazelcastFileSystem;
import br.com.cafebinario.filesystem.dtos.EditDTO;
import br.com.cafebinario.filesystem.dtos.EditableEntryDTO;
import br.com.cafebinario.filesystem.dtos.EntryDTO;
import br.com.cafebinario.filesystem.dtos.NotifyDTO;
import br.com.cafebinario.filesystem.dtos.SearchDTO;
import br.com.cafebinario.filesystem.listener.FileSystemWatcherEventMessageListener;
import br.com.cafebinario.logger.Log;
import br.com.cafebinario.logger.LogLevel;
import br.com.cafebinario.logger.VerboseMode;

@Service
public class FilesService {

    public static final int DEFAULT_DEPTH = 1;

    private static final String INVALID_VALUE = "invalid value for path: ";

    @Autowired
    private HazelcastFileSystem hazelcastFileSystem;

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Autowired
    private FileSystemWatcherEventMessageListener fileSystemWatcherEventMessageListener;

    @Log
    public String save(final EntryDTO entryDTO) {
        try {

            final Path path = getPath(entryDTO.getPath());

            return Files.write(path, entryDTO.getData()).toAbsolutePath().toString();
        } catch (IOException e) {
            throw new IllegalArgumentException(INVALID_VALUE + entryDTO.getPath());
        }
    }

    @Log
    public EntryDTO get(final String path) {
        try {

            return EntryDTO
                    .builder()
                    .path(path)
                    .data(Files.readAllBytes(getPath(path)))
                    .build();
        } catch (IOException e) {
            throw new IllegalArgumentException(INVALID_VALUE + path);
        }
    }

    @Log
    public String delete(final String path) {
        try {

            return Files.deleteIfExists(getPath(path)) ? path : null;
        } catch (IOException e) {
            throw new IllegalArgumentException(INVALID_VALUE + path);
        }
    }

    @Log
    public List<String> list(final String path) {

        try (final Stream<Path> stream = Files.list(getPath(path))) {

            return pathStreamConverter(stream);
        } catch (IOException e) {
            throw new IllegalArgumentException(INVALID_VALUE + path);
        }
    }

    @Log
    public List<String> find(final Integer maxDepth, final String name) {

        return pathStreamConverter(streamOf(maxDepth, name));
    }

    @Log
    public List<String> grep(final String path, final String keyword) {

        return pathStreamConverter(
                streamOf(DEFAULT_DEPTH, path).filter(pathPredicate -> containsData(keyword,
                        () -> get(pathPredicate.toAbsolutePath().toString()))));
    }

    @Log
    public List<String> grep(final String path, final byte[] keyword) {

        return pathStreamConverter(
                streamOf(DEFAULT_DEPTH, path).filter(pathPredicate -> containsData(keyword,
                        () -> get(pathPredicate.toAbsolutePath().toString()))));
    }

    @Log
    public List<SearchDTO> index(final String path, final String keyword) {

        return streamOf(DEFAULT_DEPTH, path)
                .map(pathPredicate -> indexOf(get(pathPredicate.toAbsolutePath().toString()),
                        keyword))
                .filter(indexOf -> indexOf > -1)
                .map(indexOf -> SearchDTO
                        .builder()
                        .indexOf(indexOf)
                        .keyword(keyword.getBytes())
                        .path(path)
                        .build())
                .collect(Collectors.toList());
    }

    @Log
    public List<SearchDTO> index(final String path, final byte[] keyword) {

        return streamOf(DEFAULT_DEPTH, path)
                .map(pathPredicate -> indexOf(get(pathPredicate.toAbsolutePath().toString()),
                        keyword))
                .filter(indexOf -> indexOf > -1)
                .map(indexOf -> SearchDTO
                        .builder()
                        .indexOf(indexOf)
                        .keyword(keyword)
                        .path(path)
                        .build())
                .collect(Collectors.toList());
    }

    @Log
    public Integer edit(final String path, final Integer position, final byte[] data) {

        return edit(EditDTO.builder().path(path)
                .editableEntrys(Arrays
                        .asList(EditableEntryDTO
                                .builder()
                                .position(position)
                                .data(data)
                                .build()))
                .build());
    }

    @Log
    public Integer edit(final EditDTO editDTO) {

        final Path path = getPath(editDTO.getPath());

        return editDTO.getEditableEntrys().stream()
                .map(editableEntryDTO -> edit(path, editableEntryDTO))
                .reduce((a, b) -> a + b)
                .orElse(0);
    }

    @Log
    public Integer edit(final Path path, final EditableEntryDTO editableEntryDTO) {
        
        try (final FileChannel fc = FileChannel.open(path, StandardOpenOption.READ,
                StandardOpenOption.WRITE)) {

            return edit(path, editableEntryDTO);

        } catch (IOException ex) {

            throw new IllegalArgumentException(INVALID_VALUE + path);
        }
    }

    @Log
    public Integer edit(final Path path, final List<EditableEntryDTO> editableEntryDTOs) {

        try (final FileChannel fc = FileChannel.open(path, StandardOpenOption.READ,
                StandardOpenOption.WRITE)) {

            return editableEntryDTOs
                    .stream()
                    .map(editableEntryDTO -> edit(path, fc, editableEntryDTO))
                    .reduce((a, b) -> a + b).orElse(0);

        } catch (IOException ex) {
            throw new IllegalArgumentException(INVALID_VALUE + path);
        }
    }

    @Log
    public void watcher(final String path, final URL url) {

        registerPath(path);

        registerWatcher(path, url);
    }

    @Log(verboseMode = VerboseMode.ON, logLevel = LogLevel.DEBUG)
    private Path getPath(final String pathString) {

        Assert.hasText(pathString, "path:" + pathString + " must not be empty");

        return hazelcastFileSystem.resolveName(pathString);
    }

    private Stream<Path> streamOf(final Integer maxDepth, final String name) {

        return hazelcastFileSystem.find(name, maxDepth, contains(name));
    }

    private List<String> pathStreamConverter(final Stream<Path> stream) {

        return stream
                .map(Path::toAbsolutePath)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    private Integer edit(final Path path, final FileChannel fc,
            final EditableEntryDTO editableEntryDTO) {

        try {
            
            fc.position(editableEntryDTO.getPosition());
            return fc.write(ByteBuffer.wrap(editableEntryDTO.getData()));
        } catch (IOException e) {
            throw new IllegalArgumentException(INVALID_VALUE + path);
        }
    }

    private void registerWatcher(final String path, final URL url) {
        
        final ITopic<NotifyDTO> topic = hazelcastInstance.getReliableTopic(path);
        
        topic.addMessageListener(fileSystemWatcherEventMessageListener);

        hazelcastFileSystem.wacther(path, (event, monitoredPath) 
                -> topic.publish(NotifyDTO
                                    .builder()
                                    .path(monitoredPath)
                                    .kind(event.getKind().name())
                                    .url(url)
                                    .build()));
    }

    private void registerPath(final String path) {
        
        final List<String> registredsPath = hazelcastInstance.getList("watcher-register");

        final int indexOf = registredsPath.indexOf(path);

        if (indexOf > -1) {
            registredsPath.set(indexOf, path);
        } else {
            registredsPath.add(path);
        }
    }
}
