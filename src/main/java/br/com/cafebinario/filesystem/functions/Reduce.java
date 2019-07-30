package br.com.cafebinario.filesystem.functions;

public final class Reduce {

	public static String reduce(final String fullPath) {
        return fullPath;
    }
	
    public static String reduce(final String fullPath, final String basePath) {
        return fullPath.substring(basePath.length());
    }

    private Reduce() {

    }
}
