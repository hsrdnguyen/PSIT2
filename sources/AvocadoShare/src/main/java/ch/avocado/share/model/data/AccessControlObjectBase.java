package ch.avocado.share.model.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Base class for all access control objects.
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
     * @param id The identifier of the object.
     * @param categories A list of categories or null if there are no categories.
     * @param creationDate The date of the object creation
     * @param rating The average of all ratings
     * @param ownerId The identifier of the owner
     * @param description Description of the object
     */
    public AccessControlObjectBase(String id, List<Category> categories, Date creationDate, float rating, String ownerId, String description) {
        this.id = id;
        setCategories(categories);
        setCreationDate(creationDate);
        this.rating = rating;
        setOwnerId(ownerId);
        setDescription(description);
        setDirty(false);
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
        if (id.isEmpty()) throw new IllegalArgumentException("id is empty");
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
        if(categories == null || categories.isEmpty()) {
            this.categories = new ArrayList<>();
        } else {
            this.categories = new ArrayList<>(categories.size());
            this.categories.addAll(categories);
        }
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
        if(!creationDate.equals(this.creationDate)) {
            this.creationDate = new Date(creationDate.getTime());
            setDirty(true);
        }
    }

    /**
     * @return The average rating
     */
    public float getRating() {
        return rating;
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
        if(ownerId != null && ownerId.isEmpty()) throw new IllegalArgumentException("ownerId is empty");
        if(!Objects.equals(this.ownerId, ownerId)) {
            this.ownerId = ownerId;
            setDirty(true);
        }
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
