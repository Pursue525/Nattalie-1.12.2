package net.pursue.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.awt.event.ActionListener;

public class DebugHelper {
    public static void sendMessage(Object message) {
        String chatPrefix = "\2477[" + TextFormatting.AQUA + TextFormatting.BOLD + "Nattalie" + TextFormatting.RESET + "\2477] " + TextFormatting.RESET;
        Minecraft.getMinecraft().player.addChatMessage(new TextComponentString(chatPrefix + message));
    }

    public static void displayTray(String Title, String Text, TrayIcon.MessageType type) {
        if (!SystemTray.isSupported()) {
            System.out.println("This platform does not support System Tray.");
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        java.awt.Image image = toolkit.createImage("icon.png");
        if (image == null) {
            System.out.println("Failed to load tray icon image.");
            return;
        }
        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("System Tray Icon Demo");

        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("Tray icon clicked!");
            }
        });

        try {
            tray.add(trayIcon);
            trayIcon.displayMessage(Title, Text, type);
        } catch (java.awt.AWTException e) {
            System.out.println("An error occurred while adding the tray icon.");
            e.printStackTrace();
        }
    }
}
