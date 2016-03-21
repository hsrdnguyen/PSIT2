package ch.avocado.share.model.data;
import java.util.Date;

/**
 * Created by bergm on 15/03/2016.
 */
public class EmailAddressVerification extends BaseMailVerification{

    public EmailAddressVerification(Date expiry) {
        super(expiry);
    }

    /**
     * Constructor for EmailAddressVerification.
     * @param expiry Expiry date of the verification
     * @param code Verification code
     */
    public EmailAddressVerification(Date expiry, String code) {
        super(expiry, code);
    }
}
