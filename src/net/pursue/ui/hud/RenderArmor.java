package net.pursue.ui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;

import java.awt.*;

public class RenderArmor {

    private static float endWidth;

    public static void draw(EntityPlayer entityplayer, float x, float y) {
        if (entityplayer.getTotalArmorValue() == 0) return;

        RoundedUtils.drawRound(x, y, 80, 7, 0, new Color(0,0,0,200));

        double airPercentage = MathHelper.clamp((entityplayer.getTotalArmorValue()) / (20.0), 0, 1);
        endWidth = (float) AnimationUtils.smooth((float) Math.max(0, (76) * airPercentage), endWidth, 8f / Minecraft.getDebugFPS());

        RoundedUtils.drawRound(x + 2, y + 2, endWidth, 3, 0, Color.LIGHT_GRAY);
    }
}

