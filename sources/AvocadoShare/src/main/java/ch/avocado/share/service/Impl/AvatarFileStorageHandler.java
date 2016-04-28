package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ImageHelper;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IAvatarStorageHandler;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.exceptions.FileStorageException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class AvatarFileStorageHandler implements IAvatarStorageHandler {

    public static final int MAX_IMAGE_SIZE = 1024 * 3;
    public static final int AVATAR_WIDTH = 128;
    public static final int AVATAR_HEIGHT = 128;

    @Override
    public String storeAvatar(InputStream imageStream) {
        IFileStorageHandler fileStorageHandler = null;
        String reference;
        BufferedImage uploadedImage, outputImage;

        try {
            fileStorageHandler = ServiceLocator.getService(IFileStorageHandler.class);
        } catch (ServiceNotFoundException e) {
            // TODO: error handling
            throw new RuntimeException(e);
        }
        try {
            uploadedImage = ImageIO.read(imageStream);
        } catch (IOException e) {
            // TODO: error handling
            e.printStackTrace();
            throw new RuntimeException("Couldn't read image");
        }
        if(uploadedImage == null) {
            throw new RuntimeException("Not an image");
        }
        outputImage = ImageHelper.resizeFilling(uploadedImage, AVATAR_WIDTH, AVATAR_HEIGHT);
        File tempFile = null;
        try {
            tempFile = File.createTempFile("ashare-avatar", ".png");
            ImageIO.write(outputImage, "png", tempFile);
            reference = fileStorageHandler.saveFile(tempFile);
        } catch (FileStorageException | IOException e) {
            // TODO: error handling
            throw new RuntimeException(e.getMessage());
        } finally {
            if(tempFile != null) {
                tempFile.delete();
            }
        }
        return reference;
    }

    @Override
    public InputStream readImages(String avatarId) {
        IFileStorageHandler fileStorageHandler;
        try {
            fileStorageHandler = ServiceLocator.getService(IFileStorageHandler.class);
        } catch (ServiceNotFoundException e) {
            // TODO: error handling
            throw new RuntimeException(e.getMessage());
        }
        try {
            return fileStorageHandler.readFile(avatarId);
        } catch (FileStorageException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getImageType(String avatarId) {
        return "image/png";
    }
}
