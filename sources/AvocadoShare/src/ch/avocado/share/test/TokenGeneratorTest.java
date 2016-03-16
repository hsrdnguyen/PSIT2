package ch.avocado.share.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.avocado.share.helper.TokenGenerator;

public class TokenGeneratorTest {
	
	static char[] LOWERCASE_CHARSET = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	static char[] ALPHANUM_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234567890".toCharArray();
	
	/**
	 * Test that constructor throws an {@link IllegalArgumentException} when initialized with null.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_constructor_with_null() {
		@SuppressWarnings("unused")
		TokenGenerator generator = new TokenGenerator(null);
	}
	
	/**
	 * Test that {@link TokenGenerator.generateInsecureToken} throws an {@link IllegalArgumentException} 
	 * when called with a length of zero.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_insecure_generation_with_zero_length() {
		TokenGenerator generator = new TokenGenerator(LOWERCASE_CHARSET);
		generator.generateInsecureToken(0);
	}

	/**
	 * Test that {@link TokenGenerator.generateInsecureToken} throws an {@link IllegalArgumentException} 
	 * when called with a negative length.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_insecure_generation_with_minus_one_length() {
		TokenGenerator generator = new TokenGenerator(LOWERCASE_CHARSET);
		generator.generateInsecureToken(-1);
	}
	
	/**
	 * Test that {@link TokenGenerator.generateSecureToken} throws an {@link IllegalArgumentException} 
	 * when called with a length of zero.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_secure_generation_with_zero_length() {
		TokenGenerator generator = new TokenGenerator(LOWERCASE_CHARSET);
		generator.generateSecureToken(0);
	}

	/**
	 * Test that {@link TokenGenerator.generateSecureToken} throws an {@link IllegalArgumentException} 
	 * when called with a negative length.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void test_secure_generation_with_minus_one_length() {
		TokenGenerator generator = new TokenGenerator(LOWERCASE_CHARSET);
		generator.generateSecureToken(-1);
	}

	/**
	 * Helper function to check if all characters from the token are 
	 * in the charset.
	 * @param token The token to check
	 * @param charset The charset to check against
	 */
	private void assertTokenInCharset(String token, char[] charset) {
		boolean matched;
		for(char tokenChar : token.toCharArray()) {
			matched = false;
			for(char charsetChar : charset) {
				if(charsetChar == tokenChar) {
					matched = true;
				}
			}
			assertTrue("Token character '" + tokenChar + "' not in charset", matched);
		}
	}
	
	/**
	 * Test {@link Generator.generateInsecureToken}
	 */
	@Test
	public void test_insecure_generation() {
		String token;
		TokenGenerator generator = new TokenGenerator(LOWERCASE_CHARSET);
		assertNotNull(generator);
		token = generator.generateInsecureToken(10);
		assertNotNull(token);
		assertTrue(token.length() == 10);
		assertTokenInCharset(token, LOWERCASE_CHARSET);
		token = generator.generateInsecureToken(1);
		assertNotNull(token);
		assertTrue(token.length() == 1);
		assertTokenInCharset(token, LOWERCASE_CHARSET);
		token = generator.generateInsecureToken(1024);
		assertNotNull(token);
		assertTrue(token.length() == 1024);
		assertTokenInCharset(token, LOWERCASE_CHARSET);
		
		generator = new TokenGenerator(ALPHANUM_CHARSET);
		token = generator.generateInsecureToken(10);
		assertNotNull(token);
		assertTrue(token.length() == 10);
		assertTokenInCharset(token, ALPHANUM_CHARSET);
		token = generator.generateInsecureToken(1);
		assertNotNull(token);
		assertTrue(token.length() == 1);
		assertTokenInCharset(token, ALPHANUM_CHARSET);
		token = generator.generateInsecureToken(1024);
		assertNotNull(token);
		assertTrue(token.length() == 1024);
		assertTokenInCharset(token, ALPHANUM_CHARSET);
	}
	
	/**
	 * Test {@link Generator.generateSecureToken}
	 */
	@Test
	public void test_secure_generation() {
		String token;
		TokenGenerator generator = new TokenGenerator(LOWERCASE_CHARSET);
		assertNotNull(generator);
		token = generator.generateSecureToken(10);
		assertNotNull(token);
		assertTrue(token.length() == 10);
		assertTokenInCharset(token, LOWERCASE_CHARSET);
		token = generator.generateSecureToken(1);
		assertNotNull(token);
		assertTrue(token.length() == 1);
		assertTokenInCharset(token, LOWERCASE_CHARSET);
		token = generator.generateSecureToken(1024);
		assertNotNull(token);
		assertTrue(token.length() == 1024);
		assertTokenInCharset(token, LOWERCASE_CHARSET);
	
		generator = new TokenGenerator(ALPHANUM_CHARSET);
		token = generator.generateSecureToken(10);
		assertNotNull(token);
		assertTrue(token.length() == 10);
		assertTokenInCharset(token, ALPHANUM_CHARSET);
		token = generator.generateSecureToken(1);
		assertNotNull(token);
		assertTrue(token.length() == 1);
		assertTokenInCharset(token, ALPHANUM_CHARSET);
		token = generator.generateSecureToken(1024);
		assertNotNull(token);
		assertTrue(token.length() == 1024);
		assertTokenInCharset(token, ALPHANUM_CHARSET);
	}
}
