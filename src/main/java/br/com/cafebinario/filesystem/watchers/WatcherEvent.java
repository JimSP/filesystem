package br.com.cafebinario.filesystem.watchers;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class WatcherEvent {
	
	private final String path;
	private final String event;

}
