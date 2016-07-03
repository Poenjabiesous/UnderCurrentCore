package api.undercurrent.iface;

import net.minecraft.tileentity.TileEntity;

/**
 * Created by Niel on 10/16/2015.
 */
public abstract class UCTileEntity extends TileEntity {

    public abstract UCTileDefinition getTileDefinition() throws Exception;

}
