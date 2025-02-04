package net.pursue.mode.hud;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.mode.combat.KillAura;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.ColorValue;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Target extends Mode {

    private final ColorValue<Color> stringColor = new ColorValue<>(this, "StringColor", Color.WHITE);

    private final ColorValue<Color> healColor = new ColorValue<>(this, "HealColor", Color.WHITE);

    public Target() {
        super("Target", "目标", "显示你正在攻击的目标", Category.HUD);
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

        String name = mc.player.getName();
        int width = Math.max(35 + FontManager.font18.getWidth(name), 110);

        int yFix = 0;
        if (getTarget() != null) {
            for (EntityLivingBase entityLivingBases : getTarget()) {
                if (entityLivingBases != null && !entityLivingBases.isDead) {

                    width = Math.max(35 + FontManager.font18.getWidth(name), 100);


                    double maxhealthnonnull = entityLivingBases.getMaxHealth() <= 0 ? 20 : entityLivingBases.getMaxHealth();
                    double healthnonnull = entityLivingBases.getHealth() <= 0 ? 1 : entityLivingBases.getHealth() + entityLivingBases.getAbsorptionAmount();
                    double healthPercentage = MathHelper.clamp((healthnonnull) / (maxhealthnonnull), 0, 1);

                    float endWidth = (float) Math.max(0, (width - 12) * healthPercentage);

                    drawTarget(x, y + yFix, width, stringColor.getColor(), healColor.getColor(), entityLivingBases);
                    yFix += 42;
                }
            }
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

    private void drawTarget(float x, float y, float width, Color stringColor, Color healColor, EntityLivingBase target) {
        RoundedUtils.drawRound(x, y, width, 40, 2, new Color(0,0,0,120));
        RoundedUtils.drawHead(target, x + 5, y + 5, 20,20,2);
        FontManager.font18.drawString(target.getName(), x + 30, y + 6, stringColor.getRGB());

        double maxhealthnonnull = target.getMaxHealth() <= 0 ? 0 : target.getMaxHealth();
        double healthnonnull = target.getHealth() <= 0 ? 0 : target.getHealth() + target.getAbsorptionAmount();
        double healthPercentage = MathHelper.clamp((healthnonnull) / (maxhealthnonnull), 0, 1);

        float endWidth = (float) Math.max(0, (width - 12) * healthPercentage);

        FontManager.font18.drawString("Health: " + String.valueOf(Math.round((target.getHealth() + target.getAbsorptionAmount()) * 10.0) / 10.0), x + 30, y + FontManager.font18.getHeight() + 6, stringColor.getRGB());

        RoundedUtils.drawRound(x + 5, y + 30, endWidth, 5, 2, healColor);
    }


    private List<EntityLivingBase> getTarget() {
        List<EntityLivingBase> entities = new ArrayList<>();
        if (KillAura.INSTANCE.isEnable()) {
            return KillAura.INSTANCE.getTargets();
        } else {
            RayTraceResult rayTraceResult = mc.objectMouseOver;

            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.ENTITY) {
                entities.add((EntityLivingBase) rayTraceResult.entityHit);
                return entities;
            }
        }
        if (mc.currentScreen instanceof GuiChat) {
            entities.add(mc.player);
            return entities;
        }
        return null;
    }
}
