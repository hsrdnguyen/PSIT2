package ch.avocado.share.model.data;

import ch.avocado.share.common.CharsetTokenGenerator;
import ch.avocado.share.common.TokenGenerator;

import java.util.Date;

/**
 * Base class for all mail-based verifcations.
 */
public abstract class BaseMailVerification {
    private final static int DEFAULT_EXPIRY_IN_HOURS = 24;
    private final static int TOKEN_LENGTH = 32;
    private final static byte[] TOKEN_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".getBytes();
    private String code;
    private Date expiry;


    public BaseMailVerification(Date expiry) {
        setExpiry(expiry);
        code = generateCode();
    }

    public BaseMailVerification(Date expiry, String code) {
        setCode(code);
        setExpiry(expiry);
    }

    /**
     * Convert a expiry time (delta) into an actual date.
     * @param expiresInHours The number of hours
     * @return The date on which the hours will expire
     */
    public static Date getDateFromExpiryInHours(int expiresInHours) {
        long expiryMillis = System.currentTimeMillis() % 1000;
        expiryMillis += expiresInHours * (60 * 60 * 1000);
        return new Date(expiryMillis);
    }

    /**
     * Generates a verification code.
     * @return The verification code.
     */
    protected static String generateCode(){
    	TokenGenerator generator = new CharsetTokenGenerator(TOKEN_CHARSET);
    	return new String(generator.generateToken(TOKEN_LENGTH));
    }

    /**
     * @return The verification code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The verification code
     */
    public void setCode(String code) {
        if (code == null) throw new IllegalArgumentException("code is null");

        this.code = code;
    }

    /**
     * @return The expiry
     */
    public Date getExpiry() {
        return expiry;
    }

    /**
     * @param expiry The expiry
     */
    public void setExpiry(Date expiry) {
        if (expiry == null) throw new IllegalArgumentException("expiry is null");
        this.expiry = expiry;
    }

    /**
     * @return {@code true} if this verification is expired.
     */
    public boolean isExpired() {
        Date now = new Date();
        return expiry.compareTo(now) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseMailVerification that = (BaseMailVerification) o;

        if (!code.equals(that.code)) return false;
        return expiry.equals(that.expiry);

    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + expiry.hashCode();
        return result;
    }
}
