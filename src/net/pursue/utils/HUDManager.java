package net.pursue.utils;

import net.pursue.Nattalie;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.client.UtilsManager;

import java.util.ArrayList;
import java.util.List;

public class HUDManager extends UtilsManager {

    public static List<HUDData> data = new ArrayList<>();

    public HUDManager() {
        for (Mode mode : Nattalie.instance.getModeManager().getModes()) {
            if (mode.getCategory().equals(Category.HUD) && !mode.getModeName().equals("ClickGUI")) {
                HUDManager.data.add(new HUDData(mode.getModeName(), 0,0,0,0));
            }
        }
    }
}
