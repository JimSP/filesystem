package br.com.cafebinario.filesystem.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@ToString(of = "path")
@EqualsAndHashCode(of = "path")
public final class EntryDTO {

	@NotBlank
	private final String path;

	@NotNull
	private final byte[] data;
}
