package net.pursue.utils.Germ;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.pursue.Nattalie;
import net.pursue.event.EventManager;
import net.pursue.event.EventTarget;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.RapeMasterFontManager;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GermUI extends GuiScreen {
    private boolean game_1;
    private boolean game_2;
    private boolean game_3;
    private boolean game_4;

    private boolean mouse0;
    private boolean mouse1;

    RapeMasterFontManager arial32 = FontManager.font32;

    public GermUI() {
        EventManager.instance.register(this);
    }

    private static final List<List<String>> HuangYuTing = List.of(
            Arrays.asList("起床战争", "空岛生存", "竞技游戏", "其他游戏")
    );

    private static final List<List<String>> ItemPng = List.of(
            Arrays.asList("textures/items/sign.png", "textures/items/apple_golden.png", "textures/items/Iron_sword.png", "textures/items/book_writable.png")
    );

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int h = 0;
        int d = 0;

        RoundedUtils.drawRound(width / 2f - 200,  height / 2f - 100, 400, 200, 5, new Color(0,0,0,255));
        FontManager.font24.drawString("GermUI", width / 2f - 198, height / 2f - 95, new Color(255,255,255).getRGB());
        RoundedUtils.drawRound(width / 2f - 200, height / 2f - 80,  400,1,  0, new Color(255,255,255));
        RoundedUtils.drawRound(width / 2f - 80, height / 2f - 77,  1,177,  0, new Color(255,255,255));

        game("  难看? 点我反馈  ", "  难看? >>点我反馈<<  ", mouseX, mouseY, width / 2f - 120, height / 2f + 87);

        if (game_1) {
            game("     起床1v1      ","   >>起床1v1<<   ", mouseX, mouseY, width / 2f - 65, height / 2f - 60, "BEDWAR/bw-solo", 9);
            game("     起床2v2     ", "   >>起床2v2<<   ", mouseX, mouseY, width / 2f - 65, height / 2f - 60 + arial32.getHeight() + 10, "BEDWAR/bw-double", 10);
            game("     起床4v4     ", "   >>起床4v4<<   ", mouseX, mouseY, width / 2f - 65, height / 2f - 60 + arial32.getHeight() * 2 + 20, "BEDWAR/bw-team", 11);
            game("    起床16v16    ", "  >>起床16v16<<  ", mouseX, mouseY, width / 2f - 65, height / 2f - 60 + arial32.getHeight() * 3 + 30, "BEDWAR/bwxp16new", 12);
            game("    起床32v32    ", "  >>起床32v32<<  ", mouseX, mouseY, width / 2f - 65, height / 2f - 60 + arial32.getHeight() * 4 + 40, "BEDWAR/bwxp-32", 13);

            game("     枪械起床     ", "   >>枪械起床<<   ", mouseX, mouseY, width / 2f + 60, height / 2f - 60, "TEAM_FIGHT/csbwxp-32", 25);
            game("     职业起床     ", "   >>职业起床<<   ", mouseX, mouseY, width / 2f + 60, height / 2f - 60 + arial32.getHeight() + 10, "TEAM_FIGHT/bwkitxp-32", 26);
        } else if (game_2) {
            game("     空岛单人     ", "   >>空岛单人<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60, "SKYWAR/nskywar", 6);
            game("     空岛双人     ", "   >>空岛双人<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() + 10, "SKYWAR/nskywar-double", 7);
        } else if (game_3) {
            game("     职业战争     ", "   >>职业战争<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60, "FIGHT/kb-game", 22);
            game("     天坑乱斗     ", "   >>天坑乱斗<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() + 10, "FIGHT/the-pit", 24);
            game("     超级战墙     ", "   >>超级战墙<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() * 2 + 20, "TEAM_FIGHT/battlewalls", 28);
        } else if (game_4) {
            game("     Scaffold测试     ", "   >>Scaffold测试<<   ", mouseX, mouseY, width / 2f + 20, height / 2f - 60, "BEDWAR/bw-dalu", 8);
            game("      小游戏派对      ", "    >>小游戏派对<<    ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() + 10, "LEISURE/mg-game", 15);
            game("       烫手山芋        ", "     >>烫手山芋<<     ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() * 2 + 20, "LEISURE/hp-game", 18);
            game("        狼入杀        ", "      >>狼入杀<<      ", mouseX, mouseY, width / 2f + 20, height / 2f - 60 + arial32.getHeight() * 3 + 30, "LEISURE/ww-game", 19);
        }

        for (List<String> hyt : HuangYuTing) {
            for (String s : hyt) {
                if (mouseX >= width / 2f - 185 + arial32.getHeight() && mouseX <= arial32.getStringWidth(s) + width / 2f - 185 + arial32.getHeight() && mouseY >= height / 2f - 60 + h && mouseY <= arial32.getHeight() + height / 2f - 60 + h) {
                    switch (s.toUpperCase()) {
                        case "起床战争":
                            RoundedUtils.drawRound(width / 2f - 92, height / 2f - 63 + h, FontManager.font20.getStringWidth("请保护好你的床!!!, 你是不是以为你开了挂就天下无敌了?"), FontManager.font20.getHeight(), 5, new Color(100, 100, 100, 255));
                            FontManager.font20.drawString("请保护好你的床!!!, 你是不是以为你开了挂就天下无敌了?", width / 2f - 92, height / 2f - 60 + h, new Color(255, 255, 255).getRGB());
                            if (org.lwjgl.input.Mouse.isButtonDown(0)) {
                                game_1 = true;
                                game_2 = false;
                                game_3 = false;
                                game_4 = false;
                            }
                            break;
                        case "空岛生存":
                            RoundedUtils.drawRound(width / 2f - 92,  height / 2f - 63 + h, FontManager.font20.getStringWidth("抢资源啊，没资源咋对刀啊"), FontManager.font20.getHeight(), 5, new Color(100,100,100,255));
                            FontManager.font20.drawString("抢资源啊，没资源咋对刀啊", width / 2f - 92, height / 2f - 60 + h, new Color(255,255,255).getRGB());
                            if (org.lwjgl.input.Mouse.isButtonDown(0)) {
                                game_1 = false;
                                game_2 = true;
                                game_3 = false;
                                game_4 = false;
                            }
                            break;
                        case "竞技游戏":
                            RoundedUtils.drawRound(width / 2f - 92,  height / 2f - 63 + h, FontManager.font20.getStringWidth("hvh对刀游戏，配置稳定你就来"), FontManager.font20.getHeight(), 5, new Color(100,100,100,255));
                            FontManager.font20.drawString("hvh对刀游戏，配置稳定你就来", width / 2f - 92, height / 2f - 60 + h, new Color(255,255,255).getRGB());
                            if (org.lwjgl.input.Mouse.isButtonDown(0)) {
                                game_1 = false;
                                game_2 = false;
                                game_3 = true;
                                game_4 = false;
                            }
                            break;
                        case "其他游戏":
                            RoundedUtils.drawRound(width / 2f - 92,  height / 2f - 63 + h, FontManager.font20.getStringWidth("开挂久了玩玩小游戏怎么了"), FontManager.font20.getHeight(), 5, new Color(100,100,100,255));
                            FontManager.font20.drawString("开挂久了玩玩小游戏怎么了", width / 2f - 92, height / 2f - 60 + h, new Color(255,255,255).getRGB());
                            if (org.lwjgl.input.Mouse.isButtonDown(0)) {
                                game_1 = false;
                                game_2 = false;
                                game_3 = false;
                                game_4 = true;
                            }
                            break;
                    }
                }

                arial32.drawString(s, width / 2f - 185 + arial32.getHeight(), height / 2f - 60 + h, new Color(255, 255, 255).getRGB());

                h += arial32.getHeight() + 17;
            }
        }

        for (List<String> pg : ItemPng) {
            for (String s : pg) {
                RenderUtils.drawImage(width / 2 - 190, height / 2 - 60 + d, arial32.getHeight(), arial32.getHeight(), new ResourceLocation(s));
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
    private void game(String name, String name2, int mouseX, int mouseY, float x, float y) {
        RoundedUtils.drawRound(x, y - 3, mouseX >= x && mouseX <= FontManager.font20.getStringWidth(name) + x && mouseY >= y && mouseY <= FontManager.font20.getHeight() + y ? FontManager.font20.getStringWidth(name2) : FontManager.font20.getStringWidth(name), FontManager.font20.getHeight(), 5, new Color(0,0,0, 255));
        FontManager.font20.drawString(mouseX >= x && mouseX <= FontManager.font20.getStringWidth(name) + x && mouseY >= y && mouseY <= FontManager.font20.getHeight() + y ? name2 : name, x, y, new Color(255,255,255).getRGB());
        if (mouse0 && mouseX >= x && mouseX <= FontManager.font20.getStringWidth(name) + x && mouseY >= y && mouseY <= FontManager.font20.getHeight() + y) {
            JOptionPane.showMessageDialog(null, "难看个蛋，不喜欢别玩", "滚滚滚", JOptionPane.INFORMATION_MESSAGE);
            mc.shutdown();
            System.exit(114514);
            mouse0 = false;
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

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
