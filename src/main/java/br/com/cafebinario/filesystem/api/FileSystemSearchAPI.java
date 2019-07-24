package br.com.cafebinario.filesystem.api;

import static br.com.cafebinario.filesystem.functions.Reduce.reduce;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.cafebinario.filesystem.dtos.SearchDTO;
import br.com.cafebinario.filesystem.services.FilesService;

@RestController
public class FileSystemSearchAPI {

    @Autowired
    private FilesService fileService;

    @GetMapping(path = "/engine/ls/{path}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<String> listFiles(@PathVariable(name = "path", required = true) final List<String> path) {

        return fileService.list(reduce(path));
    }

    @GetMapping(path = "/engine/find/{path}/{maxDepth}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<String> findNameContains(@PathVariable(name = "path", required = true) final List<String> path,
            @PathVariable(name = "maxDepth", required = false) final Integer maxDepth) {

        return fileService.find(Optional.ofNullable(maxDepth).orElse(FilesService.DEFAULT_DEPTH), reduce(path));
    }

    @GetMapping(path = "/engine/grep/{path}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<String> grep(@PathVariable(name = "path", required = true) final List<String> path,
            @RequestParam(name = "keyword", required = true) final String keyword) {

        return fileService.grep(reduce(path), keyword);
    }

    @PostMapping(path = "/engine/grep", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<String> grep(@RequestBody final SearchDTO searchDTO) {

        return fileService.grep(searchDTO.getPath(), searchDTO.getKeyword());
    }

    @PostMapping(path = "/engine/grep/{path}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<String> grep(@PathVariable(name = "path", required = true) final List<String> path,
            @RequestBody final byte[] keyword) {

        return fileService.grep(reduce(path), keyword);
    }
    
    @GetMapping(path = "/engine/index/{path}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<SearchDTO> indexOf(@PathVariable(name = "path", required = true) final List<String> path,
            @RequestParam(name = "keyword", required = true) final String keyword) {

        return fileService.index(reduce(path), keyword);
    }
    
    @PostMapping(path = "/engine/index", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<SearchDTO> indexOf(@RequestBody final SearchDTO searchDTO) {

        return fileService.index(searchDTO.getPath(), searchDTO.getKeyword());
    }
    
    @PostMapping(path = "/engine/index/{path}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody List<SearchDTO> indexOf(@PathVariable(name = "path", required = true) final List<String> path,
            @RequestBody final byte[] keyword) {

        return fileService.index(reduce(path), keyword);
    }
}
