package ch.avocado.share.controller;

import ch.avocado.share.service.IUserDataHandler;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import ch.avocado.share.service.Mock.UserDataHandlerMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by bergm on 24/03/2016.
 */
public class UserRegistrationBeanTest {

    public UserRegistrationBean bean;

    public UserRegistrationBeanTest() throws Exception{
        ServiceLocatorModifier.setService(IUserDataHandler.class, new UserDataHandlerMock());
    }
    @Before
    public void setUp() throws Exception {
        bean = new UserRegistrationBean();
    }

    @Test(expected = IllegalArgumentException.class)
    public void set_name_nullTest()
    {
        bean.setName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void set_prename_nullTest()
    {
        bean.setPrename(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void set_avatar_nullTest()
    {
        bean.setAvatar(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void set_mail_nullTest()
    {
        bean.setEmailAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void set_password_nullTest()
    {
        bean.setPassword(null);
    }

    @Test
    public void set_name_test()
    {
        String tmp = "Test";
        bean.setName(tmp);
        assertEquals(tmp, bean.getName());
    }


    @Test
    public void set_prename_test()
    {
        String tmp = "Test";
        bean.setPrename(tmp);
        assertEquals(tmp, bean.getPrename());
    }

    @Test
    public void set_avatar_test()
    {
        String tmp = "Test";
        bean.setAvatar(tmp);
        assertEquals(tmp, bean.getAvatar());
    }

    @Test
    public void set_password_test()
    {
        String tmp = "Test";
        bean.setPassword(tmp);
        assertEquals(tmp, bean.getPassword());
    }

    @Test
    public void set_mail_test_correct()
    {
        String tmp = "Test@test.com";
        bean.setEmailAddress(tmp);
        assertEquals(tmp, bean.getEmailAddress());
    }

    @Test(expected = IllegalArgumentException.class)
    public void set_mail_test_incorrect()
    {
        String tmp = "Test";
        bean.setEmailAddress(tmp);
    }



}