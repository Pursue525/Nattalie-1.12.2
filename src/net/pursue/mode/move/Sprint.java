package net.pursue.mode.move;

import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.value.values.ModeValue;

public class Sprint extends Mode {

    public static Sprint instance;

    public ModeValue<mode> modeValue = new ModeValue<>(this, "mode", mode.values(), mode.V1_14_Low);

    public enum mode {
        V1_14_High,
        V1_14_Low,
        None,
    }

    public Sprint() {
        super("Sprint", "疾跑", "让你保持疾跑", Category.MOVE);
        instance = this;
    }
}
