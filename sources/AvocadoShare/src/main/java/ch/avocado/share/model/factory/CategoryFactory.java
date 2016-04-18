package ch.avocado.share.model.factory;

import ch.avocado.share.model.data.Category;

/**
 * Created by kunzlio1 on 17.04.2016.
 */
public class CategoryFactory {
    public static Category getDefaultCategory(){
        return new Category("");
    }
}
