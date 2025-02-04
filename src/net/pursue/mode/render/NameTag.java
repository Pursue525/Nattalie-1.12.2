package net.pursue.mode.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.event.update.EventUpdate;
import net.pursue.mode.Mode;
import net.pursue.mode.hud.Armor;
import net.pursue.mode.misc.Teams;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.category.Category;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.ModeValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NameTag extends Mode {

    public static NameTag instance;

    public final ModeValue<mode> modeValue = new ModeValue<>(this, "Mode", mode.values(), mode.Normal);

    public enum mode {
        Normal,
        Nattalie
    }

    public final ColorValue<Color> backGroundColor = new ColorValue<>(this, "BackGround-Color", new Color(0,0,0,80));
    public final ColorValue<Color> backGroundColor2 = new ColorValue<>(this, "BackGround2-Color", Color.WHITE);
    public final ColorValue<Color> stringColor = new ColorValue<>(this, "String-Color", Color.WHITE);
    public final BooleanValue<Boolean> deBug = new BooleanValue<>(this, "DeBug", false);

    public NameTag() {
        super("NameTag", "玩家名称标签", "显示玩家身上的装备和他的名称等", Category.RENDER);
        instance = this;
    }

    public final List<EntityPlayer> playerList = new ArrayList<EntityPlayer>();
    private float width;

    @Override
    public void disable() {
        playerList.clear();
    }

    @EventTarget
    private void onRender(EventRender2D event) {
        if (modeValue.getValue().equals(mode.Nattalie)) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityPlayer) || entity == mc.player) continue;

                Vec2f base = RenderUtils.worldScreenPos(new Vec3d(
                        entity.posX,
                        entity.posY + 2.9,
                        entity.posZ
                ));

                if (base == null) continue;

                Vec2f pos = new Vec2f(base.x, base.y);

                String name = entity.getName() + getTag((EntityPlayer) entity);
                float w = 10f + FontManager.font22.getWidth(name);

                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.disableDepth();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

                draw((EntityPlayer) entity, pos, name, w);

                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);

                draw((EntityPlayer) entity, pos, name, w);

                GlStateManager.enableLighting();
                GlStateManager.disableBlend();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.popMatrix();
            }
        }
    }

    private void draw(EntityPlayer entity, Vec2f pos, String name, float w) {
        RoundedUtils.drawRound(pos.x - w / 2, pos.y, w, 20, 0, backGroundColor.getColor());
        RoundedUtils.drawRound(pos.x - w / 2, pos.y, w, 1, 0, backGroundColor2.getColor());
        width = Armor.drawPlayerEquipment2(Armor.getPlayerEquipment(entity), (((pos.x - w) + width * 2) - 18) + 5, pos.y - 20, true);
        Armor.drawItemStack(entity.getHeldItem(EnumHand.MAIN_HAND), ((pos.x - w) + width * 2) + 5, pos.y - 20);
        Armor.drawItemStack(entity.getHeldItem(EnumHand.OFF_HAND), (((pos.x - w) + width * 2) + 18) + 5, pos.y - 20);
        FontManager.font22.drawString(name, (pos.x - w / 2) + 5, pos.y + 6, stringColor.getColorRGB());
    }

    private String getTag(EntityPlayer player) {
        if (player == null) return null;

        if (FriendManager.isFriend(player.getName()) || Teams.instance.isTeam(player)) {
            return TextFormatting.WHITE + " [" + TextFormatting.GREEN + "Friend" + TextFormatting.WHITE + "]";
        } else {
            return TextFormatting.WHITE + " [" + TextFormatting.RED + "Hostile" + TextFormatting.WHITE + "]";
        }
    }

}
