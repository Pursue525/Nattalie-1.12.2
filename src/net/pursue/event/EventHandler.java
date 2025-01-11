package net.pursue.event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Objects;


public class EventHandler {
    private final MethodHandle handler;
    private final Object parent;

    public EventHandler(Method method, Object parent) {
        Objects.requireNonNull(method, "Method cannot be null");
        method.setAccessible(true);
        try {
            MethodHandle m = MethodHandles.lookup().unreflect(method);
            Objects.requireNonNull(m, "MethodHandle cannot be null");
            this.handler = m.asType(m.type().changeParameterType(0, Object.class).changeParameterType(1, Event.class));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to create Handler", e);
        }
        this.parent = parent;
    }

    public Object getParent() {
        return parent;
    }

    public MethodHandle getHandler() {
        return handler;
    }
}
