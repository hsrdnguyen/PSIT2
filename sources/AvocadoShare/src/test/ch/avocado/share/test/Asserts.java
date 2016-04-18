package ch.avocado.share.test;

import ch.avocado.share.model.data.Category;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by coffeemakr on 18.04.16.
 */
public class Asserts {
    public static void assertCategoriesEquals(List<Category> expected, List<Category> actual) {
        List<String> expectedNames = getCategoryNames(expected);
        List<String> actualNames = getCategoryNames(actual);
        for (Iterator<String> iterator = expectedNames.iterator(); iterator.hasNext(); ) {
            String expectedName = iterator.next();
            assertTrue("Category \"" + expectedName + "\" not in actual categories", actualNames.contains(expectedName));
            iterator.remove();
        }
        if(!expectedNames.isEmpty()) {
            String missingNames = "";
            for(String name: expectedNames) {
                missingNames += "\"" + name + "\" ";
            }
            fail("Missing categories: " + missingNames);
        }
    }

    private static List<String> getCategoryNames(List<Category> categories) {
        if(categories == null) throw new IllegalArgumentException("categories is null");
        ArrayList<String> names = new ArrayList<>(categories.size());
        for(Category category: categories) {
            names.add(category.getName());
        }
        return names;
    }
}
