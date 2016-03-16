package ch.avocado.share.model.exceptions;

/**
 * Created by bergm on 15/03/2016.
 */
public class ServiceNotFoundException extends Exception {

    public ServiceNotFoundException(String service, String source) {
        super("Service: " + service + " not found in " + source);
    }

}
