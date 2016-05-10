package ch.avocado.share.model.exceptions;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.service.exceptions.*;

import static ch.avocado.share.common.HttpStatusCode.*;

/**
 * Exception for Servlet intern errors.
 * <p>
 * This exception is used in Servlets to handle different kind of exceptions and
 * makes is easier to create an http result for every error.
 * Exceptions of this kind should normally not be thrown and catched in two different classes.
 */
public class HttpServletException extends Exception {
    private final HttpStatusCode statusCode;

    /**
     * Create a new {@link HttpServletException}.
     *
     * @param statusCode  The Http status code which fits the error
     * @param description The description shown to the user
     */
    public HttpServletException(HttpStatusCode statusCode, String description) {
        super(description);
        this.statusCode = statusCode;
    }

    /**
     * Create a new {@link HttpServletException}.
     *
     * @param statusCode    The Http status code which fits the error
     * @param description   The description shown to the user
     * @param originalCause The exception which caused the error
     */
    public HttpServletException(HttpStatusCode statusCode, String description, Throwable originalCause) {
        super(description, originalCause);
        this.statusCode = statusCode;
    }

    /**
     * Returns the error message appropriate for the exception.
     *
     * @param exception the service exception
     * @return the error message
     */
    private static String getErrorMessageFromServiceException(ServiceException exception) {
        if (exception instanceof ObjectNotFoundException) {
            return ErrorMessageConstants.OBJECT_NOT_FOUND;
        } else {
            if (exception instanceof DataHandlerException) {
                return ErrorMessageConstants.DATAHANDLER_EXPCEPTION;
            } else if (exception instanceof ServiceNotFoundException) {
                return ErrorMessageConstants.SERVICE_NOT_FOUND + ((ServiceNotFoundException) exception).getService();
            } else if (exception instanceof FileStorageException) {
                return ErrorMessageConstants.FILE_STORAGE_EXCEPTION;
            } else {
                return ErrorMessageConstants.UNKNOWN_SERVICE_EXCEPTION;
            }
        }
    }

    /**
     * Returns the http status code appropriate for the given service excpetion.
     *
     * @param exception the service exception
     * @return The status code
     */
    private static HttpStatusCode getStatusCodeFromServiceException(ServiceException exception) {
        if (exception == null) throw new NullPointerException("exception is null");
        if (exception instanceof ObjectNotFoundException) {
            return NOT_FOUND;
        } else {
            return INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Create a new excpetion based on any {@link ServiceException}.
     *
     * @param exception The service exception.
     */
    public HttpServletException(ServiceException exception) {
        this(getStatusCodeFromServiceException(exception), getErrorMessageFromServiceException(exception), exception);
    }

    /**
     * Create a new excpetion based on a {@link AccessDeniedException}.
     *
     * @param e the access denied exception
     */
    public HttpServletException(AccessDeniedException e) {
        this(HttpStatusCode.FORBIDDEN, ErrorMessageConstants.ACCESS_DENIED, e);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

}
