package net.pursue.mode.player;

import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.Mode;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoL extends Mode {

    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Hyt_SW);

    enum mode {
        Hyt4v4_BW,
        Hyt_SW
    }

    private final NumberValue<Number> delay = new NumberValue<>(this, "Delay", 1.0,0.0,10.0,0.1);

    public AutoL() {
        super("AutoL", "自动嘲讽", "自动嘲讽那些被你打死的玩家", Category.PLAYER);
    }

    public static final List<String> list = new ArrayList<>();
    private final TimerUtils timerUtils = new TimerUtils();

    @Override
    public void disable() {
        timerUtils.reset();
    }

    @EventTarget
    private void onUpdate(EventUpdate update) {
        setSuffix(modeValue.getValue().toString());
    }

    @EventTarget
    private void onPacket(EventPacket packet) {

        if (mc.player == null) return;

        if (packet.getPacket() instanceof SPacketChat chat) {

            String message = chat.getChatComponent().getUnformattedText();
            Pattern pattern;

            if (timerUtils.hasTimePassed(delay.getValue().longValue() * 10L)) {
                switch (modeValue.getValue()) {
                    case Hyt_SW -> {
                        if (message.contains("被") && message.contains("彻底摧毁")) {
                            pattern = Pattern.compile("([\\w\\u4e00-\\u9fa5]+) 被 ([\\w\\u4e00-\\u9fa5]+) 彻底摧毁");
                        } else if (message.contains("尝试逃跑! 但是他被") && message.contains("一刀砍倒")) {
                            pattern = Pattern.compile("([\\w\\u4e00-\\u9fa5]+) 尝试逃跑! 但是他被 ([\\w\\u4e00-\\u9fa5]+) 一刀砍倒");
                        } else {
                            pattern = null;
                        }

                        if (pattern != null) {
                            sendMessage(message, pattern, true);
                            timerUtils.reset();
                        }
                    }
                    case Hyt4v4_BW -> {
                        pattern = Pattern.compile("([\\w\\u4e00-\\u9fa5]+)\\[.*?] \\(.*?\\)杀死了 ([\\w\\u4e00-\\u9fa5]+) \\(.*?\\)");
                        sendMessage(message, pattern, false);
                        timerUtils.reset();
                    }
                }
            }
        }
    }

    private void sendMessage(String message, Pattern pattern, boolean sw) {
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String player1 = matcher.group(1);
            String player2 = matcher.group(2);

            if (sw) {
                if (Objects.equals(player2, mc.player.getName())) {
                    mc.player.connection.sendPacketNoEvent(new CPacketChatMessage(player1 + ", " + getRandomElement(list)));
                }
            } else {
                if (Objects.equals(player1, mc.player.getName())) {
                    mc.player.connection.sendPacketNoEvent(new CPacketChatMessage("@a " + player2 + ", " + getRandomElement(list)));
                }
            }
        }
    }

    @EventTarget
    private void onWorld(EventWorldLoad worldLoad) {
        timerUtils.reset();
    }

    public static String getRandomElement(List<String> list) {
        Random random = new Random();
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}
