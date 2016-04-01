package ch.avocado.share.common.form;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.HttpMethod;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.EmailAddress;
import ch.avocado.share.model.data.UserPassword;
import ch.avocado.share.model.exceptions.FormBuilderException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class FormBuilder {
    private Map<String, String> formErrors;
    private String action;
    private String encodingType = null;
    private AccessControlObjectBase object;
    private Class<? extends AccessControlObjectBase> objectClass;
    private String htmlInputClass = "form-control";
    private String idPrefix = null;
    private Map<String, String> readableFieldNames;

    public FormBuilder(Class<? extends AccessControlObjectBase> resourceClass, Map<String, String> formErrors) {
        this(null, resourceClass, formErrors);
    }

    static private AccessControlObjectBase checkObjectNotNull(AccessControlObjectBase object) {
        if(object == null) throw new IllegalArgumentException("object is null");
        return object;
    }

    public FormBuilder(AccessControlObjectBase object, Map<String, String> formErrors) {
        this(checkObjectNotNull(object), checkObjectNotNull(object).getClass(), formErrors);
    }

    public FormBuilder(AccessControlObjectBase object, Class<? extends AccessControlObjectBase> resourceClass, Map<String, String> formErrors) {
        if (resourceClass == null) throw new IllegalArgumentException("objectClass is null");
        if (formErrors == null) {
            formErrors = new HashMap<>();
        }
        this.object = object;
        this.objectClass = resourceClass;
        this.formErrors = formErrors;
        readableFieldNames = new HashMap<>();
        action = null;
    }

    public void setReadableFieldName(String fieldName, String readableName) {
        readableFieldNames.put(fieldName, readableName);
    }

    public String getFormErrors() {
        if (formErrors.isEmpty()) {
            return "";
        }
        String errors = "<ul class=\"form-errors\">\n";
        for (Map.Entry<String, String> entry : formErrors.entrySet()) {
            String name;
            if (readableFieldNames.containsKey(entry.getKey())) {
                name = readableFieldNames.get(entry.getKey());
            } else {
                name = entry.getKey();
            }
            errors += "\t<li>" + Encoder.forHtml(name) + ": " + Encoder.forHtml(entry.getValue()) + "</li>\n";
        }
        return errors + "</ul>";
    }

    public String getFormBegin(String method) throws FormBuilderException {
        String form = "<form ";
        String formContent = "";
        String formMethod;
        HttpMethod httpMethod = HttpMethod.fromString(method);
        if (httpMethod == null) {
            throw new FormBuilderException("Method not supported: " + method);
        }
        switch (httpMethod) {
            case POST:
            case GET:
                formMethod = httpMethod.name();
                break;
            case PUT:
            case PATCH:
            case DELETE:
            default:
                formMethod = HttpMethod.POST.name();
                formContent = getInputFor("method", "hidden", method).toString();
        }

        if (object != null) {
            formContent += getInputFor("id", "hidden");
        }
        if(getAction() != null) {
            form += formatAttribute("action", getAction());
        }
        form += formatAttribute("method", formMethod);
        form += formatAttribute("accept-charset", "UTF-8");
        if(getEncodingType() != null) {
            form += formatAttribute("enctype", getEncodingType());
        }
        form += ">\n" + formContent;
        return form;
    }

    public String getFormEnd() {
        return "</form>";
    }

    private InputType getTypeFromGetter(Method getter) throws FormBuilderException {
        if (getter == null) throw new IllegalArgumentException("getter is null");
        InputType type;
        Class fieldType = getter.getReturnType();
        if (fieldType == String.class) {
            type = InputType.TEXT;
        } else if (fieldType == UserPassword.class) {
            type = InputType.PASSWORD;
        } else if (fieldType == int.class || fieldType == Integer.class) {
            type = InputType.NUMBER;
        } else if (fieldType == EmailAddress.class) {
            type = InputType.EMAIL;
        } else {
            throw new FormBuilderException("unknown type " + fieldType);
        }
        return type;
    }


    private Method getGetterMethod(String fieldName) throws FormBuilderException {
        String getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            return objectClass.getMethod(getterMethodName);
        } catch (NoSuchMethodException e) {
            throw new FormBuilderException("Getter " + getterMethodName + " doesn't exist in " + objectClass);
        }
    }

    public InputField getInputFor(String fieldName) throws FormBuilderException {
        return getInputFor(fieldName, null, null);
    }

    public InputField getInputFor(String fieldName, String type) throws FormBuilderException {
        return getInputFor(fieldName, type, null);
    }

    private String getIdPrefix() {
        if (idPrefix == null) {
            Random random = new Random();
            idPrefix = "" + random.nextLong() + "-";
        }
        return idPrefix;
    }

    /**
     * Returns the identifier for a html field.
     *
     * @param fieldName
     * @return
     */
    private String getIdForFieldName(String fieldName) {
        return getIdPrefix() + fieldName;
    }

    public String getLabelFor(String fieldName, String htmlClass) {
        if (fieldName == null) throw new IllegalArgumentException("fieldName is null");
        String content;
        if (readableFieldNames.containsKey(fieldName)) {
            content = readableFieldNames.get(fieldName);
        } else {
            content = fieldName;
        }
        String label = "<label ";
        label += formatAttribute("for", getIdForFieldName(fieldName));
        if (htmlClass != null) {
            label += formatAttribute("class", htmlClass);
        }
        label += ">" + Encoder.forHtml(content) + "</label>";
        return label;
    }

    /**
     * @todo: return a label object
     * @param fieldName
     * @return
     */
    public String getLabelFor(String fieldName) {
        return getLabelFor(fieldName, null);
    }

    public static String formatAttribute(String name, String value) {
        return name + "=\"" + Encoder.forHtmlAttribute(value) + "\" ";
    }

    public String getSubmit(String value) {
        return getSubmit(value, null);
    }

    public String getSubmit(String value, String htmlClass) {
        // TODO: make configurable
        return "<input type=\"submit\" class=\"btn btn-primary "+ Encoder.forHtmlAttribute(htmlClass) + "\" " + formatAttribute("value", value) + "/>";
    }

    /**
     * @param fieldName
     * @param objects
     * @return A select field
     * @throws FormBuilderException
     */
    public SelectField getSelectFor(String fieldName, AccessControlObjectBase[] objects) throws FormBuilderException {
        if (fieldName == null) throw new IllegalArgumentException("fieldName is null");
        SelectField selectField = new SelectField(fieldName, getIdForFieldName(fieldName));
        if(object != null) {
            Method getter = getGetterMethod(fieldName);
            String value = getValueFromGetter(getter);
            selectField.setSelectedValue(value);
        }
        for(AccessControlObjectBase object: objects) {
            selectField.addChoice(object.getReadableName(), object.getId());
        }
        selectField.setHtmlClass(getHtmlInputClass());
        return selectField;
    }

    /**
     * @param fieldName The name of the field
     * @param type      null or the type of the element.
     * @param    @Override value     null or the value of the element.
     * @return The input field.
     * @throws FormBuilderException
     */
    public InputField getInputFor(String fieldName, String type, String value) throws FormBuilderException {
        if (fieldName == null) throw new IllegalArgumentException("field is null");
        if (type == null || value == null) {
            Method getter = getGetterMethod(fieldName);
            if (type == null) {
                type = getTypeFromGetter(getter).toString().toLowerCase();
            }
            if (value == null) {
                if (object != null && getter != null && !type.equals("password")) {
                    value = getValueFromGetter(getter);
                }
            }
        }

        InputField inputField = new InputField(fieldName, getIdForFieldName(fieldName), type);
        inputField.setHtmlClass(getHtmlInputClass());
        if (value != null) {
            inputField.setValue(value);
        }
        if (formErrors.containsKey(fieldName)) {
            inputField.setError(formErrors.get(fieldName));
        }
        return inputField;
    }

    private String getValueFromGetter(Method getter) throws FormBuilderException {
        if(getter == null) throw new IllegalArgumentException("getter is null");
        if(object == null) throw new IllegalStateException("object is null");
        String value;
        try {
            value = getter.invoke(object).toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new FormBuilderException(e.getMessage());
        }
        return value;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * @return The http action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action The http action
     */
    public void setAction(String action) {
        if (action == null) throw new IllegalArgumentException("action is null");
        this.action = action;
    }

    public String getHtmlInputClass() {
        return htmlInputClass;
    }

    public void setHtmlInputClass(String htmlInputClass) {
        this.htmlInputClass = htmlInputClass;
    }
}
