package net.pursue.ui.font;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class RapeMasterFontManager {
    private static final int[] colorCode = new int[32];
    private final byte[][] charwidth = new byte[256][];
    private final int[] textures = new int[256];
    private final FontRenderContext context = new FontRenderContext(new AffineTransform(), true, true);
    private Font font = null;
    private float size = 0.0f;
    private int fontWidth = 0;
    private int fontHeight = 0;
    private int textureWidth = 0;
    private int textureHeight = 0;

    public final float drawCenteredString(String text, float x2, float y2, int color) {
        return this.drawString(text, x2 - (float)(this.getStringWidth(text) / 2), y2, color);
    }

    public final float drawCenteredStringNoFormat(String text, float x2, float y2, int color) {
        return this.drawStringNoFormat(text, x2 - (float)(this.getStringWidth(text) / 2), y2, color, false);
    }

    public final void drawCenteredStringWithShadow(String text, float x2, float y2, int color) {
        this.drawStringWithShadow(text, x2 - (float)(this.getStringWidth(text) / 2), y2, color);
    }

    private static final String processString(String text) {
        String str = "";
        for (char c : text.toCharArray()) {
            if (c >= '\uc350' && c <= '\uea60' || c == '\u26bd') continue;
            str = str + c;
        }
        text = str.replace("\u00a7r", "").replace('\u25ac', '=').replace('\u2764', '\u2665').replace('\u22c6', '\u2606').replace('\u2620', '\u2606').replace('\u2730', '\u2606').replace("\u272b", "\u2606").replace("\u2719", "+");
        text = text.replace('\u2b05', '\u2190').replace('\u2b06', '\u2191').replace('\u2b07', '\u2193').replace('\u27a1', '\u2192').replace('\u2b08', '\u2197').replace('\u2b0b', '\u2199').replace('\u2b09', '\u2196').replace('\u2b0a', '\u2198');
        return text;
    }

    public RapeMasterFontManager(Font font) {
        this.font = font;
        this.size = font.getSize2D();
        Arrays.fill(this.textures, -1);
        Rectangle2D maxBounds = font.getMaxCharBounds(this.context);
        this.fontWidth = (int)Math.ceil(maxBounds.getWidth());
        this.fontHeight = (int)Math.ceil(maxBounds.getHeight());
        if (this.fontWidth > 127 || this.fontHeight > 127) {
            throw new IllegalArgumentException("Font size to large!");
        }
        this.textureWidth = this.resizeToOpenGLSupportResolution(this.fontWidth * 16);
        this.textureHeight = this.resizeToOpenGLSupportResolution(this.fontHeight * 16);
    }

    public final int getHeight() {
        return this.fontHeight / 2;
    }

    public final int getFontHeight() {
        return this.fontHeight / 2;
    }

    protected final int drawChar(char chr, float x2, float y2) {
        int region = chr >> 8;
        int id = chr & 0xFF;
        int xTexCoord = (id & 0xF) * this.fontWidth;
        int yTexCoord = (id >> 4) * this.fontHeight;
        byte width = this.getOrGenerateCharWidthMap(region)[id];
        GlStateManager.bindTexture(this.getOrGenerateCharTexture(region));
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GL11.glBegin((int)7);
        GL11.glTexCoord2d((double)this.wrapTextureCoord(xTexCoord, this.textureWidth), (double)this.wrapTextureCoord(yTexCoord, this.textureHeight));
        GL11.glVertex2f((float)x2, (float)y2);
        GL11.glTexCoord2d((double)this.wrapTextureCoord(xTexCoord, this.textureWidth), (double)this.wrapTextureCoord(yTexCoord + this.fontHeight, this.textureHeight));
        GL11.glVertex2f((float)x2, (float)(y2 + (float)this.fontHeight));
        GL11.glTexCoord2d((double)this.wrapTextureCoord(xTexCoord + width, this.textureWidth), (double)this.wrapTextureCoord(yTexCoord + this.fontHeight, this.textureHeight));
        GL11.glVertex2f((float)(x2 + (float)width), (float)(y2 + (float)this.fontHeight));
        GL11.glTexCoord2d((double)this.wrapTextureCoord(xTexCoord + width, this.textureWidth), (double)this.wrapTextureCoord(yTexCoord, this.textureHeight));
        GL11.glVertex2f((float)(x2 + (float)width), (float)y2);
        GL11.glEnd();
        return width;
    }

    public int drawString(String str, float x2, float y2, int color) {
        return this.drawString(str, x2, y2, color, false);
    }

    public int drawString(String str, int x2, int y2, int color) {
        return this.drawString(str, x2, y2, color, false);
    }


    public final int drawStringNoFormat(String str, float x2, float y2, int color, boolean darken) {
        str = str.replace("\u25ac", "=");
        y2 -= 2.0f;
        x2 *= 2.0f;
        y2 *= 2.0f;
        y2 -= 2.0f;
        int offset = 0;
        if (darken) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        float r2 = (float)(color >> 16 & 0xFF) / 255.0f;
        float g2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float b2 = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        if (a == 0.0f) {
            a = 1.0f;
        }
        GlStateManager.color(r2, g2, b2, a);
        GL11.glPushMatrix();
        GL11.glScaled((double)0.5, (double)0.5, (double)0.5);
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char chr = chars[i];
            if (chr == '\u00a7' && i != chars.length - 1) {
                if ((color = "0123456789abcdef".indexOf(chars[++i])) == -1 || !darken) continue;
                color |= 0x10;
                continue;
            }
            offset += this.drawChar(chr, x2 + (float)offset, y2);
        }
        GL11.glPopMatrix();
        return offset;
    }

    public final int drawString(String str, float x2, float y2, int color, boolean darken) {
        str = str.replace("\u25ac", "=");
        y2 -= 2.0f;
        x2 *= 2.0f;
        y2 *= 2.0f;
        y2 -= 2.0f;
        int offset = 0;
        if (darken) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        float r2 = (float)(color >> 16 & 0xFF) / 255.0f;
        float g2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float b2 = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        if (a == 0.0f) {
            a = 1.0f;
        }
        GlStateManager.color(r2, g2, b2, a);
        GL11.glPushMatrix();
        GL11.glScaled((double)0.5, (double)0.5, (double)0.5);
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            char chr = chars[i];
            if (chr == '\u00a7' && i != chars.length - 1) {
                if ((color = "0123456789abcdef".indexOf(chars[++i])) == -1) continue;
                if (darken) {
                    color |= 0x10;
                }
                color = colorCode[color];
                r2 = (float)(color >> 16 & 0xFF) / 255.0f;
                g2 = (float)(color >> 8 & 0xFF) / 255.0f;
                b2 = (float)(color & 0xFF) / 255.0f;
                GlStateManager.color(r2, g2, b2, a);
                continue;
            }
            offset += this.drawChar(chr, x2 + (float)offset, y2);
        }
        GL11.glPopMatrix();
        return offset;
    }

    public final int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;
        char[] currentData = text.toCharArray();
        int size = text.length();
        for (int i = 0; i < size; ++i) {
            char chr = currentData[i];
            char character = text.charAt(i);
            if (character == '\u00a7') {
                ++i;
                continue;
            }
            width += this.getOrGenerateCharWidthMap(chr >> 8)[chr & 0xFF];
        }
        return width / 2;
    }
    public final int getWidth(String text) {
        if (text == null) {
            return 0;
        }
        int width = 0;
        char[] currentData = text.toCharArray();
        int size = text.length();
        for (int i = 0; i < size; ++i) {
            char chr = currentData[i];
            char character = text.charAt(i);
            if (character == '\u00a7') {
                ++i;
                continue;
            }
            width += this.getOrGenerateCharWidthMap(chr >> 8)[chr & 0xFF];
        }
        return width / 2;
    }

    public final float getSize() {
        return this.size;
    }

    private final int generateCharTexture(int id) {
        int textureId = GL11.glGenTextures();
        int offset = id << 8;
        BufferedImage img = new BufferedImage(this.textureWidth, this.textureHeight, 2);
        Graphics2D g2 = (Graphics2D)img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setFont(this.font);
        g2.setColor(Color.WHITE);
        FontMetrics fontMetrics = g2.getFontMetrics();
        for (int y2 = 0; y2 < 16; ++y2) {
            for (int x2 = 0; x2 < 16; ++x2) {
                String chr = String.valueOf((char)(y2 << 4 | x2 | offset));
                g2.drawString(chr, x2 * this.fontWidth, y2 * this.fontHeight + fontMetrics.getAscent());
            }
        }
        GL11.glBindTexture((int)3553, (int)textureId);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
        GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)this.textureWidth, (int)this.textureHeight, (int)0, (int)6408, (int)5121, (ByteBuffer)RapeMasterFontManager.imageToBuffer(img));
        return textureId;
    }

    private final int getOrGenerateCharTexture(int id) {
        if (this.textures[id] == -1) {
            this.textures[id] = this.generateCharTexture(id);
            return this.textures[id];
        }
        return this.textures[id];
    }

    private final int resizeToOpenGLSupportResolution(int size) {
        int power = 0;
        while (size > 1 << power) {
            ++power;
        }
        return 1 << power;
    }

    private final byte[] generateCharWidthMap(int id) {
        int offset = id << 8;
        byte[] widthmap = new byte[256];
        for (int i = 0; i < widthmap.length; ++i) {
            widthmap[i] = (byte)Math.ceil(this.font.getStringBounds(String.valueOf((char)(i | offset)), this.context).getWidth());
        }
        return widthmap;
    }

    private final byte[] getOrGenerateCharWidthMap(int id) {
        if (this.charwidth[id] == null) {
            this.charwidth[id] = this.generateCharWidthMap(id);
            return this.charwidth[id];
        }
        return this.charwidth[id];
    }


    private final double wrapTextureCoord(int coord, int size) {
        return (double)coord / (double)size;
    }

    private static final ByteBuffer imageToBuffer(BufferedImage img) {
        int[] arr = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        ByteBuffer buf = ByteBuffer.allocateDirect(4 * arr.length);
        for (int i : arr) {
            buf.putInt(i << 8 | i >> 24 & 0xFF);
        }
        buf.flip();
        return buf;
    }

    protected final void finalize() throws Throwable {
        for (int textureId : this.textures) {
            if (textureId == -1) continue;
            GL11.glDeleteTextures((int)textureId);
        }
    }

    public final void drawStringWithShadow(String newstr, float i, float i1, int rgb) {
        this.drawString(newstr, i + 0.5f, i1 + 0.5f, rgb, true);
        this.drawString(newstr, i, i1, rgb);
    }

    public void drawStringWithShadow(String z, double x2, double positionY, int mainTextColor) {
        this.drawStringWithShadow(z, (float)x2, (float)positionY, mainTextColor);
    }

    public float drawStringWithShadow(String text, double x2, double y2, double sWidth, int color) {
        float shadowWidth = this.drawString(text, (float)(x2 + sWidth), (float)(y2 + sWidth), color, true);
        return Math.max(shadowWidth, (float)this.drawString(text, (float)x2, (float)y2, color, false));
    }

    static {
        for (int i = 0; i < 32; ++i) {
            int base = (i >> 3 & 1) * 85;
            int r2 = (i >> 2 & 1) * 170 + base;
            int g2 = (i >> 1 & 1) * 170 + base;
            int b2 = (i & 1) * 170 + base;
            if (i == 6) {
                r2 += 85;
            }
            if (i >= 16) {
                r2 /= 4;
                g2 /= 4;
                b2 /= 4;
            }
            RapeMasterFontManager.colorCode[i] = (r2 & 0xFF) << 16 | (g2 & 0xFF) << 8 | b2 & 0xFF;
        }
    }

    public float getMiddleOfBox(float rectHeight) {
        return rectHeight / 2f;
    }
}

