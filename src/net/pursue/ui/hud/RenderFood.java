package net.pursue.ui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.MathUtils;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;

import java.awt.*;

public class RenderFood {

    private static float endWidth;

    public static void draw(EntityPlayer entityplayer, float x, float y) {
        Color healthColor = new Color(255, 115,0, 200);
        Color background = new Color(0,0,0,200);

        float level = entityplayer.getFoodStats().getFoodLevel();
        float maxLevel = 20;
        double levelPercentage = MathHelper.clamp((level) / (maxLevel), 0, 1);

        endWidth = (float) AnimationUtils.smooth((float) Math.max(0, (80) * levelPercentage) - 4, endWidth, 8f / Minecraft.getDebugFPS());

        String food = String.valueOf(Math.round(level * 20.0) / 10.0);

        RoundedUtils.drawRound(x, y, 80, 12, 0, background);
        RoundedUtils.drawRound(x + 2, y + 2, endWidth, 8, 0, healthColor);
        FontManager.font18.drawString(food, x + 4, y + MathUtils.centre(12, FontManager.font18.getHeight()) + 3, Color.WHITE);

    }
}
