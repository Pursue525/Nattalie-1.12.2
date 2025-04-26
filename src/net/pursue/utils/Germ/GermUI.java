package net.pursue.utils.Germ;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.pursue.event.EventManager;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.FontUtils;
import net.pursue.utils.MathUtils;
import net.pursue.utils.render.RoundedUtils;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GermUI extends GuiScreen {
    private int gameInt = 0;
    private boolean mouse0;

    FontUtils arial32 = FontManager.font32;

    public GermUI() {
        gameInt = 0;
    }

    private static final List<List<String>> ItemPng = List.of(
            Arrays.asList("textures/items/sign.png", "textures/items/apple_golden.png", "textures/items/Iron_sword.png", "textures/items/book_writable.png")
    );


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int h = 0;
        int d = 0;

        RoundedUtils.drawRoundBlur(width / 2f - 200,  height / 2f - 100, 400, 200, 5, new Color(0,0,0,200), 12);

        FontManager.font24.drawString("花雨庭菜单v1.1", width / 2f - 198, height / 2f - 95, new Color(255,255,255).getRGB());
        RoundedUtils.drawRound(width / 2f - 200, height / 2f - 80,  400,1,  0, new Color(255,255,255));
        RoundedUtils.drawRound(width / 2f - 80, height / 2f - 77,  1,177,  0, new Color(255,255,255));

        drawMode(gameInt, mouseX, mouseY);

        String c;
        Color stringColor;

        for (GermType hyt : GermType.values()) {
            if (mouseX >= width / 2f - 185 + arial32.getHeight() && mouseX <= arial32.getStringWidth(hyt.getName()) + width / 2f - 185 + arial32.getHeight() && mouseY >= height / 2f - 60 + h && mouseY <= arial32.getHeight() + height / 2f - 60 + h) {
                switch (hyt.getName().toUpperCase()) {
                    case "起床战争":
                        c = "花雨庭力量、瞬移狗战争";

                        RoundedUtils.drawRound((mouseX + 5), (mouseY + 5), FontManager.font20.getStringWidth(c) + 4, FontManager.font20.getHeight() + 2, 1, new Color(0, 21, 255, 200));
                        FontManager.font20.drawString(c, (mouseX + 5) + MathUtils.centre(FontManager.font20.getStringWidth(c) + 4, FontManager.font20.getStringWidth(c)), (mouseY + 5) + MathUtils.centre(FontManager.font20.getHeight() + 2, FontManager.font20.getHeight()) + 2, Color.WHITE);
                        if (org.lwjgl.input.Mouse.isButtonDown(0)) {
                            gameInt = 1;
                        }
                        break;
                    case "空岛战争":
                        c = "花雨庭GApple战争";

                        RoundedUtils.drawRound((mouseX + 5), (mouseY + 5), FontManager.font20.getStringWidth(c) + 4, FontManager.font20.getHeight() + 2, 1, new Color(0, 21, 255, 200));
                        FontManager.font20.drawString(c, (mouseX + 5) + MathUtils.centre(FontManager.font20.getStringWidth(c) + 4, FontManager.font20.getStringWidth(c)), (mouseY + 5) + MathUtils.centre(FontManager.font20.getHeight() + 2, FontManager.font20.getHeight()) + 2, Color.WHITE);
                        if (org.lwjgl.input.Mouse.isButtonDown(0)) {
                            gameInt = 2;
                        }
                        break;
                    case "竞技游戏":
                        c = "花雨庭对刀模式";

                        RoundedUtils.drawRound((mouseX + 5), (mouseY + 5), FontManager.font20.getStringWidth(c) + 4, FontManager.font20.getHeight() + 2, 1, new Color(0, 21, 255, 200));
                        FontManager.font20.drawString(c, (mouseX + 5) + MathUtils.centre(FontManager.font20.getStringWidth(c) + 4, FontManager.font20.getStringWidth(c)), (mouseY + 5) + MathUtils.centre(FontManager.font20.getHeight() + 2, FontManager.font20.getHeight()) + 2, Color.WHITE);
                        if (org.lwjgl.input.Mouse.isButtonDown(0)) {
                            gameInt = 3;
                        }
                        break;
                    case "其他游戏":
                        c = "花雨庭刷经验模式";

                        RoundedUtils.drawRound((mouseX + 5), (mouseY + 5), FontManager.font20.getStringWidth(c) + 4, FontManager.font20.getHeight() + 2, 1, new Color(0, 21, 255, 200));
                        FontManager.font20.drawString(c, (mouseX + 5) + MathUtils.centre(FontManager.font20.getStringWidth(c) + 4, FontManager.font20.getStringWidth(c)), (mouseY + 5) + MathUtils.centre(FontManager.font20.getHeight() + 2, FontManager.font20.getHeight()) + 2, Color.WHITE);
                        if (org.lwjgl.input.Mouse.isButtonDown(0)) {
                            gameInt = 4;
                        }
                        break;
                }
            }

            stringColor = hyt.getI() == gameInt ? Color.YELLOW : Color.WHITE;

            arial32.drawString(hyt.getName(), width / 2f - 185 + arial32.getHeight(), height / 2f - 60 + h, stringColor);

            h += arial32.getHeight() + 17;
        }

        for (List<String> pg : ItemPng) {
            for (String s : pg) {
                RoundedUtils.drawImage(width / 2 - 190, height / 2 - 60 + d, arial32.getHeight(), arial32.getHeight(), new ResourceLocation(s));
                d += arial32.getHeight() + 16;
            }
        }

    }

    private void game(String name, String name2, int mouseX, int mouseY, float x, float y, String id, int num) {
        RoundedUtils.drawRound(x, y - 3,  arial32.getStringWidth(name), arial32.getHeight(),  5, new Color(50,50,50, 255));
        arial32.drawString(mouseX >= x && mouseX <= arial32.getStringWidth(name) + x && mouseY >= y && mouseY <= arial32.getHeight() + y ? name2 : name, x, y, new Color(255,255,255).getRGB());
        if (mouse0 && mouseX >= x && mouseX <= arial32.getStringWidth(name) + x && mouseY >= y && mouseY <= arial32.getHeight() + y) {
            GermManager.sendJoin(num, id);
            mouse0 = false;
        }
    }

    private void drawMode(int gameInt, int mouseX, int mouseY) {

        switch (gameInt) {
            case 1 -> {
                game("     起床1v1      ","   >>起床1v1<<   ", mouseX, mouseY, width / 2f - 65, height / 2f - 60, "BEDWAR/bw-solo", 9);
                game("     起床2v2     ", "   >>起床2v2<<   ", mouseX, mouseY, width / 2f - 65, height / 2f - 60 + arial32.getHeight() + 10, "BEDWAR/bw-double", 10);
                game("     起床4v4     ", "   >>起床4v4<<   ", mouseX, mouseY, width / 2f - 65, height / 2f - 60 + arial32.getHeight() * 2 + 20, "BEDWAR/bw-team", 11);
                game("    起床16v16    ", "  >>起床16v16<<  ", mouseX, mouseY, width / 2f - 65, height / 2f - 60 + arial32.getHeight() * 3 + 30, "BEDWAR/bwxp16new", 12);
                game("    起床32v32    ", "  >>起床32v32<<  ", mouseX, mouseY, width / 2f - 65, height / 2f - 60 + arial32.getHeight() * 4 + 40, "BEDWAR/bwxp-32", 13);

                game("     枪械起床     ", "   >>枪械起床<<   ", mouseX, mouseY, width / 2f + 60, height / 2f - 60, "TEAM_FIGHT/csbwxp-32", 25);
                game("     职业起床     ", "   >>职业起床<<   ", mouseX, mouseY, width / 2f + 60, height / 2f - 60 + arial32.getHeight() + 10, "TEAM_FIGHT/bwkitxp-32", 26);
            }
            case 2 -> {
                game("     空岛单人     ", "   >>空岛单人<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60, "SKYWAR/nskywar", 6);
                game("     空岛双人     ", "   >>空岛双人<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() + 10, "SKYWAR/nskywar-double", 7);

            }
            case 3 -> {
                game("     职业战争     ", "   >>职业战争<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60, "FIGHT/kb-game", 22);
                game("     天坑乱斗     ", "   >>天坑乱斗<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() + 10, "FIGHT/the-pit", 24);
                game("     超级战墙     ", "   >>超级战墙<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() * 2 + 20, "TEAM_FIGHT/battlewalls", 28);
            }
            case 4 -> {
                game("     Scaffold测试     ", "   >>Scaffold测试<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60, "BEDWAR/bw-dalu", 8);
                game("      小游戏派对      ", "    >>小游戏派对<<    ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() + 10, "LEISURE/mg-game", 15);
                game("       烫手山芋        ", "     >>烫手山芋<<     ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() * 2 + 20, "LEISURE/hp-game", 18);
                game("        狼入杀        ", "      >>狼入杀<<      ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() * 3 + 30, "LEISURE/ww-game", 19);
            }

            default -> {
                //
            }
        }
    }

    @Override
    public void onGuiClosed() {
        EventManager.instance.unregister(this);
        super.onGuiClosed();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            mouse0 = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        mouse0 = false;

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
