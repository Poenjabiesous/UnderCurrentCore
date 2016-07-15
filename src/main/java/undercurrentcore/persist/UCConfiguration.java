package undercurrentcore.persist;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Niel on 7/6/2016.
 */
public class UCConfiguration {

    private static int serverAPIPort;
    private static int serverWebPort;

    public static void init(File configFile) {

        Configuration config = new Configuration(configFile);
        config.load();

        serverAPIPort = config.get("serverAPIPort", "Server", 777).getInt();
        serverWebPort = config.get("serverWebPort", "Server", 778).getInt();

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static int getServerAPIPort() {
        return serverAPIPort;
    }

    public static int getServerWebPort() {
        return serverWebPort;
    }
}
