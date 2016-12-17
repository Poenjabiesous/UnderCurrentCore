package api.undercurrent.iface.editorTypes;

import api.undercurrent.iface.UCEditorType;

import java.net.URL;

/**
 * Created by Niel on 10/16/2015.
 */
public class DoubleUCEditorType extends UCEditorType {

    private int fieldPrecision;
    private double maxValue;
    private double minValue;

    public DoubleUCEditorType(String fieldName, double fieldValue, String displayName, String displayDescription, int fieldPrecision, double maxValue, double minValue, String editorGroup, URL image) throws Exception {
        super(EditorTypes.DOUBLE, editorGroup, image);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.displayName = displayName;
        this.displayDescription = displayDescription;
        this.fieldPrecision = fieldPrecision;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
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
    public boolean validateValue(Object obj) throws Exception {
        try {
            double objcast = Double.valueOf(String.valueOf(obj));

            if (objcast > getMaxValue()) {
                return false;
            }

            if (objcast < getMinValue()) {
                return false;
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

}
