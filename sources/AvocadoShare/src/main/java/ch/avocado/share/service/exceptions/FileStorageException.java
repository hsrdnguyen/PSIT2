package ch.avocado.share.service.exceptions;

import java.io.IOException;

/**
 * Created by coffeemakr on 29.03.16.
 */
public class FileStorageException extends ServiceException {
    public FileStorageException(String description, Throwable originalCause){
        super(description, originalCause);
    }

    public FileStorageException(ServiceNotFoundException e){
        super(e);
    }

    public FileStorageException(String description){
        super(description);
    }

    public FileStorageException(IOException e) {
        super(e);
    }
}
