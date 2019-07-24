package br.com.cafebinario.filesystem.api;

import static br.com.cafebinario.filesystem.functions.Reduce.reduce;

import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cafebinario.filesystem.services.FilesService;
import lombok.SneakyThrows;

@RestController
public class FileSystemWatcherAPI {

    @Autowired
    private FilesService fileService;

    @PutMapping(path = "/watcher/{path}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void register(@PathVariable("path") final List<String> path, @RequestBody final String url) {

        fileService.watcher(reduce(path), toUrl(url));
    }
    
    @SneakyThrows
    private URL toUrl(final String url) {

        return new URL(url);
    }
}
