package ch.avocado.share.common.form;

/**
 * Created by coffeemakr on 29.03.16.
 */
public enum InputType {
    TEXT(true), NUMBER(true), PASSWORD(false), TEXTAREA(true), HIDDEN(true), EMAIL(true), FILE(false);

    private final boolean hasValue;

    InputType(boolean hasValue) {
        this.hasValue = hasValue;
    }
    
    public boolean canHaveValue() {
        return hasValue;
    }
}
