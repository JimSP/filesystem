package br.com.cafebinario.filesystem.dtos;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class EditDTO {

	@NotBlank
	private final String path;
	
	@Valid
	@NotEmpty
	private final List<@NotNull EditableEntryDTO> editableEntrys;

	@JsonCreator
	public EditDTO(
			@JsonProperty("path") final String path,
			@JsonProperty("editableEntrys") final List<EditableEntryDTO> editableEntrys) {

		this.path = path;
		this.editableEntrys = editableEntrys;
	}
}