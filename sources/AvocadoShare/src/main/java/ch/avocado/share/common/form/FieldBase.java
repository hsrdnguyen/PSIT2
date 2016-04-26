package ch.avocado.share.common.form;

/**
 * Base class for form fields.
 */
abstract class FieldBase {

    private String error;
    private String name;
    private String id;
    private String htmlClass;
    private String htmlInvalidClass;
    private boolean required = true;
    FieldBase(String name, String id) {
        setName(name);
        setId(id);
        htmlClass = "";
        htmlInvalidClass = "invalid";
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

    private String concatHtmlClasses(String first, String second) {
        if(first == null) throw new IllegalArgumentException("first is null");
        if(second == null) throw new IllegalArgumentException("second is null");
        if(first.isEmpty()) {
            return second;
        } else if(second.isEmpty()) {
            return first;
        } else {
            return first + " " + second;
        }
    }

    private String getClassAttribute() {
        String htmlClass = getHtmlClass();
        if(getError() != null) {
            htmlClass = concatHtmlClasses(htmlClass, getHtmlInvalidClass());
        }
        if(!htmlClass.isEmpty()) {
            htmlClass = FormBuilder.formatAttribute("class", htmlClass);
        }
        return htmlClass;
    }

    protected String getHtmlAttributes() {
        String attributes = FormBuilder.formatAttribute("name", getName());
        attributes += FormBuilder.formatAttribute("id", getId());
        if (getError() != null) {
            attributes += FormBuilder.formatAttribute("data-input-error", getError());
        }
        attributes += getClassAttribute();
        if(isRequired()) {
            attributes += "required ";
        }
        return attributes;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getHtmlClass() {
        return htmlClass;
    }

    public void setHtmlClass(String htmlClass) {
        if(htmlClass == null) throw new IllegalArgumentException("htmlClass is null");
        this.htmlClass = htmlClass;
    }

    public String getHtmlInvalidClass() {
        return htmlInvalidClass;
    }

    public void setHtmlInvalidClass(String htmlInvalidClass) {
        this.htmlInvalidClass = htmlInvalidClass;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    abstract public String toHtml();

    @Override
    public String toString() {
        return toHtml();
    }
}
