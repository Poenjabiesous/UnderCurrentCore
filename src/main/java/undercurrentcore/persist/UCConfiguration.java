package undercurrentcore.persist;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Niel on 7/6/2016.
 */
public class UCConfiguration {

    private static int serverWebPort;
    private static String webContextBinding;

    public static void init(File configFile) {

        Configuration config = new Configuration(configFile);
        config.load();

        serverWebPort = config.get("serverWebPort", "Server", 778).getInt();
        webContextBinding = config.get("webContextBinding", "Server", "/undercurrentweb").getString();

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static int getServerWebPort() {
        return serverWebPort;
    }

    public static String getWebContextBinding() {
        return webContextBinding;
    }

}
