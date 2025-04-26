package net.pursue.ui.gui.sinka.boxes;

import lombok.Getter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.MathUtils;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.value.Value;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ValueBox {
    private final Value value;
    @Getter
    private int height;

    public ValueBox(Value value) {
        this.value = value;
    }

    public void draw(float x, float y, int mouseX, int mouseY, boolean previousMouse) {
        if (this.value.isVisitable()) {
            height = 0;
            return;
        }
        if (this.value instanceof ModeValue) {
            if (Nattalie.instance.getPursueGUI().isHovering(x - 4, y - 10, 120, 22, mouseX, mouseY)) {
                if (!previousMouse) {
                    if (Mouse.isButtonDown(0)) {
                        ModeValue theme = (ModeValue) value;
                        Enum current = (Enum)theme.getValue();
                        int next = current.ordinal() + 1 >= theme.getModes().length ? 0 : current.ordinal() + 1;
                        value.setValue(theme.getModes()[next]);
                    } else if (Mouse.isButtonDown(1)) {
                        ModeValue theme = (ModeValue) value;
                        Enum current = (Enum)theme.getValue();
                        int next = current.ordinal() - 1 < 0 ? theme.getModes().length - 1 : current.ordinal() - 1;
                        value.setValue(theme.getModes()[next]);
                    }
                }
            }
            FontManager.font14.drawString(value.getName(), x, y, new Color(255, 255 ,255, 250).getRGB());
            FontManager.font14.drawString(((ModeValue<?>) this.value).getValue().toString(), x + 110 - FontManager.font14.getStringWidth(((ModeValue<?>) this.value).getValue().toString()), y, new Color(250, 250 ,250, 220).getRGB());
            height = 22;
        }
        if (this.value instanceof BooleanValue) {
            FontManager.font14.drawString(value.getName(), x, y, new Color(255, 255 ,255, 250).getRGB());
            RoundedUtils.drawRound(x + 116 - 36, y - 6, 32, 14, 7, new Color(0, 0, 0, 80));
            if (Nattalie.instance.getPursueGUI().isHovering(x - 4, y - 10, 120, 22, mouseX, mouseY)) {
                if (Mouse.isButtonDown(0) && !previousMouse) {
                    this.value.setValue(!(Boolean) this.value.getValue());
                }
            }
            if ((Boolean) this.value.getValue()) {
                RoundedUtils.drawRound(x + 116 - 17, y - 4, 10, 10, 5, new Color(86,98,246, 160));
            } else {
                RoundedUtils.drawRound(x + 116 - 34, y - 4, 10, 10, 5, new Color(105,108,101, 160));
            }
            height = 22;
        }
        if (this.value instanceof NumberValue) {
            FontManager.font14.drawString(value.getName(), x, y - 4, new Color(255, 255 ,255, 250).getRGB());
            FontManager.font14.drawString(((NumberValue<?>) this.value).getValue().toString(), x + 110 - FontManager.font14.getStringWidth(((NumberValue<?>) this.value).getValue().toString()), y - 4, new Color(250, 250 ,250, 220).getRGB());
            double state = ((NumberValue<?>) value).getValue().doubleValue();
            double min = ((NumberValue<?>) value).getMinimum().doubleValue();
            double max = ((NumberValue<?>) value).getMaximum().doubleValue();
            double render = (112 * ((state - min) / (max - min)));
            RenderUtils.drawRect(x, y + 4, 112, 2, new Color(0,0,0, 80));
            RenderUtils.drawRect(x, y + 4, (float) render, 2, new Color(86,98,246, 80));
            RenderUtils.drawCircle(x + render, y + 5, 3, new Color(86,98,246).getRGB());

            if (Nattalie.instance.getPursueGUI().isHovering(x, y + 2, 114, 6, mouseX, mouseY)) {
                if (Mouse.isButtonDown(0)) {
                    double difference = ((NumberValue) value).getMaximum().doubleValue() - ((NumberValue) value).getMinimum().doubleValue();
                    double number = ((NumberValue) value).getMinimum().doubleValue() + MathHelper.clamp((mouseX - x) / (112), 0, 1) * difference;
                    double set = MathUtils.incValue(number, ((NumberValue) value).getIncrement().doubleValue());
                    value.setValue(set);
                }
            }
            height = 22;
        }
        if (this.value instanceof ColorValue colorValue) {
            drawColor(colorValue, 0, x, y, new Color(255,0,0,150), mouseX, mouseY);
            drawColor(colorValue, 1, x, y + 22, new Color(0,255,0,150), mouseX, mouseY);
            drawColor(colorValue, 2, x, y + 44, new Color(0,0,255,150), mouseX, mouseY);
            drawColor(colorValue, 3, x, y + 66, new Color(255,255,255,colorValue.getAlpha()), mouseX, mouseY);

            height = 88;
        }
    }


    public void drawColor(ColorValue colorValue, int id, float x, float y, Color color2, int mouseX, int mouseY) {
        Color color1 = colorValue.getColor();
        int red = color1.getRed();
        int green = color1.getGreen();
        int blue = color1.getBlue();
        int alpha = color1.getAlpha();

        String valueName = switch (id) {
            case 0 -> "Red";
            case 1 -> "Green";
            case 2 -> "Blue";
            case 3 -> "Alpha";
            default -> "Null";
        };

        String valueString = switch (id) {
            case 0 -> TextFormatting.RED + String.valueOf(red);
            case 1 -> TextFormatting.GREEN + String.valueOf(green);
            case 2 -> TextFormatting.BLUE + String.valueOf(blue);
            case 3 -> String.valueOf(alpha);
            default -> "Null";
        };

        FontManager.font14.drawString(value.getName(), x, y - 4, new Color(255, 255 ,255, 250).getRGB());
        FontManager.font14.drawString(valueName + "- " + valueString, x + 110 - FontManager.font14.getStringWidth(valueName + "- " + valueString), y - 4, new Color(250, 250 ,250, 220).getRGB());

        double state = switch (id) {
            case 0 -> red;
            case 1 -> green;
            case 2 -> blue;
            case 3 -> alpha;
            default -> 0;
        };

        double min = 0;
        double max = 255;
        double render = (112 * ((state - min) / (max - min)));


        RenderUtils.drawRect(x, y + 4, 112, 2, new Color(0,0,0, 80));
        RenderUtils.drawRect(x, y + 4, (float) render, 2, color2);
        RenderUtils.drawCircle(x + render, y + 5, 3, new Color(86,98,246).getRGB());

        if (Nattalie.instance.getPursueGUI().isHovering(x, y + 2, 114, 6, mouseX, mouseY)) {
            if (Mouse.isButtonDown(0)) {
                double difference = max - min;
                double number = min + MathHelper.clamp((mouseX - x) / (112), 0, 1) * difference;
                int set = (int) MathUtils.incValue(number, 1);

                switch (id) {
                    case 0 -> colorValue.setColorRed(set);
                    case 1 -> colorValue.setColorGreen(set);
                    case 2 -> colorValue.setColorBlue(set);
                    case 3 -> colorValue.setColoAr(set);
                }
            }
        }
    }
}
