package api.undercurrent.iface.editorTypes;

import api.undercurrent.iface.UCEditorType;

import java.net.URL;

/**
 * Created by Niel on 10/16/2015.
 */
public class BooleanUCEditorType extends UCEditorType {

    public BooleanUCEditorType(String fieldName, String displayName, String displayDescription, boolean fieldValue, String editorGroup, URL image) throws Exception {
        super(EditorTypes.BOOLEAN, editorGroup, image);
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public Object getFieldValue() {
        return (Boolean) fieldValue;
    }

    @Override
    public boolean validateValue(Object obj) throws Exception {

        try {

            boolean objcast = Boolean.valueOf(String.valueOf(obj));
            return true;

        } catch (Exception e) {
            return false;
        }


    }
}

