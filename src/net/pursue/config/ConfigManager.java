package net.pursue.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.config.configs.HUDConfig;
import net.pursue.config.configs.ModuleConfig;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.client.HWIDManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    public static String configName = "";

    public static final List<Config> configs = new ArrayList<>();

    public static final File userDir = new File(Minecraft.getMinecraft().mcDataDir, "Nattalie");

    public static final File dir = new File(userDir, "Configs");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void delete(String files) {
        File file;
        StringBuilder nameBuilder;
        for (Config config : configs) {
            nameBuilder = new StringBuilder(files);

            file = getConfigFile(nameBuilder, config);

            if (file != null && file.isFile() && file.exists()) {
                if (file.delete()) {
                    DebugHelper.sendMessage("Config","配置 " + TextFormatting.YELLOW + files + TextFormatting.WHITE + " 删除成功");
                } else {
                    DebugHelper.sendMessage("Config","配置 " + TextFormatting.YELLOW + files + TextFormatting.WHITE + " 删除失败");
                }
            }
        }
    }

    public void init() {
        if (!userDir.exists()) {
            userDir.mkdir();
        }

        if (!dir.exists()) {
            dir.mkdir();
        }

        configs.add(new ModuleConfig());
        configs.add(new HUDConfig());
    }

    private void loadConfig(String name) {
        File file = new File(userDir, name);


        if (file.exists()) {
            for (Config config : configs) {
                if (config.getName().equals(name)) {
                    try {
                        String content = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
                        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
                        config.loadConfig(jsonObject);
                        break;
                    } catch (IOException e) {
                        //
                        break;
                    }
                }
            }
        } else {
            saveConfig(name);
        }
    }

    private static void saveConfig(String name) {
        File file = new File(userDir, name);

        try {
            file.createNewFile();
            for (Config config : configs) {
                if (config.getName().equals(name)) {
                    FileUtils.writeByteArrayToFile(file, gson.toJson(config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
        } catch (IOException e) {
            //
        }
    }

    public void loadAllConfig() {
        configs.forEach(it -> loadConfig(it.getName()));
    }

    public void saveAllConfig() {
        configs.forEach(it -> saveConfig(it.getName()));
    }

    public static boolean save(String name) {
        StringBuilder nameBuilder;
        File file;

        try {
            for (Config config : configs) {
                nameBuilder = new StringBuilder(name);
                file = getConfigFile(nameBuilder, config);
                file.createNewFile();
                FileUtils.writeByteArrayToFile(file, gson.toJson(config.saveConfig()).getBytes(StandardCharsets.UTF_8));
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static File getConfigFile(StringBuilder nameBuilder, Config config) {
        switch (config.getName()) {
            case "hud.json" -> {
                nameBuilder.append("-HUD.json");

                return new File(dir, nameBuilder.toString());
            }
            case "modules.json" -> {
                nameBuilder.append("-Modules.json");

                return new File(dir, nameBuilder.toString());
            }
        }
        return null;
    }

    public static boolean load(String name) {
        StringBuilder nameBuilder;

        for (Config config : configs) {
            nameBuilder = new StringBuilder(name);

            File file = getConfigFile(nameBuilder, config);

            if (file != null && file.exists()) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
                    JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
                    config.loadConfig(jsonObject);
                } catch (IOException e) {
                    return false;
                }
                configName = name;
            } else {
                return false;
            }
        }
        System.out.println("Loaded config: " + name);
        return true;
    }

    public static String[] getList() {
        return dir.list();
    }
}
