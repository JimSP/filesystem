package br.com.cafebinario.filesystem.api;

import static br.com.cafebinario.filesystem.functions.Reduce.reduce;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

	@PutMapping(path = "/watcher/**", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public void register(
			final HttpServletRequest httpServletRequest,
			@RequestBody final String host) {

		final String[] hostNameAndPortNumber = host.split("[:]");
		
		final String hostName = hostNameAndPortNumber[0];
		
		final Integer portNumber = hostNameAndPortNumber.length > 1 ? Integer.valueOf(hostNameAndPortNumber[1]) : 0;
		
		final String fullPath = httpServletRequest.getRequestURI();
		
		fileService.watcher(reduce(fullPath, "/watcher"), toUrl(hostName, portNumber));
	}

	@SneakyThrows
	private URL toUrl(final String host, final Integer port) {

		if(port == 0) {
			return new URL("http", host, "/notify");
		}
		
		return new URL("http", host, port, "/notify");
	}
}
