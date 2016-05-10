package ch.avocado.share.common.form;

/**
 * Created by coffeemakr on 06.05.16.
 */
public class IllegalFieldTypeException extends IllegalArgumentException{
    public IllegalFieldTypeException(Class s) {
        super("Field has unknown type: " + s);
    }
}
