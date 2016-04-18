package ch.avocado.share.model.data;

import java.util.Date;

/**
 * A verification for forgotten passwords
 */
public class PasswordResetVerification extends BaseMailVerification {

    public PasswordResetVerification(Date expiry) {
        super(expiry);
    }

    public PasswordResetVerification(Date expiry, String code) {
        super(expiry, code);
    }
}
