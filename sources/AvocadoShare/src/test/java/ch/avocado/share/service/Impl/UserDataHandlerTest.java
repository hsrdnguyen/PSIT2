package ch.avocado.share.service.Impl;

import ch.avocado.share.model.data.*;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;
import ch.avocado.share.test.Asserts;
import ch.avocado.share.test.DummyFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import static org.junit.Assert.*;


/**
 * Tests for the {@link UserDataHandler}.
 */
public class UserDataHandlerTest {
    private UserDataHandler userDataHandler;
    private Stack<String> notDeletedUserIds;


    @Before
    public void init() throws Exception {
        DatabaseConnectionHandlerMock.use();
        notDeletedUserIds = new Stack<>();
        userDataHandler = new UserDataHandler();
    }

    @After
    public void tearDown() throws Exception {
        try {
            while (!notDeletedUserIds.isEmpty()) {
                String id = notDeletedUserIds.pop();
                User user = userDataHandler.getUser(id);
                if (user != null) {
                    userDataHandler.deleteUser(user);
                }
            }
        } finally {
            ServiceLocatorModifier.restore();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddUserWithNull() throws Exception {
        userDataHandler.addUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByEmailWithNull() throws Exception {
        userDataHandler.getUserByEmailAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserWithNull() throws Exception {
        userDataHandler.getUser(null);
    }

    public static User getTestUser() throws Exception {
        String string_date = "12-December-2030";
        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        Date d = f.parse(string_date);
        final Date expiry = d;
        MailVerification verification = new MailVerification(expiry, "123456789");
        EmailAddress emailAddress = new EmailAddress(false, "test@test.com", verification);
        UserPassword pwd = UserPassword.fromPassword("123456789");
        User user = new User(null, null, new Date(), 0.0f, "", pwd, "Max", "Muster", "123456789.jpg", emailAddress);
        assertNotNull(user.getDescription());
        return user;
    }


    @Test(expected = IllegalArgumentException.class)
    public void testAddNull() throws Exception {
        userDataHandler.addUser(null);
    }

    @Test
    public void testAddUser() throws Exception {
        User user = DummyFactory.newUser(1);
        final Date expiry = new Date();
        MailVerification resetVerification = new MailVerification(expiry);
        user.getPassword().setResetVerification(resetVerification);
        String id = userDataHandler.addUser(user);
        assertNotNull(id);
        notDeletedUserIds.push(id);
        assertNotNull(user.getId());
        User fetchedUser = userDataHandler.getUser(id);
        Asserts.assertEqualUsers(user, fetchedUser);


        // Add one user without password reset verification
        user = DummyFactory.newUser(2);
        assertNull(user.getPassword().getResetVerification());

        id = userDataHandler.addUser(user);
        assertNotNull(id);
        notDeletedUserIds.push(id);
        fetchedUser = userDataHandler.getUser(id);
        Asserts.assertEqualUsers(user, fetchedUser);

        // Add one user without password reset verification
        user = DummyFactory.newUser(4);
        user.getMail().setVerified(true);
        user.getMail().setVerification(null);
        id = userDataHandler.addUser(user);
        assertNotNull(id);
        notDeletedUserIds.push(id);
        fetchedUser = userDataHandler.getUser(id);
        Asserts.assertEqualUsers(user, fetchedUser);
    }

    @Test
    public void testUpdateUserWithVerifiedEmail() throws Exception {
        // Add one user without password reset verification
        User user = getTestUser();
        assertFalse(user.getMail().isVerified());
        assertNotNull(user.getMail().getVerification());

        String id = userDataHandler.addUser(user);
        assertNotNull(id);
        notDeletedUserIds.push(id);
        assertNotNull(user.getId());

        User updatedUser = userDataHandler.getUser(id);
        assertNotNull(updatedUser);
        updatedUser.getMail().verify();
        assertTrue(updatedUser.getMail().isVerified());
        assertNull(updatedUser.getMail().getVerification());
        assertTrue(updatedUser.getMail().isDirty());
        userDataHandler.updateUser(updatedUser);
        assertFalse(updatedUser.getMail().isDirty());
        User fetchedUser = userDataHandler.getUser(id);
        assertFalse(fetchedUser.getMail().isDirty());
        assertTrue(fetchedUser.getMail().isVerified());
        assertNull(fetchedUser.getMail().getVerification());

    }


    @Test(expected = IllegalArgumentException.class)
    public void testDeleteNull() throws Exception {
        userDataHandler.deleteUser(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteUserWithIdNull() throws Exception {
        User user = getTestUser();
        assertNull(user.getId());
        userDataHandler.deleteUser(user);
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = getTestUser();
        try {
            userDataHandler.getUserByEmailAddress(user.getMail().getAddress());
            fail();
        } catch (ObjectNotFoundException e) {
        }
        String id = userDataHandler.addUser(user);
        assertNotNull(id);
        notDeletedUserIds.push(id);
        assertNotNull(userDataHandler.getUserByEmailAddress(user.getMail().getAddress()));
        assertNotNull(user = userDataHandler.getUser(id));
        userDataHandler.deleteUser(user);
        notDeletedUserIds.pop();
        try {
            userDataHandler.getUser(user.getId());
            fail();
        } catch (ObjectNotFoundException e) {
        }
        try {
            userDataHandler.getUserByEmailAddress(user.getMail().getAddress());
            fail();
        } catch (ObjectNotFoundException e) {
        }
    }
}
