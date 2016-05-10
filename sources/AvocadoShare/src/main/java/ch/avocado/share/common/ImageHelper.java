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
        if(image == null) throw new NullPointerException("image is null");
        if(width < 1) throw new IllegalArgumentException("width has to be greater than 0");
        if(height < 1) throw new IllegalArgumentException("height has to be greater than 0");

        BufferedImage outputImage = new BufferedImage(width, height, image.getType());

        Graphics2D graphics2D = outputImage.createGraphics();

        // Float used because we don't need precision and it may be faster
        float outputHeightToWidthRatio = ((float) height) / width;
        float imageHeightToWidthRatio = ((float) image.getHeight()) / image.getWidth();
        if (outputHeightToWidthRatio < imageHeightToWidthRatio) {
            // original image height has to be cropped
            int drawnImageHeight =(int) (((float) width) / image.getWidth() * image.getHeight());
            graphics2D.drawImage(image, 0, 0, width, drawnImageHeight, null);
        } else {
            // original image width has to be cropped
            int drawnImageWidth = (int) (((float) width) / image.getHeight() * image.getWidth());
            graphics2D.drawImage(image, 0, 0, drawnImageWidth, height, null);
        }
        graphics2D.dispose();
        return outputImage;
    }
}

