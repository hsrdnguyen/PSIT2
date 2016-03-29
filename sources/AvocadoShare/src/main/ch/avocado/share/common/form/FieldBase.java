package ch.avocado.share.common.form;

/**
 * Created by coffeemakr on 29.03.16.
 */
abstract class FieldBase {

    protected String error;
    private String name;
    private String id;

    FieldBase(String name, String id) {
        setName(name);
        setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null) throw new IllegalArgumentException("id is null");
        this.id = id;
    }

    protected String getHtmlAttributes() {
        String attributes = FormBuilder.formatAttribute("name", getName());
        attributes += FormBuilder.formatAttribute("id", getId());
        if (getError() != null) {
            attributes += FormBuilder.formatAttribute("class", "invalid");
            attributes += FormBuilder.formatAttribute("data-input-error", getError());
        }
        return attributes;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    abstract String toHtml();
}
