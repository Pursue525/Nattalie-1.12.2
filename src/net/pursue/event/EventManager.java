package net.pursue.event;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;


public class EventManager {
    public static final EventManager instance = new EventManager();
    private final ConcurrentHashMap<Class<? extends Event>, List<EventHandler>> registry = new ConcurrentHashMap<>();

    public void register(Object... objs) {
        Arrays.stream(objs).forEach(obj -> {
            Method[] methods = obj.getClass().getDeclaredMethods();
            Arrays.stream(methods)
                    .filter(m -> m.getParameterCount() == 1 && m.isAnnotationPresent(EventTarget.class))
                    .forEach(m -> {
                        Class<Event> eventClass = (Class<Event>) m.getParameterTypes()[0];
                        List<EventHandler> eventHandlers = registry.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>());
                        eventHandlers.add(new EventHandler(m, obj));
                    });
        });
    }

    public void unregister(Object... objs) {
        Predicate<EventHandler> handlerPredicate = eventHandler -> Arrays.stream(objs).anyMatch(obj -> obj == eventHandler.getParent());
        registry.values().forEach(list -> list.removeIf(handlerPredicate));
    }

    public <E extends Event> E call(E event) {
        if (event != null) {
            List<EventHandler> list = this.registry.get(event.getClass());
            if (list != null && !list.isEmpty()) {
                for (EventHandler data : list) {
                    try {
                        data.getHandler().invokeExact(data.getParent(), event);
                    } catch (Throwable e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return event;
    }
}

