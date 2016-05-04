package ch.avocado.share.model.data;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 31.03.16.
 */
public class FileTest {

    private File file;

    @Before
    public void setUp() {
        this.file = new File("id", new ArrayList<Category>(), new Date(), 0.0f, "1234", "description", "title", "path", new Date(), "", "4123321", "image/png");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTitleNull() throws Exception {
        file.setTitle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetEmptyTitle() throws Exception {
        file.setTitle("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLastChangedNull() throws Exception {
        file.setLastChanged(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDiskFileNull() throws Exception {
        file.setDiskFile(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetModuleIdToEmptyString() throws Exception {
        file.setModuleId("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetModuleIdToNull() throws Exception {
        file.setModuleId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCategoriesToNull() throws Exception {
        file.setCategories(null);
    }

    @Test
    public void testSetTitle() throws Exception {
        String title = "Random Title";
        file.setTitle(title);
        assertEquals(title, file.getTitle());
    }


    @Test
    public void testSetLastChanged() throws Exception {
        Date lastChanged = new Date(System.currentTimeMillis());
        file.setLastChanged(lastChanged);
        assertEquals(lastChanged, file.getLastChanged());
    }

    @Test
    public void testSetModuleId() throws Exception {
        String moduleId = "1234";
        file.setModuleId(moduleId);
        assertEquals(moduleId, file.getModuleId());
    }


    @Test
    public void testSetCategories() throws Exception {
        ArrayList<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("hi"));
        file.setCategories(categoryList);
        // make sure we cannot modify its inner list.
        assertNotSame(categoryList, file.getCategoryList());
    }

}