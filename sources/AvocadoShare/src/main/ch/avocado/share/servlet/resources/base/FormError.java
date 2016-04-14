package ch.avocado.share.servlet.resources.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by coffeemakr on 14.04.16.
 */
class FormError extends HashMap<String, Collection<String>> {

    public FormError(Map<String, String> errorMap) {
        for(Entry<String, String> errorEntry : errorMap.entrySet()) {
            ArrayList<String> errors = new ArrayList<>(1);
            errors.add(errorEntry.getValue());
            put(errorEntry.getKey(), errors);
        }
    }

    public Collection<String> getErrorsFor(String formName) {
        Collection<String> errors = get(formName);
        if(errors == null) {
            return new ArrayList<>();
        }
        return errors;
    }
}
