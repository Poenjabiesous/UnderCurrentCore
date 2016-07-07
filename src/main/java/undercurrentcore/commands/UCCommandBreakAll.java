package undercurrentcore.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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

                UCPlayersWorldData data = (UCPlayersWorldData) MinecraftServer.getServer().getEntityWorld().perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

                String secretKey = data.getPlayerSecretKey(player.getUniqueID());

                if (secretKey == null) {
                    return;
                }

                UCPlayerDTO playerDto = data.getUCPlayerInfo(secretKey);

                for (UCBlockDTO block : playerDto.getBlocks()) {
                    DimensionManager.getProvider(block.getDim()).worldObj.getBlock(block.getxCoord(), block.getyCoord(), block.getzCoord()).harvestBlock(DimensionManager.getProvider(block.getDim()).worldObj, player, block.getxCoord(), block.getyCoord(), block.getzCoord(), 1);
                }

            } catch (Exception e) {
                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            }
        }
    }
}