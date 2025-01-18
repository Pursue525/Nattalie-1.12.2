package net.pursue.ui.font;


import net.pursue.Nattalie;

import java.awt.*;
import java.io.InputStream;

public class FontManager {
    public static RapeMasterFontManager font10;

    public static RapeMasterFontManager font12;
    public static RapeMasterFontManager font13;
    public static RapeMasterFontManager font14;
    public static RapeMasterFontManager font15;

    public static RapeMasterFontManager arial32;

    public static RapeMasterFontManager font16;
    public static RapeMasterFontManager font18;
    public static RapeMasterFontManager font19;
    public static RapeMasterFontManager font20;
    public static RapeMasterFontManager font22;
    public static RapeMasterFontManager font24;
    public static RapeMasterFontManager font26;

    public static RapeMasterFontManager font28;
    public static RapeMasterFontManager font32;
    public static RapeMasterFontManager font34;
    public static RapeMasterFontManager font40;
    public static RapeMasterFontManager font42;
    public static RapeMasterFontManager font64;


    public static RapeMasterFontManager Noti42;

    public static void init() {
        font10 = new RapeMasterFontManager(getFont("font.ttf", 10));


        font12 = new RapeMasterFontManager(getFont("font.ttf", 12));
        font13 = new RapeMasterFontManager(getFont("font.ttf", 13));
        font14 = new RapeMasterFontManager(getFont("font.ttf", 14));
        font15 = new RapeMasterFontManager(getFont("font.ttf", 15));

        font16 = new RapeMasterFontManager(getFont("font.ttf", 16));
        font18 = new RapeMasterFontManager(getFont("font.ttf", 18));
        font19 = new RapeMasterFontManager(getFont("font.ttf", 19));
        font20 = new RapeMasterFontManager(getFont("font.ttf", 20));
        font22 = new RapeMasterFontManager(getFont("font.ttf", 22));
        font24 = new RapeMasterFontManager(getFont("font.ttf", 24));
        font26 = new RapeMasterFontManager(getFont("font.ttf", 26));
        font28 = new RapeMasterFontManager(getFont("font.ttf", 28));
        font32 = new RapeMasterFontManager(getFont("font.ttf", 32));
        font34 = new RapeMasterFontManager(getFont("font.ttf", 34));

        font40 = new RapeMasterFontManager(getFont("font.ttf", 40));
        font42 = new RapeMasterFontManager(getFont("font.ttf", 42));
        font64 = new RapeMasterFontManager(getFont("font.ttf", 64));

        Noti42 = new RapeMasterFontManager(getFont("icon-noti.ttf", 42));
    }

    public static Font getFont(String fontName, float fontSize) {
        Font font = null;
        try {
            InputStream inputStream = Nattalie.class.getResourceAsStream("/assets/minecraft/nattalie/fonts/" + fontName);
            assert (inputStream != null);
            font = Font.createFont(0, inputStream);
            font = font.deriveFont(fontSize);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return font;
    }
}
