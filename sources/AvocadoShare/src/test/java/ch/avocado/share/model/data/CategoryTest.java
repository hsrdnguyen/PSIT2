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
    public void testGetName() {
        String name = "my name";
        Category category = new Category(name);
        assertEquals(name, category.getName());

        category = new Category(name, new ArrayList<>());
        assertEquals(name, category.getName());
    }

    @Test(expected = NullPointerException.class)
    public void testNullAsNameInConstructor() {
        new Category(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullAsNameInConstructorWithIds() {
        new Category(null, new ArrayList<>());
    }

    @Test(expected = NullPointerException.class)
    public void testNullAsIdsInConstructor() {
        new Category("some name", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyNameInConstructor() {
        new Category("");
    }

    @Test
    public void testGetObjectIds() {
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
    public void testEquals() {
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

    @Test
    public void testCompareTo() {
        assertTrue(new Category("aaaa").compareTo(new Category("bbbb")) < 0);
        assertTrue(new Category("bbbb").compareTo(new Category("aaaa")) > 0);
        assertTrue(new Category("aaaa").compareTo(new Category("BBBB")) < 0);
        assertTrue(new Category("BBBB").compareTo(new Category("aaaa")) > 0);
        assertTrue(new Category("AAAA").compareTo(new Category("bbbb")) < 0);
        assertTrue(new Category("bbbb").compareTo(new Category("AAAA")) > 0);
        assertTrue(new Category("AAaa").compareTo(new Category("aaaa")) < 0);
        assertTrue(new Category("aaaa").compareTo(new Category("AAaa")) > 0);
        assertTrue(new Category("ab").compareTo(new Category("abb")) < 0);
        assertTrue(new Category("abb").compareTo(new Category("ab")) > 0);
        assertTrue(new Category("ABCD").compareTo(new Category("ABCD")) == 0);
        assertTrue(new Category("abCd").compareTo(new Category("abCd")) == 0);
    }

    @Test(expected = NullPointerException.class)
    public void testCompareToWithNull() {
        new Category("1234").compareTo(null);
    }

    @Test
    public void testToString() throws Exception {
        Category category = new Category("1234");
        assertTrue(category.toString().contains("Category"));
        assertTrue(category.toString().contains("1234"));
    }

    @Test
    public void testHashCode() throws Exception {
        assertNotEquals(new Category("1234").hashCode(), new Category("3213"));
        ArrayList<String> ids1 = new ArrayList<>();
        ids1.add("1");
        ids1.add("2");

        ArrayList<String> ids2 = new ArrayList<>();
        ids1.add("321");
        ids1.add("421");

        assertEquals(new Category("1234").hashCode(), new Category("1234").hashCode());
        assertEquals(new Category("1234").hashCode(), new Category("1234", ids1).hashCode());
        assertEquals(new Category("1234", ids1).hashCode(), new Category("1234",  ids2).hashCode());
    }
}