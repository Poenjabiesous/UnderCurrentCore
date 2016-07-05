package api.undercurrent.iface.editorTypes;

/**
 * Created by Niel on 10/16/2015.
 */
public abstract class EditorType {

    protected EditorTypes editorType;
    protected String fieldName;
    protected String displayName;
    protected String displayDescription;
    protected Object fieldValue;

    public EditorType(EditorTypes editorType) {
        this.editorType = editorType;
    }

    public abstract void validateValue() throws Exception;

    public enum EditorTypes {
        STRING, INT, DOUBLE, BOOLEAN, INFO, COMBO
    }
}
