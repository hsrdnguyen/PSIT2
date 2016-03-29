package ch.avocado.share.common.form;

/**
 * Created by coffeemakr on 29.03.16.
 */
enum InputType {
    TEXT, NUMBER, PASSWORD, TEXTAREA, HIDDEN;

    static InputType fromString(String string) {
        for (InputType inputType : values()) {
            if (inputType.name().toLowerCase().equals(string)) {
                return inputType;
            }
        }
        return null;
    }
}
