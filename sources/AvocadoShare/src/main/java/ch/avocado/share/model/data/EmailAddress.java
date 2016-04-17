package ch.avocado.share.model.data;

/**
 * email address model.
 */
public class EmailAddress {

    private boolean isVerified;
    private String address;
    private EmailAddressVerification verification;

    public EmailAddress(boolean isVerified, String address, EmailAddressVerification verification) {
        setIsVerified(isVerified);
        setAddress(address);
        // verification can be null when created but not set.
        this.verification = verification;
    }

    /**
     * @return {@code true} if the email is valid and has been verified.
     */
    public boolean isVerified() {
        return isVerified;
    }

    /**
     * @param isVerified
     */
    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    /**
     * @return The actual address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address  The actual address
     */
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

    @Override
    public String toString() {
        return getAddress();
    }
}
