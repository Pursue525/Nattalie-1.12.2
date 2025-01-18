package net.pursue.utils.client;

import net.pursue.Nattalie;
import net.pursue.shield.IsShield;
import oshi.SystemInfo;
import oshi.hardware.Processor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@IsShield
public class HWIDManager {

    public HWIDManager() {
        getHwid();
    }

    public void getHwid() {
        String hwid;
        try {
            hwid = generateHardwareId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            boolean hwidExists = new BufferedReader(new InputStreamReader(new URL("https://raw.gitcode.com/2301_78767572/PursueHWID/raw/master/README.md").openStream()))
                    .lines()
                    .collect(Collectors.joining())
                    .contains(Nattalie.instance.getClientVersion());

            if (!hwidExists) {
                openWebsite("https://www.123684.com/s/TBusjv-CZq3d");
                showMessageDialogWithAlwaysOnTop(null, "您当前版本是旧版本", "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        if (checkKeyWithRemote(hwid)) {
            try {
                URL url = new URL("https://raw.gitcode.com/2301_78767572/PursueHWID/raw/master/11410583.txt");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                    String line;
                    List<String> fileContent = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        fileContent.add(line);
                    }

                    String[] previousCredentials = loadCredentials();
                    String username = getInput("请输入您的用户名", "用户名", previousCredentials[0]);
                    if (username == null) {
                        System.exit(0);
                    }

                    for (String lineContent : fileContent) {
                        String[] parts = lineContent.split("-");
                        if (parts.length == 3) {
                            String fileUsername = parts[0].trim();
                            String filePassword = parts[1].trim();
                            String fileHWID = parts[2].trim();

                            if (!fileUsername.equals(username)) continue;

                            String key = getPasswordInput(previousCredentials[1]);
                            if (key == null) {
                                System.exit(0);
                            }

                            if (filePassword.equals(key)) {
                                if (fileHWID.equals(hwid)) {
                                    Nattalie.instance.setUSERNAME(username);
                                    showMessageDialogWithAlwaysOnTop(null, "验证成功！" + username + " 您好！", "验证成功", JOptionPane.INFORMATION_MESSAGE);
                                    saveCredentials(username, key);

                                    return;
                                } else {
                                    showMessageDialogWithAlwaysOnTop(null, "我草拟码，你没创建账号，还登录别人账号？", "验证成功你码了隔壁", JOptionPane.INFORMATION_MESSAGE);
                                    System.exit(0);
                                }
                            } else {
                                showMessageDialogWithAlwaysOnTop(null, "密钥错误", "验证失败！", 0);
                                System.exit(0);
                            }
                        }
                    }

                    int confirmation = showMessageDialogWithAlwaysOnTop(null, "不存在该用户！是否创建？", "验证出错！", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

                    if (confirmation == JOptionPane.YES_OPTION) {
                        //createNewUserAndKey(hwid);
                    } else {
                        System.exit(0);
                    }

                } catch (IOException e) {
                    showMessageDialogWithAlwaysOnTop(null, "文件读取失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(hwid);
            StringSelection selection = new StringSelection(hwid);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            showMessageDialogWithAlwaysOnTop(null, "已复制您的HWID到剪贴板！\n" + "您的HWID: " + hwid,
                    "成功", JOptionPane.INFORMATION_MESSAGE);

            System.exit(-13);
        }
    }

    private String getPasswordInput(String defaultValue) {
        JPasswordField passwordField = new JPasswordField(20);
        if (defaultValue != null && !defaultValue.trim().isEmpty()) {
            passwordField.setText(defaultValue);
        }
        String key = null;
        while (key == null || key.trim().isEmpty()) {
            int option = showMessageDialogWithAlwaysOnTop(null, passwordField, "请输入您的密钥", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == JOptionPane.OK_OPTION) {
                char[] password = passwordField.getPassword();
                key = new String(password);
                if (key == null || key.trim().isEmpty()) {
                    showMessageDialogWithAlwaysOnTop(null, "密钥不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                return null;
            }
        }
        return key;
    }

    public static boolean NewUserEqualsOldUser(String userName) {
        try {
            URL url = new URL("https://raw.gitcode.com/2301_78767572/PursueHWID/raw/master/11410583.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                List<String> strings = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    strings.add(line);
                }

                for (String lineContent : strings) {
                    String[] parts = lineContent.split("-");
                    if (parts.length == 3) {
                        String name = parts[0].trim();

                        if (!name.equals(userName)) continue;

                        userName = null;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return userName == null;
    }

    public static void saveCredentials(String username, String key) {
        Properties properties = new Properties();
        properties.setProperty("Name", username);
        properties.setProperty("Key", key);

        try (FileOutputStream out = new FileOutputStream(Nattalie.instance.getCONFIG_FILE())) {
            properties.store(out, "User-Credentials");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String[] loadCredentials() {
        Properties properties = new Properties();
        String username = "";
        String key = "";

        try (FileInputStream in = new FileInputStream(Nattalie.instance.getCONFIG_FILE())) {
            properties.load(in);
            username = properties.getProperty("Name", "");
            key = properties.getProperty(Nattalie.instance.getKEY(), "");
        } catch (IOException e) {
        }

        return new String[]{username, key};
    }

    private static String getInput(String message, String title, String defaultValue) {
        String input = null;
        while (input == null || input.trim().isEmpty()) {
            input = (String) JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE, null, null, defaultValue);
            if (input == null || input.trim().isEmpty()) {
                showMessageDialogWithAlwaysOnTop(null, "输入不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        return input;
    }

    public static boolean checkKeyWithRemote(String key) {
        try {
            String url = "https://raw.gitcode.com/2301_78767572/PursueHWID/raw/master/hwid.txt";
            boolean hwidExists = new BufferedReader(new InputStreamReader(new URL(url).openStream()))
                    .lines()
                    .collect(Collectors.joining())
                    .contains(key);

            return hwidExists;

        } catch (Exception e) {
            showMessageDialogWithAlwaysOnTop(null, "连接HWID仓库失效，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        return false;
    }

    public static boolean checkHWIDWithRemote(String name, String key, String hwid) {
        try {
            URL url = new URL("https://raw.gitcode.com/2301_78767572/PursueHWID/raw/master/11410583.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                String line;
                List<String> fileContent = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    fileContent.add(line);
                }

                for (String lineContent : fileContent) {
                    String[] parts = lineContent.split("-");
                    if (parts.length == 3) {
                        String fileUsername = parts[0].trim();
                        String filePassword = parts[1].trim();
                        String fileHWID = parts[2].trim();

                        if (!fileUsername.equals(name)) continue;

                        if (filePassword.equals(key)) {
                            if (fileHWID.equals(hwid)) {
                                Nattalie.instance.setUSERNAME(name);
                                saveCredentials(name, key);

                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            } catch (IOException e) {
                System.exit(0);
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public static String generateHardwareId() throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        final SystemInfo systemInfo = new SystemInfo();
        final Processor[] processors = systemInfo.getHardware().getProcessors();
        final byte[] digest = messageDigest.digest(("x" + processors.length + "(" + processors[0].getName() + ":" + processors[0].getIdentifier() + ")").getBytes(StandardCharsets.UTF_8));
        final StringBuilder digestSB = new StringBuilder();

        for (byte b : digest) {
            final String hexString = Integer.toHexString(b & 0xFF);

            if (hexString.length() == 1) {
                digestSB.append('0').append(hexString);
            } else if (hexString.length() == 2) {
                digestSB.append(hexString);
            } else {
                throw new Exception("Byte toHexString return more than 2 characters? " + hexString + ":" + b);
            }
        }

        return digestSB.toString();
    }

    public static void showMessageDialogWithAlwaysOnTop(Component parentComponent, Object message, String title, int Type) {
        JOptionPane optionPane = new JOptionPane(message, Type);
        JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(null);
    }

    public static int showMessageDialogWithAlwaysOnTop(Component parentComponent, Object message, String title, int Type, int Type2) {
        JOptionPane optionPane = new JOptionPane(message, Type, Type2);
        JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(null);

        return (int) optionPane.getValue();
    }

    public static boolean isNewClient() {
        try {
            return new BufferedReader(new InputStreamReader(new URL("https://raw.gitcode.com/2301_78767572/PursueHWID/raw/master/README.md").openStream()))
                    .lines()
                    .collect(Collectors.joining())
                    .contains(Nattalie.instance.getClientVersion());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void openWebsite(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URI(url));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
