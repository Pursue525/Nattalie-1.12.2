package net.pursue.mode.hud;

import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.mode.Mode;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.FontUtils;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.category.Category;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

import java.awt.*;
import java.util.List;

public class Arraylist extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Right);

    enum mode {
        Right,
        Left,
    }

    public final ColorValue<Color> color = new ColorValue<>(this ,"StringColor", Color.WHITE);

    private final BooleanValue<Boolean> blur = new BooleanValue<>(this, "Blur", true);
    private final NumberValue<Number> blurInt = new NumberValue<>(this,"BlurInt", 10, 1, 100, 1, blur::getValue);

    public Arraylist() {
        super("ArrayList", "模块列表", "显示已经开启的模块", Category.HUD);
    }

    @EventTarget
    public void onEvent(EventRender2D e) {
        FontUtils fontManager = FontManager.font16;

        List<Mode> enableMods = Nattalie.instance.getModeManager().getEnableMods();
        float width = 0;
        int height = 0;
        float modeSou = 0;
        float modeX = 0;

        float x = 900;
        float y = 2;

        enableMods.sort((o1, o2) -> (fontManager.getWidth(o2.getName() + (o2.getSuffix() != null ? " <" + o2.getSuffix() + ">" : ""))) - (fontManager.getWidth(o1.getName() + (o1.getSuffix() != null ? " <" + o1.getSuffix() + ">" : ""))));

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = data.getX();
                y = data.getY();
            }
        }

        if (!enableMods.isEmpty()) {

            width = enableMods.getFirst().getSuffix() != null ? fontManager.getWidth(enableMods.getFirst().getName() + " <" + enableMods.getFirst().getSuffix() + ">") : fontManager.getWidth(enableMods.getFirst().getName());

            for (Mode mode : enableMods) {
                if (mode.getCategory() == Category.HUD || mode.getCategory() == Category.EXPLOIT || mode.getCategory() == Category.RENDER) continue;

                String modeName = mode.getName() + (mode.getSuffix() != null ? " <" + mode.getSuffix() + ">" : "");

                switch ((mode) modeValue.getValue()) {
                    case Right: {
                        modeSou = fontManager.getWidth(modeName);
                        modeX = 150;
                        break;
                    }
                    case Left: {
                        modeSou = 0;
                        modeX = 0;
                        break;
                    }
                }

                if (blur.getValue()) {
                    RoundedUtils.enableDrawBlur(mc);
                    RoundedUtils.drawRound_Rectangle(fontManager, modeName, x - modeSou + modeX, y + height, 0, color.getColor(), new Color(0, 0, 0, 120), 2, 2, true);
                    RoundedUtils.disableDrawBlur(blurInt.getValue().intValue());
                }

                RoundedUtils.drawRound_Rectangle(fontManager, modeName, x - modeSou + modeX, y + height, 0, color.getColor(), new Color(0, 0, 0, 120), 2, 2, true);


                RoundedUtils.drawRound(x - modeSou + (modeValue.getValue().equals(Arraylist.mode.Right) ? RoundedUtils.width : 0) - 4 + modeX, y + height, 1, RoundedUtils.height, 0, color.getColor());
                height += fontManager.getHeight();
            }
        }

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                data.setX(x);
                data.setY(y);
                data.setWidth(width);
                data.setHeight(height);
            }
        }
    }
}
