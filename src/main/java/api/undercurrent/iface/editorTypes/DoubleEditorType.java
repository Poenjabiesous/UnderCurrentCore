package api.undercurrent.iface.editorTypes;

/**
 * Created by Niel on 10/16/2015.
 */
public class DoubleEditorType extends EditorType {

    private int fieldPrecision;
    private double maxValue;
    private double minValue;

    public DoubleEditorType(String fieldName, double fieldValue, String displayName, String displayDescription, int fieldPrecision, double maxValue, double minValue) throws Exception {
        super(EditorTypes.DOUBLE);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.fieldPrecision = fieldPrecision;
        this.maxValue = maxValue;
        this.minValue = minValue;
        validateValue();
    }

    public String getFieldName() {
        return fieldName;
    }

    public double getFieldValue() {
        return (Double) fieldValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayDescription() {
        return displayDescription;
    }

    public int getFieldPrecision() {
        return fieldPrecision;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    @Override
    public void validateValue() throws Exception {

        if (getFieldValue() > getMaxValue()) {
            throw new Exception("UnderCurrentBlocks: DoubleEditorType with fieldName " + getFieldName() + " has a value of " + getFieldValue() + ", and max defined value is " + getMaxValue() + ".");
        }

        if (getFieldValue() < getMaxValue()) {
            throw new Exception("UnderCurrentBlocks: DoubleEditorType with fieldName " + getFieldName() + " has a value of " + getFieldValue() + ", and min defined value is " + getMinValue() + ".");
        }
    }

}
