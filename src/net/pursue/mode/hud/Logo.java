package net.pursue.mode.hud;


import net.minecraft.client.Minecraft;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.mode.Mode;
import net.pursue.mode.client.ClickGUI;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.category.Category;
import net.pursue.utils.client.ServerUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.NumberValue;
import net.pursue.value.values.StringValue;

import java.awt.*;

public class Logo extends Mode {

    private final ColorValue<Color> colorValue = new ColorValue<>(this, "color", Color.WHITE);
    private final StringValue<String> stringValue = new StringValue<>(this, "Logo","Nattalie");
    public final BooleanValue<Boolean> severIP = new BooleanValue<>(this, "SeverIP", false);
    public final BooleanValue<Boolean> userName = new BooleanValue<>(this, "UserName", true);
    public final BooleanValue<Boolean> ping = new BooleanValue<>(this, "Ping", false);
    public final BooleanValue<Boolean> fps = new BooleanValue<>(this, "FPS", true);

    private final BooleanValue<Boolean> blur = new BooleanValue<>(this, "Blur", true);
    private final NumberValue<Number> blurInt = new NumberValue<>(this,"BlurInt", 10, 1, 100, 1, blur::getValue);

    public Logo() {
        super("Logo", "客户端标识", "显示出客户端名称", Category.HUD);
    }

    @EventTarget
    public void onRender(EventRender2D render2D) {
        float x = 1;
        float y = 1;

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = data.getX();
                y = data.getY();
            }
        }


        String string = stringValue.getValue() +
                (severIP.getValue() ? " | ServerIP-" + ServerUtils.getIp() : "") +
                (ping.getValue() ? " | ServerPing-" + ServerUtils.getPing() : "") +
                (userName.getValue() ? " | User-" + Nattalie.USERNAME : "") +
                (fps.getValue() ? " | FPS-" + Minecraft.getDebugFPS() : "");

        if (ClickGUI.instance.chinese.getValue()) {
            string = stringValue.getValue() +
                    (severIP.getValue() ? " | 服务器地址-" + ServerUtils.getIp() : "") +
                    (ping.getValue() ? " | 服务器延迟-" + ServerUtils.getPing() : "") +
                    (userName.getValue() ? " | 用户-" + Nattalie.USERNAME : "") +
                    (fps.getValue() ? " | 帧数-" + Minecraft.getDebugFPS() : "");
        }

        if (blur.getValue()) {
            RoundedUtils.enableDrawBlur(mc);
            RoundedUtils.drawRound_Rectangle(FontManager.font20, string, x, y, 2, colorValue.getColor(), new Color(0,0,0,80), 4,6,true);
            RoundedUtils.disableDrawBlur(blurInt.getValue().intValue());
        }

        RoundedUtils.drawRound_Rectangle(FontManager.font20, string, x, y, 2, colorValue.getColor(), new Color(0,0,0,80), 4,6,true);


        float w = RoundedUtils.width;
        float h = RoundedUtils.height;

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                data.setX(x);
                data.setY(y);
                data.setWidth(w);
                data.setHeight(h);
            }
        }
    }
}
