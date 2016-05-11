package ch.avocado.share.common.util;

import java.util.*;

/**
 * Created by coffeemakr on 11.05.16.
 */
public class ChangeTrackingSet<E> extends AbstractSet<E> {
    Set<E> originalCategories;
    Set<E> current = null;

    public ChangeTrackingSet(Collection<E> originalCategories) {
        super();
        this.originalCategories = new HashSet<>(originalCategories);
    }

    /**
     * Add a element to the list
     * @param element the element to add
     */
    @Override
    public boolean add(E element) {
        if(element == null) throw new NullPointerException("element is null");
        Set<E> categories = new HashSet<>(this);
        boolean result = categories.add(element);
        this.current = categories;
        return result;
    }

    /**
     * All current which should be in the list. If these current differ from the
     * original current provided in the constructor the changes can be accessed through
     * {@link #getNewSet()} and {@link #getRemovedSet()}
     * @param current all current elements which should be in the list.
     */
    public void setCurrentSet(Collection<E> current) {
        if(current == null) throw new NullPointerException("current is null");
        this.current = new HashSet<>(current);
    }

    /**
     * @return A set of all new elements added by {@link #add(E)} or {@link #setCurrentSet(Collection)}
     */
    public Set<E> getNewSet() {
        Set<E> categories = new HashSet<>(this);
        categories.removeAll(originalCategories);
        return categories;
    }

    /**
     * @return A set of all removed elements added by {@link #remove(Object)} or {@link #setCurrentSet(Collection)}
     */
    public Set<E> getRemovedSet() {
        Set<E> categories = new HashSet<>(originalCategories);
        categories.removeAll(this);
        return categories;
    }

    @Override
    public Iterator<E> iterator() {
        if(current == null) {
            current = new HashSet<>(originalCategories);
        }
        return current.iterator();
    }

    @Override
    public int size() {
        if(current == null) {
            return originalCategories.size();
        } else {
            return current.size();
        }
    }
}
