package net.pursue.utils.player;


import net.minecraft.block.BlockAir;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.math.BlockPos;
import net.pursue.utils.client.UtilsManager;

public class MovementUtils extends UtilsManager {
    public static final double WALK_SPEED = 0.221;
    public static final double MOD_SWIM = 0.115F / WALK_SPEED;
    public static final double[] MOD_DEPTH_STRIDER = {
            1.0F,
            0.1645F / MOD_SWIM / WALK_SPEED,
            0.1995F / MOD_SWIM / WALK_SPEED,
            1.0F / MOD_SWIM,
    };

    private static double x = 0.0;
    private static double y = 0.0;
    private static double z = 0.0;

    public static final double MOD_SPRINTING = 1.3F;
    public static final double MOD_SNEAK = 0.3F;
    public static final double MOD_WEB = 0.105 / WALK_SPEED;

    public static float getMovingYaw() {
        return (float) (getDirection() * 180f / (float) Math.PI);
    }

    public static double getDirection() {
        float rotationYaw = mc.player.rotationYaw;

        if (mc.player.movementInput.field_192832_b < 0f)
            rotationYaw += 180f;

        float forward = 1f;
        if (mc.player.movementInput.field_192832_b < 0f)
            forward = -0.5f;
        else if (mc.player.movementInput.field_192832_b > 0f)
            forward = 0.5f;

        if (mc.player.movementInput.moveStrafe > 0f)
            rotationYaw -= 90f * forward;
        if (mc.player.movementInput.moveStrafe < 0f)
            rotationYaw += 90f * forward;

        return Math.toRadians(rotationYaw);
    }

    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static boolean isMoving() {
        return mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.field_192832_b!= 0;
    }

    public static double direction() {
        float rotationYaw = mc.player.rotationYaw;

        if (mc.player.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.player.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.player.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.player.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.player.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public static boolean overVoid(double posX, double posY, double posZ) {
        for (int i = (int) posY; i > -1; i--) {
            if (!(mc.world.getBlockState(new BlockPos(posX, i, posZ)).getBlock() instanceof BlockAir)) {
                return false;
            }
        }
        return true;
    }

    public static double speed() {
        return Math.hypot(mc.player.motionX, mc.player.motionZ);
    }

    public static boolean overVoid() {
        return overVoid(mc.player.posX, mc.player.posY, mc.player.posZ);
    }

    public static int depthStriderLevel() {
        return EnchantmentHelper.getDepthStriderModifier(mc.player);
    }

    public static double getSpeed() {
        double motionX = mc.player.motionX;
        double motionZ = mc.player.motionZ;
        return Math.sqrt(motionX * mc.player.motionX + motionZ * mc.player.motionZ);
    }

    public static double predictedMotion(double motion, int ticks) {
        if (ticks == 0) return motion;
        double predicted = motion;

        for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
        }

        return predicted;
    }
}
