package api.undercurrent.iface.editorTypes;

import api.undercurrent.iface.UCEditorType;

import java.net.URL;
import java.util.HashSet;

/**
 * Created by Niel on 10/16/2015.
 */
public class ListUCEditorType extends UCEditorType {

    private HashSet<String> allowableValues;
    private String defaultValue;

    public ListUCEditorType(String fieldName, String fieldValue, String displayName, String displayDescription, HashSet<String> allowableValues, String defaultValue, String editorGroup, URL image) {
        super(EditorTypes.LIST, editorGroup, image);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.allowableValues = allowableValues;
        this.defaultValue = defaultValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return (String) fieldValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean validateValue(Object obj) throws Exception {

        try {
            String objcast = String.valueOf(obj);

            if (allowableValues.contains(objcast)) {
                return true;
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }
}