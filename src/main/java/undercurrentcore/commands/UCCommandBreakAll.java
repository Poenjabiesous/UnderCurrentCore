package undercurrentcore.commands;

import api.undercurrent.iface.IUCTile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.DimensionManager;
import undercurrentcore.persist.UCBlockDTO;
import undercurrentcore.persist.UCPlayerDTO;
import undercurrentcore.persist.UCPlayersWorldData;

public class UCCommandBreakAll extends CommandBase {
    @Override
    public String getCommandName() {
        return "ucbreakallblocks";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ucbreakallblocks";
    }


    @Override
    public void processCommand(ICommandSender sender, String[] params) {

        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            try {

                UCPlayersWorldData data = (UCPlayersWorldData) DimensionManager.getWorld(0).perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

                String secretKey = data.getPlayerSecretKeyForUUID(player.getUniqueID());

                if (secretKey == null) {
                    return;
                }

                int amountDropped = 0;

                for (UCBlockDTO block : data.getPlayerBlocks(secretKey)) {

                    if (DimensionManager.getProvider(block.getDim()).worldObj.getTileEntity(block.getxCoord(), block.getyCoord(), block.getzCoord()) instanceof IUCTile) {
                        DimensionManager.getProvider(block.getDim()).worldObj.func_147480_a(block.getxCoord(), block.getyCoord(), block.getzCoord(), true);
                        data.removeBlockFromPlayer(secretKey, block);
                        amountDropped++;
                    }
                }

                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                        "UnderCurrent: " +
                        EnumChatFormatting.WHITE +
                        StatCollector.translateToLocal("ucbreak.info") +
                        ": " +

                        EnumChatFormatting.GOLD +
                        amountDropped));

            } catch (Exception e) {
                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            }
        }
    }
}