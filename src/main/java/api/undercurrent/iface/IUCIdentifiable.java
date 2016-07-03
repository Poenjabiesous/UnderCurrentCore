package api.undercurrent.iface;
/**
 * Created by Niel on 10/21/2015.
 */
public class IUCIdentifiable {

    private int x;
    private int y;
    private int z;
    private int dim;

    public IUCIdentifiable(UCTileEntity te) {
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
