package net.pursue.value.values;


import net.pursue.mode.Mode;
import net.pursue.value.Value;

import java.util.function.Supplier;



public class NumberValue<T extends Number> extends Value<T> {
    public T minimum;
    public T maximum;
    public T increment;

    public NumberValue(Mode mode, String name, T value, T min, T max, T inc) {
        super(name, value, () -> true, () -> true, () -> true);
        this.minimum = min;
        this.maximum = max;
        this.increment = inc;
        mode.addValues(this);
    }

    public NumberValue(Mode mode, String name, T value, T min, T max, T inc, Supplier<Boolean> visitable) {
        super(name, value, visitable, () -> true, () -> true);
        this.minimum = min;
        this.maximum = max;
        this.increment = inc;
        mode.addValues(this);
    }

    public NumberValue(Mode mode, String name, T value, T min, T max, T inc, Supplier<Boolean> visitable, Supplier<Boolean> visitable2) {
        super(name, value, visitable, visitable2, () -> true);
        this.minimum = min;
        this.maximum = max;
        this.increment = inc;
        mode.addValues(this);
    }

    public NumberValue(Mode mode, String name, T value, T min, T max, T inc, Supplier<Boolean> visitable, Supplier<Boolean> visitable2, Supplier<Boolean> visitable3) {
        super(name, value, visitable, visitable2, visitable3);
        this.minimum = min;
        this.maximum = max;
        this.increment = inc;
        mode.addValues(this);
    }

    public Number getMinimum() {
        return minimum;
    }

    public Number getMaximum() {
        return maximum;
    }

    public Number getIncrement() {
        return increment;
    }
}

