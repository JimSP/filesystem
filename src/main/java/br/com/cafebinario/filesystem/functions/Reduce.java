package br.com.cafebinario.filesystem.functions;

import java.util.List;

public final class Reduce {

    public static String reduce(final List<String> path) {
        return path
                .stream()
                .reduce((a, b) -> a.concat("/")
                        .concat(b))
                .orElse("");
    }

    private Reduce() {

    }
}
