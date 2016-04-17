package ch.avocado.share.common;

/**
 * Created by coffeemakr on 27.03.16.
 */
public enum HttpMethod {

    POST, GET, PUT, PATCH, DELETE;

    static public HttpMethod fromString(String methodAsString) {
        methodAsString = methodAsString.toUpperCase();
        for(HttpMethod method: values()) {
            if(method.name().equals(methodAsString)) {
               return method;
            }
        }
        return null;
    }
}
