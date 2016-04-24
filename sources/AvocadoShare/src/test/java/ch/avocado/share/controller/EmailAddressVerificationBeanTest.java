package ch.avocado.share.controller;

import ch.avocado.share.model.data.EmailAddress;
import ch.avocado.share.model.data.EmailAddressVerification;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.data.UserPassword;
import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class EmailAddressVerificationBeanTest {
    private VerificationBean bean;
    private User user;
    private IUserDataHandler userDataHandler;
    private EmailAddressVerification addressVerification;
    private EmailAddress email;

    @Before
    public void setUp() throws Exception {
        bean = new VerificationBean();
        addressVerification = new EmailAddressVerification(new Date(System.currentTimeMillis() + 10000));
        email = spy(new EmailAddress(false, "email@zhaw.ch", addressVerification));
        user = spy(new User(UserPassword.EMPTY_PASSWORD, "Prename", "Surname", "1234.jpg", email));
        userDataHandler = mock(IUserDataHandler.class);
        when(userDataHandler.getUserByEmailAddress(email.getAddress())).thenReturn(user);
        ServiceLocatorModifier.setService(IUserDataHandler.class, userDataHandler);
    }

    @After
    public void restoreServices() {
        ServiceLocatorModifier.restore();
    }

    @Test
    public void testVerify() throws Exception {
        bean.setEmail(email.getAddress());
        bean.setCode(addressVerification.getCode());
        assertTrue(bean.verifyEmailCode());

        verify(email, times(1)).verify();
        verify(userDataHandler, times(1)).updateUser(user);
        assertNull(email.getVerification());
    }

    @Test
    public void testVerifyFailsWithInvalidCode() throws Exception {
        bean.setEmail(email.getAddress());
        bean.setCode(addressVerification.getCode()  + "1234");
        assertFalse(bean.verifyEmailCode());

        verify(email, never()).verify();
        verify(userDataHandler, never()).updateUser(any(User.class));
        assertNotNull(email.getVerification());

    }

    @Test
    public void testVerifyFailsWithExpiredCode() throws Exception {
        bean.setEmail(email.getAddress());
        bean.setCode(addressVerification.getCode());

        addressVerification.setExpiry(new Date(0));
        assertTrue(addressVerification.isExpired());
        assertFalse(bean.verifyEmailCode());

        verify(email, never()).verify();
        verify(userDataHandler, never()).updateUser(any(User.class));
        assertNotNull(email.getVerification());
    }

    @Test
    public void testVerifyFailsWithInvalidEmail() throws Exception {

        when(userDataHandler.getUserByEmailAddress(any(String.class))).thenReturn(null);

        bean.setEmail(email.getAddress());
        bean.setCode(addressVerification.getCode());

        assertFalse(bean.verifyEmailCode());

        verify(email, never()).verify();
        verify(userDataHandler, never()).updateUser(any(User.class));
        assertNotNull(email.getVerification());
    }



    @Test(expected = IllegalArgumentException.class)
    public void testSetCodeToNull() throws Exception {
        bean.setCode(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEmailToNull() throws Exception {
        bean.setEmail(null);
    }

    @Test
    public void testGetCode() throws Exception {
        String code = "somebody@zhaw.ch";
        bean.setCode(code);
        assertEquals(bean.getCode(), code);
    }

    @Test
    public void testPropertyEmail() throws Exception {
        String email = "somebody@zhaw.ch";
        bean.setEmail(email);
        assertEquals(bean.getEmail(), email);
    }
}