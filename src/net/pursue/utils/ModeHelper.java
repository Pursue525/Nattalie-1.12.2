package net.pursue.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.pursue.Nattalie;
import net.pursue.event.EventManager;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.event.render.EventRender3D;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.hud.Notification;
import net.pursue.mode.player.Scaffold;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.utils.rotation.RotationUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import javax.swing.text.Utilities;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ModeHelper {

    private final Minecraft mc = Minecraft.getMinecraft();

    private double countscale = 0;

    public ModeHelper() {
        EventManager.instance.register(this);

        File fil = new File(Minecraft.getMinecraft().mcDataDir, "Nattalie/VerifyVideo");
        File file = new File(fil, "Verify.mp4");

        if (!fil.exists()) {
            fil.mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
                unpackFile(file, "assets/minecraft/nattalie/mp4/Verify.mp4");
            } catch (IOException e) {
                System.out.print("无法生成视频:  " + e);
            }
        }
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

        float width = 50 + FontManager.font18.getStringWidth(String.valueOf(getBlock()));
        float x = (sr.getScaledWidth() / 2F - width / 2) - 10;
        float y = (sr.getScaledHeight() / 2F + 12) + 160;
        float height = 18;
        if (Scaffold.INSTANCE.blocks.getValue()) {
            GL11.glPushMatrix();
            GL11.glTranslated(x + (width / 2F), y + (height / 2F), 0);
            GL11.glScaled(countscale, 1, countscale);
            GL11.glTranslated(-(x + (width / 2F)), -(y + (height / 2F)), 0);
            RoundedUtils.drawRound(x, y, width + 24, height, 1, new Color(0,0,0,125));

            this.drawItemStack(Scaffold.INSTANCE.slot >= 0 ? mc.player.inventoryContainer.getSlot(Scaffold.INSTANCE.slot + 36).getStack() : mc.player.getHeldItem(EnumHand.MAIN_HAND), x + 1, y + 1);

            if (getBlock() > 0) FontManager.font18.drawString("Remainder: " + getBlock(), x + 18, y + 6, Scaffold.INSTANCE.color.getColorRGB());

            GL11.glPopMatrix();
        }
    }

    @EventTarget
    private void onWorld(EventWorldLoad eventWorldLoad) {
        Nattalie.instance.getNotificationManager().stopNoti();
    }

    private boolean isBlock(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemBlock;
    }

    private int getBlock() {
        int blockCount = 0;
        for (int slotIndex = 9; slotIndex < mc.player.inventoryContainer.inventorySlots.size(); slotIndex++) {
            ItemStack stack = mc.player.getSlotFromPlayerContainer(slotIndex).getStack();

            if (isBlock(stack)) {
                blockCount += stack.stackSize;
            }
        }
        return blockCount;
    }

    private void drawItemStack(ItemStack itemStack, float x, float y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) x, (int) y);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
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
}
