package net.pursue.mode;

import lombok.Getter;
import net.pursue.mode.client.*;
import net.pursue.mode.combat.*;
import net.pursue.mode.exploit.AutoDisMode;
import net.pursue.mode.exploit.FakePlayer;
import net.pursue.mode.exploit.WorldManager;
import net.pursue.mode.hud.*;
import net.pursue.mode.misc.AntiBot;
import net.pursue.mode.misc.Disabler;
import net.pursue.mode.misc.Teams;
import net.pursue.mode.move.*;
import net.pursue.mode.player.*;
import net.pursue.mode.render.*;
import net.pursue.mode.world.SpeedMine;
import net.pursue.mode.world.Stuck;
import net.pursue.mode.world.Timer;

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
        modes.add(new Velocity());
        modes.add(new AutoProjectile());
        modes.add(new ArmorBreak());
        modes.add(new Criticals());
        modes.add(new SuperKB());

        //exploit
        modes.add(new AutoDisMode());
        modes.add(new Protocol());
        modes.add(new FriendGUI());
        modes.add(new FakePlayer());
        modes.add(new Tile());
        modes.add(new ConfigGUI());

        //HUD
        modes.add(new ClickGUI());
        modes.add(new Logo());
        modes.add(new Arraylist());
        modes.add(new Target());
        modes.add(new SwordBlock());
        modes.add(new Notification());
        modes.add(new Armor());
        modes.add(new Effects());
        modes.add(new Inventory());

        // misc
        modes.add(new Disabler());
        modes.add(new Teams());
        modes.add(new AntiBot());

        // move
        modes.add(new NoSlow());
        modes.add(new Sprint());
        modes.add(new InvMove());
        modes.add(new Stuck());
        modes.add(new MoveFix());
        modes.add(new Eagle());
        modes.add(new Speed());

        // render
        modes.add(new Animation());
        modes.add(new ESP());
        modes.add(new NameTag());
        modes.add(new Projectile());
        modes.add(new ItemDeBug());
        modes.add(new Shield());

        // player
        modes.add(new AutoHeal());
        modes.add(new AutoL());
        modes.add(new AutoTool());
        modes.add(new Blink());
        modes.add(new AutoCage());
        modes.add(new InvManager());
        modes.add(new Scaffold());
        modes.add(new FastPlace());
        modes.add(new AutoReport());
        modes.add(new ContainerAura());
        modes.add(new Stealer());

        // world
        modes.add(new WorldManager());
        modes.add(new Timer());
        modes.add(new SpeedMine());

        // client
        modes.add(new ClientSetting());
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

    public void addClass(Mode mode) {
        modes.add(mode);
    }
}
