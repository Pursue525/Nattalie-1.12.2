package net.pursue.ui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;

import java.awt.*;

public class RenderAir {

    private static float endWidth;
    private static int a = 0;

    public static void draw(EntityPlayer entityplayer, float x, float y) {
        if (entityplayer.getTotalArmorValue() == 0) {
            y += 9;
        }

        if (Minecraft.getMinecraft().player.getAir() < 300) {
            a = (int) AnimationUtils.smooth(200, a, 8f / Minecraft.getDebugFPS());
        } else {
            a = (int) AnimationUtils.smooth(0, a, 8f / Minecraft.getDebugFPS());
        }

        double airPercentage = MathHelper.clamp((Minecraft.getMinecraft().player.getAir()) / (300.0), 0, 1);
        endWidth = (float) AnimationUtils.smooth((float) Math.max(0, (76) * airPercentage), endWidth, 8f / Minecraft.getDebugFPS());

        RoundedUtils.drawRound(x, y, 80, 3, 0, new Color(0,0,0,a));

        RoundedUtils.drawRound(x + 2, y + 1, endWidth, 1, 0, new Color(82, 188, 255, a));
    }
}
