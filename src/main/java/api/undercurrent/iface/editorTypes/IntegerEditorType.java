package api.undercurrent.iface.editorTypes;
/**
 * Created by Niel on 10/16/2015.
 */
public class IntegerEditorType extends EditorType {

    public String fieldName;
    public int fieldValue;
    public String displayName;
    public String displayDescription;
    public int minValue;
    public int maxValue;

    public IntegerEditorType(String fieldName, int fieldValue, String displayName, String displayDescription, int minValue, int maxValue) {
        super(EditorTypes.INT);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getFieldValue() {
        return fieldValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public void validateValue() throws Exception {

        if (getFieldValue() > getMaxValue()) {
            throw new Exception("UnderCurrent: IntegerEditorType with fieldName " + getFieldName() + " has a value of " + getFieldValue() + ", and max defined value is " + getMaxValue() + ".");
        }

        if (getFieldValue() < getMinValue()) {
            throw new Exception("UnderCurrent: IntegerEditorType with fieldName " + getFieldName() + " has a value of " + getFieldValue() + ", and min defined value is " + getMinValue() + ".");
        }

    }
}
