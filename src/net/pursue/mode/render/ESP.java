package net.pursue.mode.render;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.pursue.Nattalie;
import net.pursue.event.EventTarget;
import net.pursue.event.render.EventRender2D;
import net.pursue.mode.Mode;
import net.pursue.mode.hud.Armor;
import net.pursue.mode.misc.AntiBot;
import net.pursue.mode.misc.Teams;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.MathUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ESP extends Mode {

    public ESP() {
        super("ESP", "透视", "张开第三只眼，将大局逆转吧！", Category.RENDER);
    }

    @EventTarget
    private void onRender(EventRender2D event) {
        GL11.glPushMatrix();

        final double scaling = event.getScaledResolution().getScaleFactor() / Math.pow(event.getScaledResolution().getScaleFactor(), 2.0);
        GlStateManager.scale(scaling, scaling, scaling);

        for (EntityPlayer entity : mc.world.playerEntities) {
            if (entity.getEntityId() != mc.player.getEntityId()) {
                if (entity.isDead) {
                    continue;
                }

                if (entity == mc.player && mc.gameSettings.thirdPersonView == 0)
                    continue;

                final double x = MathUtils.interpolateSmooth(entity.posX, entity.lastTickPosX, event.getPartialTicks()),
                        y = MathUtils.interpolateSmooth(entity.posY, entity.lastTickPosY, event.getPartialTicks()),
                        z = MathUtils.interpolateSmooth(entity.posZ, entity.lastTickPosZ, event.getPartialTicks()),
                        width = entity.width / 1.4,
                        height = entity.height + 0.2;

                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);

                List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
                        new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
                        new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
                        new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
                        new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

                mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

                Vector4d position = null;

                for (Vector3d vector : vectors) {
                    vector = RenderUtils.project2D(event.getScaledResolution(),
                            vector.x - mc.getRenderManager().viewerPosX,
                            vector.y - mc.getRenderManager().viewerPosY,
                            vector.z - mc.getRenderManager().viewerPosZ);

                    if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                        if (position == null) {
                            position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                        }

                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }

                mc.entityRenderer.setupOverlayRendering();

                if (position != null) {
                    RenderUtils.drawCornerBox(position.x, position.y, position.z, position.w, 2, getEntityColor(entity));
                }
            }
        }
        GL11.glPopMatrix();
        mc.entityRenderer.setupOverlayRendering();
    }

    public static Color getEntityColor(Entity entity) {
        if (entity != null) {
            String displayName = entity.getDisplayName().getFormattedText();

            int colorCodeIndex = displayName.indexOf('§');
            if (colorCodeIndex != -1 && colorCodeIndex + 1 < displayName.length()) {
                char colorChar = displayName.charAt(colorCodeIndex + 1);
                return getColorFromChar(colorChar);
            }
        }
        return new Color(255,255,255);
    }

    public String getTag(EntityPlayer player) {
        if (player == null) return null;

        if (FriendManager.isFriend(player.getName()) || Teams.instance.isTeam(player)) {
            return "[Friend]";
        } else {
            return "[Hostile]";
        }
    }

    public static Color getColorFromChar(char colorChar) {
        return switch (colorChar) {
            case '0' -> Color.BLACK;
            case '1', '7', '8' -> Color.BLUE;
            case '2' -> Color.GREEN;
            case '3' -> Color.CYAN;
            case '4' -> Color.RED;
            case '5' -> Color.MAGENTA;
            case '6' -> Color.YELLOW;
            case '9' -> Color.LIGHT_GRAY;
            case 'a' -> new Color(0x00FF00);
            case 'b' -> new Color(0x00FFFF);
            case 'c' -> new Color(0xFF0000);
            case 'd' -> new Color(0xFF00FF);
            case 'e' -> new Color(0xFFFF00);
            default ->  Color.WHITE;
        };
    }

    public static int getColor(EntityLivingBase ent) {
        Teams teams = (Teams) Nattalie.instance.getModeManager().getByClass(Teams.class);

        if (AntiBot.instance.isServerBot(ent)) {
            return new Color(255, 0, 0).getRGB();
        }
        if (teams.isTeam(ent) || ent instanceof EntityPlayerSP) {
            return new Color(0, 255, 0).getRGB();
        }
        return new Color(255, 0, 0).getRGB();
    }
}
