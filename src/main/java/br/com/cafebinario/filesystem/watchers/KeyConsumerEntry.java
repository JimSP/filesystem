package br.com.cafebinario.filesystem.watchers;

import java.nio.file.WatchKey;
import java.util.function.BiConsumer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class KeyConsumerEntry {

	private final WatchKey watchKey;
	
	private final BiConsumer<WatcherEvent, String> consumer;
}
