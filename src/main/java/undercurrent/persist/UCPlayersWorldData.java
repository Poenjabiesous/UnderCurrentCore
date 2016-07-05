package undercurrent.persist;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;


/**
 * Created by Niel Verster on 5/26/2015.
 */
public class UCPlayersWorldData extends WorldSavedData {

    private NBTTagList data = new NBTTagList();
    public static String GLOBAL_TAG = "undercurrentdata";

    public UCPlayersWorldData(String tagName) {
        super(tagName);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        data = compound.getTagList("ucRegisteredPlayers", Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setTag("ucRegisteredPlayers", data);
    }

    public boolean addPlayer(String uuid) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString("uuid").equals(uuid)) ;
            {
                return false;
            }
        }
        NBTTagCompound newPlayer = new NBTTagCompound();
        newPlayer.setString("uuid", uuid);
        NBTTagList blocks = new NBTTagList();
        newPlayer.setTag("blocks", blocks);
        data.appendTag(newPlayer);
        markDirty();
        return true;
    }

    public boolean checkPlayer(String uuid) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString("uuid").equals(uuid)) ;
            {
                return true;
            }
        }
        return false;
    }

    public boolean addBlockToPlayer(String uuid, UCBlockDTO newBlock) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString("uuid").equals(uuid)) ;
            {
                NBTTagCompound blockToBeAdded = new NBTTagCompound();
                blockToBeAdded.setInteger("xCoord", newBlock.getxCoord());
                blockToBeAdded.setInteger("yCoord", newBlock.getyCoord());
                blockToBeAdded.setInteger("zCoord", newBlock.getzCoord());
                blockToBeAdded.setInteger("dim", newBlock.getDim());
                blockToBeAdded.setString("name", newBlock.getName());
                blockToBeAdded.setString("internalName", newBlock.getInternalName());
                player.getTagList("blocks", Constants.NBT.TAG_COMPOUND).appendTag(blockToBeAdded);
                markDirty();
                return true;
            }
        }
        return false;
    }

    public boolean removeBlockFromPlayer(String uuid, UCBlockDTO oldBlock) {

        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString("uuid").equals(uuid)) ;
            {
                NBTTagList blocks = player.getTagList("blocks", Constants.NBT.TAG_COMPOUND);

                for (int j = 0; j < blocks.tagCount(); j++) {
                    System.out.println("Iteration " + j);
                    NBTTagCompound currentBlock = blocks.getCompoundTagAt(j);
                    if (currentBlock.getInteger("xCoord") == oldBlock.getxCoord()
                            && currentBlock.getInteger("yCoord") == oldBlock.getyCoord()
                            && currentBlock.getInteger("zCoord") == oldBlock.getzCoord()
                            && currentBlock.getInteger("dim") == oldBlock.getDim()) {
                        blocks.removeTag(j);
                        markDirty();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public UCBlockDTO getBlockByIndex(String uuid, int index) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString("uuid").equals(uuid)) ;
            {
                NBTTagList blocks = player.getTagList("blocks", Constants.NBT.TAG_COMPOUND);

                if (index + 1 > blocks.tagCount() || index + 1 <= 0) {
                    return null;
                }

                NBTTagCompound block = blocks.getCompoundTagAt(i);
                if (block == null) {
                    return null;
                }
                return new UCBlockDTO(block.getInteger("xCoord"), block.getInteger("yCoord"), block.getInteger("zCoord"), block.getInteger("dim"), block.getString("name"), block.getString("internalName"));
            }
        }
        return null;
    }

    public UCBlockDTO getBlockByInternalName(String uuid, String internalName) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString("uuid").equals(uuid)) ;
            {
                NBTTagList blocks = player.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j <= blocks.tagCount(); j++) {
                    NBTTagCompound block = blocks.getCompoundTagAt(j);
                    if (block.getString("internalName").equals(internalName)) {
                        return new UCBlockDTO(block.getInteger("xCoord"), block.getInteger("yCoord"), block.getInteger("zCoord"), block.getInteger("dim"), block.getString("name"), block.getString("internalName"));
                    }
                }
            }
        }
        return null;
    }

    public boolean updateBlockName(String uuid, UCBlockDTO newBlock) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString("uuid").equals(uuid)) ;
            {
                NBTTagList blocks = player.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j < blocks.tagCount(); j++) {
                    NBTTagCompound currentBlock = blocks.getCompoundTagAt(j);
                    if (currentBlock.getString("internalName").equals(newBlock.getInternalName())) {
                        currentBlock.setString("name", newBlock.getName());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public UCPlayerDTO getUCPlayerInfo(String uuid) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString("uuid").equals(uuid)) {
                UCPlayerDTO UCPlayerDTO = new UCPlayerDTO();
                UCPlayerDTO.setUuid(player.getString("uuid"));

                ArrayList<UCBlockDTO> blocks = new ArrayList<UCBlockDTO>();
                NBTTagList blocksNBT = player.getTagList("blocks", Constants.NBT.TAG_COMPOUND);

                for (int j = 0; j < blocksNBT.tagCount(); j++) {
                    NBTTagCompound block = blocksNBT.getCompoundTagAt(j);
                    UCBlockDTO newBlock = new UCBlockDTO(block.getInteger("xCoord"), block.getInteger("yCoord"), block.getInteger("zCoord"), block.getInteger("dim"), block.getString("name"), block.getString("internalName"));
                    blocks.add(newBlock);
                }

                UCPlayerDTO.setBlocks(blocks);
                return UCPlayerDTO;
            }
        }
        return null;
    }

}