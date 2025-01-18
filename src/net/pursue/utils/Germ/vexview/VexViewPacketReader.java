package net.pursue.utils.Germ.vexview;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class VexViewPacketReader {
    private final String packetSubType;
    private final String packetType;
    private final JsonElement packetData;
    private final HashMap<String, String> buttonList = new HashMap<>();
    private String buttonMode = "normal";

    public VexViewPacketReader(String json) {
        JsonParser jp = new JsonParser();
        JsonObject jsonObject = jp.parse(json).getAsJsonObject();
        this.packetSubType = jsonObject.get("packet_sub_type").getAsString();
        this.packetType = jsonObject.get("packet_type").getAsString();
        this.packetData = jsonObject.get("packet_data");

        if (this.packetData.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : this.packetData.getAsJsonObject().entrySet()) {
                switch (entry.getKey()) {
                    case "base":
                        buttonMode = "normal";
                        String[] elements = entry.getValue().getAsString().split("<#>");
                        for (String element : elements) {
                            if (element.contains("[but]")) {
                                String[] sub = element.split("<&>");
                                buttonList.put(sub[0].substring(5), sub[6]);
                            }
                        }
                        break;
                    case "scrollinglist":
                        buttonMode = "switcher";
                        elements = entry.getValue().getAsString().split("<#>");
                        for (String element : elements) {
                            if (element.contains("[but]")) {
                                String[] sub = element.split("<&>");
                                buttonList.put(sub[0].substring(5), sub[6]);
                            }
                        }
                        break;
                }
            }
        }
    }

}