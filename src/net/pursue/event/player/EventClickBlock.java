package net.pursue.event.player;

import lombok.Getter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.pursue.event.Event;

@Getter
public class EventClickBlock extends Event {
    private final BlockPos clickedBlock;
    private final EnumFacing enumFacing;

    public EventClickBlock(BlockPos clickedBlock, EnumFacing enumFacing) {
        this.clickedBlock = clickedBlock;
        this.enumFacing = enumFacing;
    }

}
