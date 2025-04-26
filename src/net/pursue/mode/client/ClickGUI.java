package net.pursue.mode.client;

import net.pursue.Nattalie;
import net.pursue.mode.Mode;
import net.pursue.ui.notification.NotificationType;
import net.pursue.utils.category.Category;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Mode {
    public static ClickGUI instance;

    public final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Nattalie);

    public enum mode {
        Nattalie,
        SinKa
    }

    public final BooleanValue<Boolean> chinese = new BooleanValue<>(this, "Chinese", false);
    public final BooleanValue<Boolean> blur = new BooleanValue<>(this, "Blur", false, () -> modeValue.getValue().equals(mode.SinKa));
    public final NumberValue<Number> radius = new NumberValue<>(this, "radius", 10.0, 1.0, 100.0, 1.0, () -> modeValue.getValue().equals(mode.SinKa) && blur.getValue());

    public ClickGUI() {
        super("ClickGUI", "模块管理器", "调整一切模块的界面", Category.CLIENT);
        setKey(Keyboard.KEY_RSHIFT);
        instance = this;
    }

    @Override
    public void enable() {
        if (Nattalie.instance.getOldGUI().sb) {
            mc.displayGuiScreen(Nattalie.instance.getOldGUI());
        } else {
            Nattalie.instance.openClickGUI();
        }
        Nattalie.instance.getNotificationManager().post("你正在ClickGUI中","正在为您保持显示！", -1, NotificationType.INFO);
        setEnable(false);
    }
}
