package br.com.cafebinario.filesystem.watcher;

import java.security.InvalidParameterException;


public final class WatcherEventKey {
	
	public static WatcherEventKey of(final String uuid, final String origin){
		if((uuid == null || "".equals(uuid)) && origin == null || "".equals(origin)){
			throw new InvalidParameterException(String.format("uuid=%s or origin=%s is null.", uuid, origin));
		}

		return new WatcherEventKey(uuid, origin);
	}

	private final String uuid;
	private final String origin;

	private WatcherEventKey(final String uuid, final String origin) {
		super();
		this.uuid = uuid;
		this.origin = origin;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		WatcherEventKey other = (WatcherEventKey) obj;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "WatcherEventKey [uuid=" + uuid + ", origin=" + origin + "]";
	}
}
