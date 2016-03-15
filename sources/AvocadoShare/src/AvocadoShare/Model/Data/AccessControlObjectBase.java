package AvocadoShare.Model.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by bergm on 15/03/2016.
 */
public abstract class AccessControlObjectBase {

    private String id;
    private List<Category> categories;
    private Date creationDate;
    private Rating rating;
    private String ownerId;
    private String description;

    public AccessControlObjectBase(String id, List<Category> categories, Date creationDate, Rating rating, String ownerId, String description) {
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
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
