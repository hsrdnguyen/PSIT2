package ch.avocado.share.model.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Group model
 */
public class Group extends AccessIdentity implements Serializable {

    private String name;
    private List<String> memberIds;

    /**
     * Create a new Group.
     * @param id The unqiue identifier.
     * @param categories A list of all categories
     * @param creationDate The date of the creation
     * @param rating Average rating
     * @param ownerId Identifier of the owner.
     * @param description A description of the group
     * @param name The name of the group
     * @param memberIds List of all member ids
     */
    public Group(String id,
                 List<Category> categories,
                 Date creationDate,
                 float rating,
                 String ownerId,
                 String description,
                 String name,
                 List<String> memberIds) {
        super(id, categories, creationDate, rating, ownerId, description);
        if(name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
        if(memberIds == null) throw new IllegalArgumentException("member is null");
        this.memberIds = memberIds;
    }

    /**
     * @return the name of the group
     */
    public String getName() {
        return name;
    }

    /**
     * Set the group name.
     * @param name
     */
    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        if(!this.name.equals(name)) {
            this.name = name;
            setDirty(true);
        }
    }

    /**
     * @return A list of all members of this group.
     */
    public String[] getMemberIds() {
        String[] idArray = new String[memberIds.size()];
        return memberIds.toArray(idArray);
    }
}
