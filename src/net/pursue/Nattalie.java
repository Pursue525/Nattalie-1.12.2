package net.pursue;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.pursue.command.CommandManager;
import net.pursue.config.ConfigManager;
import net.pursue.event.EventManager;
import net.pursue.mode.ModeManager;
import net.pursue.mode.client.ClickGUI;
import net.pursue.ui.gui.Click;
import net.pursue.ui.gui.OldClick;
import net.pursue.ui.gui.sinka.SinkaClick;
import net.pursue.ui.notification.NotificationManager;
import net.pursue.utils.HUDManager;
import net.pursue.utils.ModeHelper;
import net.pursue.utils.client.HWIDManager;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.mode.AutoLManager;
import net.pursue.utils.rotation.SilentRotation;
import net.pursue.utils.sound.WAVPlayer;
import net.pursue.utils.video.VideoPlayer;

@Getter
public class Nattalie {

    public static Nattalie instance;

    private final String clientName = "Nattalie";
    private final String clientVersion = "v1.8";


    private final String CONFIG_FILE = "user_config1.12.2.properties";
    public static String USERNAME;
    public static String KEY;

    public static boolean blur = true;
    public static boolean hwid = false; // 验证.

    private final ModeManager modeManager = new ModeManager();
    private final CommandManager commandManager = new CommandManager();
    private final ConfigManager configManager = new ConfigManager();
    private final SilentRotation eventManager = new SilentRotation();
    private final NotificationManager notificationManager = new NotificationManager();
    private final ModeHelper modeHelper = new ModeHelper();
    private final VideoPlayer player = new VideoPlayer();
    private final WAVPlayer wavPlayer = new WAVPlayer();
    private final HUDManager hudManager = new HUDManager();
    private final AutoLManager autoLManager = new AutoLManager();

    /**
     * Gui
     */
    private final Click pursueGUI = new Click();
    private final SinkaClick sinkaClick = new SinkaClick();
    private final OldClick oldGUI = new OldClick();

    public void openClickGUI() {
        switch (ClickGUI.instance.modeValue.getValue()) {
            case Nattalie -> Minecraft.getMinecraft().displayGuiScreen(pursueGUI);
            case SinKa -> Minecraft.getMinecraft().displayGuiScreen(sinkaClick);
        }

    }

    public void start() {
        modeManager.load();
        commandManager.init();
        hudManager.init();


        String[] previousCredentials = HWIDManager.loadCredentials();

        if (!previousCredentials[2].isEmpty()) {
            if (ConfigManager.load(previousCredentials[2])) {
                System.out.println("no config...");
            } else {
                configManager.loadAllConfig();
            }
        } else {
            configManager.loadAllConfig();
        }
        FriendManager.init();
        autoLManager.loadListFromJson();
        EventManager.instance.register(this);
    }

    public void close() {
        if (ConfigManager.configName.isEmpty()) {
            configManager.saveAllConfig();
            HWIDManager.saveCredentials(Nattalie.USERNAME, Nattalie.KEY, "", getOldGUI().sb);
        } else {
            ConfigManager.save(ConfigManager.configName);
            HWIDManager.saveCredentials(Nattalie.USERNAME, Nattalie.KEY, ConfigManager.configName, getOldGUI().sb);
        }

        FriendManager.saveFriend();
        autoLManager.saveList();
        EventManager.instance.register(this);
    }
}
