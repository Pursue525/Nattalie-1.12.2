package net.pursue.mode.move;

import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.pursue.event.EventTarget;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.ui.gui.Click;
import net.pursue.ui.gui.Config;
import net.pursue.ui.gui.Friend;
import net.pursue.utils.category.Category;
import net.pursue.value.values.ModeValue;
import org.lwjgl.input.Keyboard;

public class InvMove extends Mode {

    public static InvMove instance;

    public final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal);

    public enum mode {
        Normal,
        Grim
    }

    public InvMove() {
        super("InvMove", "背包移动", "打开任何gui都可以移动", Category.MOVE);
        instance = this;
    }

    public boolean sprint = false;

    @EventTarget
    public void onUpdate(EventUpdate eventUpdate) {
        setSuffix(modeValue.getValue().name());
        switch ((mode) modeValue.getValue()) {
            case Normal -> sprint = true;
            case Grim -> sprint = !isGui();
        }

        if (isGui()) {
            GameSettings gameSettings = mc.gameSettings;
            KeyBinding[] keyBindings = {gameSettings.keyBindForward, gameSettings.keyBindBack, gameSettings.keyBindLeft, gameSettings.keyBindRight, gameSettings.keyBindJump, gameSettings.keyBindSprint};
            for (KeyBinding keyBinding : keyBindings) {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), Keyboard.isKeyDown(keyBinding.getKeyCode()));
            }
        }
    }

    private boolean isGui() {
        return mc.currentScreen instanceof GuiInventory
                || mc.currentScreen instanceof GuiChest
                || mc.currentScreen instanceof GuiFurnace
                || mc.currentScreen instanceof GuiBrewingStand
                || mc.currentScreen instanceof Friend
                || mc.currentScreen instanceof Config
                || mc.currentScreen instanceof Click;
    }
}
