package br.com.cafebinario.filesystem.services;

import static br.com.cafebinario.filesystem.functions.Contains.containsData;
import static br.com.cafebinario.filesystem.functions.Contains.contains;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import br.com.cafebinario.filesystem.HazelcastFileSystem;
import br.com.cafebinario.filesystem.api.dto.EntryDTO;
import br.com.cafebinario.logger.Log;
import br.com.cafebinario.logger.LogLevel;
import br.com.cafebinario.logger.VerboseMode;

@Service
public class FilesService {

	public static final int DEFAULT_DEPTH = 1;

	private static final String INVALID_VALUE = "invalid value for path: ";

	@Autowired
	private HazelcastFileSystem hazelcastFileSystem;

	@Log
	public String save(final EntryDTO entryDTO) {

		try {

			final Path path = getPath(entryDTO.getPath());

			return Files.write(path, entryDTO.getData()).toAbsolutePath().toString();
		} catch (IOException e) {
			throw new IllegalArgumentException(INVALID_VALUE + entryDTO.getPath());
		}
	}

	@Log
	public EntryDTO get(final String path) {

		try {

			return EntryDTO.builder().path(path).data(Files.readAllBytes(getPath(path))).build();
		} catch (IOException e) {
			throw new IllegalArgumentException(INVALID_VALUE + path);
		}
	}

	@Log
	public String delete(final String path) {

		try {

			return Files.deleteIfExists(getPath(path)) ? path : null;
		} catch (IOException e) {
			throw new IllegalArgumentException(INVALID_VALUE + path);
		}
	}

	@Log
	public List<String> list(final String path) {

		try (final Stream<Path> stream = Files.list(getPath(path))) {

			return pathStreamConverter(stream);
		} catch (IOException e) {
			throw new IllegalArgumentException(INVALID_VALUE + path);
		}
	}

	@Log
	public List<String> find(final Integer maxDepth, final String name) {

		return pathStreamConverter(streamOf(maxDepth, name));
	}

	@Log
	public List<String> grep(final String path, final String keyword) {

		return pathStreamConverter(streamOf(DEFAULT_DEPTH, path)
				.filter(pathPredicate -> containsData(keyword, 
						() -> get(pathPredicate.toAbsolutePath().toString()))));
	}

	@Log
	public List<String> grep(final String path, final byte[] keyword) {

		return pathStreamConverter(streamOf(DEFAULT_DEPTH, path)
				.filter(pathPredicate -> containsData(keyword, 
						() -> get(pathPredicate.toAbsolutePath().toString()))));
	}

	@Log(verboseMode = VerboseMode.ON, logLevel = LogLevel.DEBUG)
	private Path getPath(final String pathString) {

		Assert.hasText(pathString, "path:" + pathString + " must not be empty");

		return hazelcastFileSystem.resolveName(pathString);
	}

	private Stream<Path> streamOf(final Integer maxDepth, final String name) {

		return hazelcastFileSystem.find(name, maxDepth, contains(name));
	}

	private List<String> pathStreamConverter(final Stream<Path> stream) {

		return stream.map(Path::toAbsolutePath).map(Path::toString).collect(Collectors.toList());
	}
}
