package ch.avocado.share.service.Impl;

import ch.avocado.share.model.data.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by bergm on 21/03/2016.
 */
public class MailingServiceTest {

    private MailingService service;

    @Before
    public void init()
    {
        service = new MailingService();
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_verificationEmail_noParameter() {
        service.sendRequestAccessEmail(null, null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_requestEmail_noParameter() {
        service.sendVerificationEmail(null);
    }

    @Test
    public void test_requestSendMail()
    {
        final Date expiry = new Date(0);
        User user = new User("123", new ArrayList<Category>(), new Date(0), 0, "", UserPassword.fromPassword("123456"), "", "", "", new EmailAddress(true, "bergmsas@students.zhaw.ch", new MailVerification(expiry, "123456")));
        File file = new File("123", new ArrayList<Category>(), new Date(0), 0, "", "", "", "",  new Date(0), "", "", "");

        assertTrue(service.sendRequestAccessEmail(user, user, file));
    }

    @Test
    public void test_verificationSendMail()
    {
        final Date expiry = new Date(0);
        User user = new User("123", new ArrayList<Category>(), new Date(0), 0, "", UserPassword.fromPassword("123456"), "", "", "", new EmailAddress(true, "bergmsas@students.zhaw.ch", new MailVerification(expiry, "123456")));

        assertTrue(service.sendVerificationEmail(user));
    }

}

