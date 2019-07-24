package br.com.cafebinario.filesystem.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatableEntryDTO {

    @NotNull
    @PositiveOrZero
    private final byte[] keyword;
    
    @NotEmpty
    private final byte[] data;
    
    public UpdatableEntryDTO(final byte[] keyword, final byte[] data) {
        this.keyword = keyword;
        this.data = data;
    }
}
