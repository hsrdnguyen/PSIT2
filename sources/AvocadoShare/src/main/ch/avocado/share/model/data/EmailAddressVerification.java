package ch.avocado.share.model.data;
import java.util.Date;

import ch.avocado.share.common.CharsetTokenGenerator;
import ch.avocado.share.common.TokenGenerator;

/**
 * Created by bergm on 15/03/2016.
 */
public class EmailAddressVerification {
    
	private final static int DEFAULT_EXPIRY_IN_HOURS = 24;
	private final static int TOKEN_LENGTH = 32;
	private final static byte[] TOKEN_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".getBytes();
	private String code;
    private Date expiry;

    /**
     * Generates a verification code.
     * @return The verification code.
     */
    public static String generateCode(){
    	TokenGenerator generator = new CharsetTokenGenerator(TOKEN_CHARSET);
    	return new String(generator.generateToken(TOKEN_LENGTH));
    }
    
    public EmailAddressVerification(Date expiry, String code) {
        setExpiry(expiry);
        setCode(code);
    }

    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        if (code == null) throw new IllegalArgumentException("code is null");

        this.code = code;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        if (expiry == null) throw new IllegalArgumentException("expiry is null");
        this.expiry = expiry;
    }
}
