package br.com.cafebinario.filesystem;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.cafebinario.filesystem.watchers.FileWatcher;
import br.com.cafebinario.filesystem.watchers.WatcherEvent;
import lombok.SneakyThrows;

@Component
public class HazelcastFileSystem {

    private static final String PREFIX = "/";

    @Autowired
    private FileSystem fileSystem;

    public Path resolveName(final String filePath) {
        if (filePath.startsWith(PREFIX)) {
            return fileSystem.getPath(filePath);
        }

        return fileSystem.getPath(PREFIX + filePath);

    }

    @SneakyThrows
    public Stream<Path> find(final String name,
            final BiPredicate<Path, BasicFileAttributes> matcher) {

        return Files.find(resolveName(name), 1, matcher);
    }
    
    @SneakyThrows
    public Stream<Path> find(final String name, final Integer maxDepth ,
            final BiPredicate<Path, BasicFileAttributes> matcher) {

        return Files.find(resolveName(name), maxDepth, matcher);
    }
    
    @SneakyThrows
    public void wacther(final String filePath, final BiConsumer<WatcherEvent, String> fileConsumer) {
    	
    	final FileWatcher fileWatcher = FileWatcher.of();
		
    	fileWatcher.registerConsumerAllEvents(resolveName(filePath), fileConsumer);
		
		fileWatcher.start();
    }
}
