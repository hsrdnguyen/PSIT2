package ch.avocado.share.model.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Group model
 */
public class Group extends AccessIdentity implements Serializable {

    private String name;

    /**
     * Create a new Group.
     * @param id The unqiue identifier.
     * @param categories A list of all categories
     * @param creationDate The date of the creation
     * @param rating Average rating
     * @param ownerId Identifier of the owner.
     * @param description A description of the group
     * @param name The name of the group
     */
    public Group(String id,
                 List<Category> categories,
                 Date creationDate,
                 float rating,
                 String ownerId,
                 String description,
                 String name) {
        super(id, categories, creationDate, rating, ownerId, description);
        if(name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    /**
     * @return the name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name of the group
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getReadableName() {
        return getName();
    }
}
