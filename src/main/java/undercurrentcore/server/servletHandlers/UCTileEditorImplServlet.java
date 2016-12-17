package undercurrentcore.server.servletHandlers;

import api.undercurrent.iface.IUCTile;
import api.undercurrent.iface.UCEditorType;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.google.gson.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import undercurrentcore.persist.UCBlockDTO;
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
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Niel Verster on 5/26/2015.
 */

public class UCTileEditorImplServlet extends HttpServlet {

    Gson gson;
    public static Logger logger = Logger.getLogger("UnderCurrentCore");

    public UCTileEditorImplServlet() {
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

                if (block.getInstance().getEditableFields() == null) {
                    blockOrdinal.add("editableFields", gson.toJsonTree(new ArrayList<>()));
                } else {
                    blockOrdinal.add("editableFields", gson.toJsonTree(block.getInstance().getEditableFields()));
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

        JsonArray data = obj.getAsJsonArray("data");

        for (int i = 0; i < data.size(); i++) {
            JsonObject objectJson = data.get(i).getAsJsonObject();

            if (!objectJson.has("internalName")) {
                RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.MISSING_BODY_MEMBER.toString() + "::internalName ON ITERATION " + i);
                resp.getWriter().write(gson.toJson(rro));
                return;
            }

            UCBlockDTO blockToUpdate = BlockUtils.getBlockFromWorldDataWithInternalName(secretKey, objectJson.get("internalName").getAsString());

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

            ArrayList<UCEditorType> editableFields;

            try {
                editableFields = ((IUCTile) te).getEditableFields();
            } catch (Exception e) {
                RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.ERROR_GETTING_TILE_EDITABLEFIELDS.toString() + ": " + Throwables.getStackTraceAsString(e));
                resp.getWriter().write(gson.toJson(rro));
                return;
            }

            if (editableFields == null) {
                RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.UCTILE_EDITABLEFIELDS_IS_NULL.toString());
                resp.getWriter().write(gson.toJson(rro));
                return;
            }

            ArrayList<Field> fields = new ArrayList<>();
            addDeclaredAndInheritedFields(te.getClass(), fields);
            HashSet<String> editableFieldsHashed = new HashSet<>();

            if (!objectJson.has("editedData")) {
                RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.MISSING_BODY_MEMBER.toString() + "::editedData");
                resp.getWriter().write(gson.toJson(rro));
                return;
            }

            JsonArray collections = objectJson.get("editedData").getAsJsonArray();
            for (int j = 0; j < collections.size(); j++) {
                JsonObject currentIteration = collections.get(j).getAsJsonObject();
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

                    for (UCEditorType UCEditorType : editableFields) {
                        if (UCEditorType.getFieldName().equals(currentIteration.get("fieldName").getAsString())) {
                            if (UCEditorType.validateValue(currentIteration.get("fieldValue"))) {
                                editableFieldsHashed.add(currentIteration.get("fieldName").getAsString());
                            } else {
                                RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.VALUE_NOT_VALID_FOR_FIELD.toString() + "::" + currentIteration.get("fieldName").getAsString());
                                resp.getWriter().write(gson.toJson(rro));
                                return;
                            }
                        }
                    }

                    if (!editableFieldsHashed.contains(currentIteration.get("fieldName").getAsString())) {
                        RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.SPECIFIED_EDITABLEFIELD_NOT_EDITABLE.toString() + "::" + currentIteration.get("fieldName").getAsString());
                        resp.getWriter().write(gson.toJson(rro));
                        return;
                    }

                    for (Field field : fields) {
                        if (field.getName().equalsIgnoreCase(currentIteration.get("fieldName").getAsString())) {
                            field.setAccessible(true);

                            String className = field.getType().getCanonicalName();

                            if (className.contains("Boolean") || className.contains("boolean")) {
                                field.set(te, currentIteration.get("fieldValue").getAsBoolean());
                                break;
                            }

                            if (className.contains("Double") || className.contains("double")) {
                                field.set(te, currentIteration.get("fieldValue").getAsDouble());
                                break;
                            }

                            if (className.contains("Float") || className.contains("float")) {
                                field.set(te, currentIteration.get("fieldValue").getAsFloat());
                                break;
                            }

                            if (className.contains("Int") || className.contains("int")) {
                                field.set(te, currentIteration.get("fieldValue").getAsInt());
                                break;
                            }

                            if (className.contains("String") || className.contains("string")) {
                                field.set(te, currentIteration.get("fieldValue").getAsString());
                                break;
                            }

                            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.CANT_USE_FIELD.toString() + ": " + currentIteration.get("fieldName").getAsString());
                            resp.getWriter().write(gson.toJson(rro));
                        }
                    }

                } catch (Exception e) {
                    System.out.println(Throwables.getStackTraceAsString(e));
                    RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.SERVER_ERROR.toString() + ": " + Throwables.getStackTraceAsString(e));
                    resp.getWriter().write(gson.toJson(rro));
                    return;
                }
            }
            te.getWorldObj().markBlockForUpdate(blockToUpdate.getxCoord(), blockToUpdate.getyCoord(), blockToUpdate.getzCoord());
        }
        RequestReturnObject rro = new RequestReturnObject(true);
        resp.getWriter().write(gson.toJson(rro));
    }

    private static void addDeclaredAndInheritedFields(Class<?> c, ArrayList<Field> fields) {
        fields.addAll(Arrays.asList(c.getDeclaredFields()));
        Class<?> superClass = c.getSuperclass();
        if (superClass != null) {
            addDeclaredAndInheritedFields(superClass, fields);
        }
    }
}
