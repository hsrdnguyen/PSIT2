package ch.avocado.share.service.exceptions;

public class DataHandlerException extends ServiceException {
    public DataHandlerException(String message) {
        super(message);
    }

    public DataHandlerException(Exception e) {
        super(e.getMessage());
    }
}
