package undercurrentcore.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import undercurrentcore.persist.UCBlockDTO;
import undercurrentcore.persist.UCPlayersWorldData;
import undercurrentcore.server.constants.ResponseTypes;

import java.util.logging.Logger;

/**
 * Created by Niel Verster on 12/13/2016.
 */
public class BlockUtils {

    public static Logger logger = Logger.getLogger("UnderCurrentCore");

    public static UCBlockDTO getBlockFromWorldDataWithInternalName(String secretKey, String internalBlockName) {
        // Request checks
        if (secretKey == null || secretKey.equals("") || secretKey.isEmpty()) {
            logger.warning("Error validating login:" + ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            return null;
        }

        if (internalBlockName == null || internalBlockName.equals("") || internalBlockName.isEmpty()) {
            logger.warning("Error validating login:" + ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            return null;
        }

        return UCPlayersWorldData.get().getBlockByInternalName(secretKey, internalBlockName);
    }

    public static TileEntity getBlockTileFromCoords(int x, int y, int z, int dim) {
        return DimensionManager.getWorld(dim).getTileEntity(x, y, z);
    }

}
