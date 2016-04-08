package ch.avocado.share.model.data;

import java.util.List;

/**
 * Representation of a single category.
 */
public class Category {
    private String name;
    private List<String> objectIds; //TODO @kunzlio1: Fragen wie wir auf die id der Objekte kommen, bevor wir sie in die db speichern? => hier id noch anpassen

    public Category(String name, List<String> objectIds) {
        if(name == null ) throw new IllegalArgumentException("name in category");
        if(objectIds == null ) throw new IllegalArgumentException("objectIds in category");

        this.name = name;
        this.objectIds = objectIds;
    }

    public Category(String name) {
        if(name == null ) throw new IllegalArgumentException("name in category");

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getObjectIds() {
        String[] idArray = new String[objectIds.size()];
        return objectIds.toArray(idArray);
    }

    public void addObjectId(String objectId) {
        if(objectId == null ) throw new IllegalArgumentException("objectId in category addObjectId");
        objectIds.add(objectId);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (this == other) return true;
        if (other.getClass() != getClass()) return false;
        if (!name.equals(((Category)other).getName())) return false;
        return true;
    }
}
