package net.pursue.mode.player;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.util.StringUtils;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventUpdate;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.friend.FriendManager;
import net.pursue.value.values.ModeValue;

import java.util.ArrayList;
import java.util.List;

public class AutoReport extends Mode {

    private final ModeValue<server> serverModeValue = new ModeValue<>(this, "ServerMode", server.values(), server.HuaYuTing);

    enum server {
        HuaYuTing,
        Heyixel
    }

    public AutoReport() {
        super("AutoReport", "自动举报", "自动举报同一局内的玩家", Category.PLAYER);
    }

    private final List<String> isReport = new ArrayList<>();
    private final TimerUtils timerUtils = new TimerUtils();

    private NetworkPlayerInfo playerInfo;

    private boolean report;

    @Override
    public void disable() {
        timerUtils.reset();
        playerInfo = null;
        report = false;
    }

    @EventTarget
    private void onWorld(EventWorldLoad eventWorldLoad) {
        timerUtils.reset();
        playerInfo = null;
        report = false;
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        if (mc.getConnection() != null) {
            for (NetworkPlayerInfo playerInfo : mc.getConnection().getPlayerInfoMap()) {
                String name = StringUtils.stripControlCodes(playerInfo.getGameProfile().getName());

                if (name.equals(mc.player.getName()) || isReport.contains(name) || FriendManager.isFriend(name)) continue;

                if (mc.currentScreen != null || !timerUtils.hasTimePassed(20000) || KillAura.INSTANCE.target != null || Scaffold.INSTANCE.isEnable() || Blink.instance.isEnable()) continue; //看情况吧

                if (!report) {
                    mc.player.connection.sendPacket(new CPacketChatMessage("/report " + name));
                    this.playerInfo = playerInfo;
                    timerUtils.reset();
                    report = true;
                }
            }
        }
    }

    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        if (report) {
            if (eventPacket.getPacket() instanceof SPacketOpenWindow window) {
                mc.player.setSprinting(false);
                switch (serverModeValue.getValue()) {
                    case HuaYuTing -> {
                        eventPacket.cancelEvent();

                        mc.playerController.windowClick(window.getWindowId(), 11, 0, ClickType.PICKUP, mc.player); // 11是举报杀戮光环
                        DebugHelper.sendMessage("Report", "举报成功，被举报人为：" + StringUtils.stripControlCodes(playerInfo.getGameProfile().getName()));
                        isReport.add(StringUtils.stripControlCodes(playerInfo.getGameProfile().getName()));

                        report = false;
                    }
                    case Heyixel -> {
                        //TODO: 吉吉岛如花，暂无下文
                        report = false;
                    }
                }
            }
            if (eventPacket.getPacket() instanceof SPacketChat chat) {
                String chatMessage = chat.getChatComponent().getUnformattedText();

                if (chatMessage.contains("操作过快,请稍后再试.") || chatMessage.contains("举报玩家不在线！")) {
                    DebugHelper.sendMessage("Report", "举报失败，重试！");
                    report = false;
                }
            }
        }
    }
}
