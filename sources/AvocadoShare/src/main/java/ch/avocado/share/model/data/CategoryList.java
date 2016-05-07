package ch.avocado.share.model.data;

import java.util.*;

/**
 * Class containing multiple categories.
 * This class is useful to track if categories has been added or deleted.
 */
public class CategoryList extends AbstractCollection<Category> {
    Set<Category> originalCategories;
    Set<Category> categories = null;

    /**
     * Create a new CategoryList object from a collection of categories.
     * We assume that all categories provided are already added to the list.
     * @param originalCategories The categories
     */
    public CategoryList(Collection<Category> originalCategories) {
        if(originalCategories == null) throw new NullPointerException("originalCategories is null");
        this.originalCategories = new HashSet<>(originalCategories.size());
        this.originalCategories.addAll(originalCategories);

    }

    /**
     * @return A set of all current categories.
     */
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

    /**
     * All categories which should be in the list. If these categories differ from the
     * original categories provided in the constructor the changes can be accessed through
     * {@link #getNewCategories()} and {@link #getRemovedCategories()}
     * @param categories all categories which should be in the list.
     */
    public void setCategories(Collection<Category> categories) {
        if(categories == null) throw new NullPointerException("categories is null");
        this.categories = new HashSet<>(categories);
    }

    /**
     * @return A set of all new categories added by {@link #add(Category)} or {@link #setCategories(Collection)}
     */
    public Set<Category> getNewCategories() {
        Set<Category> categories = getCategories();
        categories.removeAll(originalCategories);
        return categories;
    }

    /**
     * @return A set of all removed categories added by {@link #remove(Object)} or {@link #setCategories(Collection)}
     */
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
