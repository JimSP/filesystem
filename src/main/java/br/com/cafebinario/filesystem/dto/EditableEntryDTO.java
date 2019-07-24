package br.com.cafebinario.filesystem.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditableEntryDTO {

	@NotNull
	@PositiveOrZero
	private final Integer position;
	
	@NotEmpty
	private final byte[] data;
	
	public EditableEntryDTO(final Integer position, final byte[] data) {
		this.position = position;
		this.data = data;
	}
}
