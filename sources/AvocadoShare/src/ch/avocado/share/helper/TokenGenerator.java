package ch.avocado.share.helper;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Generator for random tokens.
 */
public class TokenGenerator {
	private final char[] charset;
	
	/**
	 * Create a new TokenGenerator using a defined charset.
	 * @param charset A list of characters to use in the token.
	 */
	public TokenGenerator(char[] charset) {
		if(charset == null) {
			throw new IllegalArgumentException("charset can't be null");
		}
		this.charset = charset;		
	}
	
	/**
	 * Generate token using the random object.
	 * @param length The length of the token
	 * @param random The random object
	 * @return The generated Token.
	 */
	private String generateTokenFromRandom(int length, Random random) {
		if (length <= 0) {
			throw new IllegalArgumentException("length has to be greater than zero");
		}
		if (random == null) {
			throw new IllegalArgumentException("random can't be null");
		}
    	char[] token = new char[length];
    	for(int i = 0; i < length; i++) {
    		int charsetPosition = random.nextInt(charset.length - 1);
    		token[i] = charset[charsetPosition];
    	}
    	return new String(token);
	}

	/**
	 * Generate a random token using SecureRandom.
	 * @param length The length of the token.
	 * @return The generated token.
	 */
	public String generateSecureToken(int length) {
		return generateTokenFromRandom(length, new SecureRandom());	
	}
	
	/**
	 * Generate a random token using (insecure) Random.
	 * @param length The length of the token.
	 * @return The generated token.
	 */
	public String generateInsecureToken(int length) {
		return generateTokenFromRandom(length, new Random());
	}
}
