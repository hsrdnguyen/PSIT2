package ch.avocado.share.model.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Representation of a single category.
 *
 * Category objects are immutable.
 */
public class Category implements Comparable<Category> {

    private final String name;
    private final List<String> objectIds;

    /**
     * Create a new category.
     * @param name The name of the category
     * @param objectIds The ids of the objects assigned to this category.
     */
    public Category(String name, List<String> objectIds) {
        if(name == null ) throw new NullPointerException("name in category");
        if(objectIds == null ) throw new NullPointerException("objectIds in category");

        this.name = name;
        this.objectIds = new ArrayList<>(objectIds.size());
        this.objectIds.addAll(objectIds);
    }

    public Category(String name) {
        if(name == null ) throw new NullPointerException("name in category");
        if(name.isEmpty()) throw new IllegalArgumentException("empty name");
        this.name = name;
        this.objectIds = new ArrayList<>();
    }

    /**
     * @return The name of the category
     */
    public String getName() {
        return name;
    }

    /**
     * @return The id's of the objects with this category.
     */
    public Collection<String> getObjectIds() {
        ArrayList<String> ids = new ArrayList<>(objectIds.size());
        ids.addAll(objectIds);
        return ids;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (this == other) return true;
        if (other.getClass() != getClass()) return false;
        if (!name.equals(((Category)other).getName())) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(Category category) {
        if(category == null) throw new NullPointerException("category is null");
        int result = name.toLowerCase().compareTo(category.getName().toLowerCase());
        if(result == 0) {
            return name.compareTo(category.getName());
        }
        return result;
    }
}
