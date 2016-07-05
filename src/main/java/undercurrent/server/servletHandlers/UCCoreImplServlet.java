package undercurrent.server.servletHandlers;

import api.undercurrent.iface.UCTileEntity;
import com.google.common.base.Throwables;
import com.google.gson.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.exception.ExceptionUtils;
import undercurrent.persist.UCBlockDTO;
import undercurrent.persist.UCPlayersWorldData;
import undercurrent.server.RequestReturnObject;
import undercurrent.server.constants.ResponseTypes;

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

        JsonArray array = new JsonArray();
        List<UCBlockDTO> blocks = data.getUCPlayerInfo(uuid).getBlocks();
        try {
            for (int i = 0; i < blocks.size(); i++) {
                array.add(gson.toJsonTree(blocks.get(i).getInstance().getTileDefinition()));
            }
            RequestReturnObject rro = new RequestReturnObject(true, array);
            resp.getWriter().write(gson.toJson(rro));
        } catch (Exception e) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.SERVER_ERROR.toString());
            resp.getWriter().write(gson.toJson(rro));
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        Gson gson = new Gson();

        String uuid = req.getParameter("uuid");

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(req.getReader()).getAsJsonObject();
        JsonArray data = obj.getAsJsonArray("data");

        System.out.println(obj.toString());

        // Request checks
        if (uuid.equals("") || uuid.isEmpty()) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            resp.getWriter().write(gson.toJson(rro));
            throw new ServletException("Cannot execute for player due to: " + ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
        }


        for (int i = 0; i < data.size(); i++) {
            JsonObject objectIteratable = data.get(i).getAsJsonObject();

            int x = objectIteratable.get("x").getAsInt();
            int y = objectIteratable.get("y").getAsInt();
            int z = objectIteratable.get("z").getAsInt();
            int dim = objectIteratable.get("dim").getAsInt();

            JsonObject swapper = new JsonObject();

            UCTileEntity te = (UCTileEntity) DimensionManager.getWorld(dim).getTileEntity(x, y, z);

            if (te == null) {
                RequestReturnObject rro = new RequestReturnObject(false, "Cannot execute for player due to: " + ResponseTypes.WORLD_TE_DOES_NOT_EXIST.toString() + ":: No tile entity found for coordinates: " + x + ", " + y + ", " + z);
                resp.getWriter().write(gson.toJson(rro));
                throw new ServletException("Cannot execute for player due to: " + ResponseTypes.WORLD_TE_DOES_NOT_EXIST.toString() + ":: No tile entity found for coordinates: " + x + ", " + y + ", " + z);
            }

            JsonArray collections = objectIteratable.get("collections").getAsJsonArray();
            for (int j = 0; j < collections.size(); j++) {
                JsonObject currentIteration = collections.get(j).getAsJsonObject();
                JsonArray editables = currentIteration.get("editableFields").getAsJsonArray();
                for (int k = 0; k < editables.size(); k++) {
                    JsonObject editInstance = editables.get(j).getAsJsonObject();
                    try {
                        swapper.add(editInstance.get("fieldName").getAsString(), editInstance.get("fieldValue"));
                    } catch (Exception e) {
                        RequestReturnObject rro = new RequestReturnObject(false, "Cannot execute for player due to: " + ExceptionUtils.getStackTrace(e));
                        resp.getWriter().write(gson.toJson(rro));
                        throw new ServletException("Cannot execute for player due to: " + ExceptionUtils.getStackTrace(e));
                    }
                }
            }
            te.getWorldObj().setTileEntity(x, y, z, gson.fromJson(swapper, te.getClass()));
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
