package net.pursue.mode.hud;

import net.pursue.Nattalie;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.value.values.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClickGUI extends Mode {
    public static ClickGUI instance;
    public final BooleanValue<Boolean> chinese = new BooleanValue<>(this, "Chinese", false);

    public final ColorValue<Integer> color = new ColorValue<>(this, "Category-Color", Color.BLUE.getRGB());

    public ClickGUI() {
        super("ClickGUI", "模块管理器", "调整一切模块的界面", Category.HUD);
        setKey(Keyboard.KEY_RSHIFT);
        instance = this;
    }

    @Override
    public void enable() {
        mc.displayGuiScreen(Nattalie.instance.getPursueGUI());
        setEnable(false);
    }
}
