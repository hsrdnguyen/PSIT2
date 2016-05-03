package ch.avocado.share.common;

import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.Category;
import ch.avocado.share.model.data.CategoryList;
import ch.avocado.share.servlet.resources.base.DetailViewConfig;
import org.omg.IOP.ENCODING_CDR_ENCAPS;

import java.time.temporal.ValueRange;
import java.util.ArrayList;
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
        CategoryList categories = object.getCategoryList();
        ArrayList<Category> sortedCategories = new ArrayList<>(categories.size());
        sortedCategories.addAll(categories);
        sortedCategories.sort(Category::compareTo);
        return sortedCategories;
    }

    public String getObjectIdField() {
        return "<input type=\"hidden\" name=\"id\" value=\"" + Encoder.forHtmlAttribute(object.getId()) + "\" />";
    }
}
