package undercurrentcore.commands;

import api.undercurrent.iface.IUCTile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraftforge.common.DimensionManager;
import undercurrentcore.persist.UCBlockDTO;
import undercurrentcore.persist.UCPlayersWorldData;

import java.util.List;
import java.util.UUID;

public class UCCommandName extends CommandBase {
    @Override
    public String getCommandName() {
        return "ucname";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ucname <name>";
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
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                            "UnderCurrent: " +
                            EnumChatFormatting.WHITE +
                            StatCollector.translateToLocal("ucname.error.length")));
                    return;
                }

                if (params[0].toString().length() > 20) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                            "UnderCurrent: " +
                            EnumChatFormatting.WHITE +
                            StatCollector.translateToLocal("ucname.error.length")));
                    return;
                }

                UCPlayersWorldData data = (UCPlayersWorldData) DimensionManager.getWorld(0).perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

                if (data != null) {

                    Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY + (2 + (player.getEyeHeight() - player.getDefaultEyeHeight())), player.posZ);
                    Vec3 vec3a = player.getLookVec();
                    Vec3 vec3b = vec3.addVector(vec3a.xCoord * 5.0F, vec3a.yCoord * 5.0F, vec3a.zCoord * 5.0F);

                    MovingObjectPosition mop = player.worldObj.rayTraceBlocks(vec3, vec3b);

                    if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        if (player.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof IUCTile) {
                            UCBlockDTO block = new UCBlockDTO(mop.blockX, mop.blockY, mop.blockZ, player.worldObj.provider.dimensionId, params[0].toString(), "");
                            String secretKey = data.getPlayerSecretKey(player.getUniqueID());

                            if (secretKey == null) {
                                return;
                            }

                            if (data.updateBlockName(secretKey, block)) {
                                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                        "UnderCurrent: " +
                                        EnumChatFormatting.WHITE +
                                        StatCollector.translateToLocal("ucname.info")));
                                return;
                            } else {
                                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                        "UnderCurrent: " +
                                        EnumChatFormatting.WHITE +
                                        StatCollector.translateToLocal("ucname.error.ownership")));
                                return;
                            }

                        } else {
                            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                    "UnderCurrent: " +
                                    EnumChatFormatting.WHITE +
                                    StatCollector.translateToLocal("ucname.error.block")));
                            return;
                        }

                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                "UnderCurrent: " +
                                EnumChatFormatting.WHITE +
                                StatCollector.translateToLocal("ucname.error.block")));
                        return;
                    }
                }

            } catch (Exception e) {
                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            }
        }
    }
}