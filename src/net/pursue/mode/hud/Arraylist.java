package net.pursue.mode.hud;

import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.RapeMasterFontManager;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.ModeValue;

import java.awt.*;
import java.util.List;

public class Arraylist extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Right);

    enum mode {
        Right,
        Left,
    }


    private final BooleanValue<Boolean> align = new BooleanValue<>(this, "Align", false);

    public final ColorValue<Integer> color = new ColorValue<>(this ,"StringColor", Color.WHITE.getRGB());

    public Arraylist() {
        super("Arraylist", "模块列表", "显示已经开启的模块", Category.HUD);
    }

    @EventTarget
    public void onEvent(EventRender2D e) {
        RapeMasterFontManager fontManager = FontManager.font20;

        List<Mode> enableMods = Nattalie.instance.getModeManager().getEnableMods();
        float x = 900;
        float y = 2;

        if (align.getValue()) {
            enableMods.sort((o1, o2) -> fontManager.getStringWidth(o2.getName()) - fontManager.getStringWidth(o1.getName()));
        }

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = data.getX();
                y = data.getY();
            }
        }

        float w = fontManager.getWidth(enableMods.getFirst().getName() + enableMods.getFirst().getSuffix());

        float h = modeValue.getValue().equals(mode.Right) ? w : 0;

        int heightY = 0;
        for (Mode mode : enableMods) {
            if (mode.getCategory().equals(Category.HUD) || mode.getCategory().equals(Category.RENDER) || mode.getCategory().equals(Category.EXPLOIT)) continue;

            float modeSou = 0;
            switch ((mode) modeValue.getValue()) {
                case Right: {
                    modeSou = fontManager.getWidth(mode.getName());
                    break;
                }
                case Left: {
                    modeSou = 0;
                    break;
                }
            }

            RoundedUtils.drawRound_Rectangle(fontManager, mode.getName(), x - modeSou + h, y + heightY,0, color.getColor(), new Color(0,0,0,120), 2,2,true);

            RoundedUtils.drawRound(x + h + (modeValue.getValue().equals(Arraylist.mode.Right) ? 2 : -6), y + heightY - 1, 1, RoundedUtils.height,0, color.getColor());
            heightY += fontManager.getHeight();
        }

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                data.setX(x);
                data.setY(y);
                data.setWidth(w);
                data.setHeight(heightY);
            }
        }
    }
}
