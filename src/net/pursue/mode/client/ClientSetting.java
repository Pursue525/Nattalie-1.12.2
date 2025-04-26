package net.pursue.mode.client;

import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;

public class ClientSetting extends Mode {

    public static ClientSetting Instance;

    public final BooleanValue<Boolean> potionEffects = new BooleanValue<>(this, "PotionEffects", false); // 右上角的药水图标显示
    public final BooleanValue<Boolean> selectedItem = new BooleanValue<>(this, "SelectedItem", true); // 物品栏
    public final BooleanValue<Boolean> expBar = new BooleanValue<>(this, "ExpBar", true); // 经验条
    public final BooleanValue<Boolean> playerStats = new BooleanValue<>(this, "PlayerStats", true); // 玩家基本信息
    /**
     * 玩家HUD4件套
     */
    public final BooleanValue<Boolean> armor = new BooleanValue<>(this, "Armor", true, playerStats::getValue); // 盔甲值
    public final BooleanValue<Boolean> health = new BooleanValue<>(this, "Health", true, playerStats::getValue); // 血条
    public final BooleanValue<Boolean> food = new BooleanValue<>(this, "Food", true, playerStats::getValue); // 饱和度
    public final BooleanValue<Boolean> air = new BooleanValue<>(this, "Air", true, playerStats::getValue); // 氧气

    public final ModeValue<playerStatsMode> playerStatsModeModeValue = new ModeValue<>(this, "PlayerStatsModeModeValue", playerStatsMode.values(), playerStatsMode.Minecraft);

    public enum playerStatsMode {
        Minecraft,
        Nattalie
    }

    public ClientSetting() {
        super("ClientSetting", "客户端设置", "修改客户端的一些功能", Category.CLIENT);
        Instance = this;
    }

    @Override
    public void enable() {
        setEnable(false);
    }
}
