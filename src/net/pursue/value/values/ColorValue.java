package net.pursue.value.values;

import net.pursue.mode.Mode;
import net.pursue.value.Value;

import java.awt.*;
import java.util.function.Supplier;


public class ColorValue<T extends Color> extends Value<T> {
    public int r;
    public int g;
    public int b;
    public int a;

    public ColorValue(Mode mode, String name, T color) {
        super(name, color, () -> true, () -> true, () -> true);
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        a = color.getAlpha();
        mode.addValues(this);
    }

    public ColorValue(Mode mode, String name, T color, Supplier<Boolean> visitable) {
        super(name, color, visitable, () -> true, () -> true);
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        a = color.getAlpha();
        mode.addValues(this);
    }

    public ColorValue(Mode mode, String name, T color, Supplier<Boolean> visitable, Supplier<Boolean> visitable2) {
        super(name, color, visitable, visitable2, () -> true);
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        a = color.getAlpha();
        mode.addValues(this);
    }

    public ColorValue(Mode mode, String name, T color, Supplier<Boolean> visitable, Supplier<Boolean> visitable2, Supplier<Boolean> visitable3) {
        super(name, color, visitable, visitable2, visitable3);
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        a = color.getAlpha();
        mode.addValues(this);
    }

    public Integer getColorRGB() {
        return new Color(r, g, b, a).getRGB();
    }

    public Color getColor() {
        return new Color(r, g, b, a);
    }

    public int getRed() {
        return r;
    }

    public int getGreen() {
        return g;
    }

    public int getBlue() {
        return b;
    }

    public int getAlpha() {
        return a;
    }

    public void setColor(T color) {
        this.setValue(color);
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        a = color.getAlpha();
    }
    public void setColorRed(int color) {
        this.setValue((T) new Color(color, g, b, a));
        r = color;
    }
    public void setColorGreen(int color) {
        this.setValue((T) new Color(r, color, b, a));
        g = color;
    }
    public void setColorBlue(int color) {
        this.setValue((T) new Color(r, g, color, a));
        b = color;
    }

    public void setColoAr(int color) {
        this.setValue((T) new Color(r, g, b, color));
        this.a = color;
    }
}
