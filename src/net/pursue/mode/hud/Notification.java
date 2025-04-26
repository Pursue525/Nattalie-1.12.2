package net.pursue.mode.hud;

import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.NumberValue;

import java.awt.*;

public class Notification extends Mode {

    public static Notification INSTANCE;

    public final ColorValue<Color> colorValue = new ColorValue<>(this, "StringColor", Color.WHITE);
    public final ColorValue<Color> backValue = new ColorValue<>(this, "BackColor", new Color(0,0,0,120));

    public final BooleanValue<Boolean> render = new BooleanValue<>(this, "Render", true);
    public final ColorValue<Color> renderValue = new ColorValue<>(this, "RenderColor", new Color(150,150,150,120), render::getValue);
    public final BooleanValue<Boolean> blur = new BooleanValue<>(this, "Blur", true);
    public final NumberValue<Number> blurInt = new NumberValue<>(this,"BlurInt", 10, 1, 100, 1, blur::getValue);

    public Notification() {
        super("Notification", "模块提示", "显示一些提示，如模块开启/关闭", Category.HUD);
        INSTANCE = this;
    }
}
