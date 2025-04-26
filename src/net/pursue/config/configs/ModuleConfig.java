package net.pursue.config.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.pursue.Nattalie;
import net.pursue.config.Config;
import net.pursue.mode.Mode;
import net.pursue.utils.HUDData;
import net.pursue.utils.HUDManager;
import net.pursue.utils.category.Category;
import net.pursue.value.Value;
import net.pursue.value.values.*;

import java.awt.*;

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

            if (module.getCategory() == Category.HUD) {
                for (HUDData hudData : HUDManager.data) {
                    if (hudData.getTitle().equals(module.getModeName())) {
                        moduleObject.addProperty("x", hudData.getX());
                        moduleObject.addProperty("y", hudData.getY());
                    }
                }
            }

            JsonObject valuesObject = new JsonObject();

            for (Value value : module.getValues()) {
                if (value instanceof NumberValue) {
                    valuesObject.addProperty(value.getName(), ((NumberValue<?>) value).getValue().toString());
                } else if (value instanceof BooleanValue) {
                    valuesObject.addProperty(value.getName(), ((BooleanValue<?>) value).getValue().toString());
                } else if (value instanceof ModeValue) {
                    valuesObject.addProperty(value.getName(), ((ModeValue<?>) value).getValue().toString());
                } else if (value instanceof ColorValue) {
                    JsonObject colorObject = new JsonObject();
                    colorObject.addProperty("red", ((ColorValue) value).getRed());
                    colorObject.addProperty("green", ((ColorValue) value).getGreen());
                    colorObject.addProperty("blue", ((ColorValue) value).getBlue());
                    colorObject.addProperty("alpha", ((ColorValue) value).getAlpha());
                    valuesObject.add(value.getName(), colorObject);
                } else if (value instanceof StringValue) {
                    valuesObject.addProperty(value.getName(), ((StringValue<?>) value).getValue());
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
                    if (module.isEnable() != moduleObject.get("state").getAsBoolean()) {
                        module.setEnable(moduleObject.get("state").getAsBoolean());
                    }
                }

                if (moduleObject.has("key")) {
                    module.setKey(moduleObject.get("key").getAsInt());
                }

                if (module.getCategory() == Category.HUD) {
                    for (HUDData hudData : HUDManager.data) {
                        if (hudData.getTitle().equals(module.getModeName())) {

                            hudData.setX(moduleObject.get("x").getAsInt());
                            hudData.setY(moduleObject.get("y").getAsInt());
                        }
                    }
                }

                if (moduleObject.has("values")) {
                    JsonObject valuesObject = moduleObject.get("values").getAsJsonObject();

                    for (Value<?> value : module.getValues()) {
                        if (valuesObject.has(value.getName())) {
                            JsonElement theValue = valuesObject.get(value.getName());
                            switch (value) {
                                case NumberValue numberValue ->
                                        numberValue.setValue(theValue.getAsNumber().doubleValue());
                                case BooleanValue booleanValue ->
                                        booleanValue.setValue(theValue.getAsBoolean());
                                case ModeValue modeValue ->
                                        modeValue.setMode(theValue.getAsString());
                                case StringValue stringValue ->
                                        stringValue.setValue(theValue.getAsString());
                                case ColorValue colorValue -> {
                                    if (theValue.isJsonObject()) {
                                        JsonObject colorObject = theValue.getAsJsonObject();
                                        int red = colorObject.get("red").getAsInt();
                                        int green = colorObject.get("green").getAsInt();
                                        int blue = colorObject.get("blue").getAsInt();
                                        int alpha = colorObject.get("alpha").getAsInt();

                                        colorValue.setColor(new Color(red, green, blue, alpha));
                                    }
                                }
                                default -> System.out.println("Invalid value: " + theValue);

                            }
                        }
                    }
                }
            }
        }
    }
}
