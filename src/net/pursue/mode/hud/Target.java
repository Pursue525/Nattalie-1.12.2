package net.pursue.mode.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.category.Category;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.player.InvUtils;
import net.pursue.utils.player.PlayerData;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.NumberValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Target extends Mode {

    private final ColorValue<Color> stringColor = new ColorValue<>(this, "StringColor", Color.WHITE);

    private final ColorValue<Color> healColor = new ColorValue<>(this, "HealColor", Color.WHITE);

    private final BooleanValue<Boolean> blur = new BooleanValue<>(this, "Blur", true);
    private final NumberValue<Number> blurInt = new NumberValue<>(this,"BlurInt", 10, 1, 100, 1, blur::getValue);


    public Target() {
        super("Target", "目标", "显示你正在攻击的目标", Category.HUD);
    }

    private double a = 1;
    private float preW;
    private float endWidth;

    private double countscale = 0;
    private EntityLivingBase entities = null;
    private final List<PlayerData> data = new ArrayList<>();

    @EventTarget
    private void onUpdate(EventUpdate update) {
        for (Entity entity : mc.world.loadedEntityList) {

            if (entity == mc.player || !(entity instanceof EntityLivingBase livingBase) || FriendManager.isFriend(entity) || FriendManager.isBot(entity))
                continue;

            if (livingBase.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemAppleGold && !InvUtils.isEnchantedGoldenApple(livingBase.getHeldItem(EnumHand.MAIN_HAND))) {
                data.add(new PlayerData(livingBase, String.valueOf(livingBase.getHeldItem(EnumHand.MAIN_HAND).stackSize)));
            }
        }

        if (getOldTarget() != null) {
            entities = getOldTarget();
        }

        if (entities != null && countscale == 0) {
            entities = null;
        }
    }

    @EventTarget
    public void onRender(EventRender2D render2D) {
        float x = 300;
        float y = 200;


        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                x = data.getX();
                y = data.getY();
            }
        }

        if (getOldTarget() != null) {
            countscale = AnimationUtils.moveUD((float) countscale, (float) 1, (float) (5 * RoundedUtils.deltaTime()), (float) (4 * RoundedUtils.deltaTime()));
        } else {
            countscale = AnimationUtils.moveUD((float) countscale, (float) 0, (float) (5 * RoundedUtils.deltaTime()), (float) (4 * RoundedUtils.deltaTime()));
        }

        String name = mc.player.getName();
        int width = Math.max(35 + FontManager.font18.getWidth(name), 120);

        if (entities != null) {
            a = AnimationUtils.smooth(entities.hurtTime > 0 ? 150 : 1, a, 8f / Minecraft.getDebugFPS());
            Color color = entities.hurtTime > 0 ? new Color(255, 0, 0, (int) a) : new Color(1, 1, 1, (int) a);
            width = Math.max(35 + FontManager.font18.getWidth(name), 120);

            GL11.glPushMatrix();
            GL11.glTranslated(x + (width / 2F), y + (40 / 2F), 0);
            GL11.glScaled(countscale, 1, countscale);
            GL11.glTranslated(-(x + (width / 2F)), -(y + (40 / 2F)), 0);
            drawTarget(x, y, width, stringColor.getColor(), healColor.getColor(), entities, color);
            GL11.glPopMatrix();
        }

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (data.getTitle().equals(this.getModeName())) {
                data.setX(x);
                data.setY(y);
                data.setWidth(width);
                data.setHeight(42);
            }
        }
    }

    private void drawTarget(float x, float y, float width, Color stringColor, Color healColor, EntityLivingBase target, Color color) {
        if (blur.getValue()) {
            RoundedUtils.drawRoundBlur(x, y, width, 40, 2, new Color(0, 0, 0, 120), blurInt.getValue().intValue());
        } else {
            RoundedUtils.drawRound(x, y, width, 40, 2, new Color(0, 0, 0, 120));
        }

        RoundedUtils.drawHead(target, x + 5, y + 5, 20,20,2, color);
        FontManager.font18.drawString(target.getName(), x + 30, y + 6, stringColor.getRGB());

        double maxhealthnonnull = target.getMaxHealth() <= 0 ? 0 : target.getMaxHealth();
        double healthnonnull = target.getHealth() <= 0 ? 0 : target.getHealth() + target.getAbsorptionAmount();
        double healthPercentage = MathHelper.clamp((healthnonnull) / (maxhealthnonnull), 0, 1);

        endWidth = (float) AnimationUtils.smooth((float) Math.max(0, (width - 12) * healthPercentage), endWidth, 8f / Minecraft.getDebugFPS());

        String hp = "Hp: " + Math.round((target.getHealth() + target.getAbsorptionAmount()) * 10.0) / 10.0;

        FontManager.font18.drawString(hp, x + 30, y + FontManager.font18.getHeight() + 6, stringColor.getRGB());

        String gapSize = "0";

        if (!data.isEmpty()) {
            for (PlayerData data1 : data) {
                if (data1.base() == target) {
                    gapSize = data1.tag();
                }
            }
        }

        FontManager.font18.drawString("Apples: " + gapSize, x + 35 + FontManager.font18.getWidth(hp), y + FontManager.font18.getHeight() + 6, stringColor.getRGB());

        if (target.hurtTime == 0) {
            preW = endWidth;
        }

        if (preW != endWidth) {
            RoundedUtils.drawRound(x + 5 + endWidth, y + 30, preW - endWidth, 5, 0, Color.RED);
        }

        RoundedUtils.drawRound(x + 5, y + 30, endWidth, 5, 1, healColor);
    }


    private EntityLivingBase getOldTarget() {
        if (KillAura.INSTANCE.target != null) {
            return KillAura.INSTANCE.target;
        }

        if (mc.currentScreen instanceof GuiChat) {
            return mc.player;
        }

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            return (EntityLivingBase) mc.objectMouseOver.entityHit;
        }
        return null;
    }
}
