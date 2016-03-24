package ch.avocado.share.service.Mock;

import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;
import ch.avocado.share.service.ICategoryDataHandler;

/**
 * Created by kunzlio1 on 23.03.2016.
 */
public class CategoryDataHandlerMock implements ICategoryDataHandler{
    @Override
    public Category getCategory(String Id) {
        return null;
    }

    @Override
    public boolean addCategory(Category category, String accessObjectReferenceId) {
        return false;
    }

    @Override
    public boolean updateCategory(Category category) {
        return false;
    }

    @Override
    public boolean deleteCategory(Category category) {
        return false;
    }

    @Override
    public Category getCategoryByName(String name) {
        return null;
    }

    @Override
    public File[] getCategoryAssignedFiles(String id) {
        return new File[0];
    }
}
