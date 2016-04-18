package ch.avocado.share.controller;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by coffeemakr on 21.03.16.
 */
public class EmailAddressVerificationBeanTest {
    private VerificationBean bean;
    @Before
    public void setUp() throws Exception {
        bean = new VerificationBean();
    }

    @Test
    public void testVerify() throws Exception {
        // TODO
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