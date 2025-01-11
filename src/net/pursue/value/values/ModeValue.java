package net.pursue.value.values;


import net.pursue.mode.Mode;
import net.pursue.value.Value;

import java.util.function.Supplier;


public class ModeValue<V extends Enum<?>> extends Value<V> {
    private final V[] modes;

    public ModeValue(Mode mode, String name, V[] modes, V value) {
        super(name, value, () -> true, () -> true, () -> true);
        this.modes = modes;
        mode.addValues(this);
    }

    public ModeValue(Mode mode, String name, V[] modes, V value, Supplier<Boolean> visitable) {
        super(name, value, visitable, () -> true, () -> true);
        this.modes = modes;
        mode.addValues(this);
    }

    public ModeValue(Mode mode, String name, V[] modes, V value, Supplier<Boolean> visitable, Supplier<Boolean> visitable2) {
        super(name, value, visitable, visitable2, () -> true);
        this.modes = modes;
        mode.addValues(this);
    }

    public ModeValue(Mode mode, String name, V[] modes, V value, Supplier<Boolean> visitable, Supplier<Boolean> visitable2, Supplier<Boolean> visitable3) {
        super(name, value, visitable, visitable2, visitable3);
        this.modes = modes;
        mode.addValues(this);
    }

    public void setMode(String mode) {
        V[] arrV = this.modes;
        int n = arrV.length;
        int n2 = 0;
        while (n2 < n) {
            V e = arrV[n2];
            if (e.name().equalsIgnoreCase(mode)) {
                this.setValue(e);
            }
            ++n2;
        }
    }

    public Object[] getModes() {
        return modes;
    }
}

