package net.pursue.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class ServerUtils {
	public static String getIp() {
		String serverIp = "Singleplayer";
		if (Minecraft.getMinecraft().world.isRemote) {
			ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
			if (serverData != null) {
				serverIp = serverData.serverIP;
			}
		}
		return serverIp;
	}

	public static int getPing() {
		if (Minecraft.getMinecraft().isSingleplayer()) {
			return 0;
		} else {
			if (Minecraft.getMinecraft().getCurrentServerData() != null) {
				return (int) Minecraft.getMinecraft().getCurrentServerData().pingToServer;
			} else {
				return -1;
			}
		}
	}
}
