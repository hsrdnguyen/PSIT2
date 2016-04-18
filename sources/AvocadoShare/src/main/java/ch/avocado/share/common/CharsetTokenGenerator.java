package ch.avocado.share.common;

import java.security.SecureRandom;

/**
 * Implements a TokenGenerator which uses characters from a given
 * character set (a list of characters) to generate the value.
 * {@link CharsetTokenGenerator} uses {@link SecureRandom} to generate
 * the values.
 *  @author muellcy1
 */
public class CharsetTokenGenerator implements TokenGenerator {

	private final byte[] charset;
	
	/**
	 * Create a new CharsetTokenGenerator
	 * @param charset
	 */
	public CharsetTokenGenerator(byte[] charset) {
		if (charset == null) {
			throw new IllegalArgumentException("charset can't be null");
		}
		this.charset = charset;
	}
	
	/* (non-Javadoc)
	 * @see ch.avocado.share.helper.TokenGenerator#generateToken(int)
	 */
	@Override
	public byte[] generateToken(int length) {
		SecureRandom random = new SecureRandom();
		if (length <= 0) {
			throw new IllegalArgumentException("length has to be greater than zero");
		}
		byte[] token = new byte[length];
		for (int i = 0; i < length; i++) {
			int charsetPosition = random.nextInt(charset.length - 1);
			token[i] = charset[charsetPosition];
		}
		return token;
	}
}