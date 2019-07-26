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
    private final byte[] keywordByteArray;
    
    @NotNull
    private final String keywordString;
    
    private final Integer indexOf;

    @JsonCreator
	public SearchDTO(
			@JsonProperty("path") final String path,
			@JsonProperty("keywordByteArray")  byte[] keywordByteArray,
			@JsonProperty("keywordString")  String keywordString,
			@JsonProperty("indexOf") final Integer indexOf) {

		this.path = path;
		this.keywordByteArray = keywordByteArray;
		this.keywordString = keywordString;
		this.indexOf = indexOf;
	}
}

