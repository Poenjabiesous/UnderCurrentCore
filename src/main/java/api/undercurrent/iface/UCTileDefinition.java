package api.undercurrent.iface;

import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

public class UCTileDefinition extends UCIdentifiable {

    ArrayList<UCCollection> collections;

    public UCTileDefinition(TileEntity te) {
        super(te);
        collections = new ArrayList<UCCollection>();
    }

    public ArrayList<UCCollection> getCollections() {
        return collections;
    }
}
