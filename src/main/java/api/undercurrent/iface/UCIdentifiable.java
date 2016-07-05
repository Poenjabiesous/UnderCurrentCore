package api.undercurrent.iface;

import net.minecraft.tileentity.TileEntity;

public class UCIdentifiable {

    private int x;
    private int y;
    private int z;
    private int dim;

    public UCIdentifiable(TileEntity te) {
        this.x = te.xCoord;
        this.y = te.yCoord;
        this.z = te.zCoord;
        this.dim = te.getWorldObj().provider.dimensionId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getDim() {
        return dim;
    }
}
