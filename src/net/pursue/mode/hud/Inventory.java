package net.pursue.mode.hud;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.ColorValue;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Inventory extends Mode {

    private final ColorValue<Integer> colorValue = new ColorValue<>(this, "Color", Color.WHITE.getRGB());

    public Inventory() {
        super("Inventory", "显示背包", "显示背包中的物品", Category.HUD);
    }

    private int i = 0;

    @EventTarget
    private void onRender2D(EventRender2D eventRender2D) {
        int x = 10;
        int y = 30;

        net.minecraft.inventory.Container container = null;
        String name = "";

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = (int) data.getX();
                y = (int) data.getY();
            }
        }

        glPushMatrix();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();

        RoundedUtils.drawRound(x, y, 172, 75, 2, new Color(0, 0, 0, 120));

        RoundedUtils.drawRound_Rectangle(FontManager.font24, ClickGUI.instance.chinese.getValue() ? "背包" : "Inventory", x + 5, y + 3, 2, colorValue.getColor(), new Color(0, 0, 0, 80), 0, 0, true);

        for (int i1 = 9; i1 < mc.player.inventoryContainer.inventorySlots.size() - 10; ++i1) {
            Slot slot = mc.player.inventoryContainer.inventorySlots.get(i1);
            int i = slot.xDisplayPosition;
            int j = slot.yDisplayPosition;
            mc.getRenderItem().renderItemAndEffectIntoGUI(slot.getStack(), (int) (x + i - 2), (int) (y + j - 65));
            mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, slot.getStack(), x + i - 2, (int) y + j - 65, slot.getStack().stackSize <= 1 ? String.valueOf(slot.getStack().stackSize) : null);
        }

        glPopMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableDepth();

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                data.setX(x);
                data.setY(y);
                data.setWidth(172);
                data.setHeight(75);
            }
        }
    }
}
