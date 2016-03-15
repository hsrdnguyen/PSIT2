package ch.avocado.share.model.data;

/**
 * Created by bergm on 15/03/2016.
 */
public class EmailAddress extends AccessControlObjectBase{

    private boolean verified;
    private String address;
    private EmailAddressVerification verification;

    public EmailAddress(boolean verified, String address, EmailAddressVerification verification) {

        setVerified(verified);
        setAddress(address);
        setVerification(verification);
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null) throw new IllegalArgumentException("address is null");
        this.address = address;
    }

    public EmailAddressVerification getVerification() {
        return verification;
    }

    public void setVerification(EmailAddressVerification verification) {
        if (verification == null) throw new IllegalArgumentException("verification is null");

        this.verification = verification;
    }
}
