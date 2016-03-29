package ch.avocado.share.common.form;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by coffeemakr on 29.03.16.
 */
class SelectField extends FieldBase {
    private String selectedValue = null;

    private Map<String, String> valuesWithName;


    SelectField(String name, String id) {
        super(name, id);
        valuesWithName = new HashMap<>();
    }

    private String getHtmlOption(String name, String value, boolean selected) {
        String option = "\t<option " + FormBuilder.formatAttribute(name, value);
        if(selected) {
            option += "selected=\"selected\"";
        }
        option += ">" + value + "</option>\n";
        return option;
    }

    @Override
    String toHtml() {
        String select = "<select " + getHtmlAttributes() + ">\n";
        for(Map.Entry<String, String> valueWithName: valuesWithName.entrySet()) {
            String value = valueWithName.getKey();
            String name = valueWithName.getValue();
            boolean selected = (selectedValue != null && selectedValue.equals(value));
            select += getHtmlOption(name, value, selected);
        }
        return select + "\n</select>";
    }

    public void addChoice(String name, String value) {
        if(name == null) throw new IllegalArgumentException("name is null");
        if(value == null) throw new IllegalArgumentException("value is null");
        valuesWithName.put(value, name);
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
        this.selectedValue = selectedValue;
    }
}
