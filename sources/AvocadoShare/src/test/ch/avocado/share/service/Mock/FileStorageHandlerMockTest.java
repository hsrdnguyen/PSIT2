package ch.avocado.share.service.Mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Files;

import static org.junit.Assert.assertNotNull;

public class FileStorageHandlerMockTest {
    private FileStorageHandlerMock handler;

    @Before
    public void setUp() {
        handler = new FileStorageHandlerMock();
    }

    @Test
    public void testTempDirectory() {
        assertNotNull(handler.getTempDirectory());
    }


    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(FileSystems.getDefault().getPath(handler.getTempDirectory()));
    }

}