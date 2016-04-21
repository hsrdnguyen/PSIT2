package ch.avocado.share.model.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bergm on 15/03/2016.
 */
public abstract class AccessControlObjectBase extends Model{

    private String id;
    private List<Category> categories;
    private Date creationDate;
    private float rating;
    private String ownerId;
    private String description;

    /**
     * Constructor
     * @param id
     * @param categories A list of categories or null if there are no categories.
     * @param creationDate
     * @param rating
     * @param ownerId
     * @param description
     */
    public AccessControlObjectBase(String id, List<Category> categories, Date creationDate, float rating, String ownerId, String description) {
        this.id = id;
        setCategories(categories);
        setCreationDate(creationDate);
        this.rating = rating;
        setOwnerId(ownerId);
        setDescription(description);
    }

    /**
     * @return the unique identifier of this object.
     */
    public String getId() {
        return id;
    }

    /**
     * @todo: change id to number
     * @param id The unique identifier of this object.
     */
    public void setId(String id) {
        if (id == null) throw new IllegalArgumentException("id is null");
        this.id = id;
    }

    /**
     * @return The categories assigned to this object.
     */
    public List<Category> getCategories() {
        return categories;
    }

    /**
     * @param categories The categories assigned to this object
     */
    public void setCategories(List<Category> categories) {
        if(categories == null) {
            categories = new ArrayList<>();
        }
        this.categories = categories;
        setDirty(true);
    }

    /**
     * @return The creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate Set the creation date
     */
    public void setCreationDate(Date creationDate) {
        if(creationDate == null) throw new IllegalArgumentException("creationDate is null");
        this.creationDate = creationDate;
        setDirty(true);
    }

    /**
     * @return The average rating
     */
    public float getRating() {
        return rating;
    }

    /**
     * @param rating The average rating
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     * @return The if of the owner.
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId Identifier of the owner object.
     */
    public void setOwnerId(String ownerId) {
        // if(ownerId == null) throw new IllegalArgumentException("ownerId is null");
        this.ownerId = ownerId;
        setDirty(true);
    }

    /**
     * @return The description of the object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description of the object.
     */
    public void setDescription(String description) {
        if(description == null) throw new IllegalArgumentException("description is null");
        this.description = description;
        setDirty(true);
    }

    /**
     * @return A human readable name.
     */
    public abstract String getReadableName();

}
