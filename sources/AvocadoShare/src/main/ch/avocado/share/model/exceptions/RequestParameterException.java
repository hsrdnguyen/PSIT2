package ch.avocado.share.model.exceptions;

/**
 * Created by coffeemakr on 22.03.16.
 */
public class RequestParameterException extends Exception {
    private final String parameter;
    private final String description;

    public RequestParameterException(String parameter, String description) {
        this.parameter = parameter;
        this.description = description;
    }

    public String getParameter() {
        return parameter;
    }

    public String getDescription() {
        return description;
    }
}