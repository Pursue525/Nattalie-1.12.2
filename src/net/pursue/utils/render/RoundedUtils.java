package net.pursue.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.pursue.Nattalie;
import net.pursue.ui.font.FontUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.pursue.utils.render.StencilUtils.checkSetupFBO;
import static org.lwjgl.opengl.GL11.*;


public class RoundedUtils {
    private final static ShaderUtils roundedShader = new ShaderUtils("roundedRect");
    private final static ShaderUtils roundedOutlineShader = new ShaderUtils("roundRectOutline");
    private final static ShaderUtils roundedGradientShader = new ShaderUtils("roundedRectGradient");

    public static float width;
    public static float height;

    public static float[] drawRound_Rectangle(FontUtils fontManager, String string, float x, float y, float radius, Color stringColor, Color backgroundColor, int width, int height, boolean fix) {

        if (fix) {
            drawRound(x - 2 - width / 2f, y - height / 2f, fontManager.getStringWidth(string) + 4 + width, fontManager.getHeight() - 4 + height, radius, backgroundColor);
            RoundedUtils.width = fontManager.getStringWidth(string) + 4 + width;
        } else {
            drawRound(x - width / 2f, y - height / 2f, fontManager.getStringWidth(string) + width, fontManager.getHeight() - 4 + height, radius, backgroundColor);

            RoundedUtils.width = fontManager.getStringWidth(string) + width;
        }
        RoundedUtils.height = fontManager.getHeight() - 4 + height;
        fontManager.drawString(string, x,y, stringColor.getRGB());

        return new float[] {RoundedUtils.width, RoundedUtils.height};
    }

    public static float[] drawRound_Rectangle(FontUtils fontManager, String string, String string2, float x, float y, float radius, Color stringColor, Color backgroundColor, int width, int height, boolean fix) {

        if (fix) {
            drawRound(x - 2 - width / 2f, y - height / 2f, fontManager.getStringWidth(string) + 4 + width, fontManager.getHeight() - 4 + height, radius, backgroundColor);
            RoundedUtils.width = fontManager.getStringWidth(string) + 4 + width;
        } else {
            drawRound(x - width / 2f, y - height / 2f, fontManager.getStringWidth(string) + width, fontManager.getHeight() - 4 + height, radius, backgroundColor);

            RoundedUtils.width = fontManager.getStringWidth(string) + width;
        }
        RoundedUtils.height = fontManager.getHeight() - 4 + height;
        fontManager.drawString(string, x,y, stringColor.getRGB());

        return new float[] {RoundedUtils.width, RoundedUtils.height};
    }

    public static void drawRoundBlur(float x, float y, float width, float height, float radius, Color color, int blurInt) {
        if (Nattalie.blur) {
            enableDrawBlur(Minecraft.getMinecraft());
            drawRound(x, y, width, height, radius, Color.BLACK);
            disableDrawBlur(blurInt);
            drawRound(x, y, width, height, radius, color);
        } else {
            drawRound(x, y, width, height, radius, color);
        }
    }

    public static void enableDrawBlur(Minecraft mc) {
        if (Nattalie.blur) {
            mc.getFramebuffer().bindFramebuffer(false);
            checkSetupFBO(mc.getFramebuffer());
            glClear(GL_STENCIL_BUFFER_BIT);
            glEnable(GL_STENCIL_TEST);
            glStencilFunc(GL_ALWAYS, 1, 1);
            glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
            glColorMask(false, false, false, false);
        }
    }

    public static void disableDrawBlur(int blurInt) {
        if (Nattalie.blur) {
            glColorMask(true, true, true, true);
            glStencilFunc(GL_EQUAL, 1, 1);
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
            GaussianBlur.renderBlur(blurInt);
            glDisable(GL_STENCIL_TEST);
        }
    }



    public static void drawRound_Rectangle(FontUtils fontManager, String string, float x, float y, float radius, Color stringColor, Color backgroundColor, Color backgroundColor2, int width, int height, boolean fix) {
        drawRound(x - 2 - width / 2f, (y - height / 2f) - 1, fontManager.getStringWidth(string) + 4 + width, 1, radius, backgroundColor2);
        drawRound_Rectangle(fontManager, string, x, y, radius, stringColor, backgroundColor, width, height, fix);
    }

    public static void drawRound(float x, float y, float width, float height, float radius, Color color) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        GlStateManager.disableBlend();
    }

    public static void enableRoundNoRender(float x, float y, float width, float height, float radius) {
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glColor4f(1f, 1, 1, 1f);
        StencilUtils.write(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();

        drawRound(x, y, width, height, radius, Color.BLACK);

        GL11.glPopMatrix();
        GlStateManager.resetColor();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        StencilUtils.erase(true);
        GL11.glPushMatrix();
    }

    public static void disableRoundNoRender() {
        GL11.glPopMatrix();
        GlStateManager.resetColor();
        StencilUtils.dispose();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
    }

    public static void drawHead(EntityLivingBase entity, float x, float y, int width, int height, float radius, Color color) {
        GL11.glPushMatrix();
        GL11.glColor4f(1f, 1, 1, 1f);
        StencilUtils.write(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        fastRoundedRect(x, y, x + width, y + height, radius);
        GL11.glPopMatrix();
        GlStateManager.resetColor();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        StencilUtils.erase(true);
        GL11.glPushMatrix();
        drawHead(entity.getLocationSkin(), (int) x, (int) y, width, height, color);
        GL11.glPopMatrix();
        GlStateManager.resetColor();
        StencilUtils.dispose();
        GL11.glPopMatrix();
    }

    public static void fastRoundedRect(float paramXStart, float paramYStart, float paramXEnd, float paramYEnd, float radius) {
        float z;
        if (paramXStart > paramXEnd) {
            z = paramXStart;
            paramXStart = paramXEnd;
            paramXEnd = z;
        }

        if (paramYStart > paramYEnd) {
            z = paramYStart;
            paramYStart = paramYEnd;
            paramYEnd = z;
        }

        double x1 = (paramXStart + radius);
        double y1 = (paramYStart + radius);
        double x2 = (paramXEnd - radius);
        double y2 = (paramYEnd - radius);

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);

        glBegin(GL_POLYGON);

        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        for (double i = 90; i <= 180; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 180; i <= 270; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 270; i <= 360; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        glEnd();
        glDisable(GL_LINE_SMOOTH);
    }


    public static void drawHead(ResourceLocation skin, int x, int y, int width, int height, Color color) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height, 64F, 64F);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void drawHead(ResourceLocation skin, int x, int y, int width, int height, float f) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glColor4f(1f, 1f, 1f, f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height, 64F, 64F);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }


    public static void drawGradientRound(float x, float y, float width, float height, float radius, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedGradientShader.init();
        setupRoundedRectUniforms(x, y, width, height, radius, roundedGradientShader);
        roundedGradientShader.setUniformf("color1", topLeft.getRed() / 255f, topLeft.getGreen() / 255f, topLeft.getBlue() / 255f, topLeft.getAlpha() / 255f);
        roundedGradientShader.setUniformf("color2", bottomRight.getRed() / 255f, bottomRight.getGreen() / 255f, bottomRight.getBlue() / 255f, bottomRight.getAlpha() / 255f);
        roundedGradientShader.setUniformf("color3", bottomLeft.getRed() / 255f, bottomLeft.getGreen() / 255f, bottomLeft.getBlue() / 255f, bottomLeft.getAlpha() / 255f);
        roundedGradientShader.setUniformf("color4", topRight.getRed() / 255f, topRight.getGreen() / 255f, topRight.getBlue() / 255f, topRight.getAlpha() / 255f);
        ShaderUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedGradientShader.unload();
        GlStateManager.disableBlend();
    }
    
    public static void drawRoundOutline(float x, float y, float width, float height, float radius, float outlineThickness, Color color, Color outlineColor) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedOutlineShader.init();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setupRoundedRectUniforms(x, y, width, height, radius, roundedOutlineShader);
        roundedOutlineShader.setUniformf("outlineThickness", outlineThickness * sr.getScaleFactor());
        roundedOutlineShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        roundedOutlineShader.setUniformf("outlineColor", outlineColor.getRed() / 255f, outlineColor.getGreen() / 255f, outlineColor.getBlue() / 255f, outlineColor.getAlpha() / 255f);

        ShaderUtils.drawQuads(x - (2 + outlineThickness), y - (2 + outlineThickness), width + (4 + outlineThickness * 2), height + (4 + outlineThickness * 2));
        roundedOutlineShader.unload();
        GlStateManager.disableBlend();
    }


    private static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderUtils roundedTexturedShader) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        roundedTexturedShader.setUniformf("location", x * sr.getScaleFactor(),
                (Minecraft.getMinecraft().displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        roundedTexturedShader.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        roundedTexturedShader.setUniformf("radius", radius * sr.getScaleFactor());
    }

    public static void round(float x, float y, float width, float height, float radius, Color color) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtils.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        rect(x, y, width, height);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void rect(float x, float y, float width, float height) {
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
    }

    // color
    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static Color getColor(int color) {
        int f = color >> 24 & 0xFF;
        int f1 = color >> 16 & 0xFF;
        int f2 = color >> 8 & 0xFF;
        int f3 = color & 0xFF;
        return new Color(f1, f2, f3, f);
    }

    public static int getHealthColor(EntityLivingBase player) {
        float f = player.getHealth();
        float f1 = player.getMaxHealth();
        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }

    public static void quickDrawRect(float x2, float y2, float x22, float y22) {
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x22, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)y22);
        GL11.glVertex2d((double)x22, (double)y22);
        GL11.glEnd();
    }

    //timer
    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (1.0000 / Minecraft.getDebugFPS()) : 1;
    }


    public static void drawImage(int x, int y, int width, int height, ResourceLocation image) {
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x,y,0,0, width, height, width, height);
        GlStateManager.disableBlend();
    }

    public static void drawStack(ItemStack itemStack, float x, float y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) x, (int) y);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}