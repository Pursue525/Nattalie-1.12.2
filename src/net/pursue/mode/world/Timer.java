package net.pursue.mode.world;

import net.pursue.event.EventTarget;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.utils.player.MovementUtils;
import net.pursue.value.values.NumberValue;

public class Timer extends Mode {

    public static Timer instance;

    public final NumberValue<Number> speed = new NumberValue<>(this, "Speed", 2.0f,0.0f,10.0f,0.1f);

    public Timer() {
        super("Timer", "时间管理器", "设置游戏时间", Category.WORLD);
        instance = this;
    }


    @Override
    public void disable() {
        mc.timer.timerSpeed = 1.0f;
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        if (MovementUtils.isMoving()) {
            mc.timer.timerSpeed = speed.getValue().floatValue();
        } else {
            mc.timer.timerSpeed = 1.0f;
        }
    }
}
