package br.com.cafebinario.filesystem.watcher;

import java.nio.file.WatchEvent.Kind;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WatchKeyOriginAndEvent {
	
	private final String origin;
	private final Kind<?> event;
}