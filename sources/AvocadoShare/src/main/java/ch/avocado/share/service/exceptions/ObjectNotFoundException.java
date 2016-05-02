package ch.avocado.share.service.exceptions;

import ch.avocado.share.common.constants.ErrorMessageConstants;

/**
 * Created by coffeemakr on 01.05.16.
 */
public class ObjectNotFoundException extends ServiceException {
    private final Class<?> resourceClass;
    private final String identifier;

    public ObjectNotFoundException(Class<?> resourceClass, String identifier){
        super(ErrorMessageConstants.OBJECT_NOT_FOUND);
        this.resourceClass = resourceClass;
        this.identifier = identifier;
    }
}
