package ch.avocado.share.service.exceptions;

/**
 * Base class for all service exceptions
 */
public class ServiceException extends Exception {

    public ServiceException(String description, Throwable originalCause) {
        super(description, originalCause);
    }

    public ServiceException(String description){
        super(description);
    }

    public ServiceException(Exception e){
        super(e.getMessage(), e.getCause());
    }
    public ServiceException(Exception e) {super(e);}
}
