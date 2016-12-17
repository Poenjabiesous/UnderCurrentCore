package api.undercurrent.iface;

import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Niel Verster on 12/13/2016.
 */
public abstract class UCCustomInvokable {

    private String displayName;
    private String description;
    private List<UCEditorType> parameters;

    public UCCustomInvokable(String name, String displayName, String description, List<UCEditorType> parameters) {
        this.displayName = displayName;
        this.description = description;
        this.parameters = parameters;
    }

    public abstract void invoke(TileEntity te, List<UCEditorType> parameters) throws Exception;

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public List<UCEditorType> getParameters() {
        return parameters;
    }

    public HashMap<String, UCEditorType> getParamsAsHashMap() {
        HashMap<String, UCEditorType> hashedEditorTypes = new HashMap<>();

        for (UCEditorType editorType : parameters) {
            hashedEditorTypes.put(editorType.getFieldName(), editorType);
        }

        return hashedEditorTypes;
    }
}
