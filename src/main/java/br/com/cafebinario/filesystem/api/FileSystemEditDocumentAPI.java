package br.com.cafebinario.filesystem.api;

import static br.com.cafebinario.filesystem.functions.Reduce.reduce;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cafebinario.filesystem.dtos.EditDTO;
import br.com.cafebinario.filesystem.dtos.EditableEntryDTO;
import br.com.cafebinario.filesystem.dtos.UpdatableEntryDTO;
import br.com.cafebinario.filesystem.dtos.UpdateDTO;
import br.com.cafebinario.filesystem.services.FilesService;

@RestController
public class FileSystemEditDocumentAPI {

	@Autowired
	private FilesService filesService;

	@PutMapping(path = "/edit", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public @ResponseBody Integer edit(
			@RequestBody final EditDTO editDTO) {

		return filesService.edit(editDTO);
	}
	
	@PutMapping(path = "/edit/**", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public @ResponseBody Integer edit(
			final HttpServletRequest httpServletRequest,
			@RequestBody final List<EditableEntryDTO> editableEntryDTOs) {

		final String fullPath = httpServletRequest.getRequestURI();
		
		return filesService.edit(EditDTO
				.builder()
				.path(reduce(fullPath, "/edit"))
				.editableEntrys(editableEntryDTOs)
				.build());
	}

	@PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public @ResponseBody Integer update(
            @RequestBody final UpdateDTO updateDTO) {

        return filesService.update(updateDTO);
    }
    
    @PutMapping(path = "/update/**", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public @ResponseBody Integer update(
    		final HttpServletRequest httpServletRequest,
            @RequestBody final List<UpdatableEntryDTO> updatableEntryDTOs) {

    	final String fullPath = httpServletRequest.getRequestURI();
    	
        return filesService.update(UpdateDTO
                .builder()
                .path(reduce(fullPath, "/update"))
                .updatableEntrys(updatableEntryDTOs)
                .build());
    }
}
