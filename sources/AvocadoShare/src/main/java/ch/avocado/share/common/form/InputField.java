package ch.avocado.share.common.form;

import ch.avocado.share.common.Encoder;

public class InputField extends FieldBase {
    private String value;
    private InputType type;

    public InputField(String name, String id, InputType type) {
        this(name, id, type, "");
    }

    public InputField(String name, String id, InputType type, String value) {
        super(name, id);
        setType(type);
        setValue(value);
    }

    /**
     * @return type input type
     */
    public InputType getType() {
        return type;
    }

    /**
     * @param type the input type
     */
    public void setType(InputType type) {
        if (type == null) throw new NullPointerException("type is null");
        this.type = type;
    }

    @Override
    public String toHtml() {
        String attributes = getHtmlAttributes();
        if (type == InputType.TEXTAREA) {
            return "<textarea " + attributes + ">" + Encoder.forHtml(getValue()) + "</textarea>\n";
        } else {
            attributes += FormBuilder.formatAttribute("type", type.name().toLowerCase());
            attributes += FormBuilder.formatAttribute("value", getValue());
            return "<input " + attributes + "/>";
        }
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value
     */
    public void setValue(String value) {
        if (value == null) throw new NullPointerException("value is null");
        this.value = value;
    }
}
