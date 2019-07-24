package br.com.cafebinario.filesystem.dtos;

import java.io.Serializable;
import java.net.URL;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class NotifyDTO implements Serializable{
    
    private static final long serialVersionUID = 5103647791124995761L;

    private final String path;
    private final String kind;
    
    @JsonIgnore
    private final URL url;
    
    @JsonCreator
    public NotifyDTO(
            @JsonProperty("path") final String path,
            @JsonProperty("kind")  final String kind,
            final URL url) {

        this.path = path;
        this.kind = kind;
        this.url = url;
    }
}
