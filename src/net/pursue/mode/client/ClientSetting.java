package net.pursue.mode.client;

import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;

public class ClientSetting extends Mode {

    public static ClientSetting Instance;

    public final BooleanValue<Boolean> potionEffects = new BooleanValue<>(this, "PotionEffects", false);
    public final BooleanValue<Boolean> selectedItem = new BooleanValue<>(this, "SelectedItem", true);
    public final BooleanValue<Boolean> expBar = new BooleanValue<>(this, "ExpBar", true);

    public final BooleanValue<Boolean> playerStats = new BooleanValue<>(this, "PlayerStats", true);
    public final ModeValue<playerStatsMode> playerStatsModeModeValue = new ModeValue<>(this, "PlayerStatsMode", playerStatsMode.values(), playerStatsMode.Minecraft, playerStats::getValue);

    public enum playerStatsMode {
        Minecraft,
        Nattalie
    }


    public final BooleanValue<Boolean> armor = new BooleanValue<>(this, "Armor", true, playerStats::getValue);
    public final BooleanValue<Boolean> health = new BooleanValue<>(this, "Health", true, playerStats::getValue);
    public final BooleanValue<Boolean> food = new BooleanValue<>(this, "Food", true, playerStats::getValue);
    public final BooleanValue<Boolean> air = new BooleanValue<>(this, "Air", true, playerStats::getValue);

    public ClientSetting() {
        super("ClientSetting", "客户端设置", "修改客户端的一些自带的HUD", Category.CLIENT);
        Instance = this;
    }

    @Override
    public void enable() {
        setEnable(false);
    }
}
