package undercurrentcore.util;

import com.google.gson.JsonObject;
import net.minecraftforge.common.DimensionManager;
import undercurrentcore.persist.UCBlockDTO;
import undercurrentcore.persist.UCPlayerDTO;
import undercurrentcore.persist.UCPlayersWorldData;
import undercurrentcore.server.RequestReturnObject;
import undercurrentcore.server.constants.ResponseTypes;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Niel Verster on 12/13/2016.
 */
public class PlayerUtils {

    public static Logger logger = Logger.getLogger("UnderCurrentCore");

    public static RequestReturnObject validateLoginWithReturnObject(String playerName, String secretKey) {

        // Request checks
        if (secretKey == null || secretKey.equals("") || secretKey.isEmpty()) {
            logger.warning("Error validating login:" + ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            return new RequestReturnObject(false, ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
        }

        if (playerName == null || playerName.equals("") || playerName.isEmpty()) {
            logger.warning("Error validating login:" + ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            return new RequestReturnObject(false, ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
        }

        UCPlayersWorldData data = UCPlayersWorldData.get();

        if (data.validateLogin(secretKey, playerName)) {
            return new RequestReturnObject(true, ResponseTypes.LOGIN_SUCCESSFUL.toString());

        } else {
            return new RequestReturnObject(false, ResponseTypes.USER_NOT_REGISTERED.toString());
        }
    }

    public static boolean validateLoginWithBooleanResponse(String playerName, String secretKey) {

        // Request checks
        if (secretKey == null || secretKey.equals("") || secretKey.isEmpty()) {
            logger.warning("Error validating login:" + ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            return false;
        }

        if (playerName == null || playerName.equals("") || playerName.isEmpty()) {
            logger.warning("Error validating login:" + ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            return false;
        }

        return UCPlayersWorldData.get().validateLogin(secretKey, playerName);
    }

    public static UCPlayerDTO getPlayerInfo(String playerName, String secretKey) {
        if (validateLoginWithBooleanResponse(playerName, secretKey)) {
            return UCPlayersWorldData.get().getPlayerInfo(secretKey);
        }
        return null;
    }

    public static List<UCBlockDTO> getPlayerBlocks(String playerName, String secretKey) {
        if (validateLoginWithBooleanResponse(playerName, secretKey)) {
            return UCPlayersWorldData.get().getPlayerBlocks(secretKey);
        }
        return null;
    }
}


