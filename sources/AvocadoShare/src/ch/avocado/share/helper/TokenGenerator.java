package ch.avocado.share.helper;

/**
 * Generator for random tokens.
 * @author muellcy1
 */
public interface TokenGenerator {
	/**
	 * Generate a random token. Whether this method uses a 
	 * secure random generator or the default insecure one
	 * depends on the implementation.
	 *  
	 * @param length
	 *            The length of the token.
	 * @return The generated token.
	 */
	public byte[] generateToken(int length);
}