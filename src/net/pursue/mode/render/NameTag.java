package net.pursue.mode.render;

import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;

public class NameTag extends Mode {

    public static NameTag instance;

    public NameTag() {
        super("NameTag", "名称标签", "显示其他玩家的名称", Category.RENDER);
        instance = this;
    }
}
