package ch.avocado.share.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class CharsetTokenGeneratorTest {
	
	static final byte[] LOWERCASE_CHARSET = "abcdefghijklmnopqrstuvwxyz".getBytes();
	static final byte[] ALPHANUM_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890".getBytes();
	
	/**
	 * Test that constructor throws an {@link IllegalArgumentException} when initialized with null.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_constructor_with_null() {
		@SuppressWarnings("unused")
		TokenGenerator generator = new CharsetTokenGenerator(null);
	}
	
	/**
	 * Test that {@link CharsetTokenGenerator.generateInsecureToken} throws an {@link IllegalArgumentException} 
	 * when called with a length of zero.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_generation_with_zero_length() {
		TokenGenerator generator = new CharsetTokenGenerator(LOWERCASE_CHARSET);
		generator.generateToken(0);
	}

	/**
	 * Test that {@link CharsetTokenGenerator.generateInsecureToken} throws an {@link IllegalArgumentException} 
	 * when called with a negative length.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_generation_with_minus_one_length() {
		TokenGenerator generator = new CharsetTokenGenerator(LOWERCASE_CHARSET);
		generator.generateToken(-1);
	}
	
	
	/**
	 * Helper function to check if all characters from the token are 
	 * in the charset.
	 * @param token The token to check
	 * @param charset The charset to check against
	 */
	private void assertTokenInCharset(byte[] token, byte[] charset) {
		boolean matched;
		for(byte tokenChar : token) {
			matched = false;
			for(byte charsetChar : charset) {
				if(charsetChar == tokenChar) {
					matched = true;
				}
			}
			assertTrue("Token character '" + tokenChar + "' not in charset", matched);
		}
	}
	
	
	/**
	 * Test {@link Generator.generateToken}
	 */
	@Test
	public void test_generation() {
		byte[] token;
		TokenGenerator generator = new CharsetTokenGenerator(LOWERCASE_CHARSET);
		assertNotNull(generator);
		token = generator.generateToken(10);
		assertNotNull(token);
		assertTrue(token.length == 10);
		assertTokenInCharset(token, LOWERCASE_CHARSET);
		token = generator.generateToken(1);
		assertNotNull(token);
		assertTrue(token.length == 1);
		assertTokenInCharset(token, LOWERCASE_CHARSET);
		token = generator.generateToken(1024);
		assertNotNull(token);
		assertTrue(token.length == 1024);
		assertTokenInCharset(token, LOWERCASE_CHARSET);
	
		generator = new CharsetTokenGenerator(ALPHANUM_CHARSET);
		token = generator.generateToken(10);
		assertNotNull(token);
		assertTrue(token.length == 10);
		assertTokenInCharset(token, ALPHANUM_CHARSET);
		token = generator.generateToken(1);
		assertNotNull(token);
		assertTrue(token.length == 1);
		assertTokenInCharset(token, ALPHANUM_CHARSET);
		token = generator.generateToken(1024);
		assertNotNull(token);
		assertTrue(token.length == 1024);
		assertTokenInCharset(token, ALPHANUM_CHARSET);
	}
}
