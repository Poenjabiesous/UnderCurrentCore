package undercurrentcore;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import undercurrentcore.commands.UCCommandBreakAll;
import undercurrentcore.commands.UCCommandLink;
import undercurrentcore.commands.UCCommandList;
import undercurrentcore.commands.UCCommandName;
import undercurrentcore.persist.UCConfiguration;
import undercurrentcore.proxy.CommonProxy;
import undercurrentcore.reference.ModInfo;
import undercurrentcore.server.ServerWrapper;
import net.minecraftforge.common.MinecraftForge;

import java.util.logging.Logger;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, acceptedMinecraftVersions = "1.7.10")
public class UnderCurrentCore {

    public static Logger logger = Logger.getLogger("UnderCurrentCore");

    @SidedProxy(clientSide = "undercurrentcore.proxy.ClientProxy", serverSide = "undercurrentcore.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(ModInfo.ID)
    public static UnderCurrentCore instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        System.out.println("UnderCurrentCore: Reading configuration. ");
        UCConfiguration.init(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        System.out.println("UnderCurrentCore: Starting Framework. ");
        logger.info("UnderCurrentCore: Registering events.");
        MinecraftForge.EVENT_BUS.register(new UCEventHandler());
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        logger.info("UnderCurrentCore: Starting Server.");
        ServerWrapper.startUCServer();
        System.out.println("UnderCurrentCore: Registering commands.");
        event.registerServerCommand(new UCCommandName());
        event.registerServerCommand(new UCCommandLink());
        event.registerServerCommand(new UCCommandList());
        event.registerServerCommand(new UCCommandBreakAll());
    }

    @EventHandler
    public void serverStopping(FMLServerStoppedEvent event) {
        logger.info("UnderCurrentCore: Stopping Server.");
        ServerWrapper.stopUCServer();
    }
}
