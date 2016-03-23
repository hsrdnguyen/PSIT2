package ch.avocado.share.model.data;

import java.util.Date;
import java.util.List;

/**
 * Created by bergm on 15/03/2016.
 */
public abstract class AccessControlObjectBase {

    private String id;
    private List<Category> categories;
    private Date creationDate;
    private float rating;
    private String ownerId;
    private String description;
    private boolean dirty;

    public AccessControlObjectBase(String id, List<Category> categories, Date creationDate, float rating, String ownerId, String description) {
        this.id = id;
        this.categories = categories;
        this.creationDate = creationDate;
        this.rating = rating;
        this.ownerId = ownerId;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null) throw new IllegalArgumentException("id is null");
        this.id = id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        setDirty(true);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        setDirty(true);
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        setDirty(true);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        setDirty(true);
    }

    public boolean isDirty() {
        return dirty;
    }

    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}
