package br.com.cafebinario.filesystem.functions;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

public final class HttpPutNotifySender {

    public static final String METHOD = HttpMethod.PUT.name();
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";
    public static final String CONTENT_TYPE = "content-type";

    public static <T> boolean sendHttpRequest(final T content, final URL url) {

         final HttpURLConnection urlConnection = connect(url);
         
         final OutputStream outputStream = getOutputStream(urlConnection);
         
        try {
            
            return send(content, urlConnection, outputStream);
        } catch (Exception e) {
            
            return false;
        }finally {
            
            disconnect(urlConnection);
        }
    }

    @SneakyThrows
    private static OutputStream getOutputStream(final URLConnection urlConnection) {
    	
        return urlConnection.getOutputStream();
    }

    private static void disconnect(final HttpURLConnection urlConnection) {

        urlConnection.disconnect();
    }

    @SneakyThrows
    private static <T> boolean send(final T content, final HttpURLConnection urlConnection,
            final OutputStream outputStream) {

        outputStream.write(new ObjectMapper().writeValueAsBytes(content));
        
        final int responseCode = urlConnection.getResponseCode();
        
        return HttpStatus.resolve(responseCode).is2xxSuccessful();
    }

    @SneakyThrows
    private static HttpURLConnection connect(final URL url) {

        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        
        
        urlConnection.setRequestMethod(METHOD);
        
        urlConnection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON_CHARSET_UTF_8);
        
        urlConnection.setDoOutput(true);
        
        urlConnection.connect();

        return urlConnection;
    }

    private HttpPutNotifySender() {

    }
}
