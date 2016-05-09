package ch.avocado.share.service.exceptions;

/**
 * Base class for all service exceptions
 */
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(Exception e) {super(e);}
}
