package net.pursue.mode.render;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.ArmorBreak;
import net.pursue.ui.notification.NotificationType;
import net.pursue.utils.category.Category;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.player.InvUtils;
import net.pursue.utils.player.PlayerData;
import net.pursue.value.values.BooleanValue;

import java.util.ArrayList;
import java.util.List;

public class ItemDeBug extends Mode {

    public static ItemDeBug Instance;

    private final BooleanValue<Boolean> axe = new BooleanValue<>(this, "GodAxe", false);
    private final BooleanValue<Boolean> apple = new BooleanValue<>(this, "GodApple", false);
    private final BooleanValue<Boolean> strength = new BooleanValue<>(this, "Strength", false);

    public ItemDeBug() {
        super("ItemDeBug", "玩家物品反馈", "提示一些玩家物品的东西", Category.RENDER);
        Instance = this;
    }

    public static final List<PlayerData> pList = new ArrayList<>();

    @EventTarget
    private void onWorld(EventWorldLoad worldLoad) {
        pList.clear();
    }

    @EventTarget
    private void onUpdate(EventUpdate update) {

        PlayerData playerData;

        for (EntityPlayer player : mc.world.playerEntities) {

            if (player.isDead || player == mc.player || FriendManager.isFriend(player) || FriendManager.isBot(player)) continue;

            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

            if (axe.getValue()) {
                if (ArmorBreak.getDamage(stack) > 100) {

                    playerData = new PlayerData(player, "秒人斧");

                    if (!isList(playerData)) {
                        pList.add(playerData);
                        Nattalie.instance.getNotificationManager().post("发现秒人斧头！", "玩家: " + player.getName(), 5000, NotificationType.INFO);
                    }
                }
            }

            if (apple.getValue()) {
                if (InvUtils.isEnchantedGoldenApple(stack)) {

                    playerData = new PlayerData(player, "附魔金苹果");

                    if (!isList(playerData)) {
                        pList.add(playerData);
                        Nattalie.instance.getNotificationManager().post("发现附魔金苹果！", "玩家: " + player.getName(), 5000, NotificationType.INFO);
                    }
                }
            }

            if (strength.getValue()) {
                for (final PotionEffect effect : PotionUtils.getEffectsFromStack(stack)) {
                    if (effect.getPotion() == Potion.getPotionById(5)) {

                        playerData = new PlayerData(player, "力量药水");

                        if (!isList(playerData)) {
                            pList.add(playerData);
                            Nattalie.instance.getNotificationManager().post("发现力量药水！", "玩家: " + player.getName(), 5000, NotificationType.INFO);
                        }
                    }
                }
            }
        }
    }

    private boolean isList(PlayerData playerData) {
        if (!pList.isEmpty()) {
            return pList.contains(playerData);
        }

        return false;
    }
}
