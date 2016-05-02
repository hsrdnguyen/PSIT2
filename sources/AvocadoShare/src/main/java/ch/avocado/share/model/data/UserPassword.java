package ch.avocado.share.model.data;

import ch.avocado.share.common.Base64;
import ch.avocado.share.common.BinaryTokenGenerator;
import ch.avocado.share.common.TokenGenerator;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.math.raw.Mod;

import java.io.Serializable;

/**
 * Class to handle user passwords.
 * It uses a strong cryptographic algorithm to hash the passwords.
 * 
 * For existing hashes you can simply use the default constructor. 
 * If you want to create a hash from a new password you have to 
 * use the @{link UserPassword.fromPassword} method.
 * 
 * @author muellcy1
 */
public class UserPassword extends Model implements Serializable {

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
    private MailVerification resetVerification;

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
	public UserPassword(String digest) {
		setDigest(digest);
        this.resetVerification = null;
	}

	
    public UserPassword(String digest, MailVerification resetVerification) {
        setDigest(digest);
        setResetVerification(resetVerification);
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
		if(digest == null) throw new IllegalArgumentException("digest can't be null");
		if(!digest.contains(":")) throw new IllegalArgumentException("invalid digest");
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
		assert digest != null;
		if (digest.contains(":")) {
			return Base64.decode(digest.split(":")[0]);
		} else {
			throw new RuntimeException("invalid digest");
		}
	}

	/**
	 * Check if a password matches.
	 * @param password
	 * @return True if the password match, False otherwise.
	 */
	public boolean matchesPassword(String password) {
		assert digest != null;
		byte[] salt = getSalt();
		String generatedDigest = generateDigest(password, salt);
		return generatedDigest.equals(digest);
	}


    public MailVerification getResetVerification() {
        return resetVerification;
    }

    public void setResetVerification(MailVerification resetVerification) {
        this.resetVerification = resetVerification;
    }
}
