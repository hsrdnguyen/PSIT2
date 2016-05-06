package ch.avocado.share.model.data;

import java.util.*;

/**
 * Class containing multiple categories
 */
public class CategoryList extends AbstractCollection<Category> {
    Set<Category> originalCategories;
    Set<Category> categories = null;

    public CategoryList(Collection<Category> originalCategories) {
        if(originalCategories == null) throw new NullPointerException("originalCategories is null");
        this.originalCategories = new HashSet<>(originalCategories.size());
        this.originalCategories.addAll(originalCategories);

    }

    public Set<Category> getCategories() {
        if(categories == null) {
            return new HashSet<>(originalCategories);
        } else {
            return new HashSet<>(categories);
        }
    }

    /**
     * Add a category to the list
     * @param category the category to add
     */
    @Override
    public boolean add(Category category) {
        if(category == null) throw new NullPointerException("category is null");
        Set<Category> categories = getCategories();
        boolean result = categories.add(category);
        this.categories = categories;
        return result;
    }

    public void setCategories(Collection<Category> categories) {
        if(categories == null) throw new NullPointerException("categories is null");
        this.categories = new HashSet<>(categories);
    }

    public Set<Category> getNewCategories() {
        Set<Category> categories = getCategories();
        categories.removeAll(originalCategories);
        return categories;
    }

    public Set<Category> getRemovedCategories() {
        Set<Category> categories = new HashSet<>(originalCategories);
        categories.removeAll(getCategories());
        return categories;
    }

    @Override
    public Iterator<Category> iterator() {
        if(categories == null) {
            categories = new HashSet<>(originalCategories);
        }
        return categories.iterator();
    }

    @Override
    public int size() {
        if(categories == null) {
            return originalCategories.size();
        } else {
            return categories.size();
        }
    }
}
