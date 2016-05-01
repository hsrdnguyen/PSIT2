package ch.avocado.share.model.data;

import java.util.Objects;

/**
 * email address model.
 */
public class EmailAddress extends Model {

    private boolean verified;
    private final String address;
    private MailVerification verification;

    public EmailAddress(boolean verified, String address, MailVerification verification) {
        if(address == null) throw new IllegalArgumentException("address is null");
        address = address.trim();
        if(address.isEmpty()) throw new IllegalArgumentException("address is empty");
        this.verified = verified;
        this.address = address;
        this.verification = verification;
    }

    /**
     * @return {@code true} if the email is valid and has been verified.
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * @param verified
     */
    public void setVerified(boolean verified) {
        if(this.verified != verified) {
            setDirty(true);
        }
        this.verified = verified;
    }

    /**
     * @return The actual address
     */
    public String getAddress() {
        return address;
    }


    public MailVerification getVerification() {
        return verification;
    }

    public void setVerification(MailVerification verification) {
        if(!Objects.equals(this.verification, verification)) {
            setDirty(true);
        }
        this.verification = verification;
    }

    @Override
    public String toString() {
        return getAddress();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailAddress that = (EmailAddress) o;

        if (verified != that.verified) return false;
        if (!address.equals(that.address)) return false;
        return verification != null ? verification.equals(that.verification) : that.verification == null;
    }

    @Override
    public int hashCode() {
        int result = (verified ? 1 : 0);
        result = 31 * result + address.hashCode();
        result = 31 * result + (verification != null ? verification.hashCode() : 0);
        return result;
    }

    /**
     * Mark the email as verified.
     */
    public void verify() {
        setVerification(null);
        setVerified(true);
    }
}
