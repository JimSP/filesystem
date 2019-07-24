package br.com.cafebinario.filesystem.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cafebinario.filesystem.dto.EditDTO;
import br.com.cafebinario.filesystem.services.FilesService;

@RestController
public class FileSystemEditDocumentAPI {

	@Autowired
	private FilesService filesService;

	@PutMapping(path = "/edit", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code=HttpStatus.ACCEPTED)
	public @ResponseBody Integer edit(@RequestBody final EditDTO editDTO) {

		return filesService.edit(editDTO.getPath(), editDTO.getPosition(), editDTO.getData());
	}
}
