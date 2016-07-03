package undercurrent.commands;

import api.undercurrent.iface.UCTileEntity;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.DimensionManager;
import undercurrent.persist.UCBlockDTO;
import undercurrent.persist.UCPlayersWorldData;

public class UCBlocksCommands extends CommandBase {
    @Override
    public String getCommandName() {
        return "uc";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/uc <count>/see <index>/name <name>";
    }


    @Override
    public void processCommand(ICommandSender sender, String[] params) {

        if (params.length < 1) {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
        }

        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            try {
                if (params[0].equals("count")) {
                    UCPlayersWorldData data = (UCPlayersWorldData) player.worldObj.perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

                    if (data != null) {
                        int blocks = data.getUCPlayerInfo(player.getUniqueID().toString()).getBlocks().size();
                        player.addChatComponentMessage(new ChatComponentText("UnderCurrent: You have " + blocks + " registered blocks that you can administer."));
                        return;
                    }
                }

                if (params[0].equals("see") && params.length > 2) {
                    UCPlayersWorldData data = (UCPlayersWorldData) player.worldObj.perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);
                    if (data != null) {
                        UCBlockDTO block = data.getBlockByIndex(player.getUniqueID().toString(), Integer.parseInt(params[1]));
                        if (block != null) {
                            sender.addChatMessage(new ChatComponentText("UC Block @ index: " + params[1] + "\n"
                                    + "X-Coord: " + block.getxCoord() + "\n"
                                    + "Y-Coord: " + block.getyCoord() + "\n"
                                    + "Z-Coord: " + block.getzCoord() + "\n"
                                    + "Dimension: " + DimensionManager.getWorld(block.getDim()).provider.getDimensionName() + "\n"
                                    + "Name: " + block.getName()));
                            return;
                        } else {
                            sender.addChatMessage(new ChatComponentText("UnderCurrent: No block found for that index number."));
                            return;
                        }
                    }
                }

                if (params[0].equals("name") && params.length > 2) {

                    if (params[1].toString().length() < 8) {
                        sender.addChatMessage(new ChatComponentText("UnderCurrent: A block's name must be at least 8 characters long."));
                        return;
                    }

                    UCPlayersWorldData data = (UCPlayersWorldData) player.worldObj.perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);
                    if (data != null) {

                        MovingObjectPosition mop = player.rayTrace(5.0D, 1.0F);

                        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                            if (player.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof UCTileEntity) {
                                UCBlockDTO block = new UCBlockDTO(mop.blockX, mop.blockY, mop.blockZ, player.worldObj.provider.dimensionId, params[1].toString());
                                boolean updated = data.updateBlockName(player.getUniqueID().toString(), block);
                                if (updated) {
                                    sender.addChatMessage(new ChatComponentText("UnderCurrent: Block name updated."));
                                    return;
                                } else {
                                    sender.addChatMessage(new ChatComponentText("UnderCurrent: You do not own that block."));
                                    return;
                                }

                            } else {
                                sender.addChatMessage(new ChatComponentText("UnderCurrent: You are not looking at an UnderCurrent applicable block."));
                                return;
                            }

                        } else {
                            sender.addChatMessage(new ChatComponentText("UnderCurrent: You are not looking at an applicable block."));
                            return;
                        }
                    }
                }

                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));

            } catch (Exception e) {
                sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
            }
        }


    }
}