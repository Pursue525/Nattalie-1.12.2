package net.pursue.mode.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventTick;
import net.pursue.event.world.EventWorldLoad;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.utils.player.PacketUtils;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.player.FakePlayer;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

import java.util.*;

public class Blink extends Mode {

    public static Blink instance;
    private final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal);

    enum mode {
        Normal,
        Slow
    }

    public final BooleanValue<Boolean> antiAimValue = new BooleanValue<>(this,"Anti Aim", true);
    public final BooleanValue<Boolean> antiAimArrowValue = new BooleanValue<>(this,"Arrow", true, antiAimValue::getValue);
    public final BooleanValue<Boolean> antiAimProjectileValue = new BooleanValue<>(this,"Projectile", true, antiAimValue::getValue);
    public final BooleanValue<Boolean> antiAimTNTValue = new BooleanValue<>(this,"TNT", true, antiAimValue::getValue);
    public final BooleanValue<Boolean> antiAimPlayerValue = new BooleanValue<>(this,"Player", true, antiAimValue::getValue);
    private final NumberValue<Number> slowTick = new NumberValue<>(this, "SlowTick", 3,3,20,1);

    public Blink() {
        super("Blink", "瞬移", "暂停所有移动发包后本体留原地，关闭后回到当前位置", Category.PLAYER);
        instance = this;
    }

    private final List<List<Packet<?>>> packets = new ArrayList<>();
    private final TimerUtils timerUtils = new TimerUtils();
    private final List<Vec3d> realPos = new ArrayList<>();
    public static FakePlayer fakePlayer;
    private boolean isAir = false;
    private Vec3d lastPos;


    @Override
    public void enable() {
        if (Blink.mc.world != null && Blink.mc.player != null) {
            timerUtils.reset();

            isAir = false;

            packets.clear();
            packets.add(new ArrayList<>());
            realPos.add(new Vec3d(
                    mc.player.posX,
                    mc.player.posY,
                    mc.player.posZ
            ));
            fakePlayer = new FakePlayer(mc.player);;
            lastPos = new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ);
        }
    }

    @Override
    public void disable() {
        if (mc.player != null && mc.world != null) {
            if (!isAir) {
                allPoll();
            } else {
                DebugHelper.sendMessage("检测到您掉下虚空并且无法自救，已为您驳回移动");

                if (Scaffold.INSTANCE.isEnable()) Scaffold.INSTANCE.setEnable(false);
                antiAir();
            }

            try {
                if (fakePlayer != null)
                    mc.world.removeEntity(fakePlayer);
            } catch (Exception ignored) {
            }
        }
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.setEnable(false);
    }

    @EventTarget
    private void onPacket(EventPacket event) {
        if (mc.player == null) return;

        if (PacketUtils.isCPacket(event.getPacket())) {
            mc.addScheduledTask(() -> {
                packets.getLast().add(event.getPacket());
            });
            event.setCancelled(true);
        }
    }

    @EventTarget
    private void onTick(EventTick event) {

        packets.add(new ArrayList<>());
        realPos.add(new Vec3d(
                mc.player.posX,
                mc.player.posY,
                mc.player.posZ
        ));
        if (!packets.isEmpty()) {
            if (mc.player.offGroundTicks >= 20) {
                if (!isAir()) {
                    isAir = true;
                    setEnable(false);
                }
            }
        }
        if (antiAimValue.getValue()) {
            while (true) {
                boolean dangerous = false;
                for (Entity entity : mc.world.getLoadedEntityList()) {
                    if ((antiAimArrowValue.getValue() && entity instanceof EntityArrow arrow && !arrow.inGround) ||
                            (antiAimProjectileValue.getValue() && entity instanceof EntitySnowball || entity instanceof EntityEgg) ||
                            (antiAimTNTValue.getValue() && entity instanceof EntityTNTPrimed) ||
                            (antiAimPlayerValue.getValue() && entity instanceof EntityPlayer player && player.getUniqueID() != mc.player.getUniqueID())
                    ) {
                        if (this.isDangerous(entity)) {
                            dangerous = true;
                            break;
                        }
                    }
                }
                if (dangerous && packets.size() >= 3) {
                    this.poll();
                } else {
                    break;
                }
            }
        }

        int i = slowTick.getValue().intValue() * 10;

        if (Scaffold.INSTANCE.isScaffold) i = 100;
        
        if (modeValue.getValue().equals(mode.Slow)) {
            if (timerUtils.hasTimePassed(i)) {
                this.poll();
                timerUtils.reset();
            }
        }
    }


    private void poll() {
        if (packets.isEmpty()) return;
        this.sendTick(packets.getFirst());
        packets.removeFirst();
    }

    private void sendTick(List<Packet<?>> tick) {
        tick.forEach(packet -> {
            PacketUtils.sendPacketNoEvent(packet);
            this.handleFakePlayerPacket(packet);
        });
    }

    private void allPoll() {
        if (!packets.isEmpty()) {
            packets.forEach(this::sendTick);
            packets.clear();
        }
    }

    private void antiAir() {
        if (!packets.isEmpty()) {
            packets.forEach(packet -> {
                if (packet instanceof CPacketConfirmTransaction) {
                    sendTick(packet);
                }
            });
            packets.clear();
        }
    }

    private boolean isDangerous(Entity entity) {
        final float width = 1.2F;
        final float height = 2.2F;
        if (entity instanceof IProjectile projectile) {
            float motionSlowdown = 0.99F, size = 1.2F, gravity = 0.05F;
            if (projectile instanceof EntityArrow) {
                motionSlowdown = 0.99F;
                size = 1.2F;
                gravity = 0.05F;
            } else if (projectile instanceof EntitySnowball || projectile instanceof EntityEgg) {
                motionSlowdown = 0.99F;
                gravity = 1.2F;
                size = 0.25F;
            }
            return predictBox(
                    entity.posX,
                    entity.posY,
                    entity.posZ,
                    entity.motionX,
                    entity.motionY,
                    entity.motionZ,
                    motionSlowdown,
                    size,
                    gravity,
                    realPos,
                    width / 2,
                    height
            );
        } else if (entity instanceof EntityTNTPrimed tnt) {
            Vec3d pos = realPos.getFirst();
            return (tnt.getDistanceSq(
                    pos.xCoord,
                    pos.yCoord,
                    pos.zCoord
            ) <= 30 && tnt.getFuse() <= 10);
        } else if (entity instanceof EntityPlayer player) {
            Vec3d pos = realPos.getFirst();
            return (player.getDistanceSq(
                    pos.xCoord,
                    pos.yCoord,
                    pos.zCoord
            ) <= 20);
        }
        return false;
    }

    private boolean isAir() {
        for (int i = 1; i < 50; i++) {
            Block blockState = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - i, mc.player.posZ)).getBlock();

            if (blockState instanceof BlockAir) continue;

            return true;
        }
        return false;
    }

    private void handleFakePlayerPacket(Packet<?> packet) {
        if (packet instanceof CPacketPlayer.Position position) {
            fakePlayer.setPositionAndRotationDirect(
                    position.getX(0D),
                    position.getY(0D),
                    position.getZ(0D),
                    fakePlayer.rotationYaw,
                    fakePlayer.rotationPitch,
                    3, true
            );
            lastPos = new Vec3d(position.getX(0D), position.getY(0D), position.getZ(0D));
            fakePlayer.onGround = position.isOnGround();
        } else if (packet instanceof CPacketPlayer.Rotation rotation) {
            fakePlayer.setPositionAndRotationDirect(
                    fakePlayer.posX,
                    fakePlayer.posY,
                    fakePlayer.posZ,
                    rotation.getYaw(0F),
                    rotation.getPitch(0F),
                    3,
                    true
            );
            fakePlayer.onGround = rotation.isOnGround();

            fakePlayer.rotationYawHead = rotation.getYaw(0F);
            fakePlayer.rotationYaw = rotation.getYaw(0F);
            fakePlayer.rotationPitch = rotation.getPitch(0F);
        } else if (packet instanceof CPacketPlayer.PositionRotation positionRotation) {
            fakePlayer.setPositionAndRotationDirect(
                    positionRotation.getX(0D),
                    positionRotation.getY(0D),
                    positionRotation.getZ(0D),
                    positionRotation.getYaw(0F),
                    positionRotation.getPitch(0F),
                    3,
                    true
            );
            fakePlayer.onGround = positionRotation.isOnGround();

            lastPos = new Vec3d(positionRotation.getX(0D), positionRotation.getY(0D), positionRotation.getZ(0D));
            fakePlayer.rotationYawHead = positionRotation.getYaw(0F);
            fakePlayer.rotationYaw = positionRotation.getYaw(0F);
            fakePlayer.rotationPitch = positionRotation.getPitch(0F);
        } else if (packet instanceof CPacketEntityAction action) {
            if (action.getAction() == CPacketEntityAction.Action.START_SPRINTING) {
                fakePlayer.setSprinting(true);
            } else if (action.getAction() == CPacketEntityAction.Action.STOP_SPRINTING) {
                fakePlayer.setSprinting(false);
            } else if (action.getAction() == CPacketEntityAction.Action.START_SNEAKING) {
                fakePlayer.setSneaking(true);
            } else if (action.getAction() == CPacketEntityAction.Action.STOP_SNEAKING) {
                fakePlayer.setSneaking(false);
            }
        } else if (packet instanceof CPacketAnimation animation) {
            fakePlayer.swingArm(animation.getHand());
        }
    }


    public boolean predictBox(double posX, double posY, double posZ, double motionX, double motionY, double motionZ, double motionSlowdown, double size, double gravity, java.util.List<Vec3d> pos, float boxWidth, float boxHeight) {
        RayTraceResult landingPosition = null;
        boolean hasLanded = false;
        int ticks = 0, ticksReleased = 0;

        while (!hasLanded && posY > -60.0D) {
            if (ticks >= pos.size()) {
                break;
            }
            Vec3d posBefore = new Vec3d(posX, posY, posZ);
            Vec3d posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            landingPosition = Minecraft.getMinecraft().world.rayTraceBlocks(posBefore, posAfter, false, true, false);
            posBefore = new Vec3d(posX, posY, posZ);
            posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
            if (landingPosition != null) {
                return false;
            }

            AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);

            Vec3d vec = pos.get(ticksReleased);
            if (arrowBox.intersects(new AxisAlignedBB(
                    vec.xCoord - boxWidth,
                    vec.yCoord - boxHeight / 2F,
                    vec.zCoord - boxWidth,
                    vec.xCoord + boxWidth,
                    vec.yCoord + boxHeight,
                    vec.zCoord + boxWidth
            ))) return true;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
            BlockPos var35 = new BlockPos(posX, posY, posZ);
            Block var36 = Minecraft.getMinecraft().world.getBlockState(var35).getBlock();
            if (var36.getBlockState().getBaseState().getMaterial() == Material.WATER) {
                motionX *= 0.6D;
                motionY *= 0.6D;
                motionZ *= 0.6D;
            } else {
                motionX *= motionSlowdown;
                motionY *= motionSlowdown;
                motionZ *= motionSlowdown;
            }

            motionY -= gravity;
            ticks++;

            ticksReleased += 0;
        }
        return false;
    }
}
