package ch.avocado.share.service.exceptions;

import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.model.data.AccessControlObjectBase;

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

    public ObjectNotFoundException(Class<?> resourceClass, long identifier) {
        this(resourceClass, Long.toString(identifier));
    }
}
