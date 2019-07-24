package br.com.cafebinario.filesystem.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class EditDTO {

	private final String path;
	private final Integer position;
	private final byte[] data;

	@JsonCreator
	public EditDTO(
			@JsonProperty("path") final String path,
			@JsonProperty("position") final Integer position,
			@JsonProperty("data") final byte[] data) {

		this.path = path;
		this.position = position;
		this.data = data;
	}
}
