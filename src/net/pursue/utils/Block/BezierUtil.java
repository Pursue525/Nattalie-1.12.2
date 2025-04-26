package net.pursue.utils.Block;


import net.pursue.utils.TimerUtils;

public class BezierUtil {
    private final TimerUtils timer;
    private final float speed;
    private float prevValue;
    private float scale;
    public float target;
    private boolean frozen;
    private float frozenValue;

    public BezierUtil(float speed, float value) {
        this.speed = speed;
        this.timer = new TimerUtils();
        this.update(value);
        this.set(value);
    }

    public void update(float target) {
        if (this.target == target) return;
        prevValue = this.get();
        timer.reset();
        this.scale = target - prevValue;
        this.target = target;
    }

    public void set(float value) {
        timer.reset();
        prevValue = value;
        this.scale = target - prevValue;
    }

    public float get() {
        if (this.frozen) return this.frozenValue;
        long delta = timer.getTimePassed();
        return this.prevValue + this.scale * ease(Math.min(delta / 1000F * speed, 1));
    }

    public void freeze() {
        this.frozen = false;
        this.frozenValue = get();
        this.frozen = true;
    }

    private static float ease(float t) {
        t = 1 - t;

        float p0 = 0F;
        float p1 = 0.01F;
        float p2 = 0F;
        float p3 = 1F;

        float tt = t * t;
        float ttt = tt * t;

        float q0 = -p0 * ttt + 3 * p0 * tt - 3 * p0 * t + p0;
        float q1 = 3 * p1 * ttt - 6 * p1 * tt + 3 * p1 * t;
        float q2 = -3 * p2 * ttt + 3 * p2 * tt;
        float q3 = p3 * ttt;

        return 1 - (q0 + q1 + q2 + q3);

//        return t;
    }
}
