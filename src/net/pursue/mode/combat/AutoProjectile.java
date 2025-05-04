package net.pursue.mode.combat;

import com.google.common.base.Predicates;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.pursue.event.EventTarget;
import net.pursue.event.update.EventMotion;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.mode.misc.Teams;
import net.pursue.mode.player.AutoHeal;
import net.pursue.mode.player.Blink;
import net.pursue.mode.player.Scaffold;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.category.MoveCategory;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.rotation.RotationUtils;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.value.values.NumberValue;

import javax.vecmath.Vector2f;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AutoProjectile extends Mode {

    public NumberValue<Number> minRangeValue = new NumberValue<>(this,"Min Range", 3, 2, 10, 0.1);
    public NumberValue<Number> maxRangeValue = new NumberValue<>(this,"Max Range", 8, 2, 16, 0.1);

    public NumberValue<Number> delayValue = new NumberValue<>(this,"Delay", 500, 0, 1000, 50);
    public NumberValue<Number> predictValue = new NumberValue<>(this,"Predict", 1.5, 0, 2, 0.1);


    public AutoProjectile() {
        super("AutoProjecitle", "自动投掷物", "自动扔投掷物到别人身上击退他", Category.COMBAT);
    }

    private final TimerUtils delay = new TimerUtils();

    private boolean isRunning;

    @Override
    public void enable() {
        delay.reset();
    }

    @EventTarget
    private void onUpdate(EventUpdate event) {
        if (!KillAura.INSTANCE.getTargets().isEmpty() || SilentRotation.getTargetRotation() != null || Scaffold.INSTANCE.isEnable() || Blink.instance.isEnable() || AutoHeal.instance.isEnable() && AutoHeal.instance.modeValue.getValue().equals(AutoHeal.mode.Golden_Apple) || mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemAppleGold
        ) {
            isRunning = false;
            delay.reset();
            return;
        }


        if (delay.hasTimePassed(delayValue.getValue().intValue())) {

            Optional<EntityOtherPlayerMP> target = getTarget(minRangeValue.getValue().floatValue(), maxRangeValue.getValue().floatValue());
            if (target.isEmpty()) return;

            if (getThrowSlot() >= 0) {
                EntityOtherPlayerMP player = target.get();
                double distance = mc.player.getDistance(player);
                float predict = (float) distance * predictValue.getValue().floatValue();

                Vector2f rotationNew = RotationUtils.toRotation(
                        player.getPositionEyes(predict), 0F
                );

                Vector2f targetRotation = new Vector2f(rotationNew.getX(), rotationNew.getY());
                RayTraceResult rayCast = rayCast(targetRotation, distance, 0f, mc.player, false, 0F, 0F);
                if (rayCast == null || rayCast.typeOfHit == RayTraceResult.Type.BLOCK) {
                    return;
                }

                targetRotation.y = MathHelper.clamp(targetRotation.getY(), -90, 90);
                SilentRotation.setRotation(targetRotation, MoveCategory.Silent);
                isRunning = true;
            }
        }
    }

    @EventTarget
    private void noMotion(EventMotion eventMotion) {
        if (eventMotion.getType() == EventMotion.Type.Post) {
            if (isRunning) {
                mc.player.connection.sendPacketNoEvent(new CPacketHeldItemChange(getThrowSlot()));
                mc.player.connection.sendPacketNoEvent(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                delay.reset();
                mc.player.connection.sendPacketNoEvent(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                isRunning = false;
            }
        }
    }

    private int getThrowSlot() {
        for (int i = 0; i < 9; i++) {
            if (!mc.player.inventory.getStackInSlot(i).func_190926_b() && (mc.player.inventory.getStackInSlot(i).getItem() == Items.SNOWBALL || mc.player.inventory.getStackInSlot(i).getItem() == Items.EGG)) {
                return i;
            }
        }
        return -1;
    }

    public static Optional<EntityOtherPlayerMP> getTarget(float min, float max) {
        final double minRange = min * min;
        final double maxRange = max * max;
        return mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityOtherPlayerMP)
                .filter(Entity::isEntityAlive)
                .map(entity -> (EntityOtherPlayerMP) entity)
                .filter(entityOtherPlayerMP -> !FriendManager.isFriend(entityOtherPlayerMP.getName()) || !Teams.instance.isTeam(entityOtherPlayerMP))
                .filter(entityLivingBase -> mc.player.getDistanceSq(entityLivingBase) <= maxRange && mc.player.getDistanceSq(entityLivingBase) >= minRange)
                .min(Comparator.comparingDouble(entity -> mc.player.getDistanceSq(entity)));
    }

    /**
     * RayTraceResult
     */
    public static RayTraceResult rayCast(final Vector2f rotation, final double range, final float expand, Entity entity, boolean throughWall, float predict, float predictPlayer) {
        RayTraceResult objectMouseOver;
        if (entity != null && mc.world != null) {
            objectMouseOver = entity.rayTraceCustom(range, rotation.x, rotation.y, predictPlayer);
            double d1 = range;
            final Vec3d vec3 = entity.getPositionEyes(predictPlayer);

            if (objectMouseOver != null && objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK && !throughWall) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }

            final Vec3d vec31 = mc.player.getVectorForRotation(rotation.y, rotation.x);
            final Vec3d vec32 = vec3.add(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            Entity pointedEntity = null;
            Vec3d vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                if (Blink.instance.isEnable() && Blink.fakePlayer != null && entity1 == Blink.fakePlayer) continue;
                if (entity1.getUniqueID().equals(mc.player.getUniqueID())) continue;

                final float f1 = entity1.getCollisionBorderSize() + expand;
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);

                if (predict != 0) {
                    axisalignedbb = axisalignedbb.offset(
                            (entity1.posX - entity1.lastTickPosX) * predict,
                            (entity1.posY - entity1.lastTickPosY) * predict,
                            (entity1.posZ - entity1.lastTickPosZ) * predict
                    );
                }

                final RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new RayTraceResult(pointedEntity, vec33);
            }

            return objectMouseOver;
        }

        return null;
    }
}
