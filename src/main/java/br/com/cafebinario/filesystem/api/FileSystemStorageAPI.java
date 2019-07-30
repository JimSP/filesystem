package br.com.cafebinario.filesystem.api;

import static br.com.cafebinario.filesystem.functions.Reduce.reduce;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cafebinario.filesystem.dtos.EntryDTO;
import br.com.cafebinario.filesystem.services.FilesService;

@RestController
public class FileSystemStorageAPI {

    @Autowired
    private FilesService fileService;

    @PostMapping(path = "/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public @ResponseBody String createFile(@RequestBody final EntryDTO entryDTO) {

        return save(entryDTO);
    }

    @PostMapping(path = "/**", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public @ResponseBody String createFile(final HttpServletRequest httpServletRequest,
            @RequestBody final byte[] data) {

    	final String fullPath = httpServletRequest.getRequestURI();
    	
        return save(reduce(fullPath), data);
    }

    @GetMapping(path = "/**", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public @ResponseBody EntryDTO getFile(final HttpServletRequest httpServletRequest) {

    	final String fullPath = httpServletRequest.getRequestURI();
    	
        return getEntryDTO(reduce(fullPath));
    }

    @GetMapping(path = "/**", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] getFileRawData(final HttpServletRequest httpServletRequest) {

    	final String fullPath = httpServletRequest.getRequestURI();
    	
        return getData(reduce(fullPath));
    }

    @PutMapping(path = "/**", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public @ResponseBody String acceptFile(final HttpServletRequest httpServletRequest,
            @RequestBody final byte[] data) {

    	final String fullPath = httpServletRequest.getRequestURI();
    	
        return save(reduce(fullPath), data);
    }

    @PutMapping(path = "/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public @ResponseBody String acceptFile(@RequestBody final EntryDTO entryDTO) {

        return save(entryDTO);
    }

    @PatchMapping(path = "/**", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public @ResponseBody EntryDTO patchFile(final HttpServletRequest httpServletRequest,
            @RequestBody final byte[] data) {

    	final String fullPath = httpServletRequest.getRequestURI();
    	
        return patch(reduce(fullPath), data);
    }

    @PatchMapping(path = "/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public @ResponseBody byte[] patchFile(@RequestBody final EntryDTO entryDTO) {

        final String path = entryDTO.getPath();
        final byte[] data = getData(path);

        save(path, entryDTO.getData());

        return data;
    }

    @DeleteMapping(path = "/**", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public @ResponseBody String deleteFile(final HttpServletRequest httpServletRequest) {

    	final String fullPath = httpServletRequest.getRequestURI();
    	
        return delete(reduce(fullPath));
    }

    @DeleteMapping(path = "/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public @ResponseBody String deleteFile(@RequestBody final EntryDTO entryDTO) {

        return delete(entryDTO.getPath());
    }

    private String save(final EntryDTO entryDTO) {

        return fileService.save(entryDTO);
    }

    private String save(final String path, final byte[] data) {
        
        return save(buildEntryDTO(path, data));
    }

    private EntryDTO getEntryDTO(final String path) {

        return fileService.get(path);
    }

    private byte[] getData(final String path) {

        return getEntryDTO(path).getData();
    }

    private String delete(final String path) {

        return fileService.delete(path);
    }

    private EntryDTO patch(final EntryDTO entryDTO) {

        final EntryDTO entryDTOExisted = getEntryDTO(entryDTO.getPath());

        Optional.of(entryDTOExisted).ifPresent(entryDTOPresent -> save(
                entryDTOPresent.toBuilder().data(entryDTO.getData()).build()));
        return entryDTO;
    }

    private EntryDTO patch(final String path, final byte[] data) {

        return patch(buildEntryDTO(path, data));
    }

    private EntryDTO buildEntryDTO(final String path, final byte[] data) {

        return EntryDTO.builder().path(path).data(data).build();
    }
}
