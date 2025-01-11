package net.pursue.mode.render;

import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.value.values.ModeValue;

public class Animation extends Mode {

    public static Animation instance;

    public final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal_1_7);
    public enum mode {
        Normal_1_7,
        Shield
    }

    public Animation() {
        super("Animation", "防砍动画", "渲染出美丽的防砍动画awa", Category.RENDER);
        instance = this;
    }
}
