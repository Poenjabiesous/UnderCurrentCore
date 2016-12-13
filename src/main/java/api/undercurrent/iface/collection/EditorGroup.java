package api.undercurrent.iface.collection;

import api.undercurrent.iface.UCEditorType;

import java.util.List;

/**
 * Created by Niel Verster on 12/13/2016.
 */
public class EditorGroup {

    private String groupName;
    private List<UCEditorType> editableFields;

    public EditorGroup(String groupName, List<UCEditorType> editableFields) {
        this.groupName = groupName;
        this.editableFields = editableFields;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<UCEditorType> getEditableFields() {
        return editableFields;
    }
}
