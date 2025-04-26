package net.pursue.mode.hud;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.mode.Mode;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.MathUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.NumberValue;

import java.awt.*;

public class Armor extends Mode {

    private final BooleanValue<Boolean> blur = new BooleanValue<>(this, "Blur", true);
    private final NumberValue<Number> blurInt = new NumberValue<>(this,"BlurInt", 10, 1, 100, 1, blur::getValue);

    public Armor() {
        super("Armor", "盔甲显示", "显示出你身上的盔甲", Category.HUD);
    }

    private float w;

    @EventTarget
    private void onRender2D(EventRender2D eventRender2D) {
        float x = 540;
        float y = 450;

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = data.getX();
                y = data.getY();
            }
        }

        FontManager.font16.drawString(this.getName(), x + 2, y + 2, Color.WHITE.getRGB());

        if (blur.getValue()) {
            RoundedUtils.drawRoundBlur(x, y, 80, FontManager.font16.getHeight() + 16, 2, new Color(0, 0, 0, 80), blurInt.getValue().intValue());
        } else {
            RoundedUtils.drawRound(x, y, 80, FontManager.font16.getHeight() + 16, 2, new Color(0, 0, 0, 80));
        }

        FontManager.font16.drawString(this.getName(), x + 2, y + 2, Color.WHITE.getRGB());

        w = drawPlayerEquipment(getPlayerEquipment(mc.player), x + MathUtils.centre(80, w), y + FontManager.font16.getHeight() - 2, true);

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                data.setX(x);
                data.setY(y);
                data.setWidth(80);
                data.setHeight(15);
            }
        }
    }

    public static ItemStack[] getPlayerEquipment(EntityPlayer entity) {
        return new ItemStack[]{entity.inventory.armorInventory.get(3), entity.inventory.armorInventory.get(2), entity.inventory.armorInventory.get(1), entity.inventory.armorInventory.get(0)};
    }

    public static float drawPlayerEquipment(ItemStack[] equipment, float x, float y, boolean transverse) {
        float xOffset = x;
        float yOffset = y;

        float width = 0;
        float height = 0;

        if (transverse) {
            for (ItemStack stack : equipment) {
                if (stack != null) {
                    drawItemStack(stack, xOffset, yOffset);
                }
                xOffset += 18;
                width += 18;
            }
        } else {
            for (ItemStack stack : equipment) {
                if (stack != null) {
                    drawItemStack(stack, xOffset, yOffset);
                }
                yOffset += 18;
                height += 18;
            }
        }
        return transverse ? width : height;
    }

    public static float drawPlayerEquipment2(ItemStack[] equipment, float x, float y, boolean transverse) {
        float xOffset = x;
        float yOffset = y;

        float width = 0;
        float height = 0;

        if (transverse) {
            for (ItemStack stack : equipment) {
                if (stack != null) {
                    drawItemStack(stack, xOffset, yOffset);
                }
                xOffset -= 18;
                width += 18;
            }
        } else {
            for (ItemStack stack : equipment) {
                if (stack != null) {
                    drawItemStack(stack, xOffset, yOffset);
                }
                yOffset -= 18;
                height += 18;
            }
        }
        return transverse ? width : height;
    }

    public static void drawItemStack(ItemStack itemStack, float x, float y) {
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
}
