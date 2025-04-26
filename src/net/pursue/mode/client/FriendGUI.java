package net.pursue.mode.client;

import net.pursue.mode.Mode;
import net.pursue.ui.gui.Friend;
import net.pursue.utils.category.Category;

public class FriendGUI extends Mode {

    public FriendGUI() {
        super("FriendManager", "好友管理器", "管理你的好友", Category.CLIENT);
    }

    @Override
    public void enable() {
        mc.displayGuiScreen(new Friend());
        setEnable(false);
    }
}
