package ch.avocado.share.model.data;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by coffeemakr on 31.03.16.
 */
public class FileTest {

    private File file;

    @Before
    public void setUp() {
        this.file = new File("id", new ArrayList<Category>(), new Date(), 0.0f, "", "", "", "", new Date(), "", "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTitleNull() throws Exception {
        file.setTitle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPathNull() throws Exception {
        file.setPath(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLastChangedNull() throws Exception {
        file.setLastChanged(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTypeNull() throws Exception {
        file.setExtension(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetModuleIdNull() throws Exception {
        file.setModuleId(null);
    }

    @Test
    public void testSetTitle() throws Exception {
        String title = "Random Title";
        file.setTitle(title);
        assertEquals(title, file.getTitle());
    }

    @Test
    public void testSetPath() throws Exception {
        String path = "RandomPath";
        file.setPath(path);
        assertEquals(path, file.getPath());
    }

    @Test
    public void testSetLastChanged() throws Exception {
        Date lastChanged = new Date(System.currentTimeMillis());
        file.setLastChanged(lastChanged);
        assertEquals(lastChanged, file.getLastChanged());
    }

    @Test
    public void testSetType() throws Exception {
        String type="Type";
        file.setExtension(type);
        assertEquals(type, file.getExtension());
    }

    @Test
    public void testSetModuleId() throws Exception {
        String moduleId = "1234";
        file.setModuleId(moduleId);
        assertEquals(moduleId, file.getModuleId());
    }

}