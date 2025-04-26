package net.pursue.mode.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.utils.player.PacketUtils;
import net.pursue.value.values.NumberValue;

public class SpeedMine extends Mode {
    private final NumberValue<Double> speed = new NumberValue<>(this ,"Speed", 1.1, 1.0, 3.0, 0.1);
    private EnumFacing facing;
    private BlockPos pos;
    private boolean boost = false;
    private float damage = 0f;

    public SpeedMine() {
        super("SpeedMine", "快速挖掘", "加速你的挖掘", Category.WORLD);
    }
    @Override
    public void enable() {
        pos = null;
        damage = 0f;
        facing = null;
        boost = false;
    }


    @EventTarget
    private void onPacket(EventPacket eventPacket) {
        if (eventPacket.getPacket() instanceof CPacketPlayerDigging digging) {
            if (digging.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK || digging.getAction() == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                pos = digging.getPosition();
                facing = digging.getFacing();
                boost = true;
                damage = 0f;
                PacketUtils.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
            } else if (digging.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                pos = null;
                damage = 0f;
                facing = null;
                boost = false;
            }
        }
    }

    @EventTarget
    private void onMotion(EventUpdate eventUpdate) {
        if (pos != null && boost) {
            IBlockState blockState = mc.world.getBlockState(pos);
            damage += blockState.getBlock().getPlayerRelativeBlockHardness(blockState, mc.player, mc.world, pos) * speed.getValue();
            if (damage >= 1) {
                try {
                    mc.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }
                PacketUtils.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, this.facing));
                PacketUtils.sendPacketNoEvent(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, facing));
                damage = 0f;
                boost = false;
            }
        }
    }
}
