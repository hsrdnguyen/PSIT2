package ch.avocado.share.service;

import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.File;

/**
 * Created by kunzlio1 on 23.03.2016.
 */
public interface ICategoryDataHandler {
    Category getCategory(String id);
    boolean addCategory(Category category, String accessObjectReferenceId);
    boolean updateCategory(Category category);
    boolean deleteCategory(Category category);
    Category getCategoryByName(String name);
    File[] getCategoryAssignedFiles(String id);
}
