package br.com.cafebinario.filesystem.watcher;

import java.nio.file.WatchService;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WatchServiceWalkAndRegisterWrapper {

	private final WatchService watchService;
	private final Boolean walkAndRegisterDirectories;

}
