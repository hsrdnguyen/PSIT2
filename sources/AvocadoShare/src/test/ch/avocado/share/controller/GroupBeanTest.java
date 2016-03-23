package ch.avocado.share.controller;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 23.03.16.
 */
public class GroupBeanTest {

    private GroupBean bean;

    @Before
    public void setUp() throws Exception {
        bean = new GroupBean();
    }

    @Test
    public void testHasIdentifierFromName() throws Exception {
        assertFalse(bean.hasIdentifier());
        bean.setName("Name");
        assertTrue(bean.hasIdentifier());
    }

    @Test
    public void testHasIdentifierFromId() throws Exception {
        assertFalse(bean.hasIdentifier());
        bean.setId("Identifier");
        assertTrue(bean.hasIdentifier());
    }

    @Test
    public void testCreate() throws Exception {

    }

    @Test
    public void testGet() throws Exception {

    }

    @Test
    public void testIndex() throws Exception {

    }

    @Test
    public void testUpdate() throws Exception {

    }

    @Test
    public void testDestroy() throws Exception {

    }

    @Test
    public void testGetAttributeName() throws Exception {

    }
}