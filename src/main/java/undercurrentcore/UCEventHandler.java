package undercurrentcore;

import api.undercurrent.iface.IUCTile;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
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

                boolean registered = data.checkPlayerOnUUID(player.getUniqueID());

                if (registered) {

                    String secretKey = data.getPlayerSecretKey(player.getUniqueID());
                    if (secretKey == null) {
                        return;
                    }

                    player.addChatComponentMessage(new ChatComponentText("UnderCurrentCore: Already Registered. You have " + data.getUCPlayerInfo(secretKey).getBlocks().size() + " blocks that you can administer."));
                    return;
                }

                boolean registration = data.addPlayer(RandomStringUtils.randomAlphanumeric(6), player.getUniqueID());

                if (registration) {
                    player.addChatComponentMessage(new ChatComponentText("UnderCurrentCore: Registration successful."));
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
                    return;
                }

                boolean added = data.addBlockToPlayer(secretKey, new UCBlockDTO(event.x, event.y, event.z, event.player.worldObj.provider.dimensionId, "new_" + event.block.getLocalizedName(), RandomStringUtils.randomAlphabetic(10)));
                if (added) {
                    event.player.addChatComponentMessage(new ChatComponentText("UnderCurrentCore: Registered new block for you at: <" + event.x + "> <" + event.y + "> <" + event.z + "> World: " + event.player.worldObj.provider.getDimensionName()));
                } else {
                    event.player.addChatComponentMessage(new ChatComponentText("Cannot add a new UnderCurrentCore block to your profile. Please try to relog, and try again."));
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().worldObj.isRemote) {
            if (event.getPlayer() instanceof EntityPlayer && event.block instanceof BlockContainer && ((BlockContainer) event.block).createNewTileEntity(event.getPlayer().worldObj, 0) instanceof IUCTile) {

                UCPlayersWorldData data = (UCPlayersWorldData) MinecraftServer.getServer().getEntityWorld().perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);
                String secretKey = data.getPlayerSecretKey(event.getPlayer().getUniqueID());

                if (secretKey == null) {
                    return;
                }

                boolean removed = data.removeBlockFromPlayer(secretKey, new UCBlockDTO(event.x, event.y, event.z, event.getPlayer().worldObj.provider.dimensionId, "", ""));
                if (removed) {
                    event.getPlayer().addChatComponentMessage(new ChatComponentText("UnderCurrentCore: Unregistered UnderCurrentCore block at: <" + event.x + "> <" + event.y + "> <" + event.z + "> World: " + event.getPlayer().worldObj.provider.getDimensionName()));
                } else {
                    event.getPlayer().addChatComponentMessage(new ChatComponentText("Cannot remove UnderCurrentCore block from your profile, it did not exist."));
                    event.setCanceled(true);
                }
            }
        }
    }
}