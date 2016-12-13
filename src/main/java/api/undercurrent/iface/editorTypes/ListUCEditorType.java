package api.undercurrent.iface.editorTypes;

import api.undercurrent.iface.UCEditorType;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Niel on 10/16/2015.
 */
public class ListUCEditorType extends UCEditorType {

    private HashSet<String> allowableValues;

    public ListUCEditorType(String fieldName, String fieldValue, String displayName, String displayDescription, HashSet<String> allowableValues, String editorGroup) {
        super(EditorTypes.LIST, editorGroup);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.allowableValues = allowableValues;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return (String) fieldValue;
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