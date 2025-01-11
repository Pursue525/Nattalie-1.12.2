package net.pursue.config;

import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * @author ChengFeng
 * @since 2023/3/19
 */
@Getter
public abstract class Config {

    private final String name;

    public Config(String name) {
        this.name = name;
    }

    public abstract JsonObject saveConfig();

    public abstract void loadConfig(JsonObject object);
}
