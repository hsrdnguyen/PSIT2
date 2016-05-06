package ch.avocado.share.model.data;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author muellcy1
 */
public class UserPasswordTest {
    static final String PASSWORD_1 = "ALong&Str0ngP4ssWörd";
    static final String PASSWORD_1_DIGEST = "9sRF9PfH:jgnjoiBIAJLk7XmtaVuS2JQKlI6TiSYFTqUbo4LIqIc=";


    @Test
    public void testDigestGeneration() {
        UserPassword pass = UserPassword.fromPassword(PASSWORD_1);
        assertTrue(pass.matchesPassword(PASSWORD_1));
    }

    @Test
    public void testFromDigestWithResetCode() {
        Date expiry = new Date(123456);
        MailVerification verification = new MailVerification(expiry);
        UserPassword password = new UserPassword(PASSWORD_1_DIGEST, verification);
        assertEquals(verification, password.getResetVerification());
    }

    /**
     * Test that a null in constructor is handled.
     */
    @Test(expected = NullPointerException.class)
    public void testFromDigestWithNull() {
        new UserPassword(null);
    }

    /**
     * Test that a null in fromPassword is handled.
     */
    @Test(expected = NullPointerException.class)
    public void testFromPasswordWithNull() {
        UserPassword.fromPassword(null);
    }

    @Test
    public void testFromDigest() {
        UserPassword pass = new UserPassword(PASSWORD_1_DIGEST);
        assertNotNull(pass.getDigest());
        assertEquals(pass.getDigest(), PASSWORD_1_DIGEST);
        assertTrue(pass.matchesPassword(PASSWORD_1));
        assertNull(pass.getResetVerification());
    }

    /**
     * Make sure we don't break older password hashes.
     */
    @Test
    public void testMigration() {
        UserPassword pass = new UserPassword(PASSWORD_1_DIGEST);
        assertTrue(pass.matchesPassword(PASSWORD_1));
    }

    @Test
    public void testDigestDifferentForSamePassword() {
        UserPassword pass1 = UserPassword.fromPassword(PASSWORD_1);
        UserPassword pass2 = UserPassword.fromPassword(PASSWORD_1);
        assertTrue(pass1.matchesPassword(PASSWORD_1));
        assertTrue(pass2.matchesPassword(PASSWORD_1));
        assertNotEquals(pass1.getDigest(), pass2.getDigest());
    }

    @Test
    public void testDigestDifferentWhenUpdated() {
        UserPassword pass = UserPassword.fromPassword(PASSWORD_1);
        assertTrue(pass.matchesPassword(PASSWORD_1));
        String digestBeforeChange = pass.getDigest();
        pass.setPassword(PASSWORD_1);
        assertNotEquals(digestBeforeChange, pass.getDigest());
        assertTrue(pass.matchesPassword(PASSWORD_1));
    }
}
