package br.com.cafebinario.filesystem.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cafebinario.filesystem.HazelcastFileSystem;
import br.com.cafebinario.filesystem.configurations.FileSystemConfiguration;
import br.com.cafebinario.filesystem.dtos.EntryDTO;
import br.com.cafebinario.filesystem.listener.FileSystemWatcherEventMessageListener;
import br.com.cafebinario.filesystem.mappers.PathToFtpFileMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { FilesService.class, HazelcastFileSystem.class,
        FileSystemConfiguration.class, PathToFtpFileMapper.class })
public class FilesServiceTest {

    @Autowired
    private FilesService filesService;

    @MockBean
    private FileSystemWatcherEventMessageListener fileSystemWatcherEventMessageListener;

    private final String helloworld = "Hello World!";

    @Test(expected = IllegalArgumentException.class)
    public void saveEntryDTO() {

        final String path = filesService
                .save(EntryDTO
                        .builder()
                        .path("test.txt")
                        .data(helloworld.getBytes())
                        .build());

        assertEquals(helloworld, new String(filesService.get(path).getData()));

        filesService.delete(path);
        filesService.get(path);
    }

    @Test
    public void list() throws InterruptedException {
        final List<String> result = filesService.list("/");
        assertThat(result, IsCollectionContaining.hasItems("/.", "/..", "/work"));

        final AtomicInteger i = new AtomicInteger(0);
        while (i.getAndIncrement() < 100) {
            filesService.save(EntryDTO
                    .builder()
                    .path(String.format("test%s.txt", i))
                    .data(helloworld.getBytes())
                    .build());
        }
        
        
        assertEquals(103, filesService.list("/").size());
        assertEquals(10, filesService.find(10, "/", "test").size());
        assertEquals(100, filesService.grep("/test99.txt", "lo Wo").size());
    }

    @Test
    public void indexWithString() {

    }

    @Test
    public void indexWithByteArray() {

    }

    @Test
    public void editWithByteArray() {

    }

    @Test
    public void editWithEditDTO() {

    }

    @Test
    public void editWithEditableEntry() {

    }

    @Test
    public void editWithEditableEntryList() {

    }

    @Test
    public void update() {

    }

    @Test
    public void updateWithByteArray() {

    }

    @Test
    public void updateWithString() {

    }

    @Test
    public void watcher() {

    }
}
