//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Administrator\Downloads\Minecraft1.12.2 Mappings"!

//Decompiled by Procyon!

package net.pursue.event.player;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.pursue.event.Event;


@Getter
public class EventAttack extends Event {
    private final Entity target;
    public EventAttack(Entity target) {
        this.target = target;
    }

    public enum Type {
        Pre,
        Post
    }
}
