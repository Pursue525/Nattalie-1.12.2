package net.pursue.config;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import net.pursue.Nattalie;
import net.pursue.config.configs.HUDConfig;
import net.pursue.config.configs.ModuleConfig;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigManager {
    public static final List<Config> configs = new ArrayList<>();
    public static final File dir = new File(new File(Minecraft.getMinecraft().mcDataDir, "Nattalie-1.12.2"), "Configs");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ConfigManager() {
        if (!dir.exists()) {
            dir.mkdir();
        }

        configs.add(new ModuleConfig());
        configs.add(new HUDConfig());
    }

    public void loadConfig(String name) {
        File file = new File(dir, name);
        if (file.exists()) {
            System.out.println("Loading config: " + name);
            for (Config config : configs) {
                if (config.getName().equals(name)) {
                    try {
                        String content = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
                        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
                        config.loadConfig(jsonObject);
                        break;
                    } catch (IOException e) {
                        System.out.println("Failed to load config: " + name);
                        e.printStackTrace();
                        break;
                    }
                }
            }
        } else {
            saveConfig(name);
        }
    }

    public void loadUserConfig(String name) {
        File file = new File(dir, name);
        if (file.exists()) {
            for (Config config : configs) {
                if (config.getName().equals("modules.json")) {
                    try {
                        String content = new String(Files.readAllBytes(Paths.get(file.getPath())), StandardCharsets.UTF_8);
                        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
                        config.loadConfig(jsonObject);
                        break;
                    } catch (IOException e) {
                        System.out.println("Failed to load config: " + name);
                        e.printStackTrace();
                        break;
                    }
                }
            }
        } else {
            saveUserConfig(name);
        }
    }

    public void saveConfig(String name) {
        File file = new File(dir, name);

        try {
            file.createNewFile();
            for (Config config : configs) {
                if (config.getName().equals(name)) {
                    FileUtils.writeByteArrayToFile(file, gson.toJson(config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to save config: " + name);
        }
    }

    public void saveUserConfig(String name) {
        File file = new File(dir, name);

        try {
            file.createNewFile();
            for (Config config : configs) {
                if (config.getName().equals("modules.json")) {
                    FileUtils.writeByteArrayToFile(file, gson.toJson(config.saveConfig()).getBytes(StandardCharsets.UTF_8));
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to save config: " + name);
        }
    }

    public void loadAllConfig() {
        configs.forEach(it -> loadConfig(it.getName()));
    }

    public void saveAllConfig() {
        configs.forEach(it -> saveConfig(it.getName()));
    }

    private static final String EXTENSION = ".json";

}
