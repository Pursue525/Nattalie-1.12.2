package net.pursue.ui.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.config.ConfigManager;
import net.pursue.mode.client.ClickGUI;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.FontUtils;
import net.pursue.utils.MathUtils;
import net.pursue.utils.client.HWIDManager;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;

import java.awt.*;
import java.io.IOException;

public class OldClick extends GuiScreen {

    public static boolean mouse0;
    public static boolean mouse1;

    public boolean sb = true;
    public boolean legit = false;
    private float animY = this.height;
    private float animH = 0;
    private final String tile = TextFormatting.RED + "警告！" + TextFormatting.BLUE + "ClickGUI" + TextFormatting.WHITE + "的" + TextFormatting.YELLOW +"操作" + TextFormatting.WHITE +"可能对你来说很" + TextFormatting.RED + "困难！";
    private final String button1 = "我是" + TextFormatting.RED + "拉参大神！" + TextFormatting.WHITE + "，我知道我自己在做什么，别再提示了！";
    private final String button2 = "我还是会一点的！，请为我切换" + TextFormatting.GREEN + "中文界面" + TextFormatting.WHITE + "并打开ClickGUI";
    private final String button3 = "我需要调整简单功能，请为我打开" + TextFormatting.GREEN + "简洁版";
    private final String button5 = "我知道了！，关闭吧";

    @Override
    public void onGuiClosed() {
        animY = height;
        animH = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        animH = AnimationUtils.moveUD(animH, height, (float) (10 * RenderUtils.deltaTime()), (float) (7 * RenderUtils.deltaTime()));

        RoundedUtils.drawRoundBlur(0,0,width,animH,0, new Color(0,0,0,10),12);

        animY = AnimationUtils.moveUD(animY, 0, (float) (10 * RenderUtils.deltaTime()), (float) (7 * RenderUtils.deltaTime()));

        super.drawScreen(mouseX, mouseY, partialTicks);

        final FontUtils tileFont = FontManager.font32;
        final FontUtils buttonFont = FontManager.font18;

        float w = tileFont.getWidth(tile) + 10;
        float h = (tileFont.getHeight() + (buttonFont.getHeight() * 5)) + 34;
        float x = MathUtils.centre(width, w);
        float y = MathUtils.centre(height, h) + animY;

        float x2 = x + 5;
        float y2 = y + 5;

        int i = tileFont.getHeight() + 4;
        int i1 = buttonFont.getHeight() + 4;

        RoundedUtils.drawRound(x, y, w, h, 3, new Color(Color.DARK_GRAY.getRed(), Color.DARK_GRAY.getGreen(), Color.DARK_GRAY.getBlue(), 200));

        tileFont.drawString(tile, x2, y2, Color.WHITE);

        drawButton(buttonFont,button1, x2, y + 5 + i,mouseX, mouseY, () -> {
            HWIDManager.saveCredentials(Nattalie.USERNAME, Nattalie.KEY, ConfigManager.configName, false);
            sb = false;
            Nattalie.instance.openClickGUI();
        });

        drawButton(buttonFont,button2, x2, y + 5 + i + i1,mouseX, mouseY, () -> {
            ClickGUI.instance.chinese.setValue(true);
            Nattalie.instance.openClickGUI();
        });
        drawButton(buttonFont,button3, x2, y + 5 + i + i1 * 2,mouseX, mouseY, () -> {
            Nattalie.instance.openClickGUI();
            legit = true;
        });
        drawButton(buttonFont,button5, x2, y + 5 + i + i1 * 4,mouseX, mouseY, () -> mc.player.closeScreen());
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            mouse0 = true;
        } else if (mouseButton == 1) {
            mouse1 = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        mouse0 = false;
        mouse1 = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    private void drawButton(FontUtils buttonFont, String buttonText, float x, float y, int mouseX, int mouseY, Runnable onClickAction) {

        if (isHovering(x, y, buttonFont.getWidth(buttonText), buttonFont.getHeight(), mouseX, mouseY)) {


            RoundedUtils.drawRound(x - 2,y - 3, buttonFont.getWidth(buttonText) + 4, buttonFont.getHeight() + 1, 1, new Color(0,0,0,180));


            buttonFont.drawString("←--", x + 3 + buttonFont.getWidth(buttonText), y, Color.YELLOW);

            if (mouse0) {
                onClickAction.run();
                mouse0 = false;
            }
        }

        buttonFont.drawString(buttonText, x, y, Color.WHITE);
    }

}
