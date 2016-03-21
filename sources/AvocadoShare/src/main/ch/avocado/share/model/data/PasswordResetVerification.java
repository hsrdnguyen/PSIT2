package ch.avocado.share.model.data;

import java.util.Date;

/**
 * Created by muellcy1 on 21.03.16.
 */
public class PasswordResetVerification extends BaseMailVerification {

    public PasswordResetVerification(Date expiry) {
        super(expiry);
    }

    public PasswordResetVerification(Date expiry, String code) {
        super(expiry, code);
    }
}
