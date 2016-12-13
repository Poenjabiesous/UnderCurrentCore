package undercurrentcore.persist;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

import javax.crypto.Cipher;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by Niel Verster on 5/26/2015.
 */
public class UCPlayersWorldData extends WorldSavedData {

    private NBTTagList data = new NBTTagList();
    public static String GLOBAL_TAG = "undercurrentdata";
    public static String REGISTERED_PLAYERS = "ucRegisteredPlayers";
    public static String SECRET_KEY = "secretKey";
    public static String BLOCKS = "blocks";
    public static String INTERNAL_NAME = "internalName";
    public static String NAME = "name";
    public static String X_COORD = "xCoord";
    public static String Y_COORD = "yCoord";
    public static String Z_COORD = "zCoord";
    public static String DIM = "dim";
    public static String UUID = "uuid";
    public static String PLAYER_NAME = "displayName";
    public static String REGISTRATION_DATE = "registrationDate";

    public UCPlayersWorldData(String tagName) {
        super(tagName);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        data = compound.getTagList(REGISTERED_PLAYERS, Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        compound.setTag(REGISTERED_PLAYERS, data);
    }

    public static UCPlayersWorldData get() {
        return (UCPlayersWorldData) DimensionManager.getWorld(0).perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);
    }

    public boolean addPlayer(String secretKey, UUID uuid, String playerName) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(UUID).equals(uuid.toString())) {
                return false;
            }
        }

        NBTTagCompound newPlayer = new NBTTagCompound();
        newPlayer.setString(UUID, uuid.toString());
        newPlayer.setString(SECRET_KEY, secretKey);
        newPlayer.setString(PLAYER_NAME, playerName);
        newPlayer.setString(REGISTRATION_DATE, new Date().toString());
        NBTTagList blocks = new NBTTagList();
        newPlayer.setTag(BLOCKS, blocks);
        data.appendTag(newPlayer);
        markDirty();
        return true;
    }

    public boolean validateLogin(String secretKey, String playerName) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(PLAYER_NAME).equals(playerName) && player.getString(SECRET_KEY).equals(secretKey)) {
                return true;
            }
        }
        return false;
    }

    public String getPlayerSecretKeyForUUID(UUID uuid) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(UUID).equals(uuid.toString())) {
                return player.getString(SECRET_KEY);
            }
        }
        return null;
    }

    public boolean checkPlayerOnUUID(UUID uuid) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(UUID).equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean addBlockToPlayer(String secretKey, UCBlockDTO newBlock) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(SECRET_KEY).equals(secretKey)) {

                if (playerOwnsBlock(secretKey, newBlock)) {
                    return false;
                }

                NBTTagCompound blockToBeAdded = new NBTTagCompound();
                blockToBeAdded.setInteger(X_COORD, newBlock.getxCoord());
                blockToBeAdded.setInteger(Y_COORD, newBlock.getyCoord());
                blockToBeAdded.setInteger(Z_COORD, newBlock.getzCoord());
                blockToBeAdded.setInteger(DIM, newBlock.getDim());
                blockToBeAdded.setString(NAME, newBlock.getName());
                blockToBeAdded.setString(INTERNAL_NAME, newBlock.getInternalName());
                player.getTagList(BLOCKS, Constants.NBT.TAG_COMPOUND).appendTag(blockToBeAdded);
                markDirty();
                return true;
            }
        }
        return false;
    }

    public boolean removeBlockFromPlayer(String secretKey, UCBlockDTO oldBlock) {

        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(SECRET_KEY).equals(secretKey)) {
                NBTTagList blocks = player.getTagList(BLOCKS, Constants.NBT.TAG_COMPOUND);

                for (int j = 0; j < blocks.tagCount(); j++) {
                    NBTTagCompound currentBlock = blocks.getCompoundTagAt(j);
                    if (currentBlock.getInteger(X_COORD) == oldBlock.getxCoord()
                            && currentBlock.getInteger(Y_COORD) == oldBlock.getyCoord()
                            && currentBlock.getInteger(Z_COORD) == oldBlock.getzCoord()
                            && currentBlock.getInteger(DIM) == oldBlock.getDim()) {
                        blocks.removeTag(j);
                        markDirty();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public UCBlockDTO getBlockByIndex(String secretKey, int index) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(SECRET_KEY).equals(secretKey)) {
                NBTTagList blocks = player.getTagList(BLOCKS, Constants.NBT.TAG_COMPOUND);

                if (index + 1 > blocks.tagCount() || index + 1 <= 0) {
                    return null;
                }

                NBTTagCompound block = blocks.getCompoundTagAt(i);
                if (block == null) {
                    return null;
                }
                return new UCBlockDTO(block.getInteger(X_COORD), block.getInteger(Y_COORD), block.getInteger(Z_COORD), block.getInteger(DIM), block.getString(NAME), block.getString(INTERNAL_NAME));
            }
        }
        return null;
    }

    public UCBlockDTO getBlockByInternalName(String secretKey, String internalName) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(SECRET_KEY).equals(secretKey)) {
                NBTTagList blocks = player.getTagList(BLOCKS, Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j <= blocks.tagCount(); j++) {
                    NBTTagCompound block = blocks.getCompoundTagAt(j);
                    if (block.getString(INTERNAL_NAME).equals(internalName)) {
                        return new UCBlockDTO(block.getInteger(X_COORD), block.getInteger(Y_COORD), block.getInteger(Z_COORD), block.getInteger(DIM), block.getString(NAME), block.getString(INTERNAL_NAME));
                    }
                }
            }
        }
        return null;
    }

    public UCBlockDTO getBlockBySecretKeyAndCoords(String secretKey, int xCoord, int yCoord, int zCoord, int dim) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(SECRET_KEY).equals(secretKey)) {
                NBTTagList blocks = player.getTagList(BLOCKS, Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j < blocks.tagCount(); j++) {
                    NBTTagCompound currentBlock = blocks.getCompoundTagAt(j);
                    if (currentBlock.getInteger(X_COORD) == xCoord
                            && currentBlock.getInteger(Y_COORD) == yCoord
                            && currentBlock.getInteger(Z_COORD) == zCoord
                            && currentBlock.getInteger(DIM) == dim) {
                        return new UCBlockDTO(currentBlock.getInteger(X_COORD), currentBlock.getInteger(Y_COORD),
                                currentBlock.getInteger(Z_COORD),
                                currentBlock.getInteger(DIM),
                                currentBlock.getString(NAME),
                                currentBlock.getString(INTERNAL_NAME));
                    }
                }
            }
        }
        return null;
    }

    public boolean playerOwnsBlock(String secretKey, UCBlockDTO block) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(SECRET_KEY).equals(secretKey)) {
                NBTTagList blocks = player.getTagList(BLOCKS, Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j < blocks.tagCount(); j++) {
                    NBTTagCompound currentBlock = blocks.getCompoundTagAt(j);
                    if (currentBlock.getInteger(X_COORD) == block.getxCoord()
                            && currentBlock.getInteger(Y_COORD) == block.getyCoord()
                            && currentBlock.getInteger(Z_COORD) == block.getzCoord()
                            && currentBlock.getInteger(DIM) == block.getDim()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean updateBlockName(String secretKey, UCBlockDTO newBlock) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(SECRET_KEY).equals(secretKey)) {
                NBTTagList blocks = player.getTagList(BLOCKS, Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j < blocks.tagCount(); j++) {
                    NBTTagCompound currentBlock = blocks.getCompoundTagAt(j);
                    if (currentBlock.getInteger(X_COORD) == newBlock.getxCoord()
                            && currentBlock.getInteger(Y_COORD) == newBlock.getyCoord()
                            && currentBlock.getInteger(Z_COORD) == newBlock.getzCoord()
                            && currentBlock.getInteger(DIM) == newBlock.getDim()) {
                        currentBlock.setString(NAME, newBlock.getName());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public UCPlayerDTO getPlayerInfo(String secretKey) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(SECRET_KEY).equals(secretKey)) {
                UCPlayerDTO UCPlayerDTO = new UCPlayerDTO(player.getString(UUID), player.getString(PLAYER_NAME), player.getString(REGISTRATION_DATE));
                return UCPlayerDTO;
            }
        }
        return null;
    }

    public List<UCBlockDTO> getPlayerBlocks(String secretKey) {
        for (int i = 0; i < data.tagCount(); i++) {
            NBTTagCompound player = data.getCompoundTagAt(i);
            if (player.getString(SECRET_KEY).equals(secretKey)) {

                ArrayList<UCBlockDTO> blocks = new ArrayList<UCBlockDTO>();
                NBTTagList blocksNBT = player.getTagList(BLOCKS, Constants.NBT.TAG_COMPOUND);

                for (int j = 0; j < blocksNBT.tagCount(); j++) {
                    NBTTagCompound block = blocksNBT.getCompoundTagAt(j);
                    UCBlockDTO newBlock = new UCBlockDTO(block.getInteger(X_COORD), block.getInteger(Y_COORD), block.getInteger(Z_COORD), block.getInteger(DIM), block.getString(NAME), block.getString(INTERNAL_NAME));
                    blocks.add(newBlock);
                }

                return blocks;
            }
        }
        return null;
    }

}