package ch.avocado.share.common;

import ch.avocado.share.common.util.ChangeTrackingSet;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.servlet.resources.base.DetailViewConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by coffeemakr on 03.05.16.
 */
public class CategoryViewHelper {
    private final DetailViewConfig viewConfig;
    private final AccessControlObjectBase object;

    public CategoryViewHelper(DetailViewConfig viewConfig) {
        this.viewConfig = viewConfig;
        this.object = (AccessControlObjectBase) viewConfig.getObject();
    }

    public List<Category> getSortedCategoryList() {
        ChangeTrackingSet<Category> categories = object.getCategoryList();
        ArrayList<Category> sortedCategories = new ArrayList<>(categories.size());
        sortedCategories.addAll(categories);
        sortedCategories.sort(new Comparator<Category>() {
            @Override
            public int compare(Category category, Category category2) {
                return category.compareTo(category2);
            }
        });
        return sortedCategories;
    }

    public String getObjectIdField() {
        return "<input type=\"hidden\" name=\"id\" value=\"" + Encoder.forHtmlAttribute(object.getId()) + "\" />";
    }
}
