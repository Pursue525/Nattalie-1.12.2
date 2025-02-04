package net.pursue.config.configs;

import com.google.gson.JsonObject;
import net.pursue.config.Config;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;

public class HUDConfig extends Config {

    public HUDConfig() {
        super("hud.json");
    }

    @Override
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();

        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            JsonObject hudObject = new JsonObject();

            hudObject.addProperty("x", data.getX());
            hudObject.addProperty("y", data.getY());

            object.add(data.getTitle(), hudObject);
        }

        return object;
    }

    @Override
    public void loadConfig(JsonObject object) {
        if (!HUDManager.data.isEmpty()) for (HUDData data : HUDManager.data) {
            if (object.has(data.getTitle())) {
                JsonObject hudObject = object.get(data.getTitle()).getAsJsonObject();

                data.setX(hudObject.get("x").getAsInt());
                data.setY(hudObject.get("y").getAsInt());
            }
        }
    }
}
