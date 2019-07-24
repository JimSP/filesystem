package br.com.cafebinario.filesystem.watcher;

import java.nio.file.WatchEvent.Kind;

public class WatchKeyOriginAndEvent {
	
	private final String origin;
	private final Kind<?> event;
	
	public WatchKeyOriginAndEvent(final String origin, final Kind<?> event) {
		this.origin = origin;
		this.event = event;
	}

	public String getOrigin() {
		return origin;
	}

	public Kind<?> getEvent() {
		return event;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WatchKeyOriginAndEvent other = (WatchKeyOriginAndEvent) obj;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WatchKeyOriginAndEvent [origin=" + origin + ", event=" + event + "]";
	}
}
