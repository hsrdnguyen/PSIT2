package ch.avocado.share.service.exceptions;

import java.io.IOException;

/**
 * Created by coffeemakr on 29.03.16.
 */
public class FileStorageException extends ServiceException {
    public FileStorageException(String message){
        super(message);
    }

    public FileStorageException(IOException e) {
        super(e.getMessage());
    }
}
