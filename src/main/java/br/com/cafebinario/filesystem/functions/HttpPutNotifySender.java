package br.com.cafebinario.filesystem.functions;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cafebinario.filesystem.dto.NotifyDTO;
import lombok.SneakyThrows;

public final class HttpPutNotifySender {

    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
    public static final String CONTENT_TYPE = "content-type";
    
    @SneakyThrows
    public static boolean sendHttpRequest(final NotifyDTO notifyDTO) {

        final URL url = notifyDTO.getUrl();
        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod(HttpMethod.PUT.name());
        urlConnection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8);

        try (final OutputStream outputStream = url.openConnection().getOutputStream()) {
            outputStream.write(new ObjectMapper().writeValueAsBytes(notifyDTO));
            final int responseCode = urlConnection.getResponseCode();
            return HttpStatus.resolve(responseCode).is2xxSuccessful();
        }
    }
    
    private HttpPutNotifySender() {
        
    }
}
