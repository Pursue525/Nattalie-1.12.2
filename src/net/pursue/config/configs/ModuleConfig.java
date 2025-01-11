package net.pursue.config.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.pursue.Nattalie;
import net.pursue.config.Config;
import net.pursue.mode.Mode;
import net.pursue.value.Value;
import net.pursue.value.values.BooleanValue;
import net.pursue.value.values.ColorValue;
import net.pursue.value.values.ModeValue;
import net.pursue.value.values.NumberValue;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2023/3/19
 */
public class ModuleConfig extends Config {

    public ModuleConfig() {
        super("modules.json");
    }

    @Override
    public JsonObject saveConfig() {
        JsonObject object = new JsonObject();

        for (Mode module : Nattalie.instance.getModeManager().getModes()) {

            JsonObject moduleObject = new JsonObject();

            moduleObject.addProperty("state", module.isEnable());
            moduleObject.addProperty("key", module.getKey());

            JsonObject valuesObject = new JsonObject();

            for (Value value : module.getValues()) {
                if (value instanceof NumberValue) {
                    valuesObject.addProperty(value.getName(), ((NumberValue<?>) value).getValue().toString());
                } else if (value instanceof BooleanValue) {
                    valuesObject.addProperty(value.getName(), ((BooleanValue<?>) value).getValue().toString());
                } else if (value instanceof ModeValue) {
                    valuesObject.addProperty(value.getName(), ((ModeValue<?>) value).getValue().toString());
                } else if (value instanceof ColorValue) {
                    valuesObject.addProperty(value.getName(), ((ColorValue<?>) value).getColorRGB());
                }
            }

            moduleObject.add("values", valuesObject);
            object.add(module.getModeName(), moduleObject);
        }

        return object;
    }

    @Override
    public void loadConfig(JsonObject object) {
       for (Mode module : Nattalie.instance.getModeManager().getModes()) {
            if (object.has(module.getModeName())) {

                JsonObject moduleObject = object.get(module.getModeName()).getAsJsonObject();

                if (moduleObject.has("state")) {
                    module.setEnable(moduleObject.get("state").getAsBoolean());
                }

                if (moduleObject.has("key")) {
                    module.setKey(moduleObject.get("key").getAsInt());
                }

                if (moduleObject.has("values")) {
                    JsonObject valuesObject = moduleObject.get("values").getAsJsonObject();

                    for (Value<?> value : module.getValues()) {
                        if (valuesObject.has(value.getName())) {
                            JsonElement theValue = valuesObject.get(value.getName());
                            if (value instanceof NumberValue) {
                                ((NumberValue) value).setValue(theValue.getAsNumber().doubleValue());
                            } else if (value instanceof BooleanValue) {
                                ((BooleanValue) value).setValue(theValue.getAsBoolean());
                            } else if (value instanceof ModeValue) {
                                ((ModeValue) value).setMode(theValue.getAsString());
                            } else if (value instanceof ColorValue) {
                                Color color = new Color(theValue.getAsInt());
                                ((ColorValue) value).setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()).getRGB());
                            }
                        }
                    }
                }
            }
        }
    }
}
