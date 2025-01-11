package net.pursue.mode.hud;


import net.minecraft.client.Minecraft;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.value.values.ColorValue;

import java.awt.*;

public class Logo extends Mode {

    private final ColorValue<Integer> colorValue = new ColorValue<>(this, "color", Color.WHITE.getRGB());

    public Logo() {
        super("Logo", "客户端标识", "显示出客户端名称", Category.HUD);
    }

    @EventTarget
    public void onRender(EventRender2D render2D) {
        float x = 1;
        float y = 1;

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = data.getX();
                y = data.getY();
            }
        }

        String text = Nattalie.instance.getClientName() + " " + Nattalie.instance.getClientVersion() +" || FPS-" + Minecraft.getDebugFPS();
        int textWidth = mc.fontRendererObj.getStringWidth(text);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        mc.fontRendererObj.drawString(text, (int) x, (int) y, colorValue.getColorRGB());

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                data.setX(x);
                data.setY(y);
                data.setWidth(textWidth);
                data.setHeight(textHeight);
            }
        }
    }
}
