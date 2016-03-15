package ch.avocado.share.model.data;

/**
 * Created by bergm on 15/03/2016.
 */
public class Category {
    private String name;

    public Category(String name) {
        if(name == null) throw new IllegalArgumentException("name in category");

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
