package br.com.cafebinario.filesystem.watcher;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class WatcherEventKey {

	private final String uuid;
	private final String origin;
}
