package net.pursue.utils;

import org.apache.commons.lang3.RandomUtils;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;


public class MathUtils {
    public static long randomDelay(final int minDelay, final int maxDelay) {
        return RandomUtils.nextInt(minDelay, maxDelay);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double PI = 3.141592653;
        double output = 1.0 / Math.sqrt(2.0 * PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }


    public static float process(float value, float defF, float defV) {
        return defV / (value / defF);
    }

    public static float centre(float value1, float value2) {
        return (value1 / 2) - (value2 / 2);
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static double incValue(double val, double inc) {
        double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

    public static float incFloatValue(float val, float inc) {
        float one = 1.0f / inc;
        return Math.round(val * one) / one;
    }

    public static double getIncremental(final double val, final double inc) {
        final double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

    public static float wrapAngleTo180_float(float value) {
        value = value % 360.0F;

        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    public static long randomClickDelay(int minCPS, int maxCPS) {
        return (long) (Math.random() * (1000 / minCPS - 1000 / maxCPS + 1) + 1000 / maxCPS);
    }

    public static double coerceAtLeast(double receiver, double minimumValue) {
        return Math.max(receiver, minimumValue);
    }

    public static double interpolateSmooth(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue){
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static float interpolateFloat(float oldValue, float newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).floatValue();
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue){
        return interpolate(oldValue, newValue, (float) interpolationValue).intValue();
    }

    public static double getRandomInRange(double min, double max) {
        SecureRandom random = new SecureRandom();
        return min == max ? min : random.nextDouble() * (max - min) + min;
    }
    public static double getRandom(double min, double max) {
        if (min == max) {
            return min;
        } else if (min > max) {
            final double d = min;
            min = max;
            max = d;
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}