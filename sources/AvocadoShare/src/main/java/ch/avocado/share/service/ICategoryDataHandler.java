package ch.avocado.share.service;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.service.exceptions.DataHandlerException;
import ch.avocado.share.service.exceptions.ObjectNotFoundException;

import java.util.List;
import java.util.Set;

/**
 * Created by kunzlio1 on 23.03.2016.
 */
public interface ICategoryDataHandler {
    /**
     * updates the categories from a AccessControlObjectBase object,
     * by passing the old Object on the database and the "new"/"changed" Object.
     * @param changedAccessObject   the "new"/"changed" AccessControlObject
     */
    void updateAccessObjectCategories(AccessControlObjectBase changedAccessObject) throws DataHandlerException, ObjectNotFoundException;

    /**
     * adds all categories from new created AccessControlObject to the database
     * @param accessObject the new created AccessControlObject
     */
    void addAccessObjectCategories(AccessControlObjectBase accessObject) throws DataHandlerException;

    /**
     * checks if a Category is already added to a AccessControlObject
     * @param name                      the name of the Category
     * @param accessObjectReferenceId   the id of the AccessControlObject
     * @return true if the Category is already added to the AccessControlObject
     */
    boolean hasCategoryAssignedObject(String name, String accessObjectReferenceId) throws DataHandlerException;

    /**
     * return the Category by passing the Category name
     * @param name the Category name
     * @return the Category object
     */
    Category getCategoryByName(String name) throws DataHandlerException, ObjectNotFoundException;

    /**
     * Returns all categories, which are assigned to AccessControlObject.
     * @param accessControlObjectId the accessObjectId, for which the categories should be returned.
     * @return the accessObject assigned categories. If the object could not be found am empty list is returned.
     */
    List<Category> getAccessObjectAssignedCategories(String accessControlObjectId) throws DataHandlerException;
}
