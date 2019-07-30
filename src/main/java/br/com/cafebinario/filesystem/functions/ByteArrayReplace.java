package br.com.cafebinario.filesystem.functions;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class ByteArrayReplace {

	private static final String INVALID_INDEX = "start index after end of src";
	private static final String INVALID_LENGTH = "Invalid length for array pattern replacement";
	private static final String INVALID_OFFSET = "Invalid offset for array pattern replacement";

	public static byte[] replace(final byte[] src, final byte[] pattern, final byte[] replacement) {

		if (src == null) {
		
			return new byte[0];
		}
		
		return replace(src, 0, src.length, pattern, replacement);
	}

	public static byte[] replace(byte[] src, int offset, int length, byte[] pattern, byte[] replacement) {

		if (offset < 0 || offset > src.length) {
			
			throw new IllegalArgumentException(INVALID_OFFSET);
		}

		if (length < 0 || offset + length > src.length) {
			
			throw new IllegalArgumentException(INVALID_LENGTH);
		}

		if (pattern == null || pattern.length == 0) {
			
			return Arrays.copyOfRange(src, offset, offset + length);
		}

		ByteBuffer dest = null;
		if (replacement == null || replacement.length != pattern.length) {
			
			int newLength = replace(src, offset, length, pattern, replacement, null);
			
			if (newLength != length) {
				
				dest = ByteBuffer.allocate(newLength);
			} else {
				
				return Arrays.copyOfRange(src, offset, offset + length);
			}
		} else {
			
			dest = ByteBuffer.allocate(length);
		}
		
		replace(src, offset, length, pattern, replacement, dest);
		return dest != null ? dest.array() : new byte[0];
	}

	public static int replace(final byte[] src, final int offset, final int length, final byte[] pattern,
			final byte[] replacement, final ByteBuffer dest) {

		if (pattern == null || pattern.length == 0) {
			
			if (dest != null) {
			
				dest.put(src, offset, length);
			}
			
			return length;
		}

		final byte patternFirst = pattern[0];
		
		final int replacementLength = replacement == null ? 0 : replacement.length;

		int lastPosition = offset;
		
		int currentPosition = offset;
		
		int newLength = 0;
		
		while (currentPosition < offset + length) {
			
			if (src[currentPosition] == patternFirst && regionEquals(src, currentPosition, pattern)) {
				
				if (dest != null) {
					
					dest.put(src, lastPosition, currentPosition - lastPosition);
					
					if (replacement != null) {
						
						dest.put(replacement);
					}
				}
				newLength += currentPosition - lastPosition + replacementLength;
				
				currentPosition += pattern.length;
				
				lastPosition = currentPosition;
				
			} else {
				
				currentPosition++;
			}
		}

		newLength += currentPosition - lastPosition;
		
		if (dest != null) {
			
			dest.put(src, lastPosition, currentPosition - lastPosition);
		}

		return newLength;
	}

	public static boolean regionEquals(final byte[] src, final int start, final byte[] pattern) {

		if (src == null) {
			
			if (start == 0) {
				
				return pattern == null;
			}
			
			throw new IllegalArgumentException(INVALID_INDEX);
		}
		
		if (pattern == null) {
			
			return false;
		}

		if (start >= src.length) {
			
			throw new IllegalArgumentException(INVALID_INDEX);
		}

		if (src.length < start + pattern.length) {
			
			return false;
		}

		for (int i = 0; i < pattern.length; i++)
			
			if (pattern[i] != src[start + i]) {
				
				return false;
			}

		return true;
	}

	private ByteArrayReplace() {

	}
}
