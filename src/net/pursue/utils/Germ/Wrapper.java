package net.pursue.utils.Germ;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Wrapper {
    private static Minecraft mc = Wrapper.getMinecraft();
    public static final Logger logger = LogManager.getLogger(Wrapper.class);

    public static void runOnMainThread(Runnable var0) {
        Wrapper.getMinecraft().addScheduledTask(var0);
    }

    public static int getRenderDistance() {
        return Wrapper.getMinecraft().gameSettings.renderDistanceChunks;
    }

    public static File getFile(String ... var0) {
        return new File(Wrapper.getMcDataDir(), Arrays.stream(var0).map(var0x -> new StringBuilder().insert(0, "/").append((String)var0x).toString()).collect(Collectors.joining()));
    }

    public static Minecraft getMinecraft() {
        if (mc == null) {
            mc = Minecraft.getMinecraft();
        }
        return mc;
    }

    public static World getWorld() {
        return Wrapper.getMinecraft().world;
    }

    public static Entity getPlayer() {
        return Wrapper.getMinecraft().getRenderViewEntity();
    }

    public static File getMcDataDir() {
        return Wrapper.getMinecraft().mcDataDir;
    }

}
