//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package net.pursue.mode.render;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.*;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.Chunk;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender3D;
import net.pursue.event.update.EventTick;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.utils.category.Category;
import net.pursue.utils.render.RenderUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.NumberValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Projectile extends Mode
{

    private final ColorValue<Color> color = new ColorValue<>(this,"Color", Color.WHITE);
    private final BooleanValue<Boolean> calculatedColor = new BooleanValue<>(this,"CalculatedColor", true);
    private final NumberValue<Double> lineWidth = new NumberValue<>(this,"LineWidth", 6.0, 1.0, 12.0, 1.0);

    public Projectile() {
        super("Projectile", "抛物线","显示抛物线出来，让你看见投掷物落点", Category.RENDER);
    }

    float yaw;
    float pitch;
    private final ArrayList<ArrayList<Vec3d>> points = new ArrayList<ArrayList<Vec3d>>();
    private final HashMap<ArrayList<Vec3d>, RayTraceResult> hashMap = new HashMap<ArrayList<Vec3d>, RayTraceResult>();


    @EventTarget
    private void onUpdate(EventUpdate eventUpdate) {
        this.pitch = mc.player.rotationPitch;
        this.yaw = mc.player.rotationYaw;
    }

    @EventTarget
    public void onTick(EventTick eventTick) {
        this.points.clear();
        this.hashMap.clear();
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity.ticksExisted >= 0 && !entity.onGround && !entity.isInWater() && (entity instanceof EntityArrow || entity instanceof EntitySnowball || entity instanceof EntityEgg || entity instanceof EntityEnderPearl || entity instanceof EntityFireball)) {
                boolean b = true;
                int ticksInAir = 0;
                double posX = entity.posX;
                double posY = entity.posY;
                double posZ = entity.posZ;
                double motionX = entity.motionX;
                double motionY = entity.motionY;
                double motionZ = entity.motionZ;
                float rotationYaw = entity.rotationYaw;
                final float rotationPitch = entity.rotationPitch;
                float prevRotationPitch = entity.prevRotationPitch;
                float prevRotationYaw = entity.prevRotationYaw;
                final ArrayList<Vec3d> vec3s = new ArrayList<Vec3d>();
                final RayTraceResult objectPosition = null;
                while (b) {
                    if (ticksInAir > 300) {
                        b = false;
                    }
                    ++ticksInAir;
                    final Vec3d vec3 = new Vec3d(posX, posY, posZ);
                    final Vec3d vec4 = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
                    final RayTraceResult movingobjectposition = mc.world.rayTraceBlocks(vec3, vec4);
                    if (movingobjectposition != null) {
                        b = false;
                    }
                    posX += motionX;
                    posY += motionY;
                    posZ += motionZ;
                    final float f1 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
                    rotationYaw = (float)(MathHelper.func_181159_b(motionX, motionZ) * 180.0 / 3.141592653589793);
                    if (entity instanceof EntityFireball) {
                        rotationYaw = (float)(MathHelper.func_181159_b(motionX, motionZ) * 180.0 / 3.141592653589793) + 90.0f;
                    }
                    while (rotationPitch - prevRotationPitch >= 180.0f) {
                        prevRotationPitch += 360.0f;
                    }
                    while (rotationYaw - prevRotationYaw < -180.0f) {
                        prevRotationYaw -= 360.0f;
                    }
                    while (rotationYaw - prevRotationYaw >= 180.0f) {
                        prevRotationYaw += 360.0f;
                    }
                    float f2 = 0.99f;
                    if (entity instanceof EntityFireball) {
                        f2 = 0.95f;
                    }
                    float f3 = 0.03f;
                    if (entity instanceof EntityArrow) {
                        f3 = 0.05f;
                    }
                    else if (entity instanceof EntityFireball) {
                        f3 = 0.0f;
                    }
                    if (entity instanceof EntityFireball) {
                        final EntityFireball entityFireball = (EntityFireball)entity;
                        motionX += entityFireball.accelerationX;
                        motionY += entityFireball.accelerationY;
                        motionZ += entityFireball.accelerationZ;
                    }
                    motionX *= f2;
                    motionY *= f2;
                    motionZ *= f2;
                    motionY -= f3;
                    vec3s.add(new Vec3d(posX, posY, posZ));
                }
                this.points.add(vec3s);
                this.hashMap.put(vec3s, objectPosition);
            }
        }
    }

    @EventTarget
    public void onEventRender3D(final EventRender3D eventRender3D) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GlStateManager.disableCull();
        GL11.glDepthMask(false);
        GL11.glLineWidth(this.lineWidth.getValue().floatValue() / 2.0f);
        for (final ArrayList<Vec3d> vec3s : this.points) {
            if (vec3s.size() > 1) {
                if (this.calculatedColor.getValue()) {
                    final double dist = Math.min(Math.max(mc.player.getDistance(vec3s.get(1).xCoord, vec3s.get(1).yCoord, vec3s.get(1).zCoord), 6.0), 36.0) - 6.0;
                    final Color color = new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.color.getAlpha());
                    final float[] hsbColor = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
                    final int colorRGB = Color.HSBtoRGB((float)(0.01141552533954382 * dist), hsbColor[1], hsbColor[2]);
                    final float f = (colorRGB >> 24 & 0xFF) / 255.0f;
                    final float red = (colorRGB >> 16 & 0xFF) / 255.0f;
                    final float green = (colorRGB >> 8 & 0xFF) / 255.0f;
                    final float blue = (colorRGB & 0xFF) / 255.0f;
                    GL11.glColor4f(red, green, blue, 0.85f);
                }
                else {
                    GL11.glColor4f(this.color.getRed() / 255.0f, this.color.getGreen() / 255.0f, this.color.getBlue() / 255.0f, 0.85f);
                }
                final Tessellator tessellator = Tessellator.getInstance();
                final BufferBuilder bufferbuilder = tessellator.getBuffer();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                bufferbuilder.begin(3, DefaultVertexFormats.POSITION);
                for (final Vec3d vec3 : vec3s) {
                    bufferbuilder.pos((float) vec3.xCoord - mc.getRenderManager().renderPosX,
                                    (float) vec3.yCoord - mc.getRenderManager().renderPosY,
                                    (float) vec3.zCoord - mc.getRenderManager().renderPosZ)
                            .endVertex();
                }
                tessellator.draw();
            }
        }
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDepthMask(true);
        GlStateManager.enableCull();
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2848);
    }

    @EventTarget
    public void onRender3D(final EventRender3D render3D) {
        boolean isBow = false;
        float pitchDifference = 0.0f;
        float motionFactor = 1.5f;
        float motionSlowdown = 0.99f;
        if (mc.player.getHeldItemMainhand() != null) {
            final Item heldItem = mc.player.getHeldItemMainhand().getItem();
            float gravity;
            float size;
            if (heldItem instanceof ItemBow) {
                isBow = true;
                gravity = 0.05f;
                size = 0.3f;
                float power = mc.player.getItemInUseCount() / 20.0f;
                power = (power * power + power * 2.0f) / 3.0f;
                if (power < 0.1) {
                    return;
                }
                if (power > 1.0f) {
                    power = 1.0f;
                }
                motionFactor = power * 3.0f;
            }
            else if (heldItem instanceof ItemFishingRod) {
                gravity = 0.04f;
                size = 0.25f;
                motionSlowdown = 0.92f;
            }
            else {
                if (!(heldItem instanceof ItemSnowball) && !(heldItem instanceof ItemEnderPearl) && !(heldItem instanceof ItemEgg) && !heldItem.equals(Item.getItemById(46))) {
                    return;
                }
                gravity = 0.03f;
                size = 0.25f;
            }
            double posX = mc.getRenderManager().renderPosX - MathHelper.cos(this.yaw / 180.0f * 3.1415927f) * 0.16f;
            double posY = mc.getRenderManager().renderPosY + mc.player.getEyeHeight() - 0.10000000149011612;
            double posZ = mc.getRenderManager().renderPosZ - MathHelper.sin(this.yaw / 180.0f * 3.1415927f) * 0.16f;
            double motionX = -MathHelper.sin(this.yaw / 180.0f * 3.1415927f) * MathHelper.cos(this.pitch / 180.0f * 3.1415927f) * (isBow ? 1.0 : 0.4);
            double motionY = -MathHelper.sin((this.pitch + pitchDifference) / 180.0f * 3.1415927f) * (isBow ? 1.0 : 0.4);
            double motionZ = MathHelper.cos(this.yaw / 180.0f * 3.1415927f) * MathHelper.cos(this.pitch / 180.0f * 3.1415927f) * (isBow ? 1.0 : 0.4);
            final float distance = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= distance;
            motionY /= distance;
            motionZ /= distance;
            motionX *= motionFactor;
            motionY *= motionFactor;
            motionZ *= motionFactor;
            RayTraceResult landingPosition = null;
            boolean hasLanded = false;
            boolean hitEntity = false;
            RenderUtils.enableRender3D(true);
            RenderUtils.color(color.getColorRGB());
            GL11.glLineWidth(2.0f);
            GL11.glBegin(3);
            while (!hasLanded && posY > 0.0) {
                Vec3d posBefore = new Vec3d(posX, posY, posZ);
                Vec3d posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
                landingPosition = mc.world.rayTraceBlocks(posBefore, posAfter, false, true, false);
                posBefore = new Vec3d(posX, posY, posZ);
                posAfter = new Vec3d(posX + motionX, posY + motionY, posZ + motionZ);
                if (landingPosition != null) {
                    hasLanded = true;
                    posAfter = new Vec3d(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
                }
                final AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
                final List entityList = this.getEntitiesWithinAABB(arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
                for (int i = 0; i < entityList.size(); ++i) {
                    final Entity var18 = (Entity) entityList.get(i);
                    if (var18.canBeCollidedWith() && var18 != mc.player) {
                        final AxisAlignedBB var19 = var18.getEntityBoundingBox().expand((double)size, (double)size, (double)size);
                        final RayTraceResult possibleEntityLanding = var19.calculateIntercept(posBefore, posAfter);
                        if (possibleEntityLanding != null) {
                            hitEntity = true;
                            hasLanded = true;
                            landingPosition = possibleEntityLanding;
                        }
                    }
                }
                posX += motionX;
                posY += motionY;
                posZ += motionZ;
                final BlockPos var20 = new BlockPos(posX, posY, posZ);
                final Block var21 = mc.world.getBlockState(var20).getBlock();
                if (var21.getMaterial() == Material.WATER) {
                    motionX *= 0.6;
                    motionY *= 0.6;
                    motionZ *= 0.6;
                }
                else {
                    motionX *= motionSlowdown;
                    motionY *= motionSlowdown;
                    motionZ *= motionSlowdown;
                }
                motionY -= gravity;
                GL11.glVertex3d(posX - mc.getRenderManager().renderPosX, posY - mc.getRenderManager().renderPosY, posZ - mc.getRenderManager().renderPosZ);
            }
            GL11.glEnd();
            GL11.glPushMatrix();
            GL11.glTranslated(posX - mc.getRenderManager().renderPosX, posY - mc.getRenderManager().renderPosY, posZ - mc.getRenderManager().renderPosZ);
            if (landingPosition != null) {
                final int side = landingPosition.sideHit.getIndex();
                if (side == 1 && heldItem instanceof ItemEnderPearl) {
                    RenderUtils.color(new Color(255, 255, 0, 255).getRGB());
                }
                else if (side == 2) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                }
                else if (side == 3) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                }
                else if (side == 4) {
                    GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                }
                else if (side == 5) {
                    GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                }
                if (hitEntity) {
                    RenderUtils.color(new Color(255, 255, 2, 255).getRGB());
                }
            }
            GL11.glPopMatrix();
            RenderUtils.disableRender3D(true);
        }
    }

    private List getEntitiesWithinAABB(final AxisAlignedBB axisalignedBB) {
        final ArrayList list = new ArrayList();
        final int chunkMinX = MathHelper.floor((axisalignedBB.minX - 2.0) / 16.0);
        final int chunkMaxX = MathHelper.floor((axisalignedBB.maxX + 2.0) / 16.0);
        final int chunkMinZ = MathHelper.floor((axisalignedBB.minZ - 2.0) / 16.0);
        final int chunkMaxZ = MathHelper.floor((axisalignedBB.maxZ + 2.0) / 16.0);

        for (int x = chunkMinX; x <= chunkMaxX; ++x) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                Chunk chunk = mc.world.getChunkProvider().getLoadedChunk(x, z);

                if (chunk != null && !chunk.isEmpty()) {
                    mc.world.getChunkFromChunkCoords(x, z).getEntitiesWithinAABBForEntity(mc.player, axisalignedBB, list, EntitySelectors.IS_ALIVE);
                }
            }
        }
        return list;
    }
}
