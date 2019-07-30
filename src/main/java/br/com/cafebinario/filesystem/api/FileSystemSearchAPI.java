package br.com.cafebinario.filesystem.api;

import static br.com.cafebinario.filesystem.functions.Reduce.reduce;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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

	@GetMapping(path = "/engine/ls/**", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<String> listFiles(final HttpServletRequest httpServletRequest) {

		final String fullPath = httpServletRequest.getRequestURI();

		return fileService.list(reduce(fullPath, "/engine/ls"));
	}

	@GetMapping(path = "/engine/find/{maxDepth}/**", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<String> findNameContains(final HttpServletRequest httpServletRequest,
			@PathVariable(name = "maxDepth", required = false) final Integer maxDepth,
			@RequestParam(name = "contains", defaultValue = "") final String expression) {

		final String fullPath = httpServletRequest.getRequestURI();

		return fileService.find(Optional.ofNullable(maxDepth).orElse(FilesService.DEFAULT_DEPTH),
				reduce(fullPath, "/engine/find/" + maxDepth), expression);
	}

	@GetMapping(path = "/engine/grep/**", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<String> grep(final HttpServletRequest httpServletRequest,
			@RequestParam(name = "keyword", required = true) final String keyword) {

		final String fullPath = httpServletRequest.getRequestURI();

		return fileService.grep(reduce(fullPath, "/engine/grep/"), keyword);
	}

	@PostMapping(path = "/engine/grep", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<String> grep(@RequestBody final SearchDTO searchDTO) {

		return fileService.grep(searchDTO.getPath(), searchDTO.getKeywordByteArray());
	}

	@PostMapping(path = "/engine/grep/**", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<String> grep(final HttpServletRequest httpServletRequest,
			@RequestBody final byte[] keyword) {

		final String fullPath = httpServletRequest.getRequestURI();

		return fileService.grep(reduce(fullPath, "/engine/grep"), keyword);
	}

	@GetMapping(path = "/engine/index/**", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<SearchDTO> indexOf(final HttpServletRequest httpServletRequest,
			@RequestParam(name = "keyword", required = true) final String keyword) {

		final String fullPath = httpServletRequest.getRequestURI();

		return fileService.index(reduce(fullPath, "/engine/index"), keyword);
	}

	@PostMapping(path = "/engine/index", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<SearchDTO> indexOf(@RequestBody final SearchDTO searchDTO) {

		return fileService.index(searchDTO.getPath(), searchDTO.getKeywordByteArray());
	}

	@PostMapping(path = "/engine/index/**", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody List<SearchDTO> indexOf(final HttpServletRequest httpServletRequest,
			@RequestBody final byte[] keyword) {

		final String fullPath = httpServletRequest.getRequestURI();

		return fileService.index(reduce(fullPath, "/engine/index"), keyword);
	}
}
