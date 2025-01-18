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
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.mode.player.AutoHeal;
import net.pursue.mode.player.Blink;
import net.pursue.utils.player.PacketUtils;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.value.values.BooleanValue;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

public class Disabler extends Mode {

    public static Disabler instance;

    private final BooleanValue<Boolean> post = new BooleanValue<>(this, "GrimPost-Dis", false);
    private final BooleanValue<Boolean> badh = new BooleanValue<>(this,"ViaAnimation-Fix", false);
    private final BooleanValue<Boolean> high1_17 = new BooleanValue<>(this,"(1.14+)Animation-Fix", false, badh::getValue);

    public Disabler() {
        super("Disabler", "禁用器", "禁用一些反作弊检测", Category.MISC);
        instance = this;
    }

    private static boolean lastResult;
    public static List<Packet<INetHandler>> storedPackets;
    public static ConcurrentLinkedDeque<Integer> pingPackets;

    private boolean animation = false;

    static {
        lastResult = false;
        storedPackets = new CopyOnWriteArrayList<Packet<INetHandler>>();
        pingPackets = new ConcurrentLinkedDeque<Integer>();
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        Packet<?> packet = event.getPacket();

        if (badh.getValue()) {
            if (packet instanceof CPacketAnimation) {
                animation = true;
            } else if (packet instanceof CPacketUseEntity) {
                if (((CPacketUseEntity) packet).getAction() != CPacketUseEntity.Action.ATTACK) return;

                if (!animation) {
                    mc.player.swingArm(EnumHand.MAIN_HAND, false);
                }
                animation = false;
            } else if (packet instanceof CPacketClickWindow window && window.getClickType() == ClickType.THROW && high1_17.getValue()) {
                if (!animation) {
                    mc.player.swingArm(EnumHand.MAIN_HAND, false);
                }
                animation = false;
            } else if (packet instanceof CPacketPlayerDigging digging && (digging.getAction() == CPacketPlayerDigging.Action.DROP_ITEM || digging.getAction() == CPacketPlayerDigging.Action.DROP_ALL_ITEMS) && high1_17.getValue()) {
                if (!animation) {
                    mc.player.swingArm(EnumHand.MAIN_HAND, false);
                }
                animation = false;
            }
        }
    }

    @EventTarget
    private void onWorld(EventWorldLoad eventWorldLoad) {
        animation = false;
    }

    public boolean getGrimPost() {
        boolean result = Nattalie.instance.getModeManager().getByClass(Disabler.class).isEnable() && post.getValue()
                && mc.player != null
                && mc.world != null
                && mc.player.isEntityAlive()
                && (!AutoHeal.instance.isEnable() || AutoHeal.instance.modeValue.getValue() != AutoHeal.mode.Golden_Apple)
                && !Nattalie.instance.getModeManager().getByClass(Blink.class).isEnable()
                && mc.player.ticksExisted >= 10;


        if (lastResult && !result) {
            lastResult = false;

            mc.addScheduledTask(this::processPackets);
        }

        lastResult = result;
        return result;
    }

    public void processPackets() {
        if (!storedPackets.isEmpty()) {
            for (Packet<INetHandler> packet : storedPackets) {
                EventPacket event = new EventPacket(packet);
                EventManager.instance.call(event);

                if (event.isCancelled() || mc.getConnection() == null || mc == null || mc.player == null || mc.world == null) {
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
