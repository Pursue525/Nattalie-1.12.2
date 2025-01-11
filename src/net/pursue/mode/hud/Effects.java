package net.pursue.mode.hud;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
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

public class Effects extends Mode {

    private final ColorValue<Integer> color = new ColorValue<>(this, "Color", Color.WHITE.getRGB());

    public Effects() {
        super("Effects", "显示药水", "显示你身上所有的药水效果", Category.HUD);
    }

    @EventTarget
    private void onRender2D(EventRender2D eventRender2D) {
        float x = 300;
        float y = 150;
        float w = 120;
        float h = 7;


        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = data.getX();
                y = data.getY();
            }
        }

        if (!mc.player.getActivePotionEffects().isEmpty()) {
            int i = 0;

            h = 7 + (26 * mc.player.getActivePotionEffects().size());

            RoundedUtils.drawRound(x - 5, y - 5, 120, h, 2, new Color(0,0,0,120));

            for (PotionEffect pot : mc.player.getActivePotionEffects()) {
                int amplifier = pot.getAmplifier();
                int l = amplifier + 1;

                String name = null;

                if (ClickGUI.instance.chinese.getValue()) {
                    name = I18n.format(pot.getPotion().getName()) + " lv." + l;
                } else {
                    String[] lines = pot.getPotion().getName().split("\n");
                    for (String lineContent : lines) {
                        String[] parts = lineContent.split("\\.");
                        if (parts.length > 1) {
                            String potionName = parts[1].trim();
                            if (!potionName.isEmpty()) {
                                potionName = potionName.substring(0, 1).toUpperCase() + potionName.substring(1);
                            }
                            name = potionName + " lv." + l;
                        }
                    }
                }

                String time = Potion.getPotionDurationString(pot,1f);

                draw(x, y + i, time, name, pot);

                i += 26;
            }
        }

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                data.setX(x);
                data.setY(y);
                data.setWidth(w);
                data.setHeight(h);
            }
        }
    }

    private void draw(float x, float y, String time, String name, PotionEffect potionEffect) {
        int iconIndex = potionEffect.getPotion().getStatusIconIndex();

        RoundedUtils.drawRound(x, y, 110, 22, 2, new Color(0, 0, 0, 120));

        mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
        GlStateManager.enableBlend();

        mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        new Gui().drawTexturedModalRect(x + 2, y + 2, iconIndex % 8 * 18, 198 + iconIndex / 8 * 18, 18, 18);

        FontManager.font20.drawString(name, x + 23, y + 2, color.getColorRGB());
        FontManager.font20.drawString(time, x + 23, y + FontManager.font20.getHeight(), color.getColorRGB());
    }
}
