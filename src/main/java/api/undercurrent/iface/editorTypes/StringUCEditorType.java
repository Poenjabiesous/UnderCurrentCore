package api.undercurrent.iface.editorTypes;

import api.undercurrent.iface.UCEditorType;

import java.net.URL;

/**
 * Created by Niel on 10/16/2015.
 */
public class StringUCEditorType extends UCEditorType {

    private int minLength;
    private int maxLength;

    public StringUCEditorType(String fieldName, String fieldValue, String displayName, String displayDescription, int minLength, int maxLength, String editorGroup, URL image) {
        super(EditorTypes.STRING, editorGroup, image);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return (String) fieldValue;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public boolean validateValue(Object obj) throws Exception {

        try {
            String objcast = String.valueOf(obj);

            if (objcast.length() > getMaxLength()) {
                return false;
            }

            if (objcast.length() < getMinLength()) {
                return false;
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }
}