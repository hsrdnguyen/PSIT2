package ch.avocado.share.model.data;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static ch.avocado.share.test.Asserts.assertCategoriesEquals;
import static org.junit.Assert.*;

/**
 * Tests for {@link AccessControlObjectBase}
 */
public class AccessControlObjectBaseTest {

    private AccessControlObjectBase object;
    private String id;
    private ArrayList<Category> categories;
    private Date creationDate;
    private Rating rating;
    private String ownerId;
    private String description;

    /**
     * Make {@link AccessControlObjectBase} concrete.
     */
    class Object extends AccessControlObjectBase {
        /**
         * Constructor
         *
         * @param id           The identifier of the object.
         * @param categories   A list of categories or null if there are no categories.
         * @param creationDate The date of the object creation
         * @param rating       The average of all ratings
         * @param ownerId      The identifier of the owner
         * @param description  Description of the object
         */
        public Object(String id, Collection<Category> categories, Date creationDate, Rating rating, String ownerId, String description) {
            super(id, categories, creationDate, rating, ownerId, description);
        }

        @Override
        public String getReadableName() {
            return "name";
        }
    }

    @Before
    public void setUp() throws Exception {
        id = "12345";
        categories = new ArrayList<>();
        categories.add(new Category("name"));
        creationDate = new Date();
        ownerId = "9876543";
        description = "Description";
        rating = new Rating();
        rating.addRating(2, Long.parseLong(ownerId));
        object = new Object(id, categories, creationDate, rating, ownerId, description);
        assertFalse(object.isDirty());
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals(id, object.getId());
    }

    @Test(expected = NullPointerException.class)
    public void testSetIdNull() throws Exception {
        object.setId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetIdEmpty() throws Exception {
        object.setId("");
    }

    @Test
    public void testSetId() throws Exception {
        String newId = "781947832091";
        object.setId(newId);
        assertEquals(newId, object.getId());
    }

    @Test
    public void testGetCategories() throws Exception {
        assertCategoriesEquals(categories, object.getCategoryList());
        assertNotSame(categories, object.getCategoryList());
    }

    @Test
    public void testSetCategories() throws Exception {
        categories.add(new Category("new"));
        assertNotEquals(categories, object.getCategoryList());
        assertFalse(object.isDirty());
        object.setCategories(categories);
        assertTrue(object.isDirty());
        assertCategoriesEquals(categories, object.getCategoryList());
        assertNotSame(categories, object.getCategoryList());
    }

    @Test
    public void testGetCreationDate() throws Exception {
        assertEquals(creationDate, object.getCreationDate());
    }

    @Test(expected = NullPointerException.class)
    public void testSetCreationDateToNull() throws Exception {
        object.setCreationDate(null);
    }

    @Test
    public void testSetCreationDate() throws Exception {
        assertFalse(object.isDirty());
        object.setCreationDate(creationDate);
        assertFalse(object.isDirty());

        creationDate.setTime(321);
        assertNotEquals(creationDate, object.getCreationDate());

        Date newCreationDate = new Date(System.currentTimeMillis() + 10000);
        assertNotEquals(newCreationDate, object.getCreationDate());
        assertFalse(object.isDirty());
        object.setCreationDate(newCreationDate);
        assertTrue(object.isDirty());
        assertEquals(newCreationDate, object.getCreationDate());
        assertNotSame(newCreationDate, object.getCreationDate());
    }

    @Test
    public void testGetRating() throws Exception {
        assertEquals(rating, object.getRating());
    }


    @Test
    public void testGetOwnerId() throws Exception {
        assertEquals(ownerId, object.getOwnerId());
    }

    /* Disabled because its allowed
    @Test(expected = NullPointerException.class)
    public void testSetOwnerIdToNull() throws Exception {
        object.setOwnerId(null);
    }*/

    @Test(expected = IllegalArgumentException.class)
    public void testSetOwnerIdToEmptyString() throws Exception {
        object.setOwnerId("");
    }

    @Test
    public void testSetOwnerId() throws Exception {
        String newOwnerId = "38909321";
        assertNotEquals(newOwnerId, object.getOwnerId());
        object.setOwnerId(newOwnerId);
    }

    @Test
    public void testGetDescription() throws Exception {
        assertEquals(description, object.getDescription());
    }

    @Test(expected = NullPointerException.class)
    public void testSetDescriptionToNull() throws Exception {
        object.setDescription(null);
    }

    @Test
    public void testSetDescription() throws Exception {
        String newDescription = "djioasghäjduos djioas dzuioasf";
        assertNotEquals(newDescription, object.getDescription());
        object.setDescription(newDescription);
        assertEquals(newDescription, object.getDescription());
    }

}