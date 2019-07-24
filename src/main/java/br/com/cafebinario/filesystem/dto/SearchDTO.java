package br.com.cafebinario.filesystem.dto;

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
    
    private final Integer indexOf; 

    @JsonCreator
	public SearchDTO(
			@JsonProperty("path") final String path,
			@JsonProperty("keyword")  byte[] keyword,
			@JsonProperty("indexOf") final Integer indexOf) {

		this.path = path;
		this.keyword = keyword;
		this.indexOf = indexOf;
	}
}

