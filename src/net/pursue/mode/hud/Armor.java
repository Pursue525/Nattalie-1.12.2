package net.pursue.mode.hud;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.render.RoundedUtils;

import java.awt.*;

public class Armor extends Mode {

    public Armor() {
        super("Armor", "盔甲显示", "显示出你身上的盔甲", Category.HUD);
    }

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


        RoundedUtils.drawRound(x, y + 1, 70, 14, 2, new Color(0, 0, 0, 80));

        drawPlayerEquipment(getPlayerEquipment(mc.player), x, y, true);

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                data.setX(x);
                data.setY(y);
                data.setWidth(70);
                data.setHeight(15);
            }
        }
    }

    public static ItemStack[] getPlayerEquipment(EntityPlayer entity) {
        ItemStack[] equipment = new ItemStack[4];

        for (int i = 0; i < 4; i++) {
            ItemStack stack = entity.inventory.armorInventory.get(i);

            if (!stack.func_190926_b()) {
                equipment[i] = stack;
            } else {
                equipment[i] = null;
            }
        }

        return equipment;
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
