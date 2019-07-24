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

@Builder
@Data
public class UpdateDTO {
    
    @NotBlank
    private final String path;
    
    @Valid
    @NotEmpty
    private final List<@NotNull UpdatableEntryDTO> updatableEntrys;

    @JsonCreator
    public UpdateDTO(
            @JsonProperty("path") final String path,
            @JsonProperty("updatableEntrys") final List<UpdatableEntryDTO> updatableEntrys) {

        this.path = path;
        this.updatableEntrys = updatableEntrys;
    }
}
