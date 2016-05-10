package ch.avocado.share.model.data;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static ch.avocado.share.test.Asserts.assertCategoriesEquals;
import static org.junit.Assert.*;

/**
 * Tests for {@link CategoryList}.
 */
public class CategoryListTest {

    private CategoryList categoryList;
    private ArrayList<Category> originalCategories;

    @Before
    public void setUp() throws Exception {
        originalCategories = new ArrayList<>();
        originalCategories.add(new Category("one"));
        originalCategories.add(new Category("two"));
        originalCategories.add(new Category("three"));
        categoryList = new CategoryList(originalCategories);
    }

    @Test
    public void testGetCategories() {
        assertCategoriesEquals(originalCategories, categoryList.getCategories());
        assertCategoriesEquals(originalCategories, categoryList);
    }

    @Test
    public void testAddCategory() {
        categoryList.add(new Category("four"));
        assertTrue(categoryList.contains(new Category("four")));
        originalCategories.add(new Category("four"));
        assertCategoriesEquals(originalCategories, categoryList);
    }

    @Test(expected = NullPointerException.class)
    public void testAddCategoryNull() {
        categoryList.add(null);
    }

    @Test
    public void testSetCategoriesWithSame() {
        ArrayList<Category> newCategories = new ArrayList<>();
        // first test we use an identical list
        newCategories.add(new Category("one"));
        newCategories.add(new Category("two"));
        newCategories.add(new Category("three"));
        categoryList.setCategories(newCategories);

        assertEquals(0, categoryList.getRemovedCategories().size());
        assertEquals(0, categoryList.getNewCategories().size());
        assertCategoriesEquals(newCategories, categoryList);
    }

    @Test(expected = NullPointerException.class)
    public void testSetCategoriesWithNull() throws Exception {
        categoryList.setCategories(null);

    }

    @Test
    public void testSetCategories() {
        ArrayList<Category> newCategories = new ArrayList<>();
        // first test we use an identical list
        newCategories.add(new Category("one"));
        newCategories.add(new Category("two"));
        newCategories.add(new Category("four"));
        newCategories.add(new Category("five"));
        categoryList.setCategories(newCategories);
        assertCategoriesEquals(newCategories, categoryList);
        assertTrue(categoryList.getNewCategories().contains(new Category("four")));
        assertTrue(categoryList.getNewCategories().contains(new Category("five")));
        assertEquals(2, categoryList.getNewCategories().size());

        assertTrue(categoryList.getRemovedCategories().contains(new Category("three")));
        assertEquals(1, categoryList.getRemovedCategories().size());
    }

    @Test
    public void testRemoveAll() {
        ArrayList<Category> categoriesToRemove = new ArrayList<>();
        categoriesToRemove.add(new Category("one"));
        categoriesToRemove.add(new Category("one"));
        categoriesToRemove.add(new Category("three"));
        categoryList.removeAll(categoriesToRemove);

        ArrayList<Category> expectedCategories = new ArrayList<>();
        expectedCategories.add(new Category("two"));
        assertEquals(1, categoryList.size());
        assertCategoriesEquals(expectedCategories, categoryList);
    }

    @Test
    public void testRemoveCategory() {
        assertFalse(categoryList.contains(new Category("four")));
        assertFalse(categoryList.remove(new Category("four")));
        assertTrue(categoryList.contains(new Category("three")));
        assertTrue(categoryList.remove(new Category("three")));
        assertFalse(categoryList.contains(new Category("three")));
        assertFalse(categoryList.remove(null));
    }

    @Test
    public void testGetNewCategories() {
        categoryList.add(new Category("four"));
        assertTrue(categoryList.contains(new Category("four")));
        assertEquals(1, categoryList.getNewCategories().size());
        assertTrue(categoryList.getNewCategories().contains(new Category("four")));
        assertTrue(categoryList.getRemovedCategories().isEmpty());

        // Only added once
        categoryList.add(new Category("four"));
        assertEquals(1, categoryList.getNewCategories().size());

        // Not new if already exist
        categoryList.add(new Category("three"));
        assertEquals(1, categoryList.getNewCategories().size());
        assertFalse(categoryList.getNewCategories().contains(new Category("three")));
    }

    @Test
    public void testGetRemovedCategories() {
        categoryList.remove(new Category("three"));
        assertTrue(categoryList.getRemovedCategories().contains(new Category("three")));
        assertEquals(1, categoryList.getRemovedCategories().size());
        categoryList.remove(new Category("unexisting"));
        assertFalse(categoryList.getRemovedCategories().contains(new Category("unexisting")));
        assertFalse(categoryList.contains(new Category("unexisting")));
    }
}