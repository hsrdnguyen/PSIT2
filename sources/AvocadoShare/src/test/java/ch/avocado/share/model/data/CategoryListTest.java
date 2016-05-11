package ch.avocado.share.model.data;

import ch.avocado.share.common.util.ChangeTrackingSet;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static ch.avocado.share.test.Asserts.assertCategoriesEquals;
import static org.junit.Assert.*;

/**
 * Tests for {@link ChangeTrackingSet<Category>}.
 */
public class CategoryListTest {

    private ChangeTrackingSet<Category> categoryList;
    private ArrayList<Category> originalCategories;

    @Before
    public void setUp() throws Exception {
        originalCategories = new ArrayList<>();
        originalCategories.add(new Category("one"));
        originalCategories.add(new Category("two"));
        originalCategories.add(new Category("three"));
        final java.util.Collection<Category> originalCategories1 = originalCategories;
        categoryList = new ChangeTrackingSet<Category>(originalCategories1);
    }

    @Test
    public void testGetCategories() {
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
        categoryList.setCurrentSet(newCategories);

        assertEquals(0, categoryList.getRemovedSet().size());
        assertEquals(0, categoryList.getNewSet().size());
        assertCategoriesEquals(newCategories, categoryList);
    }

    @Test(expected = NullPointerException.class)
    public void testSetCategoriesWithNull() throws Exception {
        categoryList.setCurrentSet(null);

    }

    @Test
    public void testSetCategories() {
        ArrayList<Category> newCategories = new ArrayList<>();
        // first test we use an identical list
        newCategories.add(new Category("one"));
        newCategories.add(new Category("two"));
        newCategories.add(new Category("four"));
        newCategories.add(new Category("five"));
        categoryList.setCurrentSet(newCategories);
        assertCategoriesEquals(newCategories, categoryList);
        assertTrue(categoryList.getNewSet().contains(new Category("four")));
        assertTrue(categoryList.getNewSet().contains(new Category("five")));
        assertEquals(2, categoryList.getNewSet().size());

        assertTrue(categoryList.getRemovedSet().contains(new Category("three")));
        assertEquals(1, categoryList.getRemovedSet().size());
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
        assertEquals(1, categoryList.getNewSet().size());
        assertTrue(categoryList.getNewSet().contains(new Category("four")));
        assertTrue(categoryList.getRemovedSet().isEmpty());

        // Only added once
        categoryList.add(new Category("four"));
        assertEquals(1, categoryList.getNewSet().size());

        // Not new if already exist
        categoryList.add(new Category("three"));
        assertEquals(1, categoryList.getNewSet().size());
        assertFalse(categoryList.getNewSet().contains(new Category("three")));
    }

    @Test
    public void testGetRemovedCategories() {
        categoryList.remove(new Category("three"));
        assertTrue(categoryList.getRemovedSet().contains(new Category("three")));
        assertEquals(1, categoryList.getRemovedSet().size());
        categoryList.remove(new Category("unexisting"));
        assertFalse(categoryList.getRemovedSet().contains(new Category("unexisting")));
        assertFalse(categoryList.contains(new Category("unexisting")));
    }
}