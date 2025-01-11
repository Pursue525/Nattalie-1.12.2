package net.pursue.mode;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.pursue.Nattalie;
import net.pursue.event.EventManager;
import net.pursue.mode.hud.ClickGUI;
import net.pursue.ui.notification.NotificationType;
import net.pursue.utils.category.Category;
import net.pursue.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Mode {
    public static Minecraft mc;
    private final String modeName;
    private final String modeChineseName;
    private final String modeDescribes;
    private final Category category;

    private boolean enable;

    private String suffix = null;
    private int key;
    private final List<Value<?>> values = new ArrayList<>();


    public Mode(String name, String chineseName, String describes, Category category) {
        mc = Minecraft.getMinecraft();
        this.modeName = name;
        this.modeChineseName = chineseName;
        this.modeDescribes = describes;
        this.category = category;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;

        if (enable) {
            enable();
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));


            if (ClickGUI.instance.chinese.getValue()) {
                Nattalie.instance.getNotificationManager().post("  开启模块  ", "[" + this.modeChineseName + "]", 2000, NotificationType.SUCCESS);
            } else {
                Nattalie.instance.getNotificationManager().post("EnableModule", "[" + this.modeName + "]", 2000, NotificationType.SUCCESS);
            }

            EventManager.instance.register(this);
        } else {
            disable();
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));

            if (ClickGUI.instance.chinese.getValue()) {
                Nattalie.instance.getNotificationManager().post("  关闭模块  ", "[" + this.modeChineseName + "]", 2000, NotificationType.ERROR);
            } else {
                Nattalie.instance.getNotificationManager().post("DisableModule", "[" + this.modeName + "]", 2000, NotificationType.ERROR);
            }

            EventManager.instance.unregister(this);
        }
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        if (ClickGUI.instance.chinese.getValue()) {
            return this.modeChineseName;
        } else {
            return this.modeName;
        }
    }

    public void addValues(Value<?>... values) {
        Collections.addAll(this.values, values);
    }

    public void enable() {
    }
    public void disable() {
    }
    public void key(int key) {
    }
}
