package ch.avocado.share.service.Impl;

import ch.avocado.share.model.data.*;
import ch.avocado.share.service.exceptions.MailingServiceException;
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
    public void init() {
        service = new MailingService();
    }

    @Test(expected = NullPointerException.class)
    public void testVerificationEmailNoParameter() throws MailingServiceException {
        service.sendRequestAccessEmail(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testRequestEmailNoParameter() throws MailingServiceException {
        service.sendVerificationEmail(null);
    }

    @Test
    public void testRequestSendMail() throws MailingServiceException {
        final Date expiry = new Date(0);
        User user = new User("123", new ArrayList<Category>(), new Date(0), new Rating(), "", UserPassword.fromPassword("123456"), "", "", "", new EmailAddress(true, "bergmsas@students.zhaw.ch", new MailVerification(expiry, "123456")));
        File file = new File("123", new ArrayList<Category>(), new Date(0), new Rating(), "1234", "", "title", "path",  new Date(0), "", "321321", "image/png");
        service.sendRequestAccessEmail(user, user, file);
    }

    @Test
    public void test_verificationSendMail() throws MailingServiceException {
        final Date expiry = new Date(0);
        User user = new User("123", new ArrayList<Category>(), new Date(0), new Rating(), "", UserPassword.fromPassword("123456"), "", "", "", new EmailAddress(true, "bergmsas@students.zhaw.ch", new MailVerification(expiry, "123456")));
        service.sendVerificationEmail(user);
    }

}

