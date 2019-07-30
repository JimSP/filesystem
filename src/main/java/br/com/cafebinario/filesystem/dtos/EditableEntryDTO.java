package br.com.cafebinario.filesystem.dtos;

import java.util.List;

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
	private final List<Integer> indexOf;
	
	@NotEmpty
	private final byte[] data;
	
	public EditableEntryDTO(final List<Integer> indexOf, final byte[] data) {
		this.indexOf = indexOf;
		this.data = data;
	}
}
