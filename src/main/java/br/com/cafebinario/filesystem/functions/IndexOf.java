package br.com.cafebinario.filesystem.functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.primitives.Bytes;

public final class IndexOf {

	public static Integer firstIndexOf(final byte[] data, final byte[] keyword) {
		
		return Bytes.indexOf(data, keyword);
	}
	
	
	public static Integer firstIndexOf(final String data, final String keyword) {
		
		return data.indexOf(keyword);
	}
	
	public static Integer lastIndexOf(final String data, final String keyword) {
		
		return data.lastIndexOf(keyword);
	}
	
	public static List<Integer> indexOf(final String data, final String keyword) {
		
		final List<Integer> indexes = new ArrayList<>();
		
		final AtomicInteger indexOf = new AtomicInteger(data.indexOf(keyword, 0));
		
		while(indexOf.get() > -1) {
			
			int currentIndex = indexOf.get();
			
			indexes.add(currentIndex);
			
			indexOf.set(data.indexOf(keyword, currentIndex + 1));
		}
		
		return indexes;
	}
	
	public static List<Integer> indexOf(final byte[] data, final byte[] keyword) {
		
		final List<Integer> indexes = new ArrayList<>();
		
		int indexOf = Bytes.indexOf(data, keyword);
		
		byte[] dataCut = null;
		
		int length = 0;
		
		while(indexOf > -1) {
			
			indexes.add(indexOf + length);
			
			length += keyword.length + indexOf;
			
			if(length >= data.length) {
				break;
			}
			
			dataCut = Arrays.copyOfRange(data, length, data.length);
			
			indexOf = Bytes.indexOf(dataCut, keyword);
		}
		
		return indexes;
	}
	
	
	private IndexOf() {
		
	}
}
