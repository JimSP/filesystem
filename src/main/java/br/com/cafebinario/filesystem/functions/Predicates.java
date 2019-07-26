package br.com.cafebinario.filesystem.functions;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

import com.google.common.primitives.Bytes;

import br.com.cafebinario.filesystem.dtos.EntryDTO;

public final class Predicates {
	
	public static BiPredicate<Path, BasicFileAttributes> all() {
		return (path, basicFileAttributes) -> true;
	}
	
	public static BiPredicate<Path, BasicFileAttributes> none() {
		return (path, basicFileAttributes) -> false;
	}
	
	public static BiPredicate<Path, BasicFileAttributes> containsData(final String keyword, final Supplier<EntryDTO> entryDTOSupplier) {
		return (path, basicFileAttributes) -> contains(entryDTOSupplier.get(), keyword);
	}
	
	public static BiPredicate<Path, BasicFileAttributes> containsData(final byte[] keyword, final Supplier<EntryDTO> entryDTOSupplier) {
		return (path, basicFileAttributes) -> contains(entryDTOSupplier.get(), keyword);
	}
	
	public static BiPredicate<Path, BasicFileAttributes> contains(final String name) {
		return (path, basicFileAttributes) -> path
		.toAbsolutePath()
		.toString()
		.contains(name);
	}
	
	public static boolean contains(final EntryDTO entryDTO, final String keyword) {
		return new String(entryDTO.getData()).contains(keyword);
	}
	
	public static boolean contains(final EntryDTO entryDTO, final byte[] keyword) {
		return Bytes.indexOf(entryDTO.getData(), keyword) > -1;
	}
	
	private Predicates() {
		
	}

}
