package br.com.cafebinario.filesystem.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.cafebinario.filesystem.dto.NotifyDTO;

@RestController
public class FileWactherWebHookAPI {

    @PutMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void notify(@RequestBody final NotifyDTO notifyDTO) {
        throw new UnsupportedOperationException();
    }
}
