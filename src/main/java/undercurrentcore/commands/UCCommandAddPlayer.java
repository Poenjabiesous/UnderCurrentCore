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
import undercurrentcore.persist.UCPlayerDTO;
import undercurrentcore.persist.UCPlayersWorldData;

import java.util.List;
import java.util.UUID;

public class UCCommandAddPlayer extends CommandBase {
    @Override
    public String getCommandName() {
        return "ucaddplayer";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ucaddplayer <player name>";
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

                UCPlayersWorldData data = UCPlayersWorldData.get();

                if (data != null) {

                    Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY + (2 + (player.getEyeHeight() - player.getDefaultEyeHeight())), player.posZ);
                    Vec3 vec3a = player.getLookVec();
                    Vec3 vec3b = vec3.addVector(vec3a.xCoord * 5.0F, vec3a.yCoord * 5.0F, vec3a.zCoord * 5.0F);

                    MovingObjectPosition mop = player.worldObj.rayTraceBlocks(vec3, vec3b);

                    if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        if (player.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof IUCTile) {
                            String secretKey = data.getPlayerSecretKeyForUUID(player.getUniqueID());

                            if (secretKey == null) {
                                return;
                            }

                            UCBlockDTO block = data.getBlockBySecretKeyAndCoords(secretKey, mop.blockX, mop.blockY, mop.blockZ, player.dimension);

                            if (block != null) {

                                UUID playerAddUUID = findPlayer(params[0]);

                                if (playerAddUUID == null) {
                                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                            "UnderCurrent: " +
                                            EnumChatFormatting.WHITE +
                                            StatCollector.translateToLocal("ucaddplayer.error.notfound")));
                                    return;
                                }

                                String secretKeyPlayerAdd = data.getPlayerSecretKeyForUUID(playerAddUUID);

                                if (secretKeyPlayerAdd == null) {
                                    if (playerAddUUID == null) {
                                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                                "UnderCurrent: " +
                                                EnumChatFormatting.WHITE +
                                                StatCollector.translateToLocal("ucaddplayer.error.notreg")));
                                        return;
                                    }
                                }

                                if (data.playerOwnsBlock(secretKey, block)) {
                                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                            "UnderCurrent: " +
                                            EnumChatFormatting.WHITE +
                                            StatCollector.translateToLocal("ucaddplayer.error.already")));
                                    return;
                                }

                                if (data.addBlockToPlayer(secretKeyPlayerAdd, block)) {

                                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                            "UnderCurrent: " +
                                            EnumChatFormatting.WHITE +
                                            StatCollector.translateToLocal("ucaddplayer.info")));
                                    return;
                                }

                                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                        "UnderCurrent: " +
                                        EnumChatFormatting.WHITE +
                                        StatCollector.translateToLocal("ucaddplayer.error.block")));

                            } else {
                                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                        "UnderCurrent: " +
                                        EnumChatFormatting.WHITE +
                                        StatCollector.translateToLocal("ucaddplayer.error.ownership")));
                            }

                        } else {
                            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                    "UnderCurrent: " +
                                    EnumChatFormatting.WHITE +
                                    StatCollector.translateToLocal("ucaddplayer.error.block")));
                        }

                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                                "UnderCurrent: " +
                                EnumChatFormatting.WHITE +
                                StatCollector.translateToLocal("ucaddplayer.error.block")));
                    }
                }

            } catch (Exception e) {
                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            }
        }
    }

    private UUID findPlayer(String playerName) {

        List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;

        for (EntityPlayerMP player : players) {
            if (player.getDisplayName().equals(playerName)) {
                return player.getUniqueID();
            }
        }
        return null;
    }

}