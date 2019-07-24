package br.com.cafebinario.filesystem.watcher;

import java.nio.file.WatchService;

public class WatchServiceWalkAndRegisterWrapper {

	private final WatchService watchService;
	private final Boolean walkAndRegisterDirectories;

	public WatchServiceWalkAndRegisterWrapper(WatchService watchService, Boolean walkAndRegisterDirectories) {
		super();
		this.watchService = watchService;
		this.walkAndRegisterDirectories = walkAndRegisterDirectories;
	}

	public WatchService getWatchService() {
		return watchService;
	}

	public Boolean getWalkAndRegisterDirectories() {
		return walkAndRegisterDirectories;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((walkAndRegisterDirectories == null) ? 0 : walkAndRegisterDirectories.hashCode());
		result = prime * result + ((watchService == null) ? 0 : watchService.hashCode());
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
		WatchServiceWalkAndRegisterWrapper other = (WatchServiceWalkAndRegisterWrapper) obj;
		if (walkAndRegisterDirectories == null) {
			if (other.walkAndRegisterDirectories != null)
				return false;
		} else if (!walkAndRegisterDirectories.equals(other.walkAndRegisterDirectories))
			return false;
		if (watchService == null) {
			if (other.watchService != null)
				return false;
		} else if (!watchService.equals(other.watchService))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WatchServiceWalkAndRegisterWrapper [watchService=" + watchService + ", walkAndRegisterDirectories="
				+ walkAndRegisterDirectories + "]";
	}
}
