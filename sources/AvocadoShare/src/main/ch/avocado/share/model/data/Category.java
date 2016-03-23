package ch.avocado.share.model.data;

/**
 * Created by bergm on 15/03/2016.
 */
public class Category {
    private String name;
    private String id; //TODO @kunzlio1: Fragen wie wir auf die id der Objekte kommen, bevor wir sie in die db speichern? => hier id noch anpassen

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
