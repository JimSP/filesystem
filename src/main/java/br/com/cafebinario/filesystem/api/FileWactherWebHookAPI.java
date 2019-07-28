package br.com.cafebinario.filesystem.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cafebinario.filesystem.dtos.NotifyDTO;
import br.com.cafebinario.logger.Log;

@RestController
public class FileWactherWebHookAPI {

	@PutMapping(path = "/notify", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	@Log
	public void notify(@RequestBody final NotifyDTO notifyDTO) {

	}
}
