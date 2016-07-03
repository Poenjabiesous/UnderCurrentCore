package undercurrent.persist;

import api.undercurrent.iface.UCTileEntity;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by Niel on 10/21/2015.
 */
public class UCBlockDTO {

    private int xCoord;
    private int yCoord;
    private int zCoord;
    private int dim;
    private String name;

    public UCBlockDTO(int xCoord, int yCoord, int zCoord, int dim, String name) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.dim = dim;
        this.name = name;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public int getzCoord() {
        return zCoord;
    }

    public int getDim() {
        return dim;
    }

    public String getName() {
        return name;
    }

    public UCTileEntity getInstance() {
        if (DimensionManager.getWorld(getDim()).getTileEntity(getxCoord(), getyCoord(), getzCoord()) instanceof UCTileEntity) {
            return (UCTileEntity) DimensionManager.getWorld(getDim()).getTileEntity(getxCoord(), getyCoord(), getzCoord());
        }
        return null;
    }
}
