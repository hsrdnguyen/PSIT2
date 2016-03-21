package ch.avocado.share.model.data;

import ch.avocado.share.common.CharsetTokenGenerator;
import ch.avocado.share.common.TokenGenerator;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by coffeemakr on 21.03.16.
 */
public abstract class BaseMailVerification {
    private final static int DEFAULT_EXPIRY_IN_HOURS = 24;
    private final static int TOKEN_LENGTH = 32;
    private final static byte[] TOKEN_CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".getBytes();
    private String code;
    private Date expiry;


    public BaseMailVerification(Date expiry) {
        setExpiry(expiry);
        generateCode();
    }

    public BaseMailVerification(Date expiry, String code) {
        setCode(code);
        setExpiry(expiry);
    }

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

    public boolean isExpired() {
        // TODO: implement
        return false;
    }
}
