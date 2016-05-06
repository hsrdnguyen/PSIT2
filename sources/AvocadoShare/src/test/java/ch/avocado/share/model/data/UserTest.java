package ch.avocado.share.model.data;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 06.05.16.
 */
public class UserTest {

    private User user;
    private String id;
    private String prename;
    private String surname;
    private UserPassword password;
    private String avatar;
    private String emailAddress;
    private EmailAddress email;
    private MailVerification emailVerification;


    @Before
    public void setUp() throws Exception {
        id = "1234";
        prename = "Felix";
        surname = "Muster";
        password = UserPassword.fromPassword("1234");
        avatar = "12345";
        emailAddress = "1234@gmail.com";
        emailVerification = new MailVerification(new Date());
        email = new EmailAddress(true, emailAddress, emailVerification);
        user = new User(password, prename, surname, avatar, email);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsPassword() {
        new User(null, prename, surname, avatar, email);
    }
    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsPrename() {
        new User(password, null, surname, avatar, email);
    }
    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsSurname() {
        new User(password, prename, null, avatar, email);
    }
    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsAvatar() {
        new User(password, prename, surname, null, email);
    }
    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullAsEmail() {
        new User(password, prename, surname, avatar, null);
    }

    @Test
    public void testGetPassword() throws Exception {
        assertEquals(password.getDigest(), user.getPassword().getDigest());
        assertTrue(password.matchesPassword("1234"));
    }

    @Test(expected = NullPointerException.class)
    public void testSetPasswordToNull() {
        user.setPassword(null);
    }

    @Test
    public void testSetPassword()  {
        String newPassword = "4812903";
        user.setPassword(newPassword);
        assertTrue(user.getPassword().matchesPassword(newPassword));
        assertNotSame(password, user.getPassword());
        assertNotEquals(password.getDigest(), user.getPassword().getDigest());
    }

    @Test
    public void testGetPrename() throws Exception {
        assertEquals(prename, user.getPrename());
    }

    @Test(expected = NullPointerException.class)
    public void testSetPrenameToNull() throws Exception {
        user.setPrename(null);
    }

    @Test
    public void testSetPrename() throws Exception {
        String newPrename = "dsjkaoi";
        assertNotEquals(newPrename, prename);
        user.setPrename(newPrename);
        assertEquals(newPrename, user.getPrename());
    }

    @Test
    public void testGetSurname() throws Exception {
        assertEquals(surname, user.getSurname());

    }

    @Test(expected = NullPointerException.class)
    public void testSetSurnameToNull() throws Exception {
        user.setSurname(null);
    }

    @Test
    public void testSetSurname() throws Exception {
        String newSurname = "Surnadasidd";
        assertNotEquals(newSurname, surname);
        user.setSurname(newSurname);
        assertEquals(newSurname, user.getSurname());
    }

    @Test(expected = NullPointerException.class)
    public void testSetAvatarToNull() throws Exception {
        user.setAvatar(null);
    }

    @Test
    public void testGetAvatar() throws Exception {
        assertEquals(avatar, user.getAvatar());
    }

    @Test
    public void testSetAvatar() throws Exception {
        String newAvatar = "321321312";
        user.setAvatar(newAvatar);
        assertEquals(newAvatar, user.getAvatar());
    }

    @Test
    public void testGetMail() throws Exception {
        assertEquals(email, user.getMail());
    }

    @Test(expected = NullPointerException.class)
    public void testSetMailToNull() throws Exception {
        user.setMail(null);
    }

    @Test
    public void testSetMail() throws Exception {
        EmailAddress newEmail = new EmailAddress(true, "1234123@ds.com", null);
        assertNotEquals(email, newEmail);
        user.setMail(newEmail);
        assertEquals(newEmail, user.getMail());
    }

    @Test
    public void testGetFullName() throws Exception {
        assertEquals(prename + " "  + surname, user.getFullName());
    }


    @Test(expected = NullPointerException.class)
    public void testResetPasswordWithPasswordNull() throws Exception {
        user.resetPassword(null, "12321321");
    }

    @Test(expected = NullPointerException.class)
    public void testResetPasswordWithCodeNull() throws Exception {
        user.resetPassword("213321321", null);
    }

    @Test
    public void testResetPassword() throws Exception {
        String newPassword = "12312421";

        // no code
        assertNull(user.getPassword().getResetVerification());
        assertFalse(user.resetPassword(newPassword, "1234"));

        // expired code
        MailVerification resetVerification = new MailVerification(new Date(10));
        assertTrue(resetVerification.isExpired());
        user.getPassword().setResetVerification(resetVerification);
        assertFalse(user.resetPassword(newPassword, resetVerification.getCode()));
        assertFalse(user.getPassword().matchesPassword(newPassword));

        // Wrong code
        resetVerification = new MailVerification(new Date(System.currentTimeMillis() + 5000));
        assertFalse(resetVerification.isExpired());
        user.getPassword().setResetVerification(resetVerification);
        assertFalse(user.resetPassword(newPassword, "1234"));
        assertFalse(user.getPassword().matchesPassword(newPassword));

        // Correct password & code
        resetVerification = new MailVerification(new Date(System.currentTimeMillis() + 5000));
        assertFalse(resetVerification.isExpired());
        user.getPassword().setResetVerification(resetVerification);
        assertTrue(user.resetPassword(newPassword, resetVerification.getCode()));
        assertTrue(user.getPassword().matchesPassword(newPassword));
        assertNull(user.getPassword().getResetVerification());
    }

    @Test
    public void testGetReadableName() throws Exception {
        assertEquals(user.getFullName(), user.getReadableName());
    }
}