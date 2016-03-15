package ch.avocado.share.model.data;

import java.util.Date;

/**
 * Created by bergm on 15/03/2016.
 */
public class EmailAddressVerification {
    private String code;
    private Date expiry;

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
