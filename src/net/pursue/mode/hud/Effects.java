package net.pursue.mode.hud;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.mode.Mode;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.category.Category;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.NumberValue;

import java.awt.*;

public class Effects extends Mode {

    private final BooleanValue<Boolean> blur = new BooleanValue<>(this, "Blur", true);
    private final NumberValue<Number> blurInt = new NumberValue<>(this,"BlurInt", 10, 1, 100, 1, blur::getValue);

    public Effects() {
        super("Effects", "药水", "显示你身上所有的药水效果", Category.HUD);
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

            h = 24 + (24 * mc.player.getActivePotionEffects().size());

            for (PotionEffect pot : mc.player.getActivePotionEffects()) {
                String time = Potion.getPotionDurationString(pot,1f);

                draw(x, y + i, time, pot);

                i += 24;
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

    private void draw(float x, float y, String time, PotionEffect potionEffect) {
        int iconIndex = potionEffect.getPotion().getStatusIconIndex();

        if (blur.getValue()) {
            RoundedUtils.drawRoundBlur(x - 2, y - 2, 24, 22, 2, new Color(0, 0, 0, 200), blurInt.getValue().intValue());
        } else {
            RoundedUtils.drawRound(x - 2, y - 2, 24, 22, 2, new Color(0, 0, 0, 200));
        }

        mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
        GlStateManager.enableBlend();

        mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        new Gui().drawTexturedModalRect(x, y, iconIndex % 8 * 18, 198 + iconIndex / 8 * 18, 18, 18);

        if (blur.getValue()) {
            RoundedUtils.enableDrawBlur(mc);
            RoundedUtils.drawRound_Rectangle(FontManager.font20, time, x + 25, y, 2, Color.WHITE, new Color(0, 0, 0, 120), 4, 4, true);
            RoundedUtils.disableDrawBlur(blurInt.getValue().intValue());
        }
        RoundedUtils.drawRound_Rectangle(FontManager.font20, time, x + 25, y, 2, Color.WHITE, new Color(0, 0, 0,120), 4, 4, true);
    }
}
