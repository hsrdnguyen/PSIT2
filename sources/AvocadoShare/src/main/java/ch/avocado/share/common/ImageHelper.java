package ch.avocado.share.common;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Manipulations and other helpers for image handling
 */
public class ImageHelper {
    /**
     * Resizes an image and keeps the proportions.
     *
     * @param image  the original image
     * @param width  the width to which the image will be adjusted
     * @param height the height to which the image will be adjusted
     */
    public static BufferedImage resizeFilling(BufferedImage image, int width, int height)  {
        BufferedImage outputImage = new BufferedImage(width, height, image.getType());

        Graphics2D graphics2D = outputImage.createGraphics();

        double outputHeightToWidthRatio = height / width;
        double imageHeightToWidthRatio = image.getHeight() / image.getWidth();
        if (outputHeightToWidthRatio > imageHeightToWidthRatio) {
            // original image height has to be cropped
            int drawnImageHeight = (int) ((((double) height) * image.getWidth()) / image.getWidth());
            graphics2D.drawImage(image, 0, 0, width, drawnImageHeight, null);
        } else {
            // original image width has to be cropped
            int drawnImageWidth = (int) ((((double) width) * image.getHeight()) / image.getWidth());
            graphics2D.drawImage(image, 0, 0, width, drawnImageWidth, null);
        }
        graphics2D.dispose();
        return outputImage;
    }
}

