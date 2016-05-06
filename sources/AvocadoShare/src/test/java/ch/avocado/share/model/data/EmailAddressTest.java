package ch.avocado.share.model.data;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Tests for {@link EmailAddress}
 */
public class EmailAddressTest {

    String address = "something@nothins.nowhere";


    @Test
    public void testSetAndIsVerified() throws Exception {
        EmailAddress emailAddress = new EmailAddress(false, address, null);
        assertFalse(emailAddress.isVerified());
        assertFalse(emailAddress.isDirty());
        emailAddress.setVerified(true);
        assertTrue(emailAddress.isDirty());
        assertTrue(emailAddress.isVerified());

        emailAddress = new EmailAddress(true, address, null);
        assertTrue(emailAddress.isVerified());
        assertFalse(emailAddress.isDirty());
        emailAddress.setVerified(false);
        assertTrue(emailAddress.isDirty());
        assertFalse(emailAddress.isVerified());

    }

    @Test(expected = NullPointerException.class)
    public void testConstructorFailsWithNullAsAddress() {
        new EmailAddress(false, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailsWithEmptyAddress() {
        new EmailAddress(false, "", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailsWithOnlyWhitespacesAddress() {
        new EmailAddress(false, "\n\t\r ", null);
    }

    @Test
    public void testGetAddress() throws Exception {
        EmailAddress emailAddress = new EmailAddress(false, address, null);
        assertFalse(emailAddress.isDirty());
        assertEquals(address, emailAddress.getAddress());
    }

    @Test
    public void testGetVerification() throws Exception {
        EmailAddress emailAddress = new EmailAddress(false, address, null);
        assertNull(emailAddress.getVerification());

        final Date expiry = new Date();
        MailVerification verification = new MailVerification(expiry);
        emailAddress.setVerification(verification);
        assertEquals(verification, emailAddress.getVerification());

        emailAddress = new EmailAddress(true, address, verification);
        assertEquals(verification, emailAddress.getVerification());
    }

    @Test
    public void testSetVerification() throws Exception {
        EmailAddress emailAddress = new EmailAddress(false, address, null);

        final Date expiry = new Date();
        MailVerification verification = new MailVerification(expiry);
        assertFalse(emailAddress.isDirty());
        emailAddress.setVerification(verification);
        assertTrue(emailAddress.isDirty());
        assertEquals(verification, emailAddress.getVerification());

        emailAddress = new EmailAddress(true, address, verification);
        assertEquals(verification, emailAddress.getVerification());

    }

    @Test
    public void testToString() throws Exception {
        EmailAddress emailAddress = new EmailAddress(false, address, null);
        assertEquals(address, emailAddress.toString());
    }

    @Test
    public void testEquals() throws Exception {
        EmailAddress emailAddress = new EmailAddress(false, address, null);
        EmailAddress emailAddressTwo = new EmailAddress(false, address, null);

        assertFalse(emailAddress.equals(null));
        assertTrue(emailAddress.equals(emailAddress));
        assertTrue(emailAddress.equals(emailAddressTwo));
        assertEquals(emailAddress.hashCode(), emailAddressTwo.hashCode());


        emailAddressTwo = new EmailAddress(false, address + "other", null);
        assertFalse(emailAddress.equals(emailAddressTwo));
        assertNotEquals(emailAddress.hashCode(), emailAddressTwo.hashCode());
        emailAddressTwo = new EmailAddress(true, address, null);
        assertFalse(emailAddress.equals(emailAddressTwo));
        assertNotEquals(emailAddress.hashCode(), emailAddressTwo.hashCode());

        emailAddress = new EmailAddress(false, address, new MailVerification(new Date(1), "1234"));
        emailAddressTwo = new EmailAddress(false, address, new MailVerification(new Date(1), "1234"));
        assertTrue(emailAddress.equals(emailAddressTwo));
        assertEquals(emailAddress.hashCode(), emailAddressTwo.hashCode());


        emailAddressTwo = new EmailAddress(false, address, new MailVerification(new Date(0), "1234"));
        assertFalse(emailAddress.equals(emailAddressTwo));
        assertNotEquals(emailAddress.hashCode(), emailAddressTwo.hashCode());

        emailAddressTwo = new EmailAddress(false, address, new MailVerification(new Date(1), "12345"));
        assertFalse(emailAddress.equals(emailAddressTwo));
        assertNotEquals(emailAddress.hashCode(), emailAddressTwo.hashCode());
    }


    @Test
    public void testVerify() throws Exception {
        EmailAddress emailAddress = new EmailAddress(false, address, new MailVerification(new Date()));
        assertNotNull(emailAddress.getVerification());
        assertFalse(emailAddress.isDirty());
        assertFalse(emailAddress.isVerified());
        emailAddress.verify();
        assertTrue(emailAddress.isDirty());
        assertTrue(emailAddress.isVerified());
        assertNull(emailAddress.getVerification());

        emailAddress = new EmailAddress(true, address, null);
        assertFalse(emailAddress.isDirty());
        assertNull(emailAddress.getVerification());
        assertTrue(emailAddress.isVerified());

        emailAddress.verify();
        assertFalse(emailAddress.isDirty());
        assertNull(emailAddress.getVerification());
        assertTrue(emailAddress.isVerified());

    }
}