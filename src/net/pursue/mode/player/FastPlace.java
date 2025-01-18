package net.pursue.mode.player;

import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.pursue.event.EventTarget;
import net.pursue.event.update.EventTick;
import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.value.values.NumberValue;

public class FastPlace extends Mode {

    private final NumberValue<Number> speed = new NumberValue<>(this, "Speed",0,0,4,1);

    public FastPlace() {
        super("FastPlace", "加速放置", "减缓你放置方块的延迟", Category.PLAYER);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
            mc.rightClickDelayTimer = Math.min(0, speed.getValue().intValue());
        }
    }
}
