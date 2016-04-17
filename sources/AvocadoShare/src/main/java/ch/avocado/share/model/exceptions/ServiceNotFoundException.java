package ch.avocado.share.model.exceptions;

/**
 * Created by bergm on 15/03/2016.
 */
public class ServiceNotFoundException extends Exception {

    private String service;
    private String source;

    public ServiceNotFoundException(String service, String source) {
        super("Service: " + service + " not found in " + source);
        this.service = service;
        this.source = source;
    }

    public String getService() {
        return service;
    }

    public String getSource() {
        return source;
    }
}
