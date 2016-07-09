package undercurrentcore.server;

import org.eclipse.jetty.server.Server;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import undercurrentcore.persist.UCConfiguration;
import undercurrentcore.server.servletHandlers.UCAuthImplServlet;
import undercurrentcore.server.servletHandlers.UCCoreImplServlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Niel Verster on 5/26/2015.
 */
public class ServerWrapper {

    private static Server server = new Server(UCConfiguration.getServerAPIPort());

    public static void startUCServer() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/undercurrentcore");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new UCCoreImplServlet()), "/core");
        context.addServlet(new ServletHolder(new UCAuthImplServlet()), "/auth");

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopUCServer() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
