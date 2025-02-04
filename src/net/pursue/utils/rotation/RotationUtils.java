package net.pursue.utils.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import org.lwjgl.util.vector.Vector2f;

import javax.vecmath.Vector3d;
import java.awt.*;

public class RotationUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static boolean isLineRotation() {
        final double yaw = Math.abs(mc.player.rotationYaw) % 90;

        return yaw >= 75 || yaw <= 15;
    }

    public static float[] getRotationsToPosition(double x, double y, double z) {
        double deltaX = x - mc.player.posX;
        double deltaY = y - mc.player.posY - mc.player.getEyeHeight();
        double deltaZ = z - mc.player.posZ;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));

        return new float[] {yaw, pitch};
    }

    public static Entity getLookingAtEntity(double range) {
        Vec3d playerPos = mc.player.getPositionEyes(1.0f);

        double lookX = -Math.sin(Math.toRadians(SilentRotation.getRotations().x)) * Math.cos(Math.toRadians(SilentRotation.getRotations().y));

        double lookY = -Math.sin(Math.toRadians(SilentRotation.getRotations().y));

        double lookZ = Math.cos(Math.toRadians(SilentRotation.getRotations().x)) * Math.cos(Math.toRadians(SilentRotation.getRotations().y));

        Vec3d lookVec = new Vec3d(lookX, lookY, lookZ).normalize();

        Vec3d targetPoint = playerPos.add(lookVec.scale(range));

        RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(playerPos, targetPoint, false, true, false);
        if (rayTraceResult != null) {
            targetPoint = rayTraceResult.hitVec;
        }

        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, mc.player.getEntityBoundingBox().expand(lookVec.xCoord * range, lookVec.yCoord * range, lookVec.zCoord * range))) {
            if (entity != mc.player) {
                if (entity.getEntityBoundingBox().intersects(playerPos, targetPoint)) {
                    double entityDistance = mc.player.getDistance(entity.posX, entity.posY, entity.posZ);
                    if (entityDistance < closestDistance) {
                        closestDistance = entityDistance;
                        closestEntity = entity;
                    }
                }
            }
        }

        return closestEntity;
    }



    public static Vec3d getVectorForRotation(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }

    public static javax.vecmath.Vector2f toRotation(final Vec3d vec, float partialTicks) {
        final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.getEntityBoundingBox().minY +
                mc.player.getEyeHeight(), mc.player.posZ).addVector(mc.player.motionX * partialTicks, mc.player.motionY * partialTicks, mc.player.motionZ * partialTicks);
        return new javax.vecmath.Vector2f(RotationNew(eyesPos, vec));
    }

    public static float[] RotationNew(Vec3d from, Vec3d to) {
        final Vec3d diff = to.subtract(from);

        float yaw = MathHelper.wrapDegrees(
                (float) Math.toDegrees(Math.atan2(diff.zCoord, diff.xCoord)) - 90F
        );
        float pitch = MathHelper.wrapDegrees(
                (float) (-Math.toDegrees(Math.atan2(diff.yCoord, Math.sqrt(diff.xCoord * diff.xCoord + diff.zCoord * diff.zCoord))))
        );

        return new float[] {yaw, pitch};
    }

    public static float[] getAngles(Entity entity) {
        double deltaX = entity.posX - mc.player.posX;
        double deltaZ = entity.posZ - mc.player.posZ;
        double deltaY = entity.posY - 1.6 + (double)entity.getEyeHeight() - mc.player.posY;
        double degrees = Math.toDegrees(Math.atan(deltaZ / deltaX));
        double yawToEntity = deltaZ < 0.0 && deltaX < 0.0 ? 90.0 + degrees : (deltaZ < 0.0 && deltaX > 0.0 ? -90.0 + degrees : Math.toDegrees(-Math.atan(deltaX / deltaZ)));
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / (double)MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ)));

        float rotationYaw = Double.isNaN((double)mc.player.rotationYaw - yawToEntity) ? 0.0f : MathHelper.wrapDegrees(-(mc.player.rotationYaw - (float)yawToEntity)) + mc.player.rotationYaw;
        float rotationPitch = Double.isNaN((double)mc.player.rotationPitch - pitchToEntity) ? 0.0f : -MathHelper.wrapDegrees(mc.player.rotationPitch - (float)pitchToEntity) + mc.player.rotationPitch;

        return new float[]{rotationYaw, rotationPitch};
    }

    public static float[] getRotations(final Entity entity) {
        if (entity == null) {
            return null;
        }
        final double diffX = entity.posX - mc.player.posX;
        final double diffZ = entity.posZ - mc.player.posZ;
        double diffY = getDiffY(entity);

        final double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        final float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI));

        return new float[]{yaw, pitch};
    }

    private static double getDiffY(Entity entity) {
        double diffY;
        if (entity instanceof EntityLivingBase) {
            diffY = entity.posY + (entity.getEyeHeight()) - (mc.player.posY + mc.player.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (mc.player.posY + mc.player.getEyeHeight());
        }

        if (mc.player.posY < entity.posY && entity instanceof EntityPlayer) {
            diffY = entity.getEntityBoundingBox().minY - (mc.player.posY + mc.player.getEyeHeight());
        }
        return diffY;
    }

    public static boolean isVisibleFOV(final Entity e, final float fov) {
        return ((Math.abs(RotationUtils.getRotations(e)[0] - mc.player.rotationYaw) % 360.0f > 180.0f) ? (360.0f - Math.abs(RotationUtils.getRotations(e)[0] - mc.player.rotationYaw) % 360.0f) : (Math.abs(RotationUtils.getRotations(e)[0] - mc.player.rotationYaw) % 360.0f)) <= fov;
    }

    public static float[] getRotation(Entity entity) {
        double pX = Minecraft.getMinecraft().player.posX;
        double pY = Minecraft.getMinecraft().player.posY + (double)Minecraft.getMinecraft().player.getEyeHeight();
        double pZ = Minecraft.getMinecraft().player.posZ;
        double eX = entity.posX;
        double eY = entity.posY + (double)(entity.height / 2.0f);
        double eZ = entity.posZ;
        double dX = pX - eX;
        double dY = pY - eY;
        double dZ = pZ - eZ;
        double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
        double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
        double pitch = Math.toDegrees(Math.atan2(dH, dY));
        return new float[]{(float)yaw, (float)(90.0 - pitch)};
    }


    public static float[] getRotation(EntityLivingBase entity) {
        double diffX = entity.posX - mc.player.posX;
        double diffZ = entity.posZ - mc.player.posZ;
        double diffY = entity.posY + (double) entity.getEyeHeight() - (mc.player.posY + (double) mc.player.getEyeHeight());
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180 / 3.141592653589) - 90.0f;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180 / 3.141592653589));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationBlockLegit(BlockPos targetPos, EnumFacing facing) {
        Vec3d playerEyePosition = mc.player.getPositionEyes(1.0F);

        double x = targetPos.getX() + 0.5D;
        double y = targetPos.getY() + 0.5D;
        double z = targetPos.getZ() + 0.5D;

        x += (double) facing.getDirectionVec().getX() * 0.5D;
        y += (double) facing.getDirectionVec().getY() * 0.5D;
        z += (double) facing.getDirectionVec().getZ() * 0.5D;

        Vec3d to = new Vec3d(x, y, z);

        final Vec3d diff = to.subtract(playerEyePosition);
        final double distance = Math.hypot(diff.xCoord, diff.zCoord);
        final float yaw = (float) (MathHelper.atan2(diff.zCoord, diff.xCoord) * (180.0D / Math.PI)) - 90.0F;
        final float pitch = (float) (-(MathHelper.atan2(diff.yCoord, distance) *(180.0D / Math.PI)));

        return new float[]{yaw, pitch};
    }

    private static float[] getRotationsByVec(final Vec3d origin, final Vec3d position) {
        final Vec3d difference = position.subtract(origin);
        final double distance = difference.flat().lengthVector();
        final float yaw = (float) Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        final float pitch = (float) (-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new float[]{yaw, pitch};
    }


    public static float[] getRotationBlock(final BlockPos pos) {
        return getRotationsByVec(mc.player.getPositionVector().addVector(0.0, mc.player.getEyeHeight(), 0.0), new Vec3d(pos.getX() + 0.51, pos.getY() + 0.51, pos.getZ() + 0.51));
    }

    public static float getRotation(float currentRotation, float targetRotation, float maxIncrement) {
        float deltaAngle = MathHelper.wrapDegrees(targetRotation - currentRotation);
        if (deltaAngle > maxIncrement) {
            deltaAngle = maxIncrement;
        }
        if (deltaAngle < -maxIncrement) {
            deltaAngle = -maxIncrement;
        }
        return currentRotation + deltaAngle / 2.0f;
    }

    public static double getAngleDifference(float a, float b) {
        return ((a - b) % 360f + 540f) % 360f - 180f;
    }

    public static float[] getCheatRotations(double x, double y, double z) {
        double var4 = x - mc.player.posX + 0.5;
        double var6 = z - mc.player.posZ + 0.5;
        double var8 = y - (mc.player.posY + (double) mc.player.getEyeHeight() - 1.0);
        double var14 = MathHelper.sqrt(var4 * var4 + var6 * var6);
        float var12 = (float)(Math.atan2(var6, var4) * 180.0 / Math.PI) - 90.0f;
        return new float[]{var12, (float)(-Math.atan2(var8, var14) * 180.0 / Math.PI)};
    }

}
