package ch.avocado.share.model.data;

import java.util.Date;
import java.util.List;

/**
 * Module model
 */
public class Module extends AccessControlObjectBase {

    private String name;

    public Module(String id, List<Category> categories, Date creationDate, float rating, String ownerId, String description, String name) {
        super(id, categories, creationDate, rating, ownerId, description);
        setName(name);
    }

    /**
     * @return The name of the module
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name of the module
     */
    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
    }


    @Override
    public String getReadableName() {
        return getName();
    }
}
