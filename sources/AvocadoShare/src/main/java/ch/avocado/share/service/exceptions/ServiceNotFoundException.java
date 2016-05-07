package ch.avocado.share.service.exceptions;

import ch.avocado.share.service.exceptions.ServiceException;

/**
 * Created by bergm on 15/03/2016.
 */
public class ServiceNotFoundException extends ServiceException {

    private final String service;
    private final String source;

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
