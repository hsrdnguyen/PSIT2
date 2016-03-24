package ch.avocado.share.service;

import ch.avocado.share.model.data.EmailAddress;
import ch.avocado.share.model.data.EmailAddressVerification;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.Impl.UserDataHandler;
import ch.avocado.share.model.data.UserPassword;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by bergm on 23/03/2016.
 */
public class UserDataHandlerTest {
    private UserDataHandler service;

    @Before
    public void init()
    {
        try {
            service = new UserDataHandler();
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_addUser_noParameter() {
        service.addUser(null);
    }

    @Test
    public void test_addUser() {
        String string_date = "12-December-2030";

        SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date d = f.parse(string_date);

            EmailAddressVerification verification = new EmailAddressVerification(d,"123456789");
            EmailAddress emailAddress = new EmailAddress(false, "test@test.com", verification);
            UserPassword pwd = new UserPassword("123456789");
            User user = new User("", null, new Date(), 0, "", "", pwd, "Max", "Muster", "123456789.jpg", emailAddress);

            assertTrue(service.addUser(user));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
