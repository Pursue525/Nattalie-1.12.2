package net.pursue.value;


import lombok.Getter;
import net.pursue.event.EventManager;
import net.pursue.mode.Mode;

import java.util.function.Supplier;

@Getter
public abstract class Value<V> {

    private final String name;

    private V value;

    private V value2;

    private final Supplier<Boolean> visitable;
    private final Supplier<Boolean> visitable2;
    private final Supplier<Boolean> visitable3;

    public Value(String name, V value, Supplier<Boolean> visitable, Supplier<Boolean> visitable2, Supplier<Boolean> visitable3) {
        this.name = name;
        this.visitable = visitable;
        this.visitable2 = visitable2;
        this.visitable3 = visitable3;
        this.value = value;
        EventManager.instance.register(this);
    }

    public Value(String name, V value, V value2, Supplier<Boolean> visitable, Supplier<Boolean> visitable2, Supplier<Boolean> visitable3) {
        this.name = name;
        this.visitable = visitable;
        this.visitable2 = visitable2;
        this.visitable3 = visitable3;
        this.value = value;
        this.value2 = value2;
        EventManager.instance.register(this);
    }

    public void setValue(final V val) {
        this.value = val;
    }

    public void setValue2(final V val) {
        this.value2 = val;
    }

    public boolean isVisitable() {
        return !this.visitable.get() || !this.visitable2.get() || !this.visitable3.get();
    }

}

