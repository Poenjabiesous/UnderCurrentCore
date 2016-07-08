package undercurrentcore;

import api.undercurrent.iface.IUCTile;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.opengl.GL11;
import tv.twitch.chat.ChatEvent;
import undercurrentcore.persist.UCBlockDTO;
import undercurrentcore.persist.UCPlayersWorldData;
import undercurrentcore.server.ServerWrapper;

import java.awt.*;
import java.io.IOException;

public class UCEventHandler {

    private RenderManager renderManager = RenderManager.instance;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void renderWorldLastEvent(RenderWorldLastEvent event) {

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;

        Vec3 vec3 = Vec3.createVectorHelper(player.posX, player.posY + ((player.getEyeHeight() - player.getDefaultEyeHeight())), player.posZ);
        Vec3 vec3a = player.getLookVec();
        Vec3 vec3b = vec3.addVector(vec3a.xCoord * 5.0F, vec3a.yCoord * 5.0F, vec3a.zCoord * 5.0F);

        MovingObjectPosition mop = player.worldObj.rayTraceBlocks(vec3, vec3b);

        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (player.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof IUCTile) {

                UCPlayersWorldData data = (UCPlayersWorldData) MinecraftServer.getServer().getEntityWorld().perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);
                String secretKey = data.getPlayerSecretKey(player.getUniqueID());

                if (secretKey == null) {
                    return;
                }

                UCBlockDTO block = data.getBlockBySecretKeyAndCoords(secretKey, mop.blockX, mop.blockY, mop.blockZ, player.dimension);

                if (block == null) {
                    return;
                }

                if (!(player.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof IUCTile)) {
                    return;
                }

                String[] lines = new String[1];
                lines[0] = block.getName();

                RenderFloatingText(lines, (float) block.getxCoord() + 0.5F, (float) block.getyCoord() + 1.0F, (float) block.getzCoord() +0.5F, Color.LIGHT_GRAY.hashCode(), true, event.partialTicks);
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (event.entity != null && !event.entity.worldObj.isRemote) {
            if (event.entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.entity;

                UCPlayersWorldData data = (UCPlayersWorldData) MinecraftServer.getServer().getEntityWorld().perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);

                if (data == null) {
                    data = new UCPlayersWorldData(UCPlayersWorldData.GLOBAL_TAG);
                    MinecraftServer.getServer().getEntityWorld().perWorldStorage.setData(UCPlayersWorldData.GLOBAL_TAG, data);
                }

                boolean registered = data.checkPlayerOnUUID(player.getUniqueID());

                if (registered) {
                    String secretKey = data.getPlayerSecretKey(player.getUniqueID());
                    if (secretKey == null) {
                        event.setCanceled(true);
                        return;
                    }
                }

                boolean registration = data.addPlayer(RandomStringUtils.randomAlphanumeric(6), player.getUniqueID());

                if (registration) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                            "UnderCurrent: " +
                            EnumChatFormatting.WHITE +
                            StatCollector.translateToLocal("register.info")
                    ));
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
                    event.setCanceled(true);
                    return;
                }

                boolean added = data.addBlockToPlayer(secretKey, new UCBlockDTO(event.x, event.y, event.z, event.player.worldObj.provider.dimensionId, "new_" + event.block.getLocalizedName(), RandomStringUtils.randomAlphabetic(10)));
                if (added) {

                    event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                            "UnderCurrent: " +
                            EnumChatFormatting.WHITE +
                            StatCollector.translateToLocal("blockPlaced.info.1") +
                            ": " +
                            "<" + event.x + "> <" + event.y + "> <" + event.z + "> Dim: " + event.player.worldObj.provider.getDimensionName() +
                            ". " +
                            StatCollector.translateToLocal("blockPlaced.info.2")
                    ));
                }
            }
        } else {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().worldObj.isRemote) {
            if (event.getPlayer() instanceof EntityPlayer && event.block instanceof BlockContainer && ((BlockContainer) event.block).createNewTileEntity(event.getPlayer().worldObj, 0) instanceof IUCTile) {

                UCPlayersWorldData data = (UCPlayersWorldData) MinecraftServer.getServer().getEntityWorld().perWorldStorage.loadData(UCPlayersWorldData.class, UCPlayersWorldData.GLOBAL_TAG);
                String secretKey = data.getPlayerSecretKey(event.getPlayer().getUniqueID());

                if (secretKey == null) {
                    event.setCanceled(true);
                    return;
                }

                boolean removed = data.removeBlockFromPlayer(secretKey, new UCBlockDTO(event.x, event.y, event.z, event.getPlayer().worldObj.provider.dimensionId, "", ""));
                if (removed) {

                    event.getPlayer().addChatComponentMessage(new ChatComponentText(EnumChatFormatting.AQUA +
                            "UnderCurrent: " +
                            EnumChatFormatting.WHITE +
                            StatCollector.translateToLocal("blockBroken.info.1") +
                            ": " +
                            "<" + event.x + "> <" + event.y + "> <" + event.z + "> Dim: " + event.getPlayer().worldObj.provider.getDimensionName()
                    ));
                }
            }
        } else {
            event.setCanceled(true);
        }
    }

    public FontRenderer getFontRendererFromRenderManager() {
        return this.renderManager.getFontRenderer();
    }

    public static void RenderFloatingText(String[] text, float x, float y, float z, int color, boolean renderBlackBox, float partialTickTime) {

        //Thanks to Electric-Expansion mod for the majority of this code
        //https://github.com/Alex-hawks/Electric-Expansion/blob/master/src/electricexpansion/client/render/RenderFloatingText.java

        Minecraft mc = Minecraft.getMinecraft();
        RenderManager renderManager = RenderManager.instance;
        FontRenderer fontRenderer = mc.fontRenderer;

        float playerX = (float) (mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTickTime);
        float playerY = (float) (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTickTime);
        float playerZ = (float) (mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTickTime);

        float dx = x - playerX;
        float dy = y - playerY;
        float dz = z - playerZ;
        float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        float multiplier = distance / 120f; //mobs only render ~120 blocks away
        float scale = 0.9f * multiplier;

        GL11.glColor4f(1f, 1f, 1f, 0.5f);
        GL11.glPushMatrix();
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int textWidth = 0;
        for (String thisMessage : text) {
            int thisMessageWidth = mc.fontRenderer.getStringWidth(thisMessage);

            if (thisMessageWidth > textWidth)
                textWidth = thisMessageWidth;
        }

        int lineHeight = 10;

        if (renderBlackBox) {
            Tessellator tessellator = Tessellator.instance;
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tessellator.startDrawingQuads();
            int stringMiddle = textWidth / 2;
            tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.5F);
            tessellator.addVertex(-stringMiddle - 1, -1 + 0, 0.0D);
            tessellator.addVertex(-stringMiddle - 1, 8 + lineHeight * text.length - lineHeight, 0.0D);
            tessellator.addVertex(stringMiddle + 1, 8 + lineHeight * text.length - lineHeight, 0.0D);
            tessellator.addVertex(stringMiddle + 1, -1 + 0, 0.0D);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        int i = 0;
        for (String message : text) {
            fontRenderer.drawString(message, -textWidth / 2, i * lineHeight, color);
            i++;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }
}