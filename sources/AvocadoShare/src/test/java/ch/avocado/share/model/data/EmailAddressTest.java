package ch.avocado.share.model.data;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 01.05.16.
 */
public class EmailAddressTest {

    String address = "somethine@nothins.nowhere";


    @Test
    public void testSetAndIsVerified() throws Exception {
        EmailAddress emailAddress = new EmailAddress(false, "something@nothing.nowhere", null);
        assertFalse(emailAddress.isVerified());
        emailAddress.setVerified(true);
        assertTrue(emailAddress.isVerified());

        emailAddress = new EmailAddress(true, "something@nothing.nowhere", null);
        assertTrue(emailAddress.isVerified());
        emailAddress.setVerified(false);
        assertFalse(emailAddress.isVerified());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailsWithNullAsAddress() {
        new EmailAddress(false, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorFailsWithEmptyAddress() {
        new EmailAddress(false, "", null);
    }

    @Test
    public void testGetAddress() throws Exception {
        String address = "somethine@nothins.nowhere";
        EmailAddress emailAddress = new EmailAddress(false, address, null);
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
        assertNotSame(verification, emailAddress.getVerification());


        emailAddress = new EmailAddress(true, address, verification);
        assertEquals(verification, emailAddress.getVerification());

    }

    @Test
    public void testSetVerification() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }

    @Test
    public void testEquals() throws Exception {

    }

    @Test
    public void testHashCode() throws Exception {

    }

    @Test
    public void testVerify() throws Exception {

    }
}