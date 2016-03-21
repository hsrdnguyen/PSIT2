package ch.avocado.share.model.data;

import ch.avocado.share.common.Base64;
import ch.avocado.share.common.BinaryTokenGenerator;
import ch.avocado.share.common.TokenGenerator;

import java.io.Serializable;

import org.bouncycastle.crypto.generators.*;

/**
 * Class to handle user passwords.
 * It uses a cryptographical strong algorithm to hash the passwords.
 * 
 * For existing hashes you can simply use the default constructor. 
 * If you want to create a hash from a new password you have to 
 * use the @{link UserPassword.fromPassword} method.
 * 
 * @author muellcy1
 */
public class UserPassword implements Serializable{
	private static final long serialVersionUID = 3245810310373510720L;

	private static final int SALT_LENGTH = 6;
	/**
	 * CPU/Memory cost.
	 * value: 2^20
	 */
	private static final int SCRYPT_CPU_MEMORY_COST = 32768;
	/**
	 * Block size
	 */
	private static final int SCRYPT_BLOCK_SIZE = 8;
	/**
	 * 
	 */
	private static final int SCRYPT_PARALLELIZING_FACTOR = 1;
	private static final int SCRYPT_KEY_LENGTH = 32;

	private String digest;
    private PasswordResetVerification passwordResetVerification;

	/**
	 * Private constructor for fromPassword.
	 */
	private UserPassword() {
		this.digest = null;
	}

	/**
	 * Create UserPassword from the digest string.
	 * 
	 * @param digest The hashed password digest.
	 */
	public UserPassword(String digest, PasswordResetVerification passwordResetVerification) {
		setDigest(digest);
        setPasswordResetVerification(passwordResetVerification);
	}

	/**
	 * Create UserPassword from password.
	 * 
	 * @param password
	 * @return
	 */
	static public UserPassword fromPassword(String password) {
		if(password == null) {
			throw new IllegalArgumentException("password can't be null");
		}
		UserPassword passwordObject = new UserPassword();
		passwordObject.setPassword(password);
		return passwordObject;
	}

	/**
	 * Generate a password salt.
	 */
	private static byte[] generateSalt() {
		TokenGenerator generator = new BinaryTokenGenerator();
		return generator.generateToken(SALT_LENGTH);
	}

	/**
	 * Create a hash digest of a password combined with the salt.
	 * 
	 * @param password
	 * @param salt
	 * @return
	 */
	private static String generateDigest(String password, byte[] salt) {
		if(password == null){
			throw new IllegalArgumentException("password can't be null");
		}
		if(salt == null) {
			throw new IllegalArgumentException("salt can't be null");
		}
		byte[] key = SCrypt.generate(password.getBytes(), salt, SCRYPT_CPU_MEMORY_COST, SCRYPT_BLOCK_SIZE,
				SCRYPT_PARALLELIZING_FACTOR, SCRYPT_KEY_LENGTH);
		return Base64.encode(salt) + ":" + Base64.encode(key);
	}

	/**
	 * @return The digest
	 */
	public String getDigest() {
		return this.digest;
	}
	
	/**
	 * Sets the digest
	 * @param digest The digest
	 */
	public void setDigest(String digest) {
		if(digest == null) {
			throw new IllegalArgumentException("digest can't be null");
		}
		this.digest = digest;
	}

	/**
	 * Sets the password and updates the digest accordingly.
	 * @param password The password
	 */
	public void setPassword(String password) {
		byte[] salt;
		if (password == null) {
			throw new IllegalArgumentException("password can't be null");
		}
		salt = generateSalt();
		this.digest = generateDigest(password, salt);
	}
	/**
	 * Extracts the salt from the digest and returns it.
	 * @return The salt
	 */
	private byte[] getSalt() {
		if (this.digest.contains(":")) {
			return Base64.decode(this.digest.split(":")[0]);
		}
		return null;
	}

	/**
	 * Check if a password matches.
	 * @param password
	 * @return True if the password match, False otherwise.
	 */
	public boolean matchesPassword(String password) {
		byte[] salt = getSalt();
		if (digest == null || salt == null) {
			return false;
		}
		String generatedDigest = generateDigest(password, salt);
		return generatedDigest.equals(digest);
	}


    public PasswordResetVerification getPasswordResetVerification() {
        return passwordResetVerification;
    }

    public void setPasswordResetVerification(PasswordResetVerification passwordResetVerification) {
        if(passwordResetVerification == null) throw new IllegalArgumentException("passwordResetVerification is null");
        this.passwordResetVerification = passwordResetVerification;
    }
}
