package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ImageHelper;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.Mock.ServiceLocatorModifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by coffeemakr on 28.04.16.
 */
public class AvatarFileStorageHandlerTest {

    private AvatarFileStorageHandler handler;
    private IFileStorageHandler fileStorageHandlerMock;

    @Before
    public void setUp() throws Exception {
        fileStorageHandlerMock = mock(IFileStorageHandler.class);
        ServiceLocatorModifier.setService(IFileStorageHandler.class, fileStorageHandlerMock);
        handler = new AvatarFileStorageHandler();
    }

    @After
    public void tearDown() throws Exception {
        ServiceLocatorModifier.restore();
    }

    @Test
    public void testStoreAvatar() throws Exception {
        when(fileStorageHandlerMock.saveFile(any(File.class))).thenReturn("1234");
        String reference = handler.storeAvatar(Thread.currentThread().getContextClassLoader().getResource("ch/avocado/share/common/1024x200.png").openStream());
        assertEquals("1234", reference);
        verify(fileStorageHandlerMock, times(1)).saveFile(any(File.class));
    }

    @Test
    public void testReadImages() throws Exception {
        InputStream stream = mock(InputStream.class);
        when(fileStorageHandlerMock.readFile("1234")).thenReturn(stream);
        InputStream imageStream = handler.readImages("1234");
        assertSame(imageStream, stream);
    }

    @Test
    public void testGetImageType() throws Exception {
        assertEquals("image/png", handler.getImageType("???"));
    }
}