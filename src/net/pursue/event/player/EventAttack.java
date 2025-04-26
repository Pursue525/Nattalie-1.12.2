//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package net.pursue.event.player;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.pursue.event.Event;


@Getter
public class EventAttack extends Event {
    private final Entity target;
    private final Type type;

    public EventAttack(Entity target, Type type) {
        this.target = target;
        this.type = type;
    }

    public enum Type {
        Pre,
        Post
    }
}
