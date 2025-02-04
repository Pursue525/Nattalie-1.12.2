package net.pursue.ui.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.pursue.Nattalie;
import net.pursue.utils.category.Category;
import net.pursue.mode.Mode;
import net.pursue.mode.exploit.ClickGUI;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.MathUtils;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.utils.render.StencilUtils;
import net.pursue.value.Value;
import net.pursue.value.exploit.PacketBooleanValue;
import net.pursue.value.values.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Click extends GuiScreen {


    private boolean mouse0;
    private boolean mouse1;
    private Category cat = null;
    private Mode mode = null;
    private boolean setKey;

    private int wheel;
    private float wheelAnim;
    private double ColorState;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {


        float roundWidth = 0;
        for (Category category : Category.values()) {
            roundWidth += FontManager.font24.getWidth(category.name()) + 15;
        }

        RoundedUtils.drawRound(200, 100, FontManager.font24.getWidth("ClickGUI") + 20 + roundWidth, 300, 0, new Color(0, 0, 0, 120));

        FontManager.font24.drawString("Nattalie", 210, 108, Color.WHITE.getRGB());
        RoundedUtils.drawRound(210, 130, 140, 260, 0, new Color(0, 0, 0, 120));

        RoundedUtils.drawRound(360, 130, FontManager.font24.getWidth("ClickGUI") + roundWidth - 150, 260, 0, new Color(0, 0, 0, 120));

        float categoryX = 0;
        for (Category category : Category.values()) {

            String name = switch (category.name()) {
                case "COMBAT" -> ClickGUI.instance.chinese.getValue() ? "战斗" : "Combat";
                case "MOVE" -> ClickGUI.instance.chinese.getValue() ? "移动" : "Move";
                case "MISC" -> ClickGUI.instance.chinese.getValue() ? "其他" : "MISC";
                case "PLAYER" -> ClickGUI.instance.chinese.getValue() ? "玩家" : "Player";
                case "EXPLOIT" -> ClickGUI.instance.chinese.getValue() ? "杂项" : "Exploit";
                case "RENDER" -> ClickGUI.instance.chinese.getValue() ? "视觉" : "Render";
                case "WORLD" -> ClickGUI.instance.chinese.getValue() ? "世界" : "World";
                case "HUD" -> ClickGUI.instance.chinese.getValue() ? "界面" : "HUD";
                default -> "Null";
            };

            RoundedUtils.drawRound_Rectangle(FontManager.font24, name, 210 + FontManager.font24.getWidth("ClickGUI") + 10 + categoryX, 105, 1, Color.WHITE, cat == category ? ClickGUI.instance.category.getColor() : new Color(0, 0, 0, 200), 6, 6, true);
            if (isHovering(210 + FontManager.font24.getWidth("ClickGUI") + 10 + categoryX, 105, RoundedUtils.width, RoundedUtils.height, mouseX, mouseY)) {
                if (mouse0) {
                    cat = category;
                    mouse0 = false;
                } else if (mouse1) {
                    cat = category;
                    mouse1 = false;
                }
            }
            categoryX += FontManager.font24.getWidth(category.name()) + 15;
        }

        float modeY = 0;

        if (cat != null) {
            for (Mode mode : Nattalie.instance.getModeManager().getModes()) {
                if (mode.getCategory() == cat) {
                    RoundedUtils.drawRound_Rectangle(FontManager.font24, mode.getName(), 220, 135 + modeY, 1, Color.WHITE, mode.isEnable() ? new Color(200, 200, 200) : new Color(0, 0, 0, 0), 0, 4, true);

                    if ((this.mode != null && this.mode == mode)) {
                        RoundedUtils.drawRound(212, 135 + modeY, 3, FontManager.font24.getHeight() - 4, 1, Color.WHITE);
                    }

                    if (isHovering(215, 135 + modeY, RoundedUtils.width, RoundedUtils.height, mouseX, mouseY)) {
                        RoundedUtils.drawRound(212, 135 + modeY, 3, FontManager.font24.getHeight() - 4, 1, Color.WHITE);
                        if (mouse0) {
                            mode.setEnable(!mode.isEnable());
                            mouse0 = false;
                        }

                        if (mouse1) {
                            this.mode = mode;
                            wheel = 0;
                            wheelAnim = 0;
                            mouse1 = false;
                        }
                    }
                    modeY += FontManager.font24.getHeight() + 4;
                }
            }
        }

        float valueY = 0;


        if (this.mode != null && this.mode.getCategory() == cat) {
            String key = Keyboard.getKeyName(this.mode.getKey());
            FontManager.font28.drawString(setKey ? "请输入按键" : "Bind-" + key, 365, 140, ClickGUI.instance.bind.getColorRGB());
            FontManager.font20.drawString(this.mode.getModeDescribes(), 370 + FontManager.font28.getWidth(setKey ? "请输入按键" : "Bind-" + key), 143, ClickGUI.instance.describes.getColorRGB());

            if (isHovering(365, 140 + wheelAnim, FontManager.font28.getWidth(setKey ? "请输入按键" : "Bind-" + key), FontManager.font28.getHeight(), mouseX, mouseY)) {
                if (mouse0) {
                    setKey = true;
                    mouse0 = false;
                }

                if (mouse1) {
                    mode.setKey(0);
                    mouse1 = false;
                }
            }

            if (setKey) {
                if (Keyboard.isKeyDown(Keyboard.getEventKey())) {
                    mode.setKey(Keyboard.getEventKey());
                    setKey = false;
                }
            }


            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glColor4f(1f, 1, 1, 1f);
            StencilUtils.write(false);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glPushMatrix();

            {
                RoundedUtils.drawRound(360, 160, FontManager.font24.getWidth("ClickGUI") + roundWidth - 150, 230, 0, new Color(0, 0, 0, 120));
            }

            GL11.glPopMatrix();
            GlStateManager.resetColor();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            StencilUtils.erase(true);
            GL11.glPushMatrix();

            {
                for (Value value : mode.getValues()) {
                    String valueString = value.getName();

                    String str = " = ";
                    int valueStringStr = FontManager.font20.getStringWidth(valueString);


                    int valueStringStrN = FontManager.font20.getStringWidth(valueString + str);

                    float y2 = 170 + valueY + wheelAnim;

                    int rgb = ClickGUI.instance.valueString.getColorRGB();
                    int rgb1 = ClickGUI.instance.value.getColorRGB();
                    int rgb2 = ClickGUI.instance.valueBoolean.getColorRGB();

                    if (value instanceof BooleanValue && !value.isVisitable()) {
                        FontManager.font20.drawString(valueString, 365, y2, rgb);
                        FontManager.font20.drawString(str, 365 + valueStringStr, y2, rgb1);

                        FontManager.font20.drawString((Boolean) value.getValue() ? "true" : "false", 365 + valueStringStrN, y2, rgb2);

                        if (isHovering(365 + valueStringStrN, y2, FontManager.font20.getWidth((Boolean) value.getValue() ? "true" : "false"), FontManager.font20.getHeight(), mouseX, mouseY)) {
                            if (mouse0) {
                                value.setValue(!(Boolean) value.getValue());
                                mouse0 = false;
                            }
                        }

                        valueY += FontManager.font20.getHeight() + 2;
                    }

                    if (value instanceof PacketBooleanValue && !value.isVisitable()) {
                        String s = " {";
                        FontManager.font20.drawString(valueString, 365, y2, rgb);

                        FontManager.font20.drawString(s, 365 + valueStringStr, y2, rgb1);

                        s = "Cancel = ";
                        FontManager.font20.drawString(s, 365, y2 + FontManager.font20.getHeight() + 2, rgb1);

                        FontManager.font20.drawString((Boolean) value.getValue() ? "true" : "false", 370 + FontManager.font20.getWidth(s), y2 + FontManager.font20.getHeight() + 2, rgb2);

                        if (isHovering(370 + FontManager.font20.getWidth(s), y2 + FontManager.font20.getHeight() + 2, FontManager.font20.getWidth((Boolean) value.getValue() ? "true" : "false"), FontManager.font20.getHeight(), mouseX, mouseY)) {
                            if (mouse0) {
                                value.setValue(!(Boolean) value.getValue());
                                mouse0 = false;
                            }
                        }

                        s = "DeBug = ";

                        FontManager.font20.drawString(s, 365, y2 + (FontManager.font20.getHeight() + 2) * 2, rgb1);

                        FontManager.font20.drawString((Boolean) value.getValue2() ? "true" : "false", 370 + FontManager.font20.getWidth(s), y2 + (FontManager.font20.getHeight() + 2) * 2, rgb2);

                        if (isHovering(370 + FontManager.font20.getWidth(s), y2 + (FontManager.font20.getHeight() + 2) * 2, FontManager.font20.getWidth((Boolean) value.getValue2() ? "true" : "false"), FontManager.font20.getHeight(), mouseX, mouseY)) {
                            if (mouse0) {
                                value.setValue2(!(Boolean) value.getValue2());
                                mouse0 = false;
                            }
                        }

                        s = "}";

                        FontManager.font20.drawString(s, 365, y2 + (FontManager.font20.getHeight() + 2) * 3, rgb1);

                        valueY += (FontManager.font20.getHeight() + 2) * 4;
                    }

                    if (value instanceof StringValue && !value.isVisitable()) {
                        FontManager.font20.drawString(valueString, 365, y2, rgb);
                        FontManager.font20.drawString(str, 365 + valueStringStr, y2, rgb1);
                        FontManager.font20.drawString(value.getValue().toString(), 365 + valueStringStrN, y2, rgb);


                        valueY += FontManager.font20.getHeight() + 2;
                    }

                    if (value instanceof ColorValue && !value.isVisitable()) {
                        FontManager.font20.drawString(valueString, 365, y2, rgb);
                        FontManager.font20.drawString(str, 365 + valueStringStr, y2, rgb1);
                        FontManager.font20.drawString("new ", 365 + valueStringStrN, y2, rgb2);
                        FontManager.font20.drawString("Color( " + ((ColorValue<?>) value).getColor().getRed() + "," + ((ColorValue<?>) value).getColor().getGreen() + "," + ((ColorValue<?>) value).getColor().getBlue() + "." + ((ColorValue<?>) value).getColor().getAlpha() + " )", 365 + FontManager.font20.getWidth("new ") + valueStringStrN, y2, rgb1);


                        float y = 170 + valueY + FontManager.font20.getHeight() + 2 + wheelAnim;
                        int x = 365;

                        Color lastColor = RenderUtils.getColor(((ColorValue) value).getColorRGB());
                        float[] color = Color.RGBtoHSB(lastColor.getRed(), lastColor.getGreen(), lastColor.getBlue(), null);
                        double[] selectXY = new double[]{ 144 - 144 * color[1],70 - 70 * color[2]};
                        double selectX = color[0] * 144f;

                        GL11.glPushMatrix();

                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glDisable(GL11.GL_CULL_FACE);
                        GL11.glShadeModel(7425);

                        RenderUtils.glColor(new Color(60,60,60,255).getRGB());
                        RenderUtils.quickDrawRect(x-1, y + 79, x + 145.8f,y + 91);

                        for (int H = 0; H <= 360; H+= 2){
                            GL11.glBegin(GL11.GL_POLYGON);
                            RenderUtils.glColor(Color.HSBtoRGB(H/360F,1,1));
                            GL11.glVertex2d(x+(H/2.5f),y+80);
                            RenderUtils.glColor(Color.HSBtoRGB((H)/360F,1,1));
                            GL11.glVertex2d(x+(H/2.5f),y+90);
                            RenderUtils.glColor(Color.HSBtoRGB((H+1)/360F,1,1));
                            GL11.glVertex2d(x+(H/2.5f)+2/2.5f,y+90);
                            RenderUtils.glColor(Color.HSBtoRGB((H+1)/360F,1,1));
                            GL11.glVertex2d(x+(H/2.5f)+2/2.5f,y+80);
                            GL11.glEnd();
                        }

                        GL11.glShadeModel(7424);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_CULL_FACE);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_LINE_SMOOTH);

                        GL11.glPopMatrix();

                        if (isHovering(x, y + 80, 145.0F, 10.0F, mouseX, mouseY)
                                && org.lwjgl.input.Mouse.isButtonDown(0)) {
                            if (!Mouse.isButtonDown(1) && org.lwjgl.input.Mouse.isButtonDown(0)) {
                                selectX = mouseX - x;
                            }
                        }

                        color = new float[]{(float) ((selectX) / 144f), 1, 1};

                        drawRect(selectX + x-0.25f,y+79.5f,selectX + x+0.25f,y+90.5f,new Color(60,60,60).getRGB());

                        GL11.glPushMatrix();

                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glDisable(GL11.GL_CULL_FACE);
                        GL11.glShadeModel(7425);

                        for(int s = 0; s<= 100; s++){
                            GL11.glBegin(GL11.GL_POLYGON);
                            RenderUtils.glColor(Color.getHSBColor(color[0],(100 - s)/100f,1).getRGB());
                            GL11.glVertex2d(x+s*1.44f,y);
                            RenderUtils.glColor(Color.getHSBColor(color[0],(100 - s)/100f,0).getRGB());
                            GL11.glVertex2d(x+s*1.44f,y+70);
                            RenderUtils.glColor(Color.getHSBColor(color[0],(100 - s)/100f,0).getRGB());
                            GL11.glVertex2d(x+s*1.44f+1.44f,y+70);
                            RenderUtils.glColor(Color.getHSBColor(color[0],(100 - s)/100f,1).getRGB());
                            GL11.glVertex2d(x+s*1.44f+1.44f,y);
                            GL11.glEnd();
                        }

                        GL11.glShadeModel(7424);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_CULL_FACE);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_LINE_SMOOTH);

                        GL11.glPopMatrix();

                        if (isHovering(x, y, 143F, 70.0F, mouseX, mouseY)
                                && org.lwjgl.input.Mouse.isButtonDown(0)) {
                            if (!Mouse.isButtonDown(1) && org.lwjgl.input.Mouse.isButtonDown(0)) {
                                selectXY = new double[]{mouseX - x, mouseY - y};
                            }
                        }
                        color = new float[]{color[0], (float) ((144f-selectXY[0])/144f), (float) ((70-selectXY[1])/70f)};

                        drawRect(x+selectXY[0]-1f,y+selectXY[1]-1f,x+selectXY[0]+1f,y+selectXY[1]+1f,new Color(0,0,0).getRGB());
                        drawRect(x+selectXY[0]-0.5f,y+selectXY[1]-0.5f,x+selectXY[0]+0.5f,y+selectXY[1]+0.5f,new Color(200,200,200).getRGB());

                        ColorState = ((ColorValue) value).getColor().getAlpha();
                        double render = (100 * (ColorState / 255));

                        RenderUtils.drawRect(x + 160, y - 2, 15, 106, new Color(0,0,0,255));
                        RenderUtils.drawRect(x + 162, y, 11, render, new Color(((ColorValue) value).getColor().getRed(),((ColorValue) value).getColor().getGreen(),((ColorValue) value).getColor().getBlue(), ((ColorValue) value).getColor().getAlpha()));

                        if (isHovering(x + 160, y, 15, 100, mouseX, mouseY)) {
                            if (Mouse.isButtonDown(0)) {
                                double difference = 255;

                                double number = 2.55 + ((double) (mouseY - y) / 100 * difference);

                                if (number == 2.55) {
                                    number = 0;
                                }

                                ((ColorValue) value).setColoAr((int) number);
                            }
                        } else {
                            ((ColorValue) value).setColor(new Color(Color.getHSBColor(color[0], color[1], color[2]).getRed(), Color.getHSBColor(color[0], color[1], color[2]).getGreen(), Color.getHSBColor(color[0], color[1], color[2]).getBlue(), (int) ColorState));
                        }

                        valueY += 120;
                    }

                    if (value instanceof NumberValue && !value.isVisitable()) {
                        double state = Double.parseDouble(decimalFormat.format(((NumberValue<?>) value).getValue().doubleValue()));
                        double increment = Double.parseDouble(decimalFormat.format(((NumberValue<?>) value).getIncrement().doubleValue()));
                        double min = Double.parseDouble(decimalFormat.format(((NumberValue<?>) value).getMinimum().doubleValue()));
                        double max = Double.parseDouble(decimalFormat.format(((NumberValue<?>) value).getMaximum().doubleValue()));
                        double render = (152 * ((state - min) / (max - min)));

                        float y = FontManager.font20.getHeight() + 2;

                        FontManager.font20.drawString(valueString, 365, y2, rgb);
                        FontManager.font20.drawString(" = ", 365 + valueStringStr, y2, rgb1);
                        FontManager.font20.drawString(value.getValue().toString(), 365 + valueStringStrN, y2, rgb1);

                        RenderUtils.drawRect(365, y2 + 2 + y, 152, 2, rgb);

                        RenderUtils.drawRect(365, y2 + 2 + y, render, 2, new Color(255,255,255, 255));
                        RenderUtils.drawRect(365 + render, y2 + 1 + y, 3, 4, new Color(255,255,255, 255));

                        if (isHovering(365, y2 + 2 + y, 154, 6, mouseX, mouseY)) {
                            if (Mouse.isButtonDown(0)) {
                                double difference = ((NumberValue) value).getMaximum().doubleValue() - ((NumberValue) value).getMinimum().doubleValue();

                                double number = ((NumberValue) value).getMinimum().doubleValue() + (double) (mouseX - 365) / 152 * difference;

                                double set = MathUtils.incValue(number, ((NumberValue) value).getIncrement().doubleValue());

                                value.setValue(set);
                            }
                        }

                        FontManager.font32.drawString("+", 530, y2 - 5 + y, Color.GREEN.getRGB());
                        FontManager.font32.drawString("-", 535 + FontManager.font32.getWidth("+"), y2 - 5 + y, Color.RED.getRGB());

                        if (isHovering(530, y2 - 5 + y, FontManager.font32.getWidth("+"), FontManager.font32.getHeight(), mouseX, mouseY)) {
                            if (mouse0) {
                                if (state + increment <= max) {
                                    double set = MathUtils.incValue(state + increment, ((NumberValue) value).getIncrement().doubleValue());

                                    value.setValue(set);
                                }
                                mouse0 = false;
                            }
                        } else if (isHovering(535 + FontManager.font32.getWidth("+"), y2 - 5 + y, FontManager.font32.getWidth("-"), FontManager.font32.getHeight(), mouseX, mouseY)) {
                            if (mouse0) {
                                if (state - increment >= min) {
                                    double set = MathUtils.incValue(state - increment, ((NumberValue) value).getIncrement().doubleValue());

                                    value.setValue(set);
                                }
                                mouse0 = false;
                            }
                        }

                        valueY += (FontManager.font20.getHeight() + 2) * 2;
                    }

                    if (value instanceof ModeValue && !value.isVisitable()) {

                        FontManager.font20.drawString(valueString, 365, y2, rgb);
                        FontManager.font20.drawString(str, 365 + valueStringStr, y2, rgb1);
                        FontManager.font20.drawString("enum ", 365 + valueStringStrN, y2, rgb2);
                        FontManager.font20.drawString(value.getValue().toString(), 365 + FontManager.font20.getStringWidth(valueString + str + "enum "), y2, rgb);

                        if (isHovering(365 + FontManager.font20.getStringWidth(valueString + str + "enum "), y2, FontManager.font20.getStringWidth(value.getValue().toString()), FontManager.font20.getHeight(), mouseX, mouseY)) {
                            if (mouse0) {
                                ModeValue theme = (ModeValue) value;
                                Enum current = (Enum) theme.getValue();
                                int next = current.ordinal() + 1 >= theme.getModes().length ? 0 : current.ordinal() + 1;
                                value.setValue(theme.getModes()[next]);
                                mouse0 = false;
                            }
                        }

                        valueY += FontManager.font20.getHeight() + 2;
                    }
                }

                int real = Mouse.getDWheel();

                if (isHovering(360, 130, FontManager.font24.getWidth("ClickGUI") + roundWidth - 150, 260, mouseX, mouseY)) {
                    if (real > 0 && wheel < 0) {
                        for (int i = 0; i < 5; i++) {
                            if (!(wheel < 0))
                                break;
                            wheel += 10;
                        }
                    } else {
                        for (int i = 0; i < 5; i++) {
                            if (!(real < 0))
                                break;
                            wheel -= 10;

                        }
                    }
                }

                wheelAnim = AnimationUtils.moveUD(wheelAnim, wheel, (float) (10 * RoundedUtils.deltaTime()), (float) (7 * RoundedUtils.deltaTime()));
            }

            GL11.glPopMatrix();
            GlStateManager.resetColor();
            StencilUtils.dispose();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            mouse0 = true;
        } else if (mouseButton == 1) {
            mouse1 = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        mouse0 = false;
        mouse1 = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


    public static boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static double round(final double value, final double inc) {
        if (inc == 0.0) return value;
        else if (inc == 1.0) return Math.round(value);
        else {
            final double halfOfInc = inc / 2.0;
            final double floored = Math.floor(value / inc) * inc;

            if (value >= floored + halfOfInc)
                return new BigDecimal(Math.ceil(value / inc) * inc)
                        .doubleValue();
            else return new BigDecimal(floored)
                    .doubleValue();
        }
    }

    public static int convertStringToInt(String str) {
        if (str != null && str.matches("\\d+")) {
            return Integer.parseInt(str);
        } else {
            return 0;
        }
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldrenderer = tessellator.getBuffer();
        double minX = Math.min(left, right);
        double maxX = Math.max(left, right);
        double minY = Math.min(top, bottom);
        double maxY = Math.max(top, bottom);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(minX, maxY, 0.0).endVertex();
        worldrenderer.pos(maxX, maxY, 0.0).endVertex();
        worldrenderer.pos(maxX, minY, 0.0).endVertex();
        worldrenderer.pos(minX, minY, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
