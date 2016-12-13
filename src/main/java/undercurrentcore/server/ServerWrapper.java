package undercurrentcore.server;

import org.eclipse.jetty.server.Server;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.webapp.WebAppContext;
import undercurrentcore.persist.UCConfiguration;
import undercurrentcore.server.servletHandlers.UCPlayerImplServlet;
import undercurrentcore.server.servletHandlers.UCTileEditorImplServlet;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.logging.Logger;

/**
 * Created by Niel Verster on 5/26/2015.
 */
public class ServerWrapper {

    public static Logger logger = Logger.getLogger("UnderCurrentCore");
    private static Server serverApi = new Server(777);
    private static Server serverWeb = new Server(UCConfiguration.getServerWebPort());

    public ServerWrapper() {
    }

    public static void startUCServer() throws Exception {

        ServletContextHandler contextApi = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextApi.setContextPath("/undercurrentcore");
        contextApi.addServlet(new ServletHolder(new UCTileEditorImplServlet()), "/core");
        contextApi.addServlet(new ServletHolder(new UCPlayerImplServlet()), "/auth");
        FilterHolder corsApi = contextApi.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        corsApi.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        corsApi.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        corsApi.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        corsApi.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        serverApi.setHandler(contextApi);

        WebAppContext contextWeb = new WebAppContext();
        contextWeb.setContextPath(UCConfiguration.getWebContextBinding());
        contextWeb.setResourceBase(ServerWrapper.class.getClassLoader().getResource("webapp").toExternalForm());
        FilterHolder corsWeb = contextWeb.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        corsWeb.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        corsWeb.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        corsWeb.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        corsWeb.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        serverWeb.setHandler(contextWeb);

        try {
            serverApi.start();
            serverWeb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopUCServer() {
        try {
            serverApi.stop();
            serverWeb.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
