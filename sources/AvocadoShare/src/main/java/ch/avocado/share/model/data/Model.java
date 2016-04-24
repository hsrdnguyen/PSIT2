package ch.avocado.share.model.data;

import java.util.HashMap;
import java.util.Map;



public class Model {

    private Map<String, String> fieldErrors = new HashMap<>();

    private boolean dirty = false;

    public void addFieldError(String field, String error) {
        fieldErrors.put(field, error);
    }

    public void clearErrors() {
        fieldErrors = new HashMap<>();
    }

    public boolean hasErrors() {
        return !isValid();
    }

    public boolean isValid() {
        return fieldErrors.isEmpty();
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }


    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }


}