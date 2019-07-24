
package br.com.cafebinario.filesystem.watcher;

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;

/**
 * Evento identificado pelo {@link FileWatcher} com o nome do diretório origem, o tipo e o caminho completo de onde
 * ocorreu o evento.
 *
 * @author Fabio
 */
public class WatcherEvent implements Comparable<WatcherEvent>{

	private final String origin;
	private final Kind<?> kind;
	private final Path path;

	public WatcherEvent(final String origin, final Kind<?> kind, final Path path) {
		this.origin = origin;
		this.kind = kind;
		this.path = path;
	}

	/**
	 * @return Nome do diretório ou do arquivo que originou o evento.
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @return O tipo do evento.
	 */
	public Kind<?> getKind() {
		return kind;
	}

	/**
	 * @return Caminho do arquivo que disparou o evento.
	 */
	public Path getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (kind == null ? 0 : kind.hashCode());
		result = prime * result + (path == null ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final WatcherEvent other = (WatcherEvent) obj;
		if (kind == null) {
			if (other.kind != null) {
				return false;
			}
		} else if (!kind.equals(other.kind)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "WatcherEvent [kind=" + kind + ", path=" + path + "]";
	}

	@Override
	public int compareTo(final WatcherEvent watcherEvent) {
		return this.path.compareTo(watcherEvent.path) + (this.kind.hashCode() - watcherEvent.kind.hashCode()); 
	}

}
