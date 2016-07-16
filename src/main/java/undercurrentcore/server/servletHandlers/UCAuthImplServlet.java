package undercurrentcore.server.servletHandlers;

import api.undercurrent.iface.IUCTile;
import api.undercurrent.iface.UCCollection;
import api.undercurrent.iface.UCTileDefinition;
import api.undercurrent.iface.editorTypes.EditorType;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Niel Verster on 5/26/2015.
 */

public class UCAuthImplServlet extends HttpServlet {

    Gson gson;
    public static Logger logger = Logger.getLogger("UnderCurrentCore");

    public UCAuthImplServlet() {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String secretKey = req.getParameter("secretKey");

        // Request checks
        if (secretKey == null || secretKey.equals("") || secretKey.isEmpty()) {
            RequestReturnObject rro = new RequestReturnObject(false, ResponseTypes.EMPTY_REQUEST_PARAMETER.toString());
            resp.getWriter().write(gson.toJson(rro));
            return;
        }

        UCPlayersWorldData data = (UCPlayersWorldData) DimensionManager.getWorld(0).perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

        if (data.validatePlayerSecretKey(secretKey)) {
            JsonObject response = new JsonObject();
            response.addProperty("auth", true);
            RequestReturnObject rro = new RequestReturnObject(true, response);

            resp.getWriter().write(gson.toJson(rro));
        } else {
            JsonObject response = new JsonObject();
            response.addProperty("auth", false);
            RequestReturnObject rro = new RequestReturnObject(true, response);
            resp.getWriter().write(gson.toJson(rro));
        }
    }
}
