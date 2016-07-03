package api.undercurrent.iface;

import java.util.ArrayList;

/**
 * Created by Niel on 10/16/2015.
 */
public class UCTileDefinition extends IUCIdentifiable {

    ArrayList<UCCollection> collections;

    public UCTileDefinition(UCTileEntity te) {
        super(te);
        collections = new ArrayList<UCCollection>();
    }

    public ArrayList<UCCollection> getCollections() {
        return collections;
    }
}
