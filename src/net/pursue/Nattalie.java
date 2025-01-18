package net.pursue;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.pursue.command.CommandManager;
import net.pursue.config.ConfigManager;
import net.pursue.event.EventManager;
import net.pursue.mode.ModeManager;
import net.pursue.ui.gui.PursueGUI;
import net.pursue.ui.notification.NotificationManager;
import net.pursue.utils.*;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.utils.video.VideoPlayer;

import java.io.File;

@Getter
public class Nattalie {

    public static Nattalie instance;

    private final String clientName = "Nattalie";
    private final String clientVersion = "v1.0.1";


    private final String CONFIG_FILE = "user_config.properties";
    @Setter
    private String USERNAME = "Name";

    @Setter
    private String KEY = "KEY";

    @Setter
    private File videoFile = new File(new File(Minecraft.getMinecraft().mcDataDir, this.clientName + "/Video"), "米塔.mp4");

    private final ModeManager modeManager = new ModeManager();
    private final CommandManager commandManager = new CommandManager();
    private final PursueGUI pursueGUI = new PursueGUI();
    private final ConfigManager configManager = new ConfigManager();

    private final SilentRotation eventManager = new SilentRotation();
    private final NotificationManager notificationManager = new NotificationManager();
    private final ModeHelper modeHelper = new ModeHelper();
    private final VideoPlayer player = new VideoPlayer();


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
