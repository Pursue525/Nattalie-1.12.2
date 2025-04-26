package net.pursue.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.pursue.Nattalie;
import net.pursue.event.EventManager;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.client.ClickGUI;
import net.pursue.mode.exploit.PacketManager;
import net.pursue.mode.player.AutoHeal;
import net.pursue.mode.player.Blink;
import net.pursue.mode.player.Scaffold;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.FontUtils;
import net.pursue.utils.player.InvUtils;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ModeHelper {

    private final Minecraft mc = Minecraft.getMinecraft();

    private double countscale = 0;

    private double animationY = -40;
    private double animY = -40;
    private double animationX;

    public void init() {
        File fil = new File(Minecraft.getMinecraft().mcDataDir, "Nattalie/Video");
        File file = new File(fil, "miyabi.mp4");

        if (!fil.exists()) {
            fil.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                unpackFile(file, "assets/minecraft/nattalie/mp4/miyabi.mp4");
            } catch (IOException e) {
                System.out.print("无法生成视频:  " + e);
            }
        }
        loadPacket();
        EventManager.instance.register(this);
    }

    @EventTarget
    private void onRender2D(EventRender2D event) {
        Nattalie.instance.getNotificationManager().drawNotifications();

        ScaledResolution sr = event.getScaledResolution();

        if (Scaffold.INSTANCE.isEnable()) {
            countscale = AnimationUtils.moveUD((float) countscale, (float) 1, (float) (5 * RoundedUtils.deltaTime()), (float) (4 * RoundedUtils.deltaTime()));
        } else {
            countscale = AnimationUtils.moveUD((float) countscale, (float) 0, (float) (5 * RoundedUtils.deltaTime()), (float) (4 * RoundedUtils.deltaTime()));
        }

        float width = 50 + FontManager.font18.getStringWidth(String.valueOf(InvUtils.getBlockIndex()));
        float x = (sr.getScaledWidth() / 2F - width / 2) - 10;
        float y = (sr.getScaledHeight() / 2F + 12) + 10;
        float height = 18;

        if (Scaffold.INSTANCE.blocks.getValue()) {

            GL11.glPushMatrix();
            GL11.glTranslated(x + (width / 2F), y + (height / 2F), 0);
            GL11.glScaled(countscale, 1, countscale);
            GL11.glTranslated(-(x + (width / 2F)), -(y + (height / 2F)), 0);

            RoundedUtils.drawRound(x, y, width + 24, height, 1, new Color(0, 0, 0, 125));


            RoundedUtils.drawStack(Scaffold.INSTANCE.slot >= 0 ? mc.player.inventoryContainer.getSlot(Scaffold.INSTANCE.slot + 36).getStack() : mc.player.getHeldItem(EnumHand.MAIN_HAND), x + 1, y + 1);

            String blocks = "Blocks: ";

            if (ClickGUI.instance.chinese.getValue()) blocks = "剩余方块数: ";

            if (InvUtils.getBlockIndex() > 0)
                FontManager.font18.drawString(blocks + InvUtils.getBlockIndex(), x + 18, y + 6, Scaffold.INSTANCE.color.getColorRGB());

            GL11.glPopMatrix();
        }

        FontUtils fontManager = FontManager.font16;

        int w = 340;
        x = (float) sr.getScaledWidth() / 2 - 170;

        if (AutoHeal.instance.modeValue.getValue() == AutoHeal.mode.Golden_Apple) {

            animationY = AnimationUtils.smooth(AutoHeal.instance.isEnable() ? 10 : -40, animationY, 8f / Minecraft.getDebugFPS());

            RoundedUtils.drawRound((float) x, (float) animationY, w, 15 + fontManager.getHeight(), 1, new Color(0, 0, 0, 180));

            fontManager.drawString("Packets", (float) (x + 170 - (fontManager.getWidth("Packets") / 2)), (float) (animationY + 5), AutoHeal.instance.colorValue.getColorRGB());

            RoundedUtils.drawRound((float) x + 5, (float) animationY + fontManager.getHeight() + 4, 330, 3, 1, Color.BLACK);

            RoundedUtils.drawRound((float) x + 5, (float) animationY + fontManager.getHeight() + 4, AutoHeal.tick * 10, 3, 1, AutoHeal.instance.colorValue.getColor());
        }

        animY = AnimationUtils.smooth(Blink.instance.isEnable() ? 70 : -40, animY, 8f / Minecraft.getDebugFPS());

        if (animY != -40) {
            double maxhealthnonnull = 9500;
            double healthnonnull = Blink.instance.getTime();
            double healthPercentage = MathHelper.clamp((healthnonnull) / (maxhealthnonnull), 0, 1);
            w = 210;

            x = (float) sr.getScaledWidth() / 2 - 105;
            float endWidth = (float) Math.max(0, 200 * healthPercentage);

            RoundedUtils.drawRound((float) x, (float) animY, w, 15 + fontManager.getHeight(), 1, new Color(0, 0, 0, 180));

            String ps = "Packets- " + Blink.instance.getPackets();

            if (Blink.instance.getPackets() > 5 && !Blink.pollPacketIng) {
                fontManager.drawString(ps, x + 5, (float) (animY + 5), Blink.instance.colorValue.getColorRGB());
                fontManager.drawString("C0FPackets- " + Blink.instance.c0fs, (x + 15) + fontManager.getWidth(ps), (float) (animY + 5), Blink.instance.colorValue.getColorRGB());
            } else {
                ps = "Release Packet...";

                fontManager.drawString(ps, x + 5, (float) (animY + 5), Blink.instance.colorValue.getColorRGB());
            }


            RoundedUtils.drawRound((float) x + 5, (float) animY + fontManager.getHeight() + 4, 200, 3, 1, Color.BLACK);

            RoundedUtils.drawRound((float) x + 5, (float) animY + fontManager.getHeight() + 4, endWidth, 3, 1, Blink.instance.colorValue.getColor());
        }
    }

    @EventTarget
    private void onWorld(EventWorldLoad eventWorldLoad) {
        Nattalie.instance.getNotificationManager().stopNoti();
    }


    public static void unpackFile(File file, String name) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(file);
        try {
            IOUtils.copy(Objects.requireNonNull(ModeHelper.class.getClassLoader().getResourceAsStream(name)), fos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * PacketManager
     */

    private void loadPacket() {
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, C00Handshake.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketConfirmTeleport.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketTabComplete.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketChatMessage.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketClientStatus.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketClientSettings.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketConfirmTransaction.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEnchantItem.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketClickWindow.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCloseWindow.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCustomPayload.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUseEntity.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketKeepAlive.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.Position.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.PositionRotation.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.Rotation.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketVehicleMove.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSteerBoat.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlaceRecipe.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerAbilities.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerDigging.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEntityAction.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketInput.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketRecipeInfo.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketResourcePackStatus.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSeenAdvancements.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketHeldItemChange.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCreativeInventoryAction.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUpdateSign.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketAnimation.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSpectate.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerTryUseItemOnBlock.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerTryUseItem.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketServerQuery.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPing.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketLoginStart.class);
        PacketManager.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEncryptionResponse.class);

        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPong.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, net.minecraft.network.login.server.SPacketDisconnect.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEncryptionRequest.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketLoginSuccess.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEnableCompression.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketServerInfo.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnObject.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnExperienceOrb.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnGlobalEntity.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnMob.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnPainting.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnPlayer.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketAnimation.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketStatistics.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketBlockBreakAnim.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateTileEntity.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketBlockAction.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketBlockChange.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateBossInfo.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketServerDifficulty.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTabComplete.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChat.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMultiBlockChange.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketConfirmTransaction.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCloseWindow.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketOpenWindow.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketWindowItems.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketWindowProperty.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSetSlot.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCooldown.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCustomPayload.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCustomSound.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDisconnect.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityStatus.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketExplosion.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUnloadChunk.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChangeGameState.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketKeepAlive.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChunkData.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEffect.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketParticles.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketJoinGame.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMaps.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.S15PacketEntityRelMove.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.S17PacketEntityLookMove.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.S16PacketEntityLook.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMoveVehicle.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSignEditorOpen.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlaceGhostRecipe.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerAbilities.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCombatEvent.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerListItem.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerPosLook.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUseBed.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketRecipeBook.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDestroyEntities.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketRemoveEntityEffect.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketResourcePackSend.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketRespawn.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityHeadLook.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSelectAdvancementsTab.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketWorldBorder.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCamera.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketHeldItemChange.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDisplayObjective.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityMetadata.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityAttach.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityVelocity.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityEquipment.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSetExperience.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateHealth.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketScoreboardObjective.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSetPassengers.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTeams.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateScore.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnPosition.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTimeUpdate.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTitle.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSoundEffect.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerListHeaderFooter.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCollectItem.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityTeleport.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketAdvancementInfo.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityProperties.class);
        PacketManager.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityEffect.class);

        Nattalie.instance.getModeManager().addClass(new PacketManager());
    }
}
