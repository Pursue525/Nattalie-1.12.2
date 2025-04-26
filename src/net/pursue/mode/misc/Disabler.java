package net.pursue.mode.misc;

import net.minecraft.inventory.ClickType;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraft.util.EnumHand;
import net.pursue.Nattalie;
import net.pursue.event.EventManager;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.mode.Mode;
import net.pursue.mode.player.AutoHeal;
import net.pursue.mode.player.Blink;
import net.pursue.utils.category.Category;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.player.PacketUtils;
import net.pursue.value.values.BooleanValue;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

public class Disabler extends Mode {

    public static Disabler instance;

    private final BooleanValue<Boolean> post = new BooleanValue<>(this, "GrimPost-Dis", false);
    private final BooleanValue<Boolean> badPacketA = new BooleanValue<>(this, "GrimBadPacketA-Dis", true);
    private final BooleanValue<Boolean> chatFix = new BooleanValue<>(this, "Chat-Cleanse", true);
    private final BooleanValue<Boolean> chatDebug = new BooleanValue<>(this, "ChatDeBug", true, chatFix::getValue);
    private final BooleanValue<Boolean> highC0E = new BooleanValue<>(this,"(1.14+)Animation-Fix", false);
    private final BooleanValue<Boolean> highC07 = new BooleanValue<>(this,"(1.14+)DropItem-Fix", false);
    private final BooleanValue<Boolean> highS0E = new BooleanValue<>(this,"(1.17+)ItemBug-Fix", false);

    public Disabler() {
        super("Disabler", "禁用器", "禁用一些反作弊检测", Category.MISC);
        instance = this;
    }

    private static boolean lastResult;

    public static List<Packet<INetHandler>> storedPackets;
    public static ConcurrentLinkedDeque<Integer> pingPackets;

    private int lastSlot;

    private boolean isGUI;

    static {
        lastResult = false;
        storedPackets = new CopyOnWriteArrayList<Packet<INetHandler>>();
        pingPackets = new ConcurrentLinkedDeque<Integer>();
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        Packet<?> packet = event.getPacket();

        if (highC0E.getValue()) {
            if (packet instanceof CPacketClickWindow clickWindow && clickType(clickWindow.getClickType(), clickWindow.getSlotId())) {
                mc.player.swingArm(EnumHand.MAIN_HAND, false);
            }
        }

        if (highC07.getValue()) {
            if (packet instanceof CPacketPlayerDigging digging) {
                switch (digging.getAction()) {
                    case DROP_ITEM -> {
                        event.cancelEvent();

                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, mc.player.inventory.currentItem + 36, 0, ClickType.THROW, mc.player);
                    }
                    case DROP_ALL_ITEMS -> {
                        event.cancelEvent();

                        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, mc.player.inventory.currentItem + 36, 1, ClickType.THROW, mc.player);
                    }
                }
            }
        }

        if (highS0E.getValue()) {
            if (packet instanceof SPacketWindowItems && !isGUI) {
                event.cancelEvent();
            }
        }

        if (chatFix.getValue()) {
            if (packet instanceof SPacketChat chat) {
                String chatMessage = chat.getChatComponent().getUnformattedText();

                String[] sensitiveWords = {"xinxin.cam","SilenceFix Best The Config Free", "SilenceFix Best Config Free", "快手搜索SilenceFix"};

                for (String word : sensitiveWords) {
                    if (chatMessage.toLowerCase().contains(word.toLowerCase())) {
                        event.cancelEvent();
                        if (chatDebug.getValue()) DebugHelper.sendMessage("净网系统", "已删除垃圾宣传");
                    }
                }
            }
        }

        if (badPacketA.getValue()) {
            if (packet instanceof CPacketHeldItemChange wrapped) {
                if (wrapped.getSlotId() == lastSlot)
                    event.cancelEvent();
                else
                    lastSlot = wrapped.getSlotId();
            }
        }

        if (packet instanceof SPacketOpenWindow) {
            isGUI = true;
        }
        if (packet instanceof SPacketCloseWindow || packet instanceof CPacketCloseWindow) {
            isGUI = false;
        }
    }

    @EventTarget
    private void onWorld(EventWorldLoad eventWorldLoad) {
        lastSlot = -1;
        isGUI = false;
    }

    private boolean clickType(ClickType type, int slotID) {
        switch (type) {
            case PICKUP -> {
                return slotID > 45 || slotID < 0;
            }
            case SWAP, QUICK_MOVE, QUICK_CRAFT, CLONE, PICKUP_ALL -> {
                return false;
            }
        }
        return true;
    }

    public boolean getGrimPost() {
        boolean result = isValidPostCondition();

        if (lastResult && !result) {
            lastResult = false;
            mc.addScheduledTask(this::processPackets);
        }

        lastResult = result;
        return result;
    }

    private boolean isValidPostCondition() {
        return Nattalie.instance.getModeManager().getByClass(Disabler.class).isEnable()
                && post.getValue()
                && mc.player != null
                && mc.world != null
                && mc.player.isEntityAlive()
                && (!AutoHeal.instance.isEnable() || AutoHeal.instance.modeValue.getValue() != AutoHeal.mode.Golden_Apple)
                && !Nattalie.instance.getModeManager().getByClass(Blink.class).isEnable()
                && mc.player.ticksExisted >= 10;
    }

    public synchronized void processPackets() {
        if (!storedPackets.isEmpty()) {
            for (Packet<INetHandler> packet : storedPackets) {
                EventPacket event = new EventPacket(packet);
                EventManager.instance.call(event);

                if (event.isCancelled() || mc.player == null || mc.player.isDead || mc.getConnection() == null || mc.world == null || !mc.getConnection().getNetworkManager().isChannelOpen()) {
                    continue;
                }

                packet.processPacket(mc.getConnection());
            }
            storedPackets.clear();
        }
    }

    public boolean grimPostDelay(Packet<?> packet) {
        if (mc.player == null) {
            return false;
        }

        if (packet instanceof SPacketServerInfo) {
            return false;
        }

        if (packet instanceof SPacketEncryptionRequest) {
            return false;
        }

        if (packet instanceof SPacketPlayerListItem) {
            return false;
        }

        if (packet instanceof SPacketDisconnect) {
            return false;
        }

        if (packet instanceof SPacketChunkData) {
            return false;
        }

        if (packet instanceof SPacketPong) {
            return false;
        }

        if (packet instanceof SPacketWorldBorder) {
            return false;
        }

        if (packet instanceof SPacketJoinGame) {
            return false;
        }

        if (packet instanceof SPacketEntityHeadLook) {
            return false;
        }

        if (packet instanceof SPacketTeams) {
            return false;
        }

        if (packet instanceof SPacketChat) {
            return false;
        }

        if (packet instanceof SPacketSetSlot) {
            return false;
        }

        if (packet instanceof SPacketEntityMetadata) {
            return false;
        }

        if (packet instanceof SPacketEntityProperties) {
            return false;
        }

        if (packet instanceof SPacketUpdateTileEntity) {
            return false;
        }

        if (packet instanceof SPacketTimeUpdate) {
            return false;
        }

        if (packet instanceof SPacketPlayerListHeaderFooter) {
            return false;
        }

        if (packet instanceof SPacketEntityVelocity sPacketEntityVelocity) {
            return sPacketEntityVelocity.getEntityID() == mc.player.getEntityId();
        }

        return packet instanceof SPacketExplosion
                || packet instanceof SPacketConfirmTransaction
                || packet instanceof SPacketPlayerPosLook
                || packet instanceof SPacketEntityEquipment
                || packet instanceof SPacketBlockChange
                || packet instanceof SPacketMultiBlockChange
                || packet instanceof SPacketKeepAlive
                || packet instanceof SPacketUpdateHealth
                || packet instanceof SPacketEntity
                || packet instanceof SPacketSpawnMob
                || packet instanceof SPacketCustomPayload;
    }

    public void fixC0F(CPacketConfirmTransaction packet) {
        int id = packet.getUid();
        if (id >= 0 || pingPackets.isEmpty()) {
            PacketUtils.sendPacketNoEvent(packet);
        } else {
            do {
                int current = pingPackets.peekFirst();
                PacketUtils.sendPacketNoEvent(new CPacketConfirmTransaction(packet.getWindowId(), (short) current, true));
                pingPackets.pollFirst();
                if (current == id) {
                    break;
                }
            } while (!pingPackets.isEmpty());
        }
    }
}
