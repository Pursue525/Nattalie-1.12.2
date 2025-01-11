package net.pursue.event.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.MoverType;
import net.pursue.event.Event;


@Getter
@Setter
public class EventMove extends Event {
    private final MoverType moverType;
    private double x;
    private double y;
    private double z;

    public EventMove(MoverType moverType, double x, double y, double z) {
        this.moverType = moverType;
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public void setStopMove(boolean y) {
        this.x = 0;
        if (y) this.y = 0;
        this.z = 0;
    }



    public void setSpeed(double x, double y, double z) {
        this.x *= x * 10;
        this.z *= z * 10;
        this.y *= y * 10;
    }

    public void setSpeed(double x, double z) {
        this.x *= x * 10;
        this.z *= z * 10;
    }

    public void setSpeed(double x) {
        this.x *= x * 10;
        this.z *= x * 10;
    }

}

