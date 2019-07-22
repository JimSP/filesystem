package br.com.comexport.filesystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FileSystemCommandLinnerRunner implements CommandLineRunner {

    @Autowired
    private HazelcastFileSystem hazelcastFileSystem;

    @Override
    public void run(final String... args) throws Exception {
        final Path path1 = hazelcastFileSystem.resolveName("/helloword1.txt");
        final Path path2 = hazelcastFileSystem.resolveName("/helloword2.txt");
        final Path path3 = hazelcastFileSystem.resolveName("/helloword3.txt");
        final Path path4 = hazelcastFileSystem.resolveName("/helloword4.txt");
        final Path path5 = hazelcastFileSystem.resolveName("/helloword5.txt");
        
        Files.write(path1, "teste 1".getBytes());
        Files.write(path2, "teste 2".getBytes());
        Files.write(path3, "teste 3".getBytes());
        Files.write(path4, "teste 4".getBytes());
        Files.write(path5, "teste 5".getBytes());

        final BiPredicate<Path, BasicFileAttributes> matcher = (p,
                ba) -> p.toString().contains("3");

        try (final Stream<Path> stream = hazelcastFileSystem.find("/", matcher)) {
            stream.forEach(System.out::println);
        }
    }
}
