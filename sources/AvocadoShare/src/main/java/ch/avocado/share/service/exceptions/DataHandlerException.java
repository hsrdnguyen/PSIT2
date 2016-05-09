package ch.avocado.share.service.exceptions;

public class DataHandlerException extends ServiceException {
    public DataHandlerException(String description, Throwable originalCause) {
        super(description, originalCause);
    }

    public DataHandlerException(Exception e) {
        super(e);
    }

    public DataHandlerException(String description){
        super(description);
    }
}
