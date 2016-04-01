package ch.avocado.share.common.form;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 31.03.16.
 */
public class InputFieldTest {

    public static final String DEFAULT_TYPE = "text";
    private InputField field;

    @Before
    public void setUp() throws Exception {
        field = new InputField("name", "id", DEFAULT_TYPE);
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(DEFAULT_TYPE, field.getType());
    }

    @Test
    public void testSetType() throws Exception {
        assertEquals(DEFAULT_TYPE, field.getType());
        field.setValue("new_type");
        assertEquals("new_type", field.getType());
    }

    @Test
    public void testToString() throws Exception {
        String html = field.toString();
        assertTrue(html.contains("id=\"id\""));
        assertTrue(html.contains("type=\""+ DEFAULT_TYPE + "\""));
        assertTrue(html.contains("input"));
        assertFalse(html.contains("textarea"));
        field.setType("textarea");
        html = field.toString();
        assertFalse(html.contains("input"));
        assertTrue(html.contains("textarea"));
        assertFalse(html.contains("type="));
    }

    @Test
    public void testGetValue() throws Exception {

    }

    @Test
    public void testSetValue() throws Exception {

    }
}