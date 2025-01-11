package net.pursue;

import com.ibm.icu.impl.duration.impl.Utils;
import lombok.Getter;
import lombok.Setter;
import net.pursue.command.CommandManager;
import net.pursue.config.ConfigManager;
import net.pursue.event.EventManager;
import net.pursue.mode.ModeManager;
import net.pursue.mode.hud.Notification;
import net.pursue.shield.IsShield;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.gui.PursueGUI;
import net.pursue.ui.notification.NotificationManager;
import net.pursue.utils.*;
import net.pursue.utils.client.UtilsManager;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.rotation.SilentRotation;

@Getter
public class Nattalie {

    public static Nattalie instance;

    private final String clientName = "Nattalie";
    private final String clientVersion = "v1.0.0_Beta";


    private final String CONFIG_FILE = "user_config.properties";
    @Setter
    private String USERNAME_KEY = "Name";
    private final String KEY_KEY = "KEY";

    private final ModeManager modeManager = new ModeManager();
    private final CommandManager commandManager = new CommandManager();
    private final PursueGUI pursueGUI = new PursueGUI();
    private final ConfigManager configManager = new ConfigManager();

    private final SilentRotation eventManager = new SilentRotation();
    private final NotificationManager notificationManager = new NotificationManager();
    private final ModeHelper modeHelper = new ModeHelper();


    private HUDManager hudManager;

    public void start() {
        modeManager.load();
        commandManager.init();

        hudManager = new HUDManager();

        configManager.loadAllConfig();

        EventManager.instance.register(this);
    }

    public void close() {
        EventManager.instance.register(this);
        configManager.saveAllConfig();
    }
}
