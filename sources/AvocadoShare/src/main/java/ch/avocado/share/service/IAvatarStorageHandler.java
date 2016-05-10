package ch.avocado.share.service;

import ch.avocado.share.service.exceptions.FileStorageException;
import org.apache.commons.fileupload.FileItem;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handles the storage of avatar images
 */
public interface IAvatarStorageHandler {

    /**
     * @param avatarImage the correctly resisized avatar image
     * @return The avatar id
     */
    String storeAvatar(InputStream avatarImage) throws FileStorageException;

    /**
     * @param avatarId The identifier of the avatar
     * @return The readable image content
     */
    InputStream readImages(String avatarId) throws FileStorageException;

    /**
     * @param avatarId The identifier of the avatar
     * @return The mime type of the image retrieved
     */
    String getImageType(String avatarId);
}
