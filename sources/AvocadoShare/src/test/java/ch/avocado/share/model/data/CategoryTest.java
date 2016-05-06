package ch.avocado.share.model.data;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Tests for Category
 */
public class CategoryTest {

    @Test
    public void testGetName() throws Exception {
        String name = "my name";
        Category category = new Category(name);
        assertEquals(name, category.getName());

        category = new Category(name, new ArrayList<>());
        assertEquals(name, category.getName());
    }

    @Test(expected = NullPointerException.class)
    public void testNullAsNameInConstructor() throws Exception {
        new Category(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullAsNameInConstructorWithIds() throws Exception {
        new Category(null, new ArrayList<>());
    }

    @Test(expected = NullPointerException.class)
    public void testNullAsIdsInConstructor() throws Exception {
        new Category("some name", null);
    }

    @Test
    public void testGetObjectIds() throws Exception {
        String name = "category name";
        ArrayList<String> ids = new ArrayList<>();
        Category category = new Category(name, ids);
        assertEquals(ids, category.getObjectIds());
        assertNotSame(ids, category.getObjectIds());
        ids.add("1234");
        ids.add("3456");
        category = new Category(name, ids);
        assertEquals(ids, category.getObjectIds());
        assertNotSame(ids, category.getObjectIds());
    }

    @Test
    public void testEquals() throws Exception {
        String name = "category name";
        ArrayList<String> ids = new ArrayList<>();
        Category category = new Category(name, ids);

        ids.add("1234");
        ids.add("3456");
        Category categoryTwo = new Category(name, ids);

        assertTrue(category.equals(categoryTwo));
        assertTrue(categoryTwo.equals(category));
        assertFalse(category.equals(null));

        categoryTwo = new Category(name + "different");
        assertFalse(category.equals(categoryTwo));
        assertFalse(categoryTwo.equals(category));
    }
}