package undercurrentcore.server;

import net.minecraft.util.ResourceLocation;
import org.eclipse.jetty.server.Server;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import undercurrentcore.UnderCurrentCore;
import undercurrentcore.persist.UCConfiguration;
import undercurrentcore.reference.ModInfo;
import undercurrentcore.server.servletHandlers.UCAuthImplServlet;
import undercurrentcore.server.servletHandlers.UCCoreImplServlet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * Created by Niel Verster on 5/26/2015.
 */
public class ServerWrapper {

    public static Logger logger = Logger.getLogger("UnderCurrentCore");
    private static Server serverApi = new Server(UCConfiguration.getServerAPIPort());
    private static Server serverWeb = new Server(UCConfiguration.getServerWebPort());

    public ServerWrapper() {
    }

    public static void startUCServer() throws Exception {

        ServletContextHandler contextApi = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextApi.setContextPath("/undercurrentcore");
        serverApi.setHandler(contextApi);
        contextApi.addServlet(new ServletHolder(new UCCoreImplServlet()), "/core");
        contextApi.addServlet(new ServletHolder(new UCAuthImplServlet()), "/auth");

        WebAppContext contextWeb = new WebAppContext();
        contextWeb.setContextPath("/undercurrent");
        contextWeb.setResourceBase(ServerWrapper.class.getClassLoader().getResource("webapp").toExternalForm());

        FilterHolder cors = contextWeb.addFilter(CrossOriginFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

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
