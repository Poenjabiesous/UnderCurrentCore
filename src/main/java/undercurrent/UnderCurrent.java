package undercurrent;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import undercurrent.commands.UCBlocksCommands;
import undercurrent.proxy.CommonProxy;
import undercurrent.reference.ModInfo;
import undercurrent.server.ServerWrapper;
import net.minecraftforge.common.MinecraftForge;

import java.util.logging.Logger;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, acceptedMinecraftVersions = "1.7.10")
public class UnderCurrent {

    public static Logger logger = Logger.getLogger("UnderCurrent");

    @SidedProxy(clientSide = "undercurrent.proxy.ClientProxy", serverSide = "undercurrent.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(ModInfo.ID)
    public static UnderCurrent instance;

    @EventHandler
    public void init(FMLInitializationEvent event) {

        System.out.println("UnderCurrent: Starting Framework. ");
        logger.info("UnderCurrent: Registering events.");
        MinecraftForge.EVENT_BUS.register(new UCEventHandler());
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        logger.info("UnderCurrent: Starting Server.");
        ServerWrapper.startUCServer();
        System.out.println("UnderCurrent: Registering commands.");
        event.registerServerCommand(new UCBlocksCommands());
    }

    @EventHandler
    public void serverStopping(FMLServerStoppedEvent event) {
        logger.info("UnderCurrent: Stopping Server.");
        ServerWrapper.stopUCServer();
    }
}
