package ch.avocado.share.model.data;

import java.util.Collection;
import java.util.Date;

/**
 * Abstract class for all AccessControlObjectBase objects which can access another object
 * @todo Rename appropriate
 */
public abstract class AccessIdentity extends AccessControlObjectBase{
    public AccessIdentity(String id, Collection<Category> categories, Date creationDate, Rating rating, String ownerId, String description) {
        super(id, categories, creationDate, rating, ownerId, description);
    }
}
