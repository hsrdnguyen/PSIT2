package ch.avocado.share.controller;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.ResponseHelper;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.exceptions.HttpServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

public class ErrorDisplayBean implements Serializable{
    private Throwable exception = null;
    private HttpServletRequest request;
    private HttpServletResponse response;

    public Throwable getException() {
        return this.exception;
    }

    public String getDescription() {
        if(exception != null && exception instanceof HttpServletException) {
            return exception.getMessage();
        } else {
            String message = (String) request.getAttribute("javax.servlet.error.message");
            if (message != null && getStatusCode().equals(HttpStatusCode.NOT_FOUND) && message.equals(request.getAttribute("javax.servlet.error.request_uri"))) {
                return ErrorMessageConstants.PATH_NOT_FOUND + message;
            } else if(message == null && exception != null) {
                return ErrorMessageConstants.ERROR_INTERNAL_SERVER + ": " + exception.getClass().getName();
            }
        }
        return "Unbekannter Fehler";
    }

    public HttpStatusCode getStatusCode() {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if(statusCode == null) {
            statusCode = response.getStatus();
        }
        return HttpStatusCode.fromCode(statusCode);
    }

    public void setRequest(HttpServletRequest request) {
        if(request == null) throw new NullPointerException("request is null");
        this.request = request;
        Throwable exception = (Throwable) request.getAttribute(ResponseHelper.EXCEPTION_ATTRIBUTE);
        if(exception != null) {
            this.exception = exception;
        }
    }

    public void setException(Throwable exception) {
        if(exception == null) throw new NullPointerException("exception is null");
        this.exception = exception;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
