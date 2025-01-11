package net.pursue.value.values;

import net.pursue.mode.Mode;
import net.pursue.value.Value;

import java.awt.*;
import java.util.function.Supplier;


public class ColorValue<T extends Integer> extends Value<T> {
    public T color;

    public ColorValue(Mode mode, String name, T color) {
        super(name, color, () -> true, () -> true, () -> true);
        this.color = color;
        mode.addValues(this);
    }

    public ColorValue(Mode mode, String name, T color, Supplier<Boolean> visitable) {
        super(name, color, visitable, () -> true, () -> true);
        this.color = color;
        mode.addValues(this);
    }

    public ColorValue(Mode mode, String name, T color, Supplier<Boolean> visitable, Supplier<Boolean> visitable2) {
        super(name, color, visitable, visitable2, () -> true);
        this.color = color;
        mode.addValues(this);
    }

    public ColorValue(Mode mode, String name, T color, Supplier<Boolean> visitable, Supplier<Boolean> visitable2, Supplier<Boolean> visitable3) {
        super(name, color, visitable, visitable2, visitable3);
        this.color = color;
        mode.addValues(this);
    }

    public Integer getColorRGB() {
        return this.color;
    }

    public Color getColor() {
        return new Color(this.color);
    }

    public void setColor(T color) {
        this.setValue(color);
        this.color = color;
    }
}
