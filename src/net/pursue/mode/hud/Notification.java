package net.pursue.mode.hud;

import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;

import net.pursue.value.values.ColorValue;

import java.awt.*;

public class Notification extends Mode {

    public static Notification INSTANCE;

    public final ColorValue<Integer> colorValue = new ColorValue<>(this, "color", Color.WHITE.getRGB());

    public Notification() {
        super("Notification", "模块提示", "显示一些提示，如模块开启/关闭", Category.HUD);
        INSTANCE = this;
    }
}
