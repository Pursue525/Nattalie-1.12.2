package net.pursue.mode.client;

import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.update.EventTick;
import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.value.values.StringValue;
import org.lwjgl.opengl.Display;

import java.util.Objects;

public class Tile extends Mode {

    private final StringValue<String> stringValue = new StringValue<>(this, "Tile", Nattalie.instance.getClientName() + " *1.12.2 [" + Nattalie.instance.getClientVersion() + "]");

    public Tile() {
        super("Tile", "标题", "修复游戏的标题", Category.CLIENT);
    }

    @EventTarget
    private void onTick(EventTick tick) {
        if (stringValue.getValue().isEmpty()) {
            Display.setTitle("Minecraft *1.12.2");
        } else if (!Objects.equals(Display.getTitle(), stringValue.getValue())){
            Display.setTitle(stringValue.getValue());
        }
    }
}
