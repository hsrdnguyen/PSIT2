package ch.avocado.share.common.form;

import ch.avocado.share.common.Encoder;
import ch.avocado.share.common.HttpMethod;
import ch.avocado.share.model.data.AccessControlObjectBase;
import ch.avocado.share.model.data.EmailAddress;
import ch.avocado.share.model.data.UserPassword;
import ch.avocado.share.servlet.resources.base.DetailViewConfig;
import ch.avocado.share.servlet.resources.base.FormError;
import org.apache.commons.fileupload.FileItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class FormBuilder {
    private FormError formErrors;
    private String action;
    private FormEncoding encodingType = null;
    private AccessControlObjectBase object;
    private Class<? extends AccessControlObjectBase> objectClass;
    private String htmlInputClass = "form-control";
    private String idPrefix = null;
    private Map<String, String> readableFieldNames;
    private String formErrorsString = null;

    public FormBuilder(Class<? extends AccessControlObjectBase> resourceClass, FormError formErrors) {
        this(null, resourceClass, formErrors);
    }

    static private AccessControlObjectBase checkObjectNotNull(AccessControlObjectBase object) {
        if (object == null) throw new NullPointerException("object is null");
        return object;
    }

    public FormBuilder(AccessControlObjectBase object, FormError formErrors) {
        this(checkObjectNotNull(object), checkObjectNotNull(object).getClass(), formErrors);
    }

    public FormBuilder(DetailViewConfig viewConfig, Class<? extends AccessControlObjectBase> resourceClass) {
        this(viewConfig.getObject(resourceClass), resourceClass, viewConfig.getFormErrors());
    }

    public FormBuilder(AccessControlObjectBase object, Class<? extends AccessControlObjectBase> resourceClass, FormError formErrors) {
        if (resourceClass == null) throw new NullPointerException("objectClass is null");
        if (formErrors == null) {
            this.formErrors = new FormError();
        } else {
            this.formErrors = formErrors;
        }
        this.object = object;
        this.objectClass = resourceClass;
        readableFieldNames = new HashMap<>();
        action = null;
    }

    public void setReadableFieldName(String fieldName, String readableName) {
        readableFieldNames.put(fieldName, readableName);
    }

    public String getFormErrors() {
        if (formErrorsString == null) {
            if (formErrors.isEmpty()) {
                formErrorsString = "";
            } else {
                String errors = "<ul class=\"form-errors\">\n";
                for (Map.Entry<String, List<String>> entry : formErrors.entrySet()) {
                    String name;
                    if (readableFieldNames.containsKey(entry.getKey())) {
                        name = readableFieldNames.get(entry.getKey());
                    } else {
                        name = entry.getKey();
                    }
                    errors += "\t<li>" + Encoder.forHtml(name) + ": " + Encoder.forHtml(entry.getValue().get(0)) + "</li>\n";
                }
                formErrorsString = errors + "</ul>";
            }

        }
        return formErrorsString;
    }

    public String getFormBegin(HttpMethod method){
        String form = "<form ";
        String formContent = "";
        String formMethod;
        switch (method) {
            case POST:
            case GET:
                formMethod = method.name();
                break;
            case PUT:
            case PATCH:
            case DELETE:
            default:
                formMethod = HttpMethod.POST.name();
                formContent = getInputFor("method", InputType.HIDDEN, method.toString()).toString();
        }

        if (object != null) {
            formContent += getInputFor("id", InputType.HIDDEN);
        }
        if (getAction() != null) {
            form += formatAttribute("action", getAction());
        }
        form += formatAttribute("method", formMethod);
        form += formatAttribute("accept-charset", "UTF-8");
        if (getEncodingType() != null) {
            form += formatAttribute("enctype", getEncodingType().getContentType());
        }
        form += ">\n" + formContent;
        return form;
    }

    public String getFormEnd() {
        return "</form>";
    }

    /**
     * @param getter The getter method
     * @throws IllegalFieldTypeException if the type the getter returns cannot be interpreted.
     * @return The input type of the method
     */
    private InputType getTypeFromGetter(Method getter) {
        if (getter == null) throw new NullPointerException("getter is null");
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
        } else if (fieldType == FileItem.class) {
            type = InputType.FILE;
        } else {
            throw new IllegalFieldTypeException(fieldType);
        }
        return type;
    }


    private Method getGetterMethod(String fieldName) {
        String getterMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        try {
            return objectClass.getMethod(getterMethodName);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Getter " + getterMethodName + " doesn't exist in " + objectClass);
        }
    }

    public InputField getInputFor(String fieldName) {
        return getInputFor(fieldName, null, null);
    }

    public InputField getInputFor(String fieldName, InputType type) {
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
        if (fieldName == null) throw new NullPointerException("fieldName is null");
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
     * @param fieldName
     * @return
     * @todo: return a label object
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
        return "<input type=\"submit\" class=\"btn btn-primary " + Encoder.forHtmlAttribute(htmlClass) + "\" " + formatAttribute("value", value) + "/>";
    }

    /**
     * @param fieldName
     * @param objects
     * @return A select field
     */
    public SelectField getSelectFor(String fieldName, Iterable<? extends AccessControlObjectBase> objects, String selectedId) {
        if (fieldName == null) throw new NullPointerException("fieldName is null");
        SelectField selectField = new SelectField(fieldName, getIdForFieldName(fieldName));
        if(selectedId == null) {
            if (object != null) {
                Method getter = getGetterMethod(fieldName);
                String value = getValueFromGetter(getter);
                selectField.setSelectedValue(value);
            }
        } else {
            selectField.setSelectedValue(selectedId);
        }
        for (AccessControlObjectBase object : objects) {
            selectField.addChoice(object.getReadableName(), object.getId());
        }
        selectField.setHtmlClass(getHtmlInputClass());
        return selectField;
    }

    public SelectField getSelectFor(String fieldName, Iterable<? extends AccessControlObjectBase> objects) {
        return getSelectFor(fieldName, objects, null);
    }

    /**
     * @param fieldName The name of the field
     * @param type      null or the type of the element.
     * @param value     null or the value of the element.
     * @return The input field.
     */
    public InputField getInputFor(String fieldName, InputType type, String value) {
        if (fieldName == null) throw new NullPointerException("field is null");
        if (type == null || value == null) {
            Method getter = null;
            if (type == null) {
                getter = getGetterMethod(fieldName);
                type = getTypeFromGetter(getter);
            }
            if (value == null && object != null && type.canHaveValue()) {
                if(getter == null) {
                    getter = getGetterMethod(fieldName);
                }
                value = getValueFromGetter(getter);
            }
        }
        InputField inputField = new InputField(fieldName, getIdForFieldName(fieldName), type);
        inputField.setHtmlClass(getHtmlInputClass());
        if (value != null) {
            inputField.setValue(value);
        }
        if (formErrors.containsKey(fieldName)) {
            inputField.setError(formErrors.get(fieldName).get(0));
        }
        return inputField;
    }

    private String getValueFromGetter(Method getter) {
        if (getter == null) throw new NullPointerException("getter is null");
        if (object == null) throw new IllegalStateException("object is null");
        String value;
        try {
            value = "" + getter.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Method " + getter + " is not callable");
        }
        return value;
    }

    public FormEncoding getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(FormEncoding encodingType) {
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
        if (action == null) throw new NullPointerException("action is null");
        this.action = action;
    }

    public String getHtmlInputClass() {
        return htmlInputClass;
    }

    public void setHtmlInputClass(String htmlInputClass) {
        this.htmlInputClass = htmlInputClass;
    }
}
