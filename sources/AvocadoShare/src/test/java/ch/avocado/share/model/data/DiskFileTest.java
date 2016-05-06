package ch.avocado.share.model.data;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link DiskFile}
 */
public class DiskFileTest {
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyPath() {
        new DiskFile("", "image/png", ".png");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithPathNull() {
        new DiskFile(null, "image/png", ".png");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyMimeType() {
        new DiskFile("4783291084032432", "", ".png");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithMimeTypeNull() {
        new DiskFile("3u21039210321890", null, ".png");
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithExtensionNull() {
        new DiskFile("3u21039210321890", "image/png", null);
    }


    @Test
    public void testGetExtension() throws Exception {
        String extension = "png";
        DiskFile diskFile = new DiskFile("3218i9321", "image/png", extension);
        assertEquals(extension, diskFile.getExtension());
    }

    @Test
    public void testGetMimeType() throws Exception {
        DiskFile diskFile = new DiskFile("3218i9321", "image/png", "png");
        assertEquals("image/png", diskFile.getMimeType());
    }

    @Test
    public void testGetPath() throws Exception {
        DiskFile diskFile = new DiskFile("3218i9321", "image/png", "png");
        assertEquals("3218i9321", diskFile.getPath());
    }

    @Test
    public void testEquals() throws Exception {
        DiskFile diskFile = new DiskFile("3218i9321", "image/png", "png");
        DiskFile diskFileTwo = new DiskFile("3218i9321", "image/png", "png");
        assertTrue(diskFile.equals(diskFileTwo));
        assertTrue(diskFileTwo.equals(diskFile));
        assertTrue(diskFile.equals(diskFile));
        assertFalse(diskFile.equals(null));

        diskFileTwo = new DiskFile("3218i93213", "image/png", "png");
        assertFalse(diskFile.equals(diskFileTwo));
        assertFalse(diskFileTwo.equals(diskFile));

        diskFileTwo = new DiskFile("3218i9321", "image/jpeg", "png");
        assertFalse(diskFile.equals(diskFileTwo));
        assertFalse(diskFileTwo.equals(diskFile));

        diskFileTwo = new DiskFile("3218i9321", "image/png", "jpg");
        assertFalse(diskFile.equals(diskFileTwo));
        assertFalse(diskFileTwo.equals(diskFile));
    }
}