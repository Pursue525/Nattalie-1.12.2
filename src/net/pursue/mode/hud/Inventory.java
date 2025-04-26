package net.pursue.mode.hud;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.mode.Mode;
import net.pursue.mode.client.ClickGUI;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.MathUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.NumberValue;

import java.awt.*;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

public class Inventory extends Mode {

    private final ColorValue<Color> colorValue = new ColorValue<>(this, "Color", Color.WHITE);

    private final BooleanValue<Boolean> blur = new BooleanValue<>(this, "Blur", true);
    private final NumberValue<Number> blurInt = new NumberValue<>(this,"BlurInt", 10, 1, 100, 1, blur::getValue);

    public Inventory() {
        super("Inventory", "背包", "显示背包中的物品", Category.HUD);
    }


    @EventTarget
    private void onRender2D(EventRender2D eventRender2D) {
        int x = 10;
        int y = 30;

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = (int) data.getX();
                y = (int) data.getY();
            }
        }

        glPushMatrix();
        GlStateManager.disableDepth();
        RenderHelper.disableStandardItemLighting();

        if (blur.getValue()) {
            RoundedUtils.drawRoundBlur(x, y, 172, 75, 2, new Color(0, 0, 0, 120), blurInt.getValue().intValue());
        } else {
            RoundedUtils.drawRound(x, y, 172, 75, 2, new Color(0, 0, 0, 120));
        }

        FontManager.font16.drawString(this.getName(), x + 86 - (FontManager.font16.getWidth(this.getName()) / 2), y + 3, colorValue.getColorRGB());

        RoundedUtils.drawRound(x + 4, y + FontManager.font16.getHeight() + 4, 164, 1, 0, colorValue.getColor());

        if (mc.currentScreen == null) {

            for (int i1 = 9; i1 < mc.player.inventoryContainer.inventorySlots.size() - 10; ++i1) {
                Slot slot = mc.player.inventoryContainer.inventorySlots.get(i1);
                int i = slot.xDisplayPosition;
                int j = slot.yDisplayPosition;
                mc.getRenderItem().renderItemAndEffectIntoGUI(slot.getStack(), (int) (x + i - 2), (int) (y + j - 65));
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, slot.getStack(), x + i - 2, (int) y + j - 65, slot.getStack().stackSize <= 1 ? String.valueOf(slot.getStack().stackSize) : null);
            }
        } else {

            String string = "On GUI...";

            if (ClickGUI.instance.chinese.getValue()) {
                string = "在GUI当中...";
            }

            FontManager.font20.drawString(string, x + 4 + MathUtils.centre(172, FontManager.font20.getWidth(string)), y + 3 + MathUtils.centre(75, FontManager.font20.getHeight()) + 2, Color.WHITE);
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
