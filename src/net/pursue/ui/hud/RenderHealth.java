package net.pursue.ui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.MathUtils;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;

import java.awt.*;

public class RenderHealth {

    private static float preW;
    private static float endWidth;

    public static void draw(EntityPlayer entityplayer, float x, float y) {
        Color healthColor = new Color(0,255,0, 200);
        Color background = new Color(0,0,0,200);

        double maxhealthnonnull = entityplayer.getMaxHealth() <= 0 ? 0 : entityplayer.getMaxHealth();

        double absorptionAmount = entityplayer.getAbsorptionAmount() <= 0 ? 0 : entityplayer.getAbsorptionAmount();

        double healthnonnull = entityplayer.getHealth() <= 0 ? 0 : entityplayer.getHealth();

        double healthAA = MathHelper.clamp((healthnonnull + absorptionAmount) / (maxhealthnonnull), 0, 1);


        float playerHealth2 = (float) (Math.max(0, (76) * healthAA));

        endWidth = (float) AnimationUtils.smooth(playerHealth2, endWidth, 8f / Minecraft.getDebugFPS());

        String hp = "HP: " + Math.round((entityplayer.getHealth() + entityplayer.getAbsorptionAmount()) * 10.0) / 10.0;
        RoundedUtils.drawRound(x, y, 80, 12, 0, background);

        if (entityplayer.hurtTime == 0) {
            preW = (float) AnimationUtils.smooth(endWidth, preW, 8f / Minecraft.getDebugFPS());
        }

        if (preW != endWidth) {
            RoundedUtils.drawRound((x + 2) + endWidth, y + 2, preW - endWidth, 8, 0, Color.RED);
        }

        RoundedUtils.drawRound(x + 2, y + 2, endWidth, 8, 0, healthColor);

        FontManager.font18.drawString(hp, x + 4, y + MathUtils.centre(12, FontManager.font18.getHeight()) + 3, Color.WHITE);
    }


}
