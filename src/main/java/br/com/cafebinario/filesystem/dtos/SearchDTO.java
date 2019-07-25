package br.com.cafebinario.filesystem.dtos;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public final class SearchDTO {

    @NotBlank
    private final String path;

    @NotNull
    private final byte[] keyword;
    
    private final List<Integer> indexes; 

    @JsonCreator
	public SearchDTO(
			@JsonProperty("path") final String path,
			@JsonProperty("keyword")  byte[] keyword,
			@JsonProperty("indexes") final List<Integer> indexes) {

		this.path = path;
		this.keyword = keyword;
		this.indexes = indexes;
	}
}

