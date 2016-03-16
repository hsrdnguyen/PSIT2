package ch.avocado.share.model.data;

import java.util.Date;
import java.util.List;

/**
 * Created by bergm on 15/03/2016.
 */
public class Group extends AccessControlObjectBase {

    private String name;

    public Group(String id, List<Category> categories, Date creationDate, Rating rating, String ownerId, String description, String name) {
        super(id, categories, creationDate, rating, ownerId, description);
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
    }
}
