package ch.avocado.share.service;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;

import java.util.List;

/**
 * Created by kunzlio1 on 23.03.2016.
 */
public interface ICategoryDataHandler {
    /**
     * updates the categories from a AccessControlObjectBase object,
     * by passing the old Object on the database and the "new"/"changed" Object.
     * @param oldAccessObject       the AccessControlObject on the database
     * @param changedAccessObject   the "new"/"changed" AccessControlObject
     * @return true if updated successfully
     */
    boolean updateAccessObjectCategories(AccessControlObjectBase oldAccessObject,
                                         AccessControlObjectBase changedAccessObject);

    /**
     * adds all categories from new created AccessControlObject to the database
     * @param accessObject the new created AccessControlObject
     * @return true if added all categories successful
     */
    boolean addAccessObjectCategories(AccessControlObjectBase accessObject);

    /**
     * checks if a Category is already added to a AccessControlObject
     * @param name                      the name of the Category
     * @param accessObjectReferenceId   the id of the AccessControlObject
     * @return true if the Category is already added to the AccessControlObject
     */
    boolean hasCategoryAssignedObject(String name, String accessObjectReferenceId);

    /**
     * return the Category by passing the Category name
     * @param name the Category name
     * @return the Category object
     */
    Category getCategoryByName(String name);

    /**
     * Returns all categories, which are assigned to AccessControlObject.
     * @param accessControlObjectId the accessObjectId, for which the categories should be returned.
     * @return the accessObject assigned categories.
     */
    List<Category> getAccessObjectAssignedCategories(String accessControlObjectId);
}
