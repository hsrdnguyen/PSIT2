package ch.avocado.share.common.form;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 31.03.16.
 */
public class InputFieldTest {

    public static final InputType DEFAULT_TYPE = InputType.TEXT;
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
        field.setType(InputType.EMAIL);
        assertEquals(InputType.EMAIL, field.getType());
    }

    @Test
    public void testToString() throws Exception {
        String html = field.toString();
        assertTrue(html.contains("id=\"id\""));
        assertTrue(html.contains("type=\""+ DEFAULT_TYPE.name().toLowerCase() + "\""));
        assertTrue(html.contains("input"));
        assertFalse(html.contains("textarea"));
        field.setType(InputType.TEXTAREA);
        html = field.toString();
        assertFalse(html.contains("input"));
        assertTrue(html.contains("textarea"));
        assertFalse(html.contains("type="));
    }

    @Test
    public void testGetValue() throws Exception {
        String value = "A value";
        assertTrue("Value not empty", field.getValue().isEmpty());
        field.setValue(value);
        assertEquals("Value not set correctly", value, field.getValue());
    }
}