package net.pursue.value.values;


import net.pursue.mode.Mode;
import net.pursue.value.Value;

import java.util.function.Supplier;


public class BooleanValue<V> extends Value<V> {
    public BooleanValue(Mode mode, String name, V enabled) {
        super(name, enabled, () -> true, () -> true, () -> true);
        mode.addValues(this);
    }

    public BooleanValue(Mode mode,String name, V enabled, Supplier<Boolean> visitable) {
        super(name, enabled, visitable, () -> true, () -> true);
        mode.addValues(this);
    }

    public BooleanValue(Mode mode,String name, V enabled, Supplier<Boolean> visitable, Supplier<Boolean> visitable2) {
        super(name, enabled, visitable, visitable2, () -> true);
        mode.addValues(this);
    }

    public BooleanValue(Mode mode,String name, V enabled, Supplier<Boolean> visitable, Supplier<Boolean> visitable2, Supplier<Boolean> visitable3) {
        super(name, enabled, visitable, visitable2, visitable3);
        mode.addValues(this);
    }
}