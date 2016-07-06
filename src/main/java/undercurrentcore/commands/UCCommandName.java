package undercurrentcore.commands;

import api.undercurrent.iface.IUCTile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.DimensionManager;
import undercurrentcore.persist.UCBlockDTO;
import undercurrentcore.persist.UCPlayersWorldData;

public class UCCommandName extends CommandBase {
    @Override
    public String getCommandName() {
        return "ucname";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "ucname <name>";
    }


    @Override
    public void processCommand(ICommandSender sender, String[] params) {

        if (params.length < 1) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
        }

        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            try {

                if (params.length > 1) {
                    sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
                    return;
                }

                if (params[0].toString().length() < 4) {
                    sender.addChatMessage(new ChatComponentText("UnderCurrentCore: A block's name must be at least 4 characters long."));
                    return;
                }

                UCPlayersWorldData data = (UCPlayersWorldData) MinecraftServer.getServer().getEntityWorld().perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

                if (data != null) {

                    MovingObjectPosition mop = player.rayTrace(5.0D, 1.0F);

                    if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        if (player.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof IUCTile) {
                            UCBlockDTO block = new UCBlockDTO(mop.blockX, mop.blockY, mop.blockZ, player.worldObj.provider.dimensionId, params[0].toString(), "");
                            String secretKey = data.getPlayerSecretKey(player.getUniqueID());

                            if (secretKey == null) {
                                sender.addChatMessage(new ChatComponentText("UnderCurrentCore: You are not registered."));
                                return;
                            }

                            if (data.updateBlockName(secretKey, block)) {
                                sender.addChatMessage(new ChatComponentText("UnderCurrentCore: Block name updated."));
                                return;
                            } else {
                                sender.addChatMessage(new ChatComponentText("UnderCurrentCore: You do not own that block."));
                                return;
                            }

                        } else {
                            sender.addChatMessage(new ChatComponentText("UnderCurrentCore: You are not looking at an UnderCurrentCore applicable block."));
                            return;
                        }

                    } else {
                        sender.addChatMessage(new ChatComponentText("UnderCurrentCore: You are not looking at a block."));
                        return;
                    }
                }

            } catch (Exception e) {
                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            }
        }
    }
}