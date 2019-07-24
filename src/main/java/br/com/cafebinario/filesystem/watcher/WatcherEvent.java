
package br.com.cafebinario.filesystem.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WatcherEvent implements Comparable<WatcherEvent>{

	private final String origin;
	private final Kind<?> kind;
	private final Path path;

	@Override
	public int compareTo(final WatcherEvent watcherEvent) {
		return this.path.compareTo(watcherEvent.path) + (this.kind.hashCode() - watcherEvent.kind.hashCode()); 
	}
}
