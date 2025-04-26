package net.pursue.utils.mode;

import net.minecraft.client.Minecraft;
import net.pursue.mode.player.AutoL;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.client.UtilsManager;

import java.io.*;

public class AutoLManager extends UtilsManager {
    private static final File fil = new File(Minecraft.getMinecraft().mcDataDir, "Nattalie/AutoL");
    private static final File f = new File(fil, "Taunt.json");

    public void addString(String string) {
        AutoL.list.add(string);
        DebugHelper.sendMessage("AutoL", "成功添加嘲讽词：" + string);
    }

    public void removeString(int i) {
        AutoL.list.removeIf(string -> {
            if (string.hashCode() == i) {
                DebugHelper.sendMessage("AutoL", "删除嘲讽词：" + string);
                return true;
            } else return false;
        });
    }

    public void saveList() {
        try {
            f.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter writer = new FileWriter(f, false)) {
            for (String s : AutoL.list) {
                writer.write(s + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadListFromJson() {
        if (!fil.exists()) {
            fil.mkdirs();
        }

        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            try (FileInputStream fis = new FileInputStream(f)) {
                try (InputStreamReader isr = new InputStreamReader(fis)) {
                    try (BufferedReader br = new BufferedReader(isr)) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            AutoL.list.add(line);
                        }
                    }
                }
            }

            if (AutoL.list.isEmpty()) {
                AutoL.list.add("弱的不是端，而是你啊！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
