package undercurrentcore;

import api.undercurrent.iface.IUCTile;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent;
import org.apache.commons.lang3.RandomStringUtils;
import undercurrentcore.persist.UCBlockDTO;
import undercurrentcore.persist.UCPlayersWorldData;

public class UCEventHandler {

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.entity != null && !event.entity.worldObj.isRemote) {
            if (event.entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.entity;

                UCPlayersWorldData data = (UCPlayersWorldData) MinecraftServer.getServer().getEntityWorld().perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

                if (data == null) {
                    data = new UCPlayersWorldData(UCPlayersWorldData.GLOBAL_TAG);
                    MinecraftServer.getServer().getEntityWorld().perWorldStorage.setData(UCPlayersWorldData.GLOBAL_TAG, data);
                }

                boolean registered = data.checkPlayerOnUUID(player.getUniqueID());

                if (registered) {
                    String secretKey = data.getPlayerSecretKey(player.getUniqueID());
                    if (secretKey == null) {
                        event.setCanceled(true);
                        return;
                    }
                }

                boolean registration = data.addPlayer(RandomStringUtils.randomAlphanumeric(6), player.getUniqueID());

                if (registration) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                            "UnderCurrent: " +
                            EnumChatFormatting.WHITE +
                            StatCollector.translateToLocal("register.info")
                    ));
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.player != null && !event.player.worldObj.isRemote) {
            if (event.player instanceof EntityPlayer && event.placedBlock instanceof BlockContainer && ((BlockContainer) event.placedBlock).createNewTileEntity(event.player.worldObj, 0) instanceof IUCTile) {

                UCPlayersWorldData data = (UCPlayersWorldData) MinecraftServer.getServer().getEntityWorld().perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);
                String secretKey = data.getPlayerSecretKey(event.player.getUniqueID());

                if (secretKey == null) {
                    event.setCanceled(true);
                    return;
                }

                boolean added = data.addBlockToPlayer(secretKey, new UCBlockDTO(event.x, event.y, event.z, event.player.worldObj.provider.dimensionId, "new_" + event.block.getLocalizedName(), RandomStringUtils.randomAlphabetic(10)));
                if (added) {

                    event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                            "UnderCurrent: " +
                            EnumChatFormatting.WHITE +
                            StatCollector.translateToLocal("blockPlaced.info.1") +
                            ": " +
                            "<" + event.x + "> <" + event.y + "> <" + event.z + "> Dim: " + event.player.worldObj.provider.getDimensionName() +
                            ". " +
                            StatCollector.translateToLocal("blockPlaced.info.2")
                    ));
                }
            }
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().worldObj.isRemote) {
            if (event.getPlayer() instanceof EntityPlayer && event.block instanceof BlockContainer && ((BlockContainer) event.block).createNewTileEntity(event.getPlayer().worldObj, 0) instanceof IUCTile) {

                UCPlayersWorldData data = (UCPlayersWorldData) MinecraftServer.getServer().getEntityWorld().perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);
                String secretKey = data.getPlayerSecretKey(event.getPlayer().getUniqueID());

                if (secretKey == null) {
                    event.setCanceled(true);
                    return;
                }

                boolean removed = data.removeBlockFromPlayer(secretKey, new UCBlockDTO(event.x, event.y, event.z, event.getPlayer().worldObj.provider.dimensionId, "", ""));
                if (removed) {

                    event.getPlayer().addChatComponentMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                            "UnderCurrent: " +
                            EnumChatFormatting.WHITE +
                            StatCollector.translateToLocal("blockBroken.info.1") +
                            ": " +
                            "<" + event.x + "> <" + event.y + "> <" + event.z + "> Dim: " + event.getPlayer().worldObj.provider.getDimensionName()
                    ));
                }
            }
        } else {
            event.setCanceled(true);
        }
    }
}