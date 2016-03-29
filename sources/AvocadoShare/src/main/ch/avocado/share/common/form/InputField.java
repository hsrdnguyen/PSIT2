package ch.avocado.share.common.form;

import ch.avocado.share.common.Encoder;

/**
 * Created by coffeemakr on 29.03.16.
 */
class InputField extends FieldBase {
    private String value = "";
    private String type;

    public InputField(String name, String id, String type) {
        super(name, id);
        setType(type);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null) throw new IllegalArgumentException("type is null");
        this.type = type;
    }

    @Override
    public String toHtml() {
        String attributes = getHtmlAttributes();
        if (InputType.fromString(type) == InputType.TEXTAREA) {
            return "<textarea " + attributes + ">" + Encoder.forHtml(getValue()) + "</textarea>\n";
        } else {
            attributes += FormBuilder.formatAttribute("type", type.toLowerCase());
            attributes += FormBuilder.formatAttribute("value", getValue());
            return "<input " + attributes + "/>";
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null) throw new IllegalArgumentException("value is null");
        this.value = value;
    }
}
