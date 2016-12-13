package undercurrentcore.server.servletHandlers;

import com.google.gson.Gson;
import undercurrentcore.persist.UCPlayerDTO;
import undercurrentcore.server.RequestReturnObject;
import undercurrentcore.server.constants.ResponseTypes;
import undercurrentcore.util.PlayerUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Niel Verster on 5/26/2015.
 */

public class UCPlayerImplServlet extends HttpServlet {

    Gson gson;
    public static Logger logger = Logger.getLogger("UnderCurrentCore");

    public UCPlayerImplServlet() {
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String secretKey = req.getParameter("secretKey");
        String playerName = req.getParameter("playerName");

        UCPlayerDTO playerDTO = PlayerUtils.getPlayerInfo(playerName, secretKey);

        if (playerDTO == null) {
            resp.getWriter().write(gson.toJson(new RequestReturnObject(false, ResponseTypes.USER_NOT_REGISTERED.toString())));
            return;
        }

        resp.getWriter().write(gson.toJson(new RequestReturnObject(false, gson.toJsonTree(playerDTO))));

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String secretKey = req.getParameter("secretKey");
        String playerName = req.getParameter("playerName");

        RequestReturnObject rro = PlayerUtils.validateLoginWithReturnObject(playerName, secretKey);
        resp.getWriter().write(gson.toJson(rro));
    }
}
