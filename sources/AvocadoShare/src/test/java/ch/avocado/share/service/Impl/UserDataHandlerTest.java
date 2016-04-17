package ch.avocado.share.service.Impl;

import ch.avocado.share.model.data.EmailAddress;
import ch.avocado.share.model.data.EmailAddressVerification;
import ch.avocado.share.model.data.User;
import ch.avocado.share.service.Impl.UserDataHandler;
import ch.avocado.share.model.data.UserPassword;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by bergm on 23/03/2016.
 */
public class UserDataHandlerTest {
    private UserDataHandler service;

    @Before
    public void init() throws Exception {
        service = new UserDataHandler();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserWithNull() throws Exception {
        service.addUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByEmailWithNull() throws Exception {
        service.getUserByEmailAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserWithNull() throws Exception {
        service.getUser(null);
    }

    public static User getTestUser() throws Exception {
        String string_date = "12-December-2030";
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        Date d = f.parse(string_date);
        EmailAddressVerification verification = new EmailAddressVerification(d, "123456789");
        EmailAddress emailAddress = new EmailAddress(false, "test@test.com", verification);
        UserPassword pwd = UserPassword.fromPassword("123456789");
        User user = new User(null, null, new Date(), 0.0f, "", pwd, "Max", "Muster", "123456789.jpg", emailAddress);
        assertNotNull(user.getDescription());
        return user;
    }

    @Test
    public void testAddUser() throws Exception {
        User user = getTestUser();
        String id = service.addUser(user);
        assertNotNull(id);

        User addedUser = service.getUser(id);

        assertTrue(addedUser != user);
        assertEquals(id, addedUser.getId());
        assertTrue(addedUser.getCreationDate().getTime() > 0);
        assertEquals(user.getPrename(), addedUser.getPrename());
        assertEquals(user.getSurname(), addedUser.getSurname());
        assertEquals(user.getDescription(), addedUser.getDescription());
        assertEquals(user.getPassword().getDigest(), addedUser.getPassword().getDigest());
        assertEquals(user.getAvatar(), addedUser.getAvatar());

        assertTrue("Failed to delete user", service.deleteUser(addedUser));
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = getTestUser();
        assertNull(service.getUserByEmailAddress(user.getMail().getAddress()));
        String id = service.addUser(user);
        assertNotNull(id);
        assertNotNull(service.getUserByEmailAddress(user.getMail().getAddress()));
        assertNotNull(user = service.getUser(id));
        assertTrue(service.deleteUser(user));
        assertNull(service.getUser(user.getId()));
        assertNull(service.getUserByEmailAddress(user.getMail().getAddress()));
    }

}
