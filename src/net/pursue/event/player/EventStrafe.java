package net.pursue.event.player;

import lombok.Getter;
import lombok.Setter;
import net.pursue.event.Event;


@Setter
@Getter
public class EventStrafe extends Event {
	private float forward, strafe;
	private float friction;
	private float yaw;

	public EventStrafe(float strafe, float forward, float friction, float rotationYaw) {
		this.forward = forward;
		this.strafe = strafe;
		this.friction = friction;
		this.yaw = rotationYaw;
	}

	public void setSpeed(final double speed) {
		setFriction((float) (getForward() != 0 && getStrafe() != 0 ? speed * 0.98F : speed));
	}

}