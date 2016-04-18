package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.service.ICategoryDataHandler;

import java.util.List;

/**
 * Created by kunzlio1 on 23.03.2016.
 */
public class CategoryDataHandlerMock implements ICategoryDataHandler{

    @Override
    public boolean updateAccessObjectCategories(AccessControlObjectBase oldAccessObject, AccessControlObjectBase changedAccessObject) {
        return false;
    }

    @Override
    public boolean addAccessObjectCategories(AccessControlObjectBase accessObject) {
        return false;
    }

    @Override
    public boolean hasCategoryAssignedObject(String name, String accessObjectReferenceId) {
        return false;
    }

    @Override
    public Category getCategoryByName(String name) {
        return null;
    }

    @Override
    public List<Category> getAccessObjectAssignedCategories(String accessControlObjectId) {
        return null;
    }
}
