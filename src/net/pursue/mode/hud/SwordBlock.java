package net.pursue.mode.hud;

import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.render.EventRender2D;
import net.pursue.event.update.EventTick;
import net.pursue.mode.Mode;
import net.pursue.mode.client.ClickGUI;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.category.Category;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.NumberValue;

import java.awt.*;

public class SwordBlock extends Mode {

    private final ColorValue<Color> colorValue = new ColorValue<>(this, "Color", Color.WHITE);

    private final BooleanValue<Boolean> blur = new BooleanValue<>(this, "Blur", true);
    private final NumberValue<Number> blurInt = new NumberValue<>(this,"BlurInt", 10, 1, 100, 1, blur::getValue);

    public SwordBlock() {
        super("SwordBlock", "防砍率显示", "显示你的防砍率", Category.HUD);
    }

    private int blockV = 0;
    private int slot = -1;

    @EventTarget
    private void onUpdate(EventTick tick) {
        if (mc.player != null) {
            if (slot != -1) {
                if (mc.player.getSlotFromPlayerContainer(slot + 36).getStack().getItem() instanceof ItemSword && mc.player.isHandActive()) {
                    if (blockV <= 9) blockV++;
                } else {
                    if (blockV > 0) blockV--;
                }
            }
        }
    }

    @EventTarget
    private void onRender(EventRender2D eventRender2D) {
        float x = eventRender2D.getScaledResolution().getScaledWidth() / 2f;
        float y = eventRender2D.getScaledResolution().getScaledHeight() / 2f;

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = data.getX();
                y = data.getY();
            }
        }

        String string = blockV > 0 ? "BlockIng: " + (blockV * 10) + "%" : "NoBlockIng";

        if (ClickGUI.instance.chinese.getValue()) {
            string = blockV > 0 ? "防砍率为: " + (blockV * 10) + "%" : "无防砍";
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

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        Packet<?> packet = eventPacket.getPacket();

        if (packet instanceof CPacketHeldItemChange heldItemChange) {
            slot = heldItemChange.getSlotId();
        }
    }
}
