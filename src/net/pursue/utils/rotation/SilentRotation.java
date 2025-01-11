package net.pursue.utils.rotation;


import lombok.Getter;
import lombok.Setter;
import net.pursue.event.EventTarget;
import net.pursue.event.player.EventJump;
import net.pursue.event.player.EventStrafe;
import net.pursue.event.update.EventMotion;
import net.pursue.event.update.EventUpdate;
import net.pursue.utils.category.MoveCategory;
import net.pursue.utils.client.UtilsManager;

import javax.vecmath.Vector2f;

public class SilentRotation extends UtilsManager {
    @Getter
    @Setter
    private static Vector2f targetRotation;
    @Getter
    private static Vector2f rotations;
    @Getter
    private static MoveCategory category;

    @EventTarget
    private void onStrafe(EventStrafe eventStrafe) {
        if (targetRotation != null) {
            eventStrafe.setYaw(targetRotation.x);
        }
    }

    @EventTarget
    private void onJump(EventJump eventJump) {
        if (targetRotation != null) {
            eventJump.setYaw(targetRotation.x);
        }
    }

    @EventTarget
    private void onMotion(EventMotion eventMotion) {
        if (targetRotation != null) {
            if (eventMotion.getType() == EventMotion.Type.Pre) {
                eventMotion.setYaw(targetRotation.x);
                eventMotion.setPitch(targetRotation.y);

                mc.player.renderYawOffset = targetRotation.x;
                mc.player.rotationYawHead = targetRotation.x;
            }

            if (eventMotion.getType() == EventMotion.Type.Post) {
                targetRotation = null;
            }
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        rotations = targetRotation != null ? targetRotation : mc.player.getRotation();
    }

    public static void setRotation(Vector2f rotation, MoveCategory category) {
        SilentRotation.targetRotation = rotation;
        SilentRotation.category = category;
    }

}
