package net.pursue.mode.client;

import net.pursue.mode.Mode;
import net.pursue.ui.gui.Config;
import net.pursue.utils.category.Category;

public class ConfigGUI extends Mode {


    public ConfigGUI() {
        super("ConfigManager", "配置管理器", "管理你的配置列表", Category.CLIENT);
    }

    @Override
    public void enable() {
        mc.displayGuiScreen(new Config());
        setEnable(false);
    }

}
