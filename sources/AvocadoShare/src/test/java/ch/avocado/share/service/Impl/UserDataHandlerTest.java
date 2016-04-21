package ch.avocado.share.service.Impl;

import ch.avocado.share.model.data.*;
import ch.avocado.share.service.Mock.DatabaseConnectionHandlerMock;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;


/**
 * Created by bergm on 23/03/2016.
 */
public class UserDataHandlerTest {
    private UserDataHandler userDataHandler;

    @Before
    public void init() throws Exception {
        DatabaseConnectionHandlerMock.use();
        userDataHandler = new UserDataHandler();
    }

    @After
    public void tearDown() throws Exception {
        ServiceLocatorModifier.restore();
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
        EmailAddressVerification verification = new EmailAddressVerification(d, "123456789");
        EmailAddress emailAddress = new EmailAddress(false, "test@test.com", verification);
        UserPassword pwd = UserPassword.fromPassword("123456789");
        User user = new User(null, null, new Date(), 0.0f, "", pwd, "Max", "Muster", "123456789.jpg", emailAddress);
        assertNotNull(user.getDescription());
        return user;
    }

    static public void assertEqualUsers(User expected, User actual) {
        assertFalse("They are the same object", actual == expected);
        assertEquals("Id", expected.getId(), actual.getId());
        assertEquals("Creation Date", actual.getCreationDate(), actual.getCreationDate());
        assertEquals("Prename", expected.getPrename(), actual.getPrename());
        assertEquals("Surname", expected.getSurname(), actual.getSurname());
        assertEquals("Description", expected.getDescription(), actual.getDescription());
        assertEquals("Password digest", expected.getPassword().getDigest(), actual.getPassword().getDigest());
        assertEquals("Avatar", expected.getAvatar(), actual.getAvatar());
        assertEquals("Mail", expected.getMail(), actual.getMail());
        assertEquals("Reset verification", expected.getPassword().getResetVerification(), actual.getPassword().getResetVerification());
    }

    @Test
    public void testAddUser() throws Exception {
        User user = getTestUser();
        PasswordResetVerification resetVerification = new PasswordResetVerification(new Date());
        user.getPassword().setResetVerification(resetVerification);
        String id = userDataHandler.addUser(user);
        assertNotNull(id);
        assertNotNull(user.getId());
        User addedUser = userDataHandler.getUser(id);
        try {
            assertEqualUsers(user, addedUser);
        } finally {
            userDataHandler.deleteUser(addedUser);
        }

        // Add one user without password reset verification
        user = getTestUser();
        id = userDataHandler.addUser(user);
        addedUser = userDataHandler.getUser(id);
        try {
            assertEqualUsers(user, addedUser);
        } finally {
            userDataHandler.deleteUser(addedUser);
        }

        // Add one user without password reset verification
        user = getTestUser();
        user.getMail().setVerified(true);
        user.getMail().setVerification(null);
        id = userDataHandler.addUser(user);
        addedUser = userDataHandler.getUser(id);
        try {
            assertEqualUsers(user, addedUser);
        } finally {
            userDataHandler.deleteUser(addedUser);
        }
    }

    @Ignore
    @Test
    public void testUpdateUserWithVerifiedEmail() throws Exception {
        // Add one user without password reset verification
        User user = getTestUser();
        assertFalse(user.getMail().isVerified());
        assertNotNull(user.getMail().getVerification());

        String id = userDataHandler.addUser(user);
        assertNotNull(id);
        assertNotNull(user.getId());

        try {
            User updatedUser = userDataHandler.getUser(id);
            assertNotNull(updatedUser);
            updatedUser.getMail().verify();
            assertTrue(updatedUser.getMail().isVerified());
            assertNull(updatedUser.getMail().getVerification());
            assertTrue(updatedUser.getMail().isDirty());
            assertTrue(userDataHandler.updateUser(updatedUser));
            assertFalse(updatedUser.getMail().isDirty());
            User addedUser = userDataHandler.getUser(id);
            assertFalse(addedUser.getMail().isDirty());
            assertTrue(addedUser.getMail().isVerified());
            assertNull(addedUser.getMail().getVerification());
        }finally {
            userDataHandler.deleteUser(user);
        }
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = getTestUser();
        assertNull(userDataHandler.getUserByEmailAddress(user.getMail().getAddress()));
        String id = userDataHandler.addUser(user);
        assertNotNull(id);
        assertNotNull(userDataHandler.getUserByEmailAddress(user.getMail().getAddress()));
        assertNotNull(user = userDataHandler.getUser(id));
        assertTrue(userDataHandler.deleteUser(user));
        assertNull(userDataHandler.getUser(user.getId()));
        assertNull(userDataHandler.getUserByEmailAddress(user.getMail().getAddress()));
    }
}
