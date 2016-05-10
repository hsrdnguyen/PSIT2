package ch.avocado.share.common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by coffeemakr on 28.04.16.
 */
public class ImageHelperTest {

    private BufferedImage originalImage;
    private BufferedImage croppedAndScaledImage;

    @Before
    public void setUp() throws Exception {
        originalImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("ch/avocado/share/common/1024x200.png"));
        croppedAndScaledImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("ch/avocado/share/common/1024x200_cropped_and_scaled_to_100x100.png"));
    }

    static private void assertImageEquals(BufferedImage expected, BufferedImage actual, int[] allowedDeltas) {
        if (allowedDeltas.length != 3) throw new IllegalArgumentException("allowedDeltas has to be {r,g,b}");
        assertEquals(expected.getWidth(), actual.getWidth());
        assertEquals(expected.getHeight(), actual.getHeight());
        Raster expectedRaster = expected.getData();
        Raster actualRaster = actual.getData();
        int[] expectedPixel = null, actualPixel = null;
        for (int x = 0; x < expectedRaster.getWidth(); ++x) {
            for (int y = 0; y < expectedRaster.getHeight(); ++y) {
                expectedPixel = expectedRaster.getPixel(x, y, expectedPixel);
                actualPixel = actualRaster.getPixel(x, y, actualPixel);
                for (int i = 0; i < 3; i++) {
                    int difference = expectedPixel[i] - actualPixel[i];
                    difference = Math.abs(difference);
                    if (difference > allowedDeltas[i]) {
                        System.out.println(Arrays.toString(expectedPixel));
                        System.out.println(Arrays.toString(actualPixel));
                        fail(String.format("Pixel at %d, %d differes by %d > %d in channel %d", x, y, difference, allowedDeltas[i], i));
                    }
                }
            }
        }
    }

    private void showImages(BufferedImage image, BufferedImage image2) {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.getContentPane().add(new JLabel(new ImageIcon(image2)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        while (frame.isShowing()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testResizeFilling() throws Exception {
        BufferedImage outputImage = ImageHelper.resizeFilling(originalImage, 100, 100);
        assertEquals(100, outputImage.getWidth());
        assertEquals(100, outputImage.getHeight());
        assertImageEquals(croppedAndScaledImage, outputImage, new int[]{30, 30, 30});
        //showImages(croppedAndScaledImage, outputImage);
    }
}