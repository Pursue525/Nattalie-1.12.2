package net.pursue.utils.render;

import static java.lang.Math.max;
import static java.lang.Math.min;


public class AnimationUtils {
    public static float moveUD(final float current, final float end, final float smoothSpeed, final float minSpeed) {
        float movement = (end - current) * smoothSpeed;
        if (movement > 0.0f) {
            movement = Math.max(minSpeed, movement);
            movement = Math.min(end - current, movement);
        } else if (movement < 0.0f) {
            movement = Math.min(-minSpeed, movement);
            movement = Math.max(end - current, movement);
        }
        return current + movement;
    }

    public static double smooth(double target, double current, double speed) {
        boolean larger = target > current;
        if (speed < 0.0) {
            speed = 0.0;
        } else if (speed > 1.0) {
            speed = 1.0;
        }
        if (target == current) {
            return target;
        }
        double dif = max(target, current) - min(target, current);
        double factor = dif * speed;
        if (factor < 0.1) {
            factor = 0.1;
        }
        if (larger) {
            if (current + factor > target) {
                current = target;
            } else {
                current += factor;
            }
        } else {
            if (current - factor < target) {
                current = target;
            } else {
                current -= factor;
            }
        }
        return current;
    }
}
