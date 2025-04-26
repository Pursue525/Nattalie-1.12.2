package net.pursue.ui.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.mode.Mode;
import net.pursue.mode.client.ClickGUI;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.FontUtils;
import net.pursue.ui.gui.exploit.ClickKey;
import net.pursue.ui.guiButton.DelayTextField;
import net.pursue.utils.MathUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.Value;
import net.pursue.value.exploit.PacketBooleanValue;
import net.pursue.value.values.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Click extends GuiScreen {
    public static boolean mouse0;
    public static boolean mouse1;
    private final float clickHeight = 230;
    private float animY = height + clickHeight;
    private String catString = null;
    private boolean cat = true;
    private Mode modeValue = null;
    private double animMode = 0;
    private float x = width / 2f + 250;
    private float y = MathUtils.centre(height, clickHeight) + animY;
    private float preX, preY;
    private boolean drag;
    private Color color = new Color(255,255,255,150);

    private int wheel;
    private float wheelAnim;
    private float valueHeight;

    private final List<DelayTextField> disclaimer = new ArrayList<>();


    @Override
    public void onGuiClosed() {
        animY = height + clickHeight;

        Nattalie.instance.getOldGUI().legit = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        animY = AnimationUtils.moveUD(animY, 0, (float) (10 * RenderUtils.deltaTime()), (float) (7 * RenderUtils.deltaTime()));
        doDrag(mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);

        FontUtils catFont = FontManager.font24;
        FontUtils modeFont = FontManager.font20;
        FontUtils dpFont = FontManager.font16;

        float catWidth = 0;

        for (Category category : Category.values()) {
            catWidth += catFont.getWidth(category.toString()) + 15;
        }

        if (catWidth == 0) return;

        float width = catWidth + 10;
        animMode = AnimationUtils.smooth(cat ? clickHeight + 10 : catFont.getHeight() + 14, animMode, 8f / Minecraft.getDebugFPS());

        float height = (float) animMode;

        RoundedUtils.enableRoundNoRender(x, y, width, height, 1);

        RoundedUtils.drawRoundBlur(x, y, width, height, 3, new Color(0,0,0,120), 10);
        RoundedUtils.drawRound(x + 5, y + 5, width - 10, catFont.getHeight() + 4, 0, new Color(0,0,0,100));

        if (clickHeight == catFont.getHeight() + 14) {
            catString = null;
        }

        int catX = 0;
        for (Category category : Category.values()) {
            if (Nattalie.instance.getOldGUI().legit) {
                if (category == Category.PLAYER
                        || category == Category.COMBAT
                        || category == Category.MISC
                        || category == Category.WORLD
                        || category == Category.MOVE) {
                    continue;
                }
            }

            catFont.drawString(category.toString(), x + 10 + catX, y + 9, catString != null && catString.equals(category.toString()) ? Color.YELLOW : Color.WHITE);

            if (isHovering(x + 10 + catX, y + 9, catFont.getWidth(category.toString()), catFont.getHeight(), mouseX, mouseY)) {
                if (mouse0) {
                    catString = category.toString();
                    cat = true;
                    mouse0 = false;
                } else if (mouse1) {
                    cat = !cat;
                    wheel = 0;
                    wheelAnim = 0;
                    modeValue = null;
                    mouse1 = false;
                }
            }
            catX += catFont.getWidth(category.toString()) + 15;
        }

        final List<Mode> modeList = new ArrayList<>();

        if (catString != null) {

            for (Mode mode : Nattalie.instance.getModeManager().getModes()) {
                if (mode.getCategory().toString().equals(catString)) {
                    modeList.add(mode);
                }
            }

            /*
              我不检查他是不是空，因为我知道有这个枚举就一定会有模块在里面
             */
            modeList.sort((o1, o2) -> modeFont.getStringWidth(o2.getName()) - modeFont.getStringWidth(o1.getName()));

            RoundedUtils.enableRoundNoRender(x + 5, y + 14 + catFont.getHeight(), 100, height - (19 + catFont.getHeight()), 0);
            RoundedUtils.drawRound(x + 5, y + 14 + catFont.getHeight(), 100, height - (19 + catFont.getHeight()), 0, new Color(0, 0, 0, 100));

            float x = this.x + 5;
            float y = this.y + 19 + catFont.getHeight();

            int modeY = 0;
            Mode modeDescribes = null;
            for (Mode mode : modeList) {

                Color color = mode.isEnable() ? new Color(0,255,0,150) : isHovering((x + 10) - 2, (y + modeY) - 2, 84, modeFont.getHeight(), mouseX, mouseY) ? new Color(255,255,255,50) : new Color(255,255,0,0);

                RoundedUtils.drawRound((x + 10) - 2, (y + modeY) - 2, 84, modeFont.getHeight(), 2, color);
                modeFont.drawString(mode.getName(), (x + 50) - ((float) modeFont.getStringWidth(mode.getName()) / 2), y + modeY, Color.WHITE);

                if (modeValue == mode) {
                    RoundedUtils.drawRound((x + 6), (y + modeY) - 2, 2, modeFont.getHeight(), 0, Color.YELLOW);
                    RoundedUtils.drawRound((x + 10) + 82, (y + modeY) - 2, 2, modeFont.getHeight(), 0, Color.YELLOW);
                }

                if (isHovering((x + 10) - 2, (y + modeY) - 2, 84, modeFont.getHeight(), mouseX, mouseY)) {
                    if (mouse0) {
                        mode.setEnable(!mode.isEnable());
                        mouse0 = false;
                    } else if (mouse1) {
                        wheel = 0;
                        wheelAnim = 0;
                        disclaimer.clear();
                        modeValue = mode;
                        mouse1 = false;
                    }

                    if (modeValue != mode) {
                        modeDescribes = mode;
                    }
                }

                modeY += modeFont.getHeight() + 6;
            }
            RoundedUtils.disableRoundNoRender();

            x = this.x + 110;
            y = this.y + 14 + catFont.getHeight();

            if (modeValue != null) {
                String key = Keyboard.getKeyName(modeValue.getKey());

                RoundedUtils.drawRound_Rectangle(catFont, "Bind[" + TextFormatting.RED + key + TextFormatting.BLACK + "]", x + 5, y + 5, 2, Color.BLACK, color, 4, 8, true);

                if (isHovering(x + 5, y + 5, RoundedUtils.width, RoundedUtils.height, mouseX, mouseY)) {
                    color = new Color(200,200,200,150);

                    if (mouse0) {
                        mc.displayGuiScreen(new ClickKey(modeValue));
                        mouse0 = false;
                    } else if (mouse1) {
                        modeValue.setKey(0);
                        mouse1 = false;
                    }
                } else {
                    color = new Color(255,255,255,150);
                }

                RoundedUtils.drawRound((x + RoundedUtils.width + 8), y, width - 115 - (RoundedUtils.width + 8), 25, 1, new Color(0,0,0,150));
                modeFont.drawString(modeValue.getModeDescribes(), (x + RoundedUtils.width + 8) + 5, ((y + ((float) 25 / 2)) - modeFont.getHeight() / 2f) + 2, Color.WHITE);

                RoundedUtils.enableRoundNoRender(x, y + 30, width - 115, (height - (19 + catFont.getHeight())) - 30, 3);
                RoundedUtils.drawRound(x, y + 30, width - 115, (height - (19 + catFont.getHeight())) - 30, 3, new Color(0,0,0,150));

                valueHeight = draw(catFont, modeValue, x, y + 30 + wheelAnim, width - 115, (height - (19 + catFont.getHeight())) - 30, mouseX, mouseY, ClickGUI.instance.chinese.getValue());

                RoundedUtils.disableRoundNoRender();

                int real = Mouse.getDWheel();

                if (isHovering(x, y + 30, width - 115, (height - (19 + catFont.getHeight())) - 30, mouseX, mouseY)) {
                    if (real > 0 && wheel < 0) {
                        for (int i = 0; i < 5; i++) {
                            if (!(wheel < 0))
                                break;
                            wheel += 10;
                        }
                    } else {
                        for (int i = 0; i < 5; i++) {
                            if (!(real < 0 && wheel + valueHeight > (height - (19 + catFont.getHeight())) - 30))
                                break;
                            wheel -= 10;

                        }
                    }
                }

                wheelAnim = AnimationUtils.moveUD(wheelAnim, wheel, (float) (10 * RoundedUtils.deltaTime()), (float) (7 * RoundedUtils.deltaTime()));
            } else {
                disclaimer.clear();
            }

            if (modeDescribes != null) {
                RoundedUtils.drawRound_Rectangle(dpFont, modeDescribes.getModeDescribes(), mouseX + 5, mouseY + 5, 2, Color.BLACK, new Color(255,255,255,180), 4, 4, true);
            }
        }

        RoundedUtils.disableRoundNoRender();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isHovering(mouseX, mouseY)) {
            if (mouseButton == 1) {
                this.drag = true;
                this.preX = mouseX - this.x;
                this.preY = mouseY - this.y;
            }
        }

        disclaimer.forEach(delayTextField -> delayTextField.mouseClicked(mouseX, mouseY, mouseButton));

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
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        disclaimer.forEach(delayTextField -> delayTextField.textboxKeyTyped(typedChar, keyCode));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


    /**
     * move
     */
    public void doDrag(int mouseX, int mouseY) {
        if (this.drag) {
            if (!Mouse.isButtonDown(1)) {
                this.drag = false;
            }
            this.x = (int) (mouseX - this.preX);
            this.y = (int) (mouseY - this.preY);
        }
    }

    public boolean isHovering(int mouseX, int mouseY) {
        float width = 0;

        for (Category category : Category.values()) {
            width += FontManager.font24.getWidth(category.toString()) + 15;
        }

        width += 10;

        float startX = x;
        float startY = y;
        float w = width;
        float h = FontManager.font24.getHeight() + 14;

        if (width < 0) {
            startX += width;
            w = Math.abs(w);
        }

        if (FontManager.font24.getHeight() + 9 < 0) {
            startY += FontManager.font24.getHeight() + 14;
            h = Math.abs(h);
        }

        return mouseX >= startX && mouseX <= startX + w && mouseY >= startY && mouseY <= startY + h;
    }

    public float draw(FontUtils font, Mode mode, float x, float y, float width, float height, int mouseX, int mouseY, boolean c) {
        String valueName;
        String string2;
        Color valueColor;

        float x1;
        float x2;

        float w1;
        float w2;

        int valueHeight = 0;

        for (Value value : mode.getValues()) {

            if (value.isVisitable()) continue;

            String name = switch (value) {
                case StringValue<?> ignored -> !c ? "String" : "文本";
                case ColorValue<?> ignored -> !c ? "Color" : "颜色";
                case NumberValue<?> ignored -> !c ? "Number" : "数值";
                case BooleanValue<?> ignored -> !c ? "Boolean" : "单布尔值";
                case ModeValue<?> ignored -> !c ? "EnumMode" : "枚举模组";
                case PacketBooleanValue<?> ignored -> !c ? "Boolean" : "双布尔值";

                default -> "?";
            };

            valueName = "[" + TextFormatting.YELLOW + name + TextFormatting.WHITE + "] " + value.getName();

            float y2 = (((y + valueHeight) + 5 + (float) 15 / 2) - (float) font.getHeight() / 2) + 2;

            if (value instanceof BooleanValue) {

                font.drawString(valueName, x + 5, y2, Color.WHITE);

                boolean valueBoolean = ((Boolean) value.getValue());
                valueColor = valueBoolean ? new Color(0,255,0,150) : new Color(255,0,0,150);

                w1 = 30;
                x1 = x + width - w1 - 5;

                w2 = (float) 25 / 2;

                RoundedUtils.drawRound(x1, (y + valueHeight) + 5, w1, 15, 2, valueColor);

                if (Nattalie.instance.getPursueGUI().isHovering(x1, (y + valueHeight) + 5, w1, 15, mouseX, mouseY)) {
                    if (mouse0) {
                        value.setValue(!((Boolean) value.getValue()));
                        mouse0 = false;
                    }
                }

                x2 = valueBoolean ? x1 + 17 : x1 + 2;

                RoundedUtils.drawRound(x2, (y + valueHeight) + 7, w2 - 2, w2 - 2, 2, Color.WHITE);

                valueHeight += 24;
            }
            if (value instanceof ColorValue colorValue) {
                font.drawString(valueName, x + 5, y2, Color.WHITE);

                w1 = 45;
                x1 = x + width - w1 - 5;
                RoundedUtils.drawRound(x1, (y + valueHeight) + 5, w1, 15, 2, ((ColorValue<?>) value).getColor());

                drawColor(colorValue, 0, x + 30, y + (valueHeight + 29), width - 35, 15, new Color(255,0,0,150), mouseX, mouseY);
                drawColor(colorValue, 1, x + 30, y + (valueHeight + 53), width - 35, 15, new Color(0,255,0,150), mouseX, mouseY);
                drawColor(colorValue, 2, x + 30, y + (valueHeight + 77), width - 35, 15, new Color(0,0,255,150), mouseX, mouseY);

                drawColor(colorValue, 3, x + 30, y + (valueHeight + 101), width - 35, 15, new Color(255,255,255,colorValue.getAlpha()), mouseX, mouseY);

                valueHeight += 125;
            }

            if (value instanceof ModeValue) {
                font.drawString(valueName, x + 5, y2, Color.WHITE);

                w1 = font.getWidth(value.getValue().toString()) + 4;
                x1 = x + width - w1 - 5;

                float h = 15;

                if (Nattalie.instance.getPursueGUI().isHovering(x1, (y + valueHeight) + 5, w1, h, mouseX, mouseY)) {
                    if (mouse0) {
                        ModeValue theme = (ModeValue) value;
                        Enum current = (Enum)theme.getValue();
                        int next = current.ordinal() + 1 >= theme.getModes().length ? 0 : current.ordinal() + 1;
                        value.setValue(theme.getModes()[next]);
                        mouse0 = false;
                    } else if (mouse1) {
                        ModeValue theme = (ModeValue) value;
                        Enum current = (Enum)theme.getValue();
                        int next = current.ordinal() - 1 < 0 ? theme.getModes().length - 1 : current.ordinal() - 1;
                        value.setValue(theme.getModes()[next]);
                        mouse1 = false;
                    }
                }

                RoundedUtils.drawRound(x1, (y + valueHeight) + 5, w1, h, 2, Color.ORANGE);
                font.drawString(value.getValue().toString(), x1 + 2, y2, Color.BLACK);

                valueHeight += 24;
            }

            if (value instanceof StringValue stringValue) {
                font.drawString(valueName, x + 5, y2, Color.WHITE);

                if (disclaimer.isEmpty()) {
                    disclaimer.add(new DelayTextField(stringValue.hashCode(), ((StringValue<?>) value).getValue(), Minecraft.getMinecraft().fontRendererObj, (int) x + font.getWidth(valueName) + 10, (int) y2 - 6, (int) (width - (font.getWidth(valueName) + 15)), 20, 0, new Color(120, 120, 120, 180), false));
                } else {
                    for (DelayTextField delayTextField : disclaimer) {
                        if (delayTextField.getId() == stringValue.hashCode()) continue;

                        disclaimer.add(new DelayTextField(stringValue.hashCode(), ((StringValue<?>) value).getValue(), Minecraft.getMinecraft().fontRendererObj, (int) x + font.getWidth(valueName) + 10, (int) y2 - 6, (int) (width - (font.getWidth(valueName) + 15)), 20, 0, new Color(120, 120, 120, 180), false));
                    }
                }
                if (!disclaimer.isEmpty()) {
                    for (DelayTextField delayTextField : disclaimer) {
                        if (delayTextField.getId() == stringValue.hashCode()) {
                            delayTextField.xPosition = (int) (x + font.getWidth(valueName) + 10);
                            delayTextField.yPosition = (int) y2 - 6;
                            delayTextField.setMaxStringLength(100);
                            delayTextField.drawTextBox();
                            value.setValue(delayTextField.getText());
                            delayTextField.setText(((StringValue<?>) value).getValue());
                        }
                    }
                }

                valueHeight += 24;
            }

            if (value instanceof NumberValue) {
                font.drawString(valueName, x + 5, y2, Color.WHITE);

                double state = ((NumberValue<?>) value).getValue().doubleValue();
                double min = ((NumberValue<?>) value).getMinimum().doubleValue();
                double max = ((NumberValue<?>) value).getMaximum().doubleValue();
                double i = ((NumberValue<?>) value).getIncrement().doubleValue();
                float wid = width - 50;
                double render = wid * ((state - min) / (max - min));

                w1 = 30;
                x1 = x + width - w1 - 5;

                RoundedUtils.drawRound(x1, (y + valueHeight) + 5, w1, 15, 2, Color.ORANGE);

                if (Nattalie.instance.getPursueGUI().isHovering(x1, (y + valueHeight) + 5, w1, 15, mouseX, mouseY)) {
                    if (mouse0) {
                        value.setValue(MathUtils.incValue(state + i, i));
                        mouse0 = false;
                    } else if (mouse1) {
                        value.setValue(MathUtils.incValue(state - i, i));
                        mouse1 = false;
                    }
                }

                font.drawString("< >", x1 + MathUtils.centre(w1, font.getWidth("< >")), y2, Color.BLACK);

                font.drawString(String.valueOf(state), x + 5, y2 + 24, Color.ORANGE);
                RoundedUtils.drawRound(x + 45, y2 + 28, wid, 2,1, Color.DARK_GRAY);
                RoundedUtils.drawRound(x + 45, y2 + 28, (float) render, 2,1, Color.ORANGE);
                RoundedUtils.drawRound((float) (x + 43 + render), y2 + 26, 2, 6, 0, Color.WHITE);

                if (Nattalie.instance.getPursueGUI().isHovering(x + 45, y2 + 26, wid, 6, mouseX, mouseY)) {
                    if (Mouse.isButtonDown(0)) {
                        double difference = ((NumberValue) value).getMaximum().doubleValue() - ((NumberValue) value).getMinimum().doubleValue();
                        double number = ((NumberValue) value).getMinimum().doubleValue() + MathHelper.clamp((mouseX - (x + 45)) / (wid), 0, 1) * difference;
                        double set = MathUtils.incValue(number, i);
                        value.setValue(set);
                    }
                }

                valueHeight += 48;
            }
        }

        return valueHeight;
    }

    public void drawColor(ColorValue colorValue, int id, float x, float y, float width, float height, Color color2, int mouseX, int mouseY) {
        double max = 255;
        double min = 0;
        double i = 1;
        Color color1 = colorValue.getColor();
        int red = color1.getRed();
        int green = color1.getGreen();
        int blue = color1.getBlue();
        int alpha = color1.getAlpha();

        String valueString = switch (id) {
            case 0 -> TextFormatting.RED + String.valueOf(red);
            case 1 -> TextFormatting.GREEN + String.valueOf(green);
            case 2 -> TextFormatting.BLUE + String.valueOf(blue);
            case 3 -> String.valueOf(alpha);
            default -> "Null";
        };

        double renderWidth = switch (id) {
            case 0 -> width * ((red - min) / (max - min));
            case 1 -> width * ((green - min) / (max - min));
            case 2 -> width * ((blue - min) / (max - min));
            case 3 -> width * ((alpha - min) / (max - min));
            default -> -1;
        };

        double difference = max - min;
        double number = min + ((mouseX - x) / (width) * difference);
        int set = (int) MathUtils.incValue(number, i);

        FontManager.font24.drawString(valueString, x - 25, y + MathUtils.centre(height, FontManager.font24.getHeight()) + 2, Color.WHITE);
        RoundedUtils.drawRound(x, y, width, height, 2, new Color(255, 255, 255, 180));

        if (isHovering(x, y, width + 1, height, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0)) {
                switch (id) {
                    case 0 -> colorValue.setColorRed(set);
                    case 1 -> colorValue.setColorGreen(set);
                    case 2 -> colorValue.setColorBlue(set);
                    case 3 -> colorValue.setColoAr(set);
                }
            }
        }
        RoundedUtils.drawRound(x + 3, y + 3, width - 6, height - 6, 0, new Color(0, 0, 0, 180));
        RoundedUtils.drawRound(x + 3, y + 3, (float) renderWidth - 6, height - 6, 0, color2);
    }
}
