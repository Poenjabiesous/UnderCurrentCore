package undercurrentcore.persist;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Niel on 7/6/2016.
 */
public class UCConfiguration {

    private static int serverAPIPort;
    private static int chatWebSocketPortRead;
    private static int chatWebSocketPortWrite;

    public static void init(File configFile) {

        Configuration config = new Configuration(configFile);
        config.load();

        serverAPIPort = config.get("serverAPIPort", "Server", 777).getInt();
        chatWebSocketPortRead = config.get("chatWebSocketPortRead", "Server", 780).getInt();
        chatWebSocketPortWrite = config.get("chatWebSocketPortWrite", "Server", 783).getInt();

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static int getServerAPIPort() {
        return serverAPIPort;
    }

    public static int getChatWebSocketPortRead() {
        return chatWebSocketPortRead;
    }

    public static int getChatWebSocketPortWrite() {
        return chatWebSocketPortWrite;
    }
}
