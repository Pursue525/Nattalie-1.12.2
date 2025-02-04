package net.pursue;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumConnectionState;
import net.pursue.command.CommandManager;
import net.pursue.config.ConfigManager;
import net.pursue.event.EventManager;
import net.pursue.mode.ModeManager;
import net.pursue.ui.gui.Click;
import net.pursue.ui.gui.Friend;
import net.pursue.ui.notification.NotificationManager;
import net.pursue.utils.*;
import net.pursue.utils.client.HWIDManager;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.utils.video.VideoPlayer;

import java.io.File;

@Getter
public class Nattalie {

    public static Nattalie instance;

    private final String clientName = "Nattalie";
    private final String clientVersion = "v1.0.4";


    private final String CONFIG_FILE = "user_config.properties";
    public static String USERNAME;
    public static String KEY;

    @Setter
    private File videoFile = new File(new File(Minecraft.getMinecraft().mcDataDir, this.clientName + "/Video"), "米塔.mp4");

    private final ModeManager modeManager = new ModeManager();
    private final CommandManager commandManager = new CommandManager();
    private final ConfigManager configManager = new ConfigManager();
    private final SilentRotation eventManager = new SilentRotation();
    private final NotificationManager notificationManager = new NotificationManager();
    private final ModeHelper modeHelper = new ModeHelper();
    private final VideoPlayer player = new VideoPlayer();
    private final HUDManager hudManager = new HUDManager();

    /**
     * Gui
     */
    private final Click pursueGUI = new Click();

    public void start() {
        modeManager.load();
        commandManager.init();
        hudManager.init();

        String[] previousCredentials = HWIDManager.loadCredentials();

        if (!previousCredentials[2].isEmpty()) {
            if (ConfigManager.load(previousCredentials[2])) {
                //
            } else {
                configManager.loadAllConfig();
            }
        } else {
            configManager.loadAllConfig();
        }
        FriendManager.init();
        EventManager.instance.register(this);
    }

    public void close() {
        if (ConfigManager.configName.isEmpty()) {
            configManager.saveAllConfig();
            HWIDManager.saveCredentials(Nattalie.USERNAME, Nattalie.KEY, "");
        } else {
            ConfigManager.save(ConfigManager.configName);
            HWIDManager.saveCredentials(Nattalie.USERNAME, Nattalie.KEY, ConfigManager.configName);
        }

        FriendManager.saveFriend();
        EventManager.instance.register(this);
    }
}
