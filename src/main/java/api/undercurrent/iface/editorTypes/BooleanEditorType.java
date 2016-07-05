package api.undercurrent.iface.editorTypes;

/**
 * Created by Niel on 10/16/2015.
 */
public class BooleanEditorType extends EditorType {

    public BooleanEditorType(String fieldName, String displayName, String displayDescription, boolean fieldValue) throws Exception {
        super(EditorTypes.BOOLEAN);
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.fieldValue = fieldValue;
        validateValue();
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

    public boolean isFieldValue() {
        return (Boolean) fieldValue;
    }

    @Override
    public void validateValue() throws Exception {

    }
}

