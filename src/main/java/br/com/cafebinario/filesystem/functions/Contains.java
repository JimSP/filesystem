package br.com.cafebinario.filesystem.functions;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import com.google.common.primitives.Bytes;

import br.com.cafebinario.filesystem.dto.EntryDTO;

public final class Contains {
	
	public static boolean containsData(final String keyword, final Supplier<EntryDTO> entryDTOSupplier) {
		return contains(entryDTOSupplier.get(), keyword);
	}
	
	public static boolean containsData(final byte[] keyword, final Supplier<EntryDTO> entryDTOSupplier) {
		return contains(entryDTOSupplier.get(), keyword);
	}
	
	public static BiPredicate<Path, BasicFileAttributes> contains(final String name) {
		return (path, basicFileAttributes) -> path
		.toAbsolutePath()
		.toString()
		.contains(name);
	}
	
	private static boolean contains(final EntryDTO entryDTO, final String keyword) {
		return new String(entryDTO.getData()).contains(keyword);
	}
	
	private static boolean contains(final EntryDTO entryDTO, final byte[] keyword) {
		return Bytes.indexOf(entryDTO.getData(), keyword) > -1;
	}
	
	private Contains() {
		
	}

}
