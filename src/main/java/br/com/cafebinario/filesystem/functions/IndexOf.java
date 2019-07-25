package br.com.cafebinario.filesystem.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.primitives.Bytes;

import br.com.cafebinario.filesystem.dtos.EntryDTO;

public final class IndexOf {

	public static int firstIndexOf(final EntryDTO entryDTO, final String keyword) {
		return new String(entryDTO.getData()).indexOf(keyword);
	}
	
	public static int lastIndexOf(final EntryDTO entryDTO, final String keyword) {
		return new String(entryDTO.getData()).lastIndexOf(keyword);
	}
	
	public static List<Integer> indexOf(final EntryDTO entryDTO, final String keyword) {
		final String data = new String(entryDTO.getData());
		final List<Integer> indexes = new ArrayList<>();
		
		final AtomicInteger indexOf = new AtomicInteger(data.indexOf(keyword, 0)); 
		
		while(indexOf.get() > -1) {
			int currentIndex = indexOf.get();
			indexes.add(currentIndex);
			indexOf.set(data.indexOf(keyword, currentIndex));
		}
		
		return indexes;
	}
	
	public static int firstIndexOf(final EntryDTO entryDTO, final byte[] keyword) {
		return Bytes.indexOf(entryDTO.getData(), keyword);
	}
	
	public static List<Integer> indexOf(final EntryDTO entryDTO, final byte[] keyword) {
		final byte[] data = entryDTO.getData();
		final List<Integer> indexes = new ArrayList<>();
		
		final AtomicInteger indexOf = new AtomicInteger(Bytes.indexOf(data, keyword));
		
		while(indexOf.get() > -1) {
			int currentIndex = indexOf.get();
			indexes.add(currentIndex);
			final int length = data.length - keyword.length;
			byte[] cutData = new byte[length];
			System.arraycopy(data, currentIndex, cutData, 0, length);
			indexOf.set(Bytes.indexOf(cutData, keyword));
		}
		
		return indexes;
	}
	
	private IndexOf() {
		
	}
}
