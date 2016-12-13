package undercurrentcore.server.servletHandlers;

import api.undercurrent.iface.IUCTile;
import api.undercurrent.iface.UCCustomInvokable;
import api.undercurrent.iface.UCEditorType;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import undercurrentcore.persist.UCBlockDTO;
import undercurrentcore.persist.UCPlayerDTO;
import undercurrentcore.server.RequestReturnObject;
import undercurrentcore.server.constants.ResponseTypes;
import undercurrentcore.util.BlockUtils;
import undercurrentcore.util.PlayerUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Niel Verster on 5/26/2015.
 */

public class UCTileInvokableImplServlet extends HttpServlet {

    Gson gson;
    public static Logger logger = Logger.getLogger("UnderCurrentCore");

    public UCTileInvokableImplServlet() {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String secretKey = req.getParameter("secretKey");
        String playerName = req.getParameter("playerName");

        if (!PlayerUtils.validateLoginWithBooleanResponse(playerName, secretKey)) {
            resp.getWriter().write(gson.toJson(new RequestReturnObject(false, ResponseTypes.USER_NOT_REGISTERED.toString())));
            return;
        }

        List<UCBlockDTO> blocks = PlayerUtils.getPlayerBlocks(playerName, secretKey);
        List<JsonObject> blocksToReturn = new ArrayList<>();

        try {
            for (UCBlockDTO block : blocks) {
                JsonObject blockOrdinal = new JsonObject();
                JsonObject generalBlockInfo = new JsonObject();

                generalBlockInfo.addProperty("internalName", block.getInternalName());
                generalBlockInfo.addProperty("name", block.getName());
                generalBlockInfo.addProperty("xCoord", block.getxCoord());
                generalBlockInfo.addProperty("yCoord", block.getyCoord());
                generalBlockInfo.addProperty("zCoord", block.getzCoord());
                generalBlockInfo.addProperty("dim", block.getDim());
                generalBlockInfo.addProperty("dimName", DimensionManager.getProvider(block.getDim()).getDimensionName());
                blockOrdinal.add("generalBlockInfo", generalBlockInfo);

                if (block.getInstance().getCustomInvokables() == null) {
                    blockOrdinal.add("customInvokables", gson.toJsonTree(new ArrayList<>()));
                } else {
                    blockOrdinal.add("customInvokables", gson.toJsonTree(block.getInstance().getCustomInvokables()));
                }

                blocksToReturn.add(blockOrdinal);
            }
            RequestReturnObject rro = new RequestReturnObject(true, gson.toJsonTree(blocksToReturn));
            resp.getWriter().write(gson.toJson(rro));
        } catch (Exception e) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.SERVER_ERROR.toString() + "::" + e.getMessage());
            resp.getWriter().write(gson.toJson(rro));
            logger.warning(Throwables.getStackTraceAsString(e));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        Gson gson = new Gson();

        String secretKey = req.getParameter("secretKey");
        String playerName = req.getParameter("playerName");

        if (!PlayerUtils.validateLoginWithBooleanResponse(playerName, secretKey)) {
            resp.getWriter().write(gson.toJson(new RequestReturnObject(false, ResponseTypes.USER_NOT_REGISTERED.toString())));
            return;
        }

        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(CharStreams.toString(new InputStreamReader(req.getInputStream()))).getAsJsonObject();

        if (!obj.has("data")) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.MISSING_BODY_MEMBER.toString() + "::data");
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        JsonObject data = obj.get("data").getAsJsonObject();

        if (!data.has("internalName")) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.MISSING_BODY_MEMBER.toString() + "::internalName");
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        UCBlockDTO blockToUpdate = BlockUtils.getBlockFromWorldDataWithInternalName(secretKey, data.get("internalName").getAsString());

        if (blockToUpdate == null) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.NO_BLOCK_FOUND_FOR_INTERNAL_NAME.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        TileEntity te = BlockUtils.getBlockTileFromCoords(blockToUpdate.getxCoord(), blockToUpdate.getyCoord(), blockToUpdate.getzCoord(), blockToUpdate.getDim());

        if (te == null) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.WORLD_TE_DOES_NOT_EXIST.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        if (!(te instanceof IUCTile)) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.TE_NOT_IUCTILE.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        HashMap<String, UCCustomInvokable> customInvokables;

        try {
            customInvokables = ((IUCTile) te).getCustomInvokables();
        } catch (Exception e) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.ERROR_GETTING_TILE_CUSTOMIVOKABLES.toString() + ": " + Throwables.getStackTraceAsString(e));
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        if (customInvokables == null) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.UCTILE_CUSTOMINVOKABLES_IS_NULL.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        if (!data.has("invokableData")) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.MISSING_BODY_MEMBER.toString() + "::invokableData");
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        if (!data.has("invokableName")) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.MISSING_INVOKABLE_NAME.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        List<UCEditorType> invokableParamsDef;
        List<UCEditorType> invokableParams = new ArrayList<>();

        try {
            invokableParamsDef = ((IUCTile) te).getCustomInvokables().get(data.get("invokableName")).getParameters();
        } catch (Exception e) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.ERROR_GETTING_TILE_CUSTOMIVOKABLES.toString() + ": " + Throwables.getStackTraceAsString(e));
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        JsonArray invokableData = data.get("invokableData").getAsJsonArray();
        for (int j = 0; j < invokableData.size(); j++) {
            JsonObject currentIteration = invokableData.get(j).getAsJsonObject();
            try {

                if (!currentIteration.has("fieldName")) {
                    RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.MISSING_BODY_MEMBER.toString() + "::fieldName");
                    resp.getWriter().write(gson.toJson(rro));
                    return;
                }

                if (!currentIteration.has("fieldValue")) {
                    RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.MISSING_BODY_MEMBER.toString() + "::fieldValue");
                    resp.getWriter().write(gson.toJson(rro));
                    return;
                }


                for (UCEditorType UCEditorType : invokableParamsDef) {
                    if (UCEditorType.getFieldName().equals(currentIteration.get("fieldName").getAsString())) {
                        if (UCEditorType.validateValue(currentIteration.get("fieldValue"))) {
                            invokableParams.add(gson.fromJson(currentIteration, UCEditorType.getClass()));
                        } else {
                            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.VALUE_NOT_VALID_FOR_FIELD.toString() + "::" + currentIteration.get("fieldName").getAsString());
                            resp.getWriter().write(gson.toJson(rro));
                            return;
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println(Throwables.getStackTraceAsString(e));
                RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.SERVER_ERROR.toString() + ": " + Throwables.getStackTraceAsString(e));
                resp.getWriter().write(gson.toJson(rro));
                return;
            }
        }

        for (UCEditorType UCEditorTypeDef : invokableParamsDef) {
            for (UCEditorType UCEditorType : invokableParams) {
                if (UCEditorTypeDef.getFieldName().equals(UCEditorType.fieldName)) {
                    continue;
                }
            }
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.MISSING_PARAMETER_FOR_INVOKABLE.toString() + "::" + UCEditorTypeDef.getFieldName());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        try {
            ((IUCTile) te).getCustomInvokables().get(data.get("invokableName")).invoke(te, invokableParams);
            te.getWorldObj().markBlockForUpdate(blockToUpdate.getxCoord(), blockToUpdate.getyCoord(), blockToUpdate.getzCoord());
        } catch (Exception e) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.ERROR_WHILE_INVOKING_CUSTOMINVOKABLE.toString() + ": " + Throwables.getStackTraceAsString(e));
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        RequestReturnObject rro = new RequestReturnObject(true, ResponseTypes.TE_UPDATE_SUCCESS.toString());
        resp.getWriter().write(gson.toJson(rro));
    }
}
