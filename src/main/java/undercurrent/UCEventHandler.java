package undercurrent;

import api.undercurrent.iface.UCTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.BlockEvent;
import org.apache.commons.lang3.RandomStringUtils;
import undercurrent.persist.UCBlockDTO;
import undercurrent.persist.UCPlayersWorldData;

public class UCEventHandler {

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.entity != null && !event.entity.worldObj.isRemote) {
            if (event.entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.entity;
                System.out.println("UnderCurrent: Checking registered status of " + player.getDisplayName());
                UCPlayersWorldData data = (UCPlayersWorldData) player.worldObj.perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

                if (data == null) {
                    data = new UCPlayersWorldData(UCPlayersWorldData.GLOBAL_TAG);
                    player.worldObj.perWorldStorage.setData(UCPlayersWorldData.GLOBAL_TAG, data);
                }

                boolean registered = data.addPlayer(player.getUniqueID().toString());
                if (registered) {
                    player.addChatComponentMessage(new ChatComponentText("UnderCurrent: Registration successful."));
                } else {
                    player.addChatComponentMessage(new ChatComponentText("UnderCurrent: Already Registered. You have " + data.getUCPlayerInfo(player.getUniqueID().toString()).getBlocks().size() + " blocks that you can administer."));
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        if (event.player != null && !event.player.worldObj.isRemote) {
            if (event.player instanceof EntityPlayer && event.placedBlock instanceof BlockContainer && ((BlockContainer) event.placedBlock).createNewTileEntity(event.player.worldObj, 0) instanceof UCTileEntity) {
                UCPlayersWorldData data = (UCPlayersWorldData) event.player.worldObj.perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

                boolean added = data.addBlockToPlayer(event.player.getUniqueID().toString(), new UCBlockDTO(event.x, event.y, event.z, event.player.worldObj.provider.dimensionId, "new" + event.block.getLocalizedName(), RandomStringUtils.randomAlphabetic(10)));
                if (added) {
                    event.player.addChatComponentMessage(new ChatComponentText("UnderCurrent: Registered new block for you at: <" + event.x + "> <" + event.y + "> <" + event.z + "> World: " + event.player.worldObj.provider.getDimensionName()));
                } else {
                    event.player.addChatComponentMessage(new ChatComponentText("Cannot add a new UnderCurrent block to your profile. Please try to relog, and try again."));
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().worldObj.isRemote) {
            if (event.getPlayer() instanceof EntityPlayer && event.block instanceof BlockContainer && ((BlockContainer) event.block).createNewTileEntity(event.getPlayer().worldObj, 0) instanceof UCTileEntity) {
                UCPlayersWorldData data = (UCPlayersWorldData) event.getPlayer().worldObj.perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);
                boolean removed = data.removeBlockFromPlayer(event.getPlayer().getUniqueID().toString(), new UCBlockDTO(event.x, event.y, event.z, event.getPlayer().worldObj.provider.dimensionId, "", ""));
                if (removed) {
                    event.getPlayer().addChatComponentMessage(new ChatComponentText("UnderCurrent: Unregistered UnderCurrent block at: <" + event.x + "> <" + event.y + "> <" + event.z + "> World: " + event.getPlayer().worldObj.provider.getDimensionName()));
                } else {
                    event.getPlayer().addChatComponentMessage(new ChatComponentText("Cannot remove UnderCurrent block from your profile, it did not exist."));
                    event.setCanceled(true);
                }
            }
        }
    }
}