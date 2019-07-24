package br.com.cafebinario.filesystem.functions;

import com.google.common.primitives.Bytes;

import br.com.cafebinario.filesystem.dto.EntryDTO;

public final class IndexOf {

	public static int indexOf(final EntryDTO entryDTO, final String keyword) {
		return new String(entryDTO.getData()).indexOf(keyword);
	}
	
	public static int indexOf(final EntryDTO entryDTO, final byte[] keyword) {
		return Bytes.indexOf(entryDTO.getData(), keyword);
	}
	
	private IndexOf() {
		
	}
}
