package ch.avocado.share.model.data;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author muellcy1
 */
public class UserPasswordTest {
    static final String PASSWORD_1 = "ALong&Str0ngP4ssWörd";
    static final String PASSWORD_1_DIGEST = "9sRF9PfH:jgnjoiBIAJLk7XmtaVuS2JQKlI6TiSYFTqUbo4LIqIc=";


    @Test
    public void test_digest_generation() {
        UserPassword pass = UserPassword.fromPassword(PASSWORD_1);
        assertTrue(pass.matchesPassword(PASSWORD_1));
    }

    /**
     * Test that a null in constructor is handled.
     */
    @Test(expected = NullPointerException.class)
    public void test_from_digest_with_null() {
        new UserPassword(null);
    }

    /**
     * Test that a null in fromPassword is handled.
     */
    @Test(expected = NullPointerException.class)
    public void test_from_password_with_null() {
        UserPassword.fromPassword(null);
    }

    @Test
    public void test_from_digest() {
        UserPassword pass = new UserPassword(PASSWORD_1_DIGEST);
        assertNotNull(pass.getDigest());
        assertEquals(pass.getDigest(), PASSWORD_1_DIGEST);
        assertTrue(pass.matchesPassword(PASSWORD_1));
    }

    /**
     * Make sure we don't break older password hashes.
     */
    @Test
    public void test_migration() {
        UserPassword pass = new UserPassword(PASSWORD_1_DIGEST);
        assertTrue(pass.matchesPassword(PASSWORD_1));
    }

    @Test
    public void test_digest_different_for_same_password() {
        UserPassword pass1 = UserPassword.fromPassword(PASSWORD_1);
        UserPassword pass2 = UserPassword.fromPassword(PASSWORD_1);
        assertTrue(pass1.matchesPassword(PASSWORD_1));
        assertTrue(pass2.matchesPassword(PASSWORD_1));
        assertNotEquals(pass1.getDigest(), pass2.getDigest());
    }

    @Test
    public void test_digest_different_when_updated() {
        UserPassword pass = UserPassword.fromPassword(PASSWORD_1);
        assertTrue(pass.matchesPassword(PASSWORD_1));
        String digestBeforeChange = pass.getDigest();
        pass.setPassword(PASSWORD_1);
        assertNotEquals(digestBeforeChange, pass.getDigest());
        assertTrue(pass.matchesPassword(PASSWORD_1));
    }
}
