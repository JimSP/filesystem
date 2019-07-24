package br.com.cafebinario.filesystem.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cafebinario.filesystem.services.FilesService;

@RestController
public class FileSystemWatcherAPI {
	
	@Autowired
	private FilesService fileService;

	@PutMapping(path = "/watcher/{path}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public void register() {
		
	}
}
