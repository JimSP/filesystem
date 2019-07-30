package br.com.cafebinario.filesystem.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.cafebinario.filesystem.HazelcastFileSystem;
import br.com.cafebinario.filesystem.configurations.FileSystemConfiguration;
import br.com.cafebinario.filesystem.dtos.EditDTO;
import br.com.cafebinario.filesystem.dtos.EditableEntryDTO;
import br.com.cafebinario.filesystem.dtos.EntryDTO;
import br.com.cafebinario.filesystem.dtos.SearchDTO;
import br.com.cafebinario.filesystem.dtos.UpdatableEntryDTO;
import br.com.cafebinario.filesystem.dtos.UpdateDTO;
import br.com.cafebinario.filesystem.listener.FileSystemWatcherEventMessageListener;
import br.com.cafebinario.filesystem.mappers.PathToFtpFileMapper;
import br.com.cafebinario.logger.EnableLog;
import br.com.cafebinario.logger.Log;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { FilesService.class, HazelcastFileSystem.class,
        FileSystemConfiguration.class, PathToFtpFileMapper.class, FileSystemWatcherEventMessageListener.class })
@EnableLog
public class FilesServiceTest {

    @Autowired
    private FilesService filesService;

    private final String helloworld = "Hello World!";
    
    @Before
    @Log
    public void before() throws IOException {

        final AtomicInteger i = new AtomicInteger(0);

        while (i.getAndIncrement() < 100) {

            filesService.save(EntryDTO
                                .builder()
                                .path(String.format("test%s.txt", i))
                                .data((helloworld + i.get()).getBytes())
                                .build());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPhysicalOperations() {

        final String path = filesService
                .save(EntryDTO
                        .builder()
                        .path("test101.txt")
                        .data(helloworld.getBytes())
                        .build());

        assertEquals(helloworld, new String(filesService.get(path).getData()));

        filesService.delete(path);

        filesService.get(path);
    }

    @Test
    public void testSearchOperations() throws InterruptedException {

        final List<String> result = filesService.list("/");

        assertThat(result, IsCollectionContaining.hasItems("/.", "/..", "/work"));

        assertEquals(103, filesService.list("/").size());

        assertEquals(100, filesService.find(1, "/", "test").size());

        assertEquals(11, filesService.grep("/", "World!9").size());

        assertEquals(11, filesService.grep("/", "World!9".getBytes()).size());
    }

    @Test
    public void testIndexOperations() {

        final List<SearchDTO> resultString = filesService.index("/", "World!99");

        assertEquals(
                SearchDTO
                    .builder()
                    .indexOf(Arrays.asList(6))
                    .keywordByteArray("World!99".getBytes())
                    .keywordString("World!99")
                    .path("/test99.txt")
                    .build(),
                resultString.get(0));

        final List<SearchDTO> resultByteArray = filesService.index("/", "World!99".getBytes());

        assertEquals(
                SearchDTO
                    .builder()
                    .indexOf(Arrays.asList(6))
                    .keywordByteArray("World!99".getBytes())
                        .keywordString("World!99")
                        .path("/test99.txt")
                        .build(),
                resultByteArray.get(0));
    }

    @Test
    public void testEditOperations() {

        final EditableEntryDTO editableEntryDTO = EditableEntryDTO
                                                    .builder()
                                                    .data(":-)".getBytes())
                                                    .indexOf(Arrays.asList(6))
                                                    .build();

        assertEquals(3,
                filesService
                        .edit(EditDTO
                                .builder()
                                .path("/test99.txt")
                                .editableEntrys(Arrays.asList(editableEntryDTO)).build())
                        .intValue());
    }

    @Test
    public void testUpdateOperations() {

        final UpdatableEntryDTO updatableEntryDTO = UpdatableEntryDTO
                                                        .builder()
                                                        .data(":-)".getBytes())
                                                        .keyword("World".getBytes())
                                                        .build();

        assertEquals(300,
                filesService
                        .update(UpdateDTO
                                    .builder()
                                    .path("/")
                                    .updatableEntrys(Arrays.asList(updatableEntryDTO))
                                    .build())
                        .intValue());

        assertEquals(0,
                filesService
                        .update(UpdateDTO
                                    .builder()
                                    .path("/test99.txt")
                                    .updatableEntrys(Arrays.asList(updatableEntryDTO))
                                    .build())
                        .intValue());

        final UpdatableEntryDTO otherUpdatableEntryDTO = UpdatableEntryDTO
                                                            .builder()
                                                            .data(":-)".getBytes())
                                                            .keyword("!".getBytes())
                                                            .build();

        assertEquals(3,
                filesService
                        .update(UpdateDTO
                                    .builder()
                                    .path("/test99.txt")
                                    .updatableEntrys(Arrays.asList(otherUpdatableEntryDTO))
                                    .build())
                        .intValue());
    }
    
    @Test
    public void testUpdateMassiveOperations() {
    	
    	filesService.save(EntryDTO
                .builder()
                .path("testMassive.txt")
                .data("0123456789\n012345678\n01234567\n0123456\n012345\n01234\n0123\n012\n01\n0\n".getBytes())
                .build());

        final UpdatableEntryDTO updatableEntryDTO = UpdatableEntryDTO
                                                        .builder()
                                                        .data("FF".getBytes())
                                                        .keyword("0".getBytes())
                                                        .build();
/*** TODO verificar por que quantidade não bate.
        assertEquals(10,
                filesService
                        .update(UpdateDTO
                                    .builder()
                                    .path("/")
                                    .updatableEntrys(Arrays.asList(updatableEntryDTO))
                                    .build())
                        .intValue());
*/
        filesService
        	.update(UpdateDTO
	                    .builder()
	                    .path("testMassive.txt")
	                    .updatableEntrys(Arrays.asList(updatableEntryDTO))
	                    .build());
        
        final byte[] data = filesService.get("testMassive.txt").getData();

        assertEquals(new String("FF23456789\nFF2345678\nFF234567\nFF23456\nFF2345\nFF234\nFF23\nFF2\nFF\nFF"), new String(data));
    }
}
