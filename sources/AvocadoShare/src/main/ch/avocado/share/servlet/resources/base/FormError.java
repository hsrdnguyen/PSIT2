package ch.avocado.share.servlet.resources.base;

import java.util.*;

/**
 * Created by coffeemakr on 14.04.16.
 */
public class FormError extends HashMap<String, List<String>> {

    public FormError() {
        super();
    }


    public FormError(Map<String, String> errorMap) {
        super();
        for(Entry<String, String> errorEntry : errorMap.entrySet()) {
            ArrayList<String> errors = new ArrayList<>(1);
            errors.add(errorEntry.getValue());
            put(errorEntry.getKey(), errors);
        }
    }

    public Collection<String> getErrorsFor(String formName) {
        List<String> errors = get(formName);
        if(errors == null) {
            return new ArrayList<>();
        }
        return errors;
    }
}
