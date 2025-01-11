package net.pursue.mode;

import lombok.Getter;
import net.pursue.mode.combat.*;
import net.pursue.mode.exploit.*;
import net.pursue.mode.hud.*;
import net.pursue.mode.misc.*;
import net.pursue.mode.move.*;
import net.pursue.mode.player.*;
import net.pursue.mode.render.Animation;
import net.pursue.mode.render.NameTag;

import net.pursue.mode.world.SpeedMine;
import net.pursue.mode.world.Timer;
import net.pursue.mode.world.WorldManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ModeManager {
    private final List<Mode> modes = new ArrayList<>();


    public List<Mode> getEnableMods() {
        return modes.stream().filter(Mode::isEnable).collect(Collectors.toList());
    }

    public void load() {
        // combat
        modes.add(new KillAura());
        modes.add(new AntiKnockBack());
        modes.add(new ArmorBreak());

        //exploit
        modes.add(new AutoDisMode());

        //HUD
        modes.add(new ClickGUI());
        modes.add(new Logo());
        modes.add(new Arraylist());
        modes.add(new Target());
        modes.add(new Notification());
        modes.add(new Armor());
        modes.add(new Effects());
        modes.add(new Inventory());

        // misc
        modes.add(new Disabler());
        modes.add(new PacketManager());
        modes.add(new Teams());
        modes.add(new AntiBot());

        // move
        modes.add(new NoSlow());
        modes.add(new Sprint());
        modes.add(new InvMove());
        modes.add(new Stuck());
        modes.add(new MoveFix());
        modes.add(new Speed());

        // render
        modes.add(new Animation());
        modes.add(new NameTag());

        // player
        modes.add(new AutoHeal());
        modes.add(new AutoTool());
        modes.add(new Blink());
        modes.add(new AutoArmor());
        modes.add(new Manager());
        modes.add(new Scaffold());
        modes.add(new Stealer());

        // world
        modes.add(new WorldManager());
        modes.add(new Timer());
        modes.add(new SpeedMine());
    }

    public void onKey(int key) {
        for (Mode enableMod : modes) {
            if (enableMod.getKey() == key) {
                enableMod.setEnable(!enableMod.isEnable());
            }
        }
    }

    public Mode getByName(String name) {
        for (Mode mod : modes) {
            if (name.equalsIgnoreCase(mod.getModeName())) {
                return mod;
            }
        }
        return null;
    }

    public Mode getByClass(Class<? extends Mode> modClass) {
        for (Mode mod : modes) {
            if (mod.getClass() == modClass) {
                return mod;
            }
        }
        return null;
    }
}
