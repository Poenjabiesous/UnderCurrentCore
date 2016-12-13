package api.undercurrent.iface;

import java.util.ArrayList;
import java.util.HashMap;

public interface IUCTile {

    ArrayList<UCEditorType> getEditableFields() throws Exception;

    HashMap<String, UCCustomInvokable> getCustomInvokables() throws Exception;

    ArrayList<UCNotification> getNotifiers() throws Exception;

}
