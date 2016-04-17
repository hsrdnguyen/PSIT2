/**
 * 
 */
package ch.avocado.share.common;

import java.security.SecureRandom;

/**
 * A binary token generator generates random bytes (0-255) and
 * puts them together to a token.
 *
 * {@link BinaryTokenGenerator} uses {@link SecureRandom} to generate
 * the values.
 * @author muellcy1
 */
public class BinaryTokenGenerator implements TokenGenerator{

	@Override
	public byte[] generateToken(int length) {
		SecureRandom random = new SecureRandom();
		if(length <= 0) {
			throw new IllegalArgumentException("length has to be greater than 0");
		}
		byte[] token = new byte[length];
		random.nextBytes(token);
		return token;
	}
}
