package net.pursue.ui.font;


import net.pursue.Nattalie;

import java.awt.*;
import java.io.InputStream;

public class FontManager {
    public static FontUtils font10;

    public static FontUtils font12;
    public static FontUtils font13;
    public static FontUtils font14;
    public static FontUtils font15;

    public static FontUtils font16;
    public static FontUtils font18;
    public static FontUtils font19;
    public static FontUtils font20;
    public static FontUtils font22;
    public static FontUtils font24;
    public static FontUtils font26;

    public static FontUtils font28;
    public static FontUtils font32;
    public static FontUtils font36;
    public static FontUtils font34;
    public static FontUtils font40;
    public static FontUtils font42;
    public static FontUtils font64;


    public static FontUtils Noti42;

    public static void init() {
        font10 = new FontUtils(getFont("font.ttf", 10));


        font12 = new FontUtils(getFont("font.ttf", 12));
        font13 = new FontUtils(getFont("font.ttf", 13));
        font14 = new FontUtils(getFont("font.ttf", 14));
        font15 = new FontUtils(getFont("font.ttf", 15));

        font16 = new FontUtils(getFont("font.ttf", 16));
        font18 = new FontUtils(getFont("font.ttf", 18));
        font19 = new FontUtils(getFont("font.ttf", 19));
        font20 = new FontUtils(getFont("font.ttf", 20));
        font22 = new FontUtils(getFont("font.ttf", 22));
        font24 = new FontUtils(getFont("font.ttf", 24));
        font26 = new FontUtils(getFont("font.ttf", 26));
        font28 = new FontUtils(getFont("font.ttf", 28));
        font32 = new FontUtils(getFont("font.ttf", 32));
        font36 = new FontUtils(getFont("font.ttf", 36));
        font34 = new FontUtils(getFont("font.ttf", 34));

        font40 = new FontUtils(getFont("font.ttf", 40));
        font42 = new FontUtils(getFont("font.ttf", 42));
        font64 = new FontUtils(getFont("font.ttf", 64));

        Noti42 = new FontUtils(getFont("icon-noti.ttf", 42));
    }

    public static Font getFont(String fontName, float fontSize) {
        Font font = null;
        try {
            InputStream inputStream = Nattalie.class.getResourceAsStream("/assets/minecraft/nattalie/fonts/" + fontName);
            assert (inputStream != null);
            font = Font.createFont(0, inputStream);
            font = font.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return font;
    }
}
