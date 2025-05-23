package net.pursue.mode.combat;

import de.florianmichael.viamcp.fixes.AttackOrder;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.pursue.event.EventTarget;
import net.pursue.event.packet.EventPacket;
import net.pursue.event.render.EventRender3D;
import net.pursue.event.update.EventMotion;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.mode.player.Blink;
import net.pursue.mode.player.Scaffold;
import net.pursue.shield.IsShield;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.category.MoveCategory;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.rotation.RotationUtils;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@IsShield
public class KillAura extends Mode {

    public static KillAura INSTANCE;

    private final NumberValue<Number> cps = new NumberValue<>(this, "CPS", 15,1,20,1);

    private final NumberValue<Number> range = new NumberValue<>(this, "Range", 3.35,1.00,8.00,0.01);
    private final NumberValue<Number> fakeRange = new NumberValue<>(this, "RotationRange", 5,3.00,8.00,0.01);

    public final BooleanValue<Boolean> rayTrace =new BooleanValue<>(this,"RayTrace",false);

    public final ModeValue<rayMode> rayModeValue = new ModeValue<>(this, "RayTraceMode", rayMode.values(), rayMode.Normal, rayTrace::getValue);

    public enum rayMode {
        Normal,
        Legit
    }

    private final ModeValue<auraModes> auraModesValue = new ModeValue<>(this ,"AuraMode", auraModes.values(), auraModes.Switch);

    enum auraModes {
        Switch,
        Single,
        Multiple
    }

    private final NumberValue<Double> delay = new NumberValue<>(this,"HandoffDelay",100.0,0.0,1000.0,10.0, () -> auraModesValue.getValue().equals(auraModes.Switch));

    public final BooleanValue<Boolean> keepSprint =new BooleanValue<>(this,"KeepSprint",true);
    private final ModeValue<MoveCategory> StrafeValue = new ModeValue<>(this,"StrafeMode", MoveCategory.values(), MoveCategory.Strict);

    private final ModeValue<blockMode> blockModeValue = new ModeValue<>(this, "BlockMode", blockMode.values(), blockMode.Normal);

    enum blockMode {
        Normal,
        Fake,
        OFF
    }

    private final BooleanValue<Boolean> circleValue =new BooleanValue<>(this,"Circle",true);

    private final BooleanValue<Boolean> box =new BooleanValue<>(this,"Box",true);

    private final ColorValue<Color> circleRGB = new ColorValue<>(this,"CircleRGB", Color.WHITE, circleValue::getValue);

    private final NumberValue<Number> circleAccuracy = new NumberValue<>(this,"CircleAccuracy", 60, 0, 60,5, circleValue::getValue);

    private final NumberValue<Number> fov = new NumberValue<>(this,"FOV", 180.0,10.0,180.0,10.0);

    private final BooleanValue<Boolean>
            players = new BooleanValue<>(this,"Players", true),
            animals = new BooleanValue<>(this,"Animals", false),
            mobs = new BooleanValue<>(this,"Mobs", false),
            dead = new BooleanValue<>(this,"Dead",false),
            invisible = new BooleanValue<>(this,"Invisible", false);

    public KillAura() {
        super("KillAura", "杀戮光环", "自动攻击范围内的所有生物", Category.COMBAT);
        INSTANCE = this;
    }

    public EntityLivingBase target = null;
    public boolean isBlock = false;
    private double reach = 0;
    private final TimerUtils attackTimer = new TimerUtils();
    private final TimerUtils switchTimer = new TimerUtils();

    @Override
    public void enable() {
        target = null;
        isBlock = false;
        attackTimer.reset();
        switchTimer.reset();
        reach = range.getValue().doubleValue();
    }

    @Override
    public void disable() {
        target = null;
        if (mc.player == null) return;

        stopBlock();
    }

    @EventTarget
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getType() == EventMotion.Type.Pre) {
            reach = range.getValue().doubleValue();

            if ((target instanceof EntityPlayer || target instanceof EntityMob || target instanceof EntityAnimal) && (((target.getHealth() <= 0 || target.isDead) || dead.getValue()) || mc.player.getDistance(target.posX, target.posY, target.posZ) > fakeRange.getValue().doubleValue())) {
                target = null;
            }
            if (target != null) {
                if (!RotationUtils.isVisibleFOV(target, fov.getValue().intValue())) {
                    target = null;
                }
                if (target instanceof EntityAnimal ||
                        target instanceof EntityVillager ||
                        target instanceof EntitySquid ||
                        target instanceof EntityGolem ||
                        target instanceof EntityBat ||
                        target instanceof EntityMob ||
                        target instanceof EntitySlime ||
                        target instanceof EntityGhast ||
                        target instanceof EntityDragon) {
                    if (target.getHealth() <= 0 || target.isDead || mc.player.getDistance(target.posX, target.posY, target.posZ) > fakeRange.getValue().doubleValue()) {
                        target = null;
                    }
                }
            }
            List<EntityLivingBase> targets = getTargets();


            if (rayTrace.getValue() && rayModeValue.getValue().equals(rayMode.Legit)) {
                target = mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY ? (EntityLivingBase) mc.objectMouseOver.entityHit : null;
            } else {
                int index = 0;
                switch ((auraModes) auraModesValue.getValue()) {
                    case Single: {
                        if (!targets.isEmpty()) {
                            target = targets.getFirst();
                        }
                        break;
                    }
                    case Switch: {
                        if (!targets.isEmpty()) {
                            if (targets.size() > 1) {
                                if (switchTimer.hasTimePassed(delay.getValue().intValue())) {
                                    ++index;
                                    switchTimer.reset();
                                }
                            }
                            if (index >= targets.size()) {
                                index = 0;
                            }

                            target = targets.get(index);
                        }
                        break;
                    }
                    case Multiple: {
                        if (!targets.isEmpty()) {
                            doBlock();

                            targets.forEach(entityLivingBase -> {
                                if (entityLivingBase.getDistance(mc.player) <= range.getValue().doubleValue()) {
                                    attackEntity(entityLivingBase);
                                }
                            });
                        } else {
                            stopBlock();
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate event) {

        setSuffix(auraModesValue.getValue().name());

        if (target != null) {

            if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword && !Scaffold.INSTANCE.isEnable()) {
                doBlock();
            } else {
                stopBlock();
            }

            EntityLivingBase targetEntity = null;

            if (mc.player.getDistance(target.posX, target.posY, target.posZ) < range.getValue().doubleValue()) {
                targetEntity = target;
            }

            if (rayTrace.getValue() && rayModeValue.getValue().equals(rayMode.Normal)) {
                targetEntity = (EntityLivingBase) RotationUtils.getLookingAtEntity(3.3);
            }

            if (targetEntity != null) {
                attackEntity(targetEntity);
            }
        } else {
            stopBlock();
        }
    }

    @EventTarget
    private void onPacket(EventPacket packet) {
        if (mc.player == null) return;

        if (mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword && isBlock) {
            if (packet.getPacket() instanceof CPacketPlayerDigging digging && digging.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                isBlock = false;

                if (target != null && target.getDistance(mc.player) < range.getValue().doubleValue()) {
                    DebugHelper.sendMessage("AutoBlock", "你他妈的漏防了");
                }
            }
        }
    }

    @EventTarget
    private void on3DRender(EventRender3D eventRender3D) {
        if (circleValue.getValue()) {
            GL11.glPushMatrix();


            GL11.glTranslated(
                    mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * mc.timer.field_194147_b - mc.getRenderManager().renderPosX,
                    mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * mc.timer.field_194147_b - mc.getRenderManager().renderPosY,
                    mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * mc.timer.field_194147_b - mc.getRenderManager().renderPosZ
            );
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            GL11.glLineWidth(1F);
            GL11.glColor4f(circleRGB.getColor().getRed() / 255.0F, circleRGB.getColor().getGreen() / 255.0F, circleRGB.getColor().getBlue() / 255.0F, circleRGB.getColor().getAlpha() / 255.0F);
            GL11.glRotatef(90F, 1F, 0F, 0F);
            GL11.glBegin(GL11.GL_LINE_STRIP);

            for (int i = 0; i <= 360; i += (61 - circleAccuracy.getValue().intValue())) {
                GL11.glVertex2f((float) (Math.cos(i * Math.PI / 180.0) * range.getValue().floatValue()), (float) (Math.sin(i * Math.PI / 180.0) * range.getValue().floatValue()));
            }
            GL11.glVertex2f((float) (Math.cos(360 * Math.PI / 180.0) * range.getValue().floatValue()), (float) (Math.sin(360 * Math.PI / 180.0) * range.getValue().floatValue()));

            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glEnd();

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);

            GL11.glPopMatrix();
        }

        if (box.getValue()) {
            if (!getTargets().isEmpty()) {
                for (EntityLivingBase livingBase : getTargets()) {

                    AxisAlignedBB bb = livingBase.getEntityBoundingBox();
                    livingBase.setEntityBoundingBox(bb.expand(0.02, 0.02, 0.02));
                    RenderUtils.drawEntityBox(livingBase, livingBase.hurtTime != 0 ? new Color(255, 0, 0, 60) : new Color(circleRGB.getColor().getRed(), circleRGB.getColor().getGreen(), circleRGB.getColor().getBlue(), 60), true);
                    livingBase.setEntityBoundingBox(bb);
                }
            }
        }
    }

    public List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityLivingBase sb) {
                if (!checkEntity(sb) || mc.player.getDistance(sb.posX, sb.posY, sb.posZ) > fakeRange.getValue().doubleValue()) continue;

                targets.add(sb);
            }
        }
        return targets;
    }

    private void attackEntity(Entity entity) {

        SilentRotation.setRotation(new Vector2f(RotationUtils.getRotations(entity)), MoveCategory.valueOf(StrafeValue.getValue().name()));

        if (attackTimer.hasTimePassed((long) (1000.0 / (cps.getValue().intValue() * 1.5)))) {
            AttackOrder.sendFixedAttack(mc.player, entity, EnumHand.MAIN_HAND);
            attackTimer.reset();
        }
    }

    private void doBlock() {
        switch ((blockMode) blockModeValue.getValue()) {
            case Normal: {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                isBlock = true;
                break;
            }
            case Fake: {
                isBlock = true;
                break;
            }
            case OFF: {
                isBlock = false;
                break;
            }
        }
    }

    private void stopBlock() {
        if (isBlock && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSword && blockModeValue.getValue().equals(blockMode.Normal)) {
            isBlock = false;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        } else if (isBlock) {
            isBlock = false;
        }
    }

    public boolean checkEntity(Entity entity) {
        if (entity == mc.player || FriendManager.isFriend(entity.getName())) {
            return false;
        }

        if (FriendManager.isBot(entity) || !entity.isEntityAlive() && !dead.getValue()) {
            return false;
        }

        if (Scaffold.INSTANCE.isScaffold || Blink.fakePlayer != null && Blink.fakePlayer == entity) {
            return false;
        }

        if (entity instanceof EntityPlayer && players.getValue() && !FriendManager.isFriend(entity)) {
            return true;
        }

        if (entity.isInvisible() && invisible.getValue()) {
            return true;
        }

        if (!RotationUtils.isVisibleFOV(entity, fov.getValue().intValue())) {
            return false;
        }

        if ((entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityGhast || entity instanceof EntityDragon) && mobs.getValue()) {
            return !(((EntityLiving) entity).getHealth() <= 0);
        }

        if ((entity instanceof EntityAnimal ||
                entity instanceof EntityVillager ||
                entity instanceof EntitySquid ||
                entity instanceof EntityGolem ||
                entity instanceof EntityBat) && animals.getValue()) {
            return !(((EntityLiving) entity).getHealth() <= 0);
        }

        return false;
    }
}
