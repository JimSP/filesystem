package br.com.cafebinario.filesystem.functions;

import java.util.function.Predicate;

public final class Retry {

    public static <T> boolean retry(final Predicate<T> predicate, final T content, final int qty) {
        int retry = 0;
        while (retry < qty) {
            if (predicate.test(content)) {
                return true;
            }

            retry++;
        }
        
        return false;
    }
    
    private Retry() {
        
    }
}
