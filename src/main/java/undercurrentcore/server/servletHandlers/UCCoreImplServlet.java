package undercurrentcore.server.servletHandlers;

import com.google.common.base.Throwables;
import com.google.gson.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import undercurrentcore.persist.UCBlockDTO;
import undercurrentcore.persist.UCPlayersWorldData;
import undercurrentcore.server.RequestReturnObject;
import undercurrentcore.server.constants.ResponseTypes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Niel Verster on 5/26/2015.
 */

public class UCCoreImplServlet extends HttpServlet {

    Gson gson;
    public static Logger logger = Logger.getLogger("UnderCurrentCore");

    public UCCoreImplServlet() {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);


        String uuid = req.getParameter("uuid");

        // Request checks
        if (uuid.equals("") || uuid.isEmpty()) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        EntityPlayer player = lookupOwner(uuid);

        if (player == null) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.USER_NOT_FOUND.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        UCPlayersWorldData data = (UCPlayersWorldData) player.worldObj.perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

        if (!data.checkPlayer(uuid)) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.USER_NOT_REGISTERED.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }


        List<UCBlockDTO> blocks = data.getUCPlayerInfo(uuid).getBlocks();
        JsonArray blocksToReturn = new JsonArray();
        try {
            for (UCBlockDTO block : blocks) {
                JsonObject blockObject = new JsonObject();
                JsonArray editableFields = new JsonArray();
                blockObject.addProperty("internalName", block.getInternalName());
                blockObject.addProperty("name", block.getName());
                blockObject.addProperty("xCoord", block.getxCoord());
                blockObject.addProperty("yCoord", block.getyCoord());
                blockObject.addProperty("zCoord", block.getzCoord());
                blockObject.addProperty("dim", block.getDim());
                blockObject.addProperty("dimName", DimensionManager.getProvider(block.getDim()).getDimensionName());
                editableFields.add(gson.toJsonTree(block.getInstance().getTileDefinition()));
                blockObject.add("editableFields", editableFields);
                blocksToReturn.add(blockObject);
            }
            RequestReturnObject rro = new RequestReturnObject(true, blocksToReturn);
            resp.getWriter().write(gson.toJson(rro));
        } catch (Exception e) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.SERVER_ERROR.toString());
            resp.getWriter().write(gson.toJson(rro));
            logger.severe("UnderCurrentCore: Problem while serializing block description objects due to: " + Throwables.getStackTraceAsString(e));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        Gson gson = new Gson();

        String uuid = req.getParameter("uuid");

        // Request checks
        if (uuid.equals("") || uuid.isEmpty()) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        EntityPlayer player = lookupOwner(uuid);

        if (player == null) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.USER_NOT_FOUND.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        UCPlayersWorldData playerData = (UCPlayersWorldData) player.worldObj.perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

        if (!playerData.checkPlayer(uuid)) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.USER_NOT_REGISTERED.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(req.getReader()).getAsJsonObject();
        JsonArray data = obj.getAsJsonArray("data");


        for (int i = 0; i < data.size(); i++) {
            JsonObject objectIteratable = data.get(i).getAsJsonObject();

            UCBlockDTO blockToUpdate = playerData.getBlockByInternalName(uuid, objectIteratable.get("internalName").getAsString());

            if (blockToUpdate == null) {
                RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.NO_BLOCK_FOUND_FOR_INTERNAL_NAME.toString());
                resp.getWriter().write(gson.toJson(rro));
                return;
            }

            JsonObject swapper = new JsonObject();
            TileEntity te = DimensionManager.getWorld(blockToUpdate.getDim()).getTileEntity(blockToUpdate.getxCoord(), blockToUpdate.getyCoord(), blockToUpdate.getzCoord());

            if (te == null) {
                RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.WORLD_TE_DOES_NOT_EXIST.toString());
                resp.getWriter().write(gson.toJson(rro));
                logger.severe("UnderCurrentCore: Problem locating TE while preparing for swapping. Object: " + blockToUpdate.toString());
                return;
            }

            JsonArray collections = objectIteratable.get("collections").getAsJsonArray();
            for (int j = 0; j < collections.size(); j++) {
                JsonObject currentIteration = collections.get(j).getAsJsonObject();
                JsonArray editable = currentIteration.get("editableFields").getAsJsonArray();
                for (int k = 0; k < editable.size(); k++) {
                    JsonObject editInstance = editable.get(j).getAsJsonObject();
                    try {
                        swapper.add(editInstance.get("fieldName").getAsString(), editInstance.get("fieldValue"));
                    } catch (Exception e) {
                        RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.CANT_USE_FIELD.toString() + ": " + editInstance.get("fieldName").getAsString());
                        resp.getWriter().write(gson.toJson(rro));
                        logger.severe("UnderCurrentCore: Problem adding field for swapping: " + editInstance.get("fieldName").getAsString() + "::" + Throwables.getStackTraceAsString(e));
                        return;
                    }
                }
            }

            try {
                te.getWorldObj().setTileEntity(blockToUpdate.getxCoord(), blockToUpdate.getyCoord(), blockToUpdate.getzCoord(), gson.fromJson(swapper, te.getClass()));
            } catch (Exception e) {
                RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.CANT_DO_TE_SWOP.toString());
                resp.getWriter().write(gson.toJson(rro));
                logger.severe("UnderCurrentCore: Problem swapping TE due to: " + Throwables.getStackTraceAsString(e));
                return;
            }
        }

        RequestReturnObject rro = new RequestReturnObject(true);
        resp.getWriter().write(gson.toJson(rro));
    }

    private EntityPlayer lookupOwner(String uuid) {
        if (uuid == null) {
            return null;
        }
        List<EntityPlayer> allPlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (EntityPlayer player : allPlayers) {
            if (player.getUniqueID().toString().equals(uuid)) {
                return player;
            }
        }
        return null;
    }


}
