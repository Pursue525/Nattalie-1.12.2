package net.pursue.utils.friend;



import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.pursue.Nattalie;
import net.pursue.mode.misc.AntiBot;
import net.pursue.mode.misc.Teams;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FriendManager {

    public static final File dir = new File(Minecraft.getMinecraft().mcDataDir, Nattalie.instance.getClientName() + "/Friends");
    public static final List<String> friends = new ArrayList<String>();
    public static String name = "";

    public static void init() {
        if (!dir.exists()) {
            dir.mkdirs();
        }

        final File f = new File(dir, "player.config");

        if (f.exists()) {
            List<String> configs = read();
            for (String v : configs) {
                String playerName = v.split(":")[0];

                friends.add(playerName);
            }
        }
    }

    public static void saveFriend() {
        StringBuilder values = null;

        if (!friends.isEmpty()) {
            values = new StringBuilder();

            for (String friend : friends) {
                values.append(String.format("%s:[%s:%s]%s", friend, "Info", "Null", System.lineSeparator()));
            }
        }

        if (values != null) {
            save(values.toString());
        }
    }

    public static boolean isFriend(String name) {
        for (String friend : friends) {
            if (!friend.equals(name)) continue;

            return true;
        }
        return false;
    }

    public static boolean isFriend(Entity entity) {
        for (String friend : friends) {
            if (!friend.equals(entity.getName())) continue;

            return true;
        }
        return Teams.instance.isTeam((EntityLivingBase) entity);
    }

    public static boolean isBot(Entity entity) {
        return AntiBot.instance.isServerBot(entity);
    }

    private static void save(final String content) {
        try {
            final File f = new File(dir, "player.config");
            if (!f.exists()) {
                if (f.createNewFile()) {
                    System.out.println("e");
                }
            }
            new Thread(() -> {
                try (FileWriter writer = new FileWriter(f, false)) {
                    writer.write(content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> read() {
        final List<String> out = new ArrayList<>();
        try {
            if (!dir.exists()) {
                if (dir.mkdir()) {
                    System.out.println("e");
                }
            }
            final File f = new File(dir, "player.config");
            if (!f.exists()) {
                if (f.createNewFile()) {
                    System.out.println("e");
                }
            }
            try (FileInputStream fis = new FileInputStream(f)) {
                try (InputStreamReader isr = new InputStreamReader(fis)) {
                    try (BufferedReader br = new BufferedReader(isr)) {
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            out.add(line);
                        }
                    }
                }
                fis.close();
                return out;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }
}
