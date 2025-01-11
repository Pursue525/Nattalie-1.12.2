package net.pursue.mode.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketEntity;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiBot
        extends Mode {

    public static AntiBot instance;

    private final BooleanValue<Boolean> entityID = new BooleanValue<>(this,"EntityID", false);
    private final BooleanValue<Boolean> sleep = new BooleanValue<>(this,"Sleep", false);
    private final BooleanValue<Boolean> noArmor = new BooleanValue<>(this,"NoArmor", false);
    private final BooleanValue<Boolean> height = new BooleanValue<>(this,"Height", false);
    private final BooleanValue<Boolean> ground = new BooleanValue<>(this,"Ground", false);
    private final BooleanValue<Boolean> dead = new BooleanValue<>(this,"Dead", false);
    private final BooleanValue<Boolean> health = new BooleanValue<>(this,"Health", false);
    private final BooleanValue<Boolean> hytGetNames = new BooleanValue<>(this,"HytGetName", false);
    private final ModeValue<Enum<?>> hytGetNameModes = new ModeValue<>(this,"HytGetNameMode", hytGetNameMode.values(), hytGetNameMode.HytBedWars1v1);

    public enum hytGetNameMode {
        HytBedWars4v4,
        HytBedWars1v1,
        HytBedWars32,
        HytBedWars16;
    }


    private static final List<Integer> groundBotList = new ArrayList<>();
    private static final List<String> playerName = new ArrayList<>();

    public AntiBot() {
        super("AntiBot", "反假人", "防止杀戮攻击假人", Category.MISC);
        instance = this;
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.clearAll();
    }

    private void clearAll() {
        playerName.clear();
    }

    @EventTarget
    public void onPacketReceive(EventPacket event) {
        Entity entity;
        if (mc.player == null || mc.world == null) {
            return;
        }
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof SPacketEntity && ground.getValue() && (entity = ((SPacketEntity)event.getPacket()).getEntity(mc.world)) instanceof EntityPlayer && ((SPacketEntity)event.getPacket()).onGround && !groundBotList.contains(entity.getEntityId())) {
            groundBotList.add(entity.getEntityId());
        }
        if (hytGetNames.getValue() && packet instanceof SPacketChat s02PacketChat) {
            if (s02PacketChat.getChatComponent().getUnformattedText().contains("获得胜利!") || s02PacketChat.getChatComponent().getUnformattedText().contains("游戏开始 ...")) {
                this.clearAll();
            }
            switch ((hytGetNameMode)((Object)hytGetNameModes.getValue())) {
                case HytBedWars4v4:
                case HytBedWars1v1:
                case HytBedWars32: {
                    String name;
                    String text = s02PacketChat.getChatComponent().getUnformattedText();
                    Matcher matcher = Pattern.compile("杀死了 (.*?)\\(").matcher(text);
                    Matcher matcher2 = Pattern.compile("起床战争>> (.*?) (\\((((.*?) 死了!)))").matcher(text);

                    if (matcher.find() && !text.contains(": 起床战争>>") && !text.contains(": 杀死了")) {
                        name = matcher.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            String finalName = name;
                            new Thread(() -> {
                                try {
                                    Thread.sleep(6000L);
                                    playerName.remove(finalName);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }

                    if (matcher2.find() && text.contains(": 起床战争>>")) {
                        name = matcher2.group(1).trim();
                        if (!name.isEmpty()) {
                            playerName.add(name);
                            String finalName1 = name;
                            new Thread(() -> {
                                try {
                                    Thread.sleep(6000L);
                                    playerName.remove(finalName1);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }).start();
                        }
                    }
                    break;
                }
                case HytBedWars16: {
                    String name;
                    Matcher matcher = Pattern.compile("击败了 (.*?)!").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    Matcher matcher2 = Pattern.compile("玩家 (.*?)死了！").matcher(s02PacketChat.getChatComponent().getUnformattedText());
                    if ((matcher.find() && !s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了") || !s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ")) && !(name = matcher.group(1).trim()).isEmpty()) {
                        playerName.add(name);
                        String finalName = name;
                        new Thread(() -> {
                            try {
                                Thread.sleep(10000L);
                                playerName.remove(finalName);
                            }
                            catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }).start();
                    }
                    if ((!matcher2.find() || s02PacketChat.getChatComponent().getUnformattedText().contains(": 击败了")) && s02PacketChat.getChatComponent().getUnformattedText().contains(": 玩家 ") || (name = matcher2.group(1).trim()).isEmpty()) break;
                    playerName.add(name);
                    String finalName1 = name;
                    new Thread(() -> {
                        try {
                            Thread.sleep(10000L);
                            playerName.remove(finalName1);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                    break;
                }
            }
        }
    }

    public boolean isServerBot(Entity entity) {
        if (this.isEnable() && entity instanceof EntityPlayer) {
            if (hytGetNames.getValue() && playerName.contains(entity.getName())) {
                return true;
            }
            if (height.getValue() && ((double)entity.height <= 0.5 || ((EntityPlayer)entity).isPlayerSleeping() || entity.ticksExisted < 80)) {
                return true;
            }
            if (dead.getValue() && entity.isDead) {
                return true;
            }
            if (health.getValue() && ((EntityPlayer)entity).getHealth() == 0.0f) {
                return true;
            }
            if (sleep.getValue() && ((EntityPlayer)entity).isPlayerSleeping()) {
                return true;
            }
            if (entityID.getValue() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1)) {
                return true;
            }
            if (ground.getValue() && !groundBotList.contains(entity.getEntityId())) {
                return true;
            }
            return noArmor.getValue() && ((EntityPlayer) entity).inventory.armorInventory.get(0).func_190926_b() && ((EntityPlayer) entity).inventory.armorInventory.get(1).func_190926_b() && ((EntityPlayer) entity).inventory.armorInventory.get(2).func_190926_b() && ((EntityPlayer) entity).inventory.armorInventory.get(3).func_190926_b();
        }
        return false;
    }
}
