package ch.avocado.share.service.Impl;

import ch.avocado.share.common.ImageHelper;
import ch.avocado.share.common.ServiceLocator;
import ch.avocado.share.service.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.IAvatarStorageHandler;
import ch.avocado.share.service.IFileStorageHandler;
import ch.avocado.share.service.exceptions.FileStorageException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class AvatarFileStorageHandler implements IAvatarStorageHandler {

    public static final int MAX_IMAGE_SIZE = 1024 * 3;
    public static final int AVATAR_WIDTH = 128;
    public static final int AVATAR_HEIGHT = 128;

    @Override
    public String storeAvatar(InputStream imageStream) throws FileStorageException {
        IFileStorageHandler fileStorageHandler;
        String reference;
        BufferedImage uploadedImage, outputImage;

        try {
            fileStorageHandler = ServiceLocator.getService(IFileStorageHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new FileStorageException(e.getMessage());
        }
        try {
            uploadedImage = ImageIO.read(imageStream);
        } catch (IOException e) {
            throw new FileStorageException(e);
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
        } catch (IOException e) {
            throw new FileStorageException(e);
        } finally {
            if(tempFile != null) {
                if(!tempFile.delete()) {
                    System.out.println("File couldn't be deleted: " + tempFile);
                }
            }
        }
        return reference;
    }

    @Override
    public InputStream readImages(String avatarId) throws FileStorageException {
        IFileStorageHandler fileStorageHandler;
        try {
            fileStorageHandler = ServiceLocator.getService(IFileStorageHandler.class);
        } catch (ServiceNotFoundException e) {
            throw new FileStorageException(e.getMessage());
        }
        return fileStorageHandler.readFile(avatarId);
    }

    @Override
    public String getImageType(String avatarId) {
        return "image/png";
    }
}
