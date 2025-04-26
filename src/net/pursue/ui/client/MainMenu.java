package net.pursue.ui.client;

import de.florianmichael.viamcp.gui.GuiProtocolSelector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.pursue.Nattalie;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.gui.Click;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RoundedUtils;
import optifine.Reflector;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainMenu extends GuiScreen {
    private java.util.List<String> videoList;
    private double animationX = width - 2;

    private int wheel;
    private float wheelAnim;

    private boolean click = false;

    private float[] floats = new float[] {0,0};

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, 40, 110, 150, 20, I18n.format("menu.singleplayer"), true));
        this.buttonList.add(new GuiButton(2, 40, 140, 150, 20, I18n.format("menu.multiplayer"), true));
        this.buttonList.add(new GuiButton(0, 40, 170, 150, 20, I18n.format("menu.options"), true));
        this.buttonList.add(new GuiButton(4, 40, 200, 150, 20, I18n.format("menu.quit"), true));
        this.buttonList.add(new GuiButton(70, 40, 260, 150, 20, "ClickGUI", true));
        this.buttonList.add(new GuiButton(69,40,230,  150, 20, "Version", true));
        animationX = width - 2;
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        videoList = List.of(new File(Minecraft.getMinecraft().mcDataDir, "Nattalie/Video").list());

        try {
            Nattalie.instance.getPlayer().render(0, 0, width, height);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }

        this.drawCenteredString(mc.fontRendererObj, String.valueOf(Nattalie.instance.getPlayer().count),2 + mc.fontRendererObj.getStringWidth(String.valueOf(Nattalie.instance.getPlayer().count)) / 2, height - 10, Color.WHITE.getRGB());

        if (isHovering(0, 0, width / 3f, height, mouseX, mouseY)) {
            super.drawScreen(mouseX, mouseY, partialTicks);
        } else {
            if (!this.buttonList.isEmpty()) for (GuiButton button : this.buttonList) {
                button.animationX = 0;
            }
        }

        if (isHovering(width - 200, height / 3f, width, 200, mouseX, mouseY)) {
            animationX = AnimationUtils.smooth(width - 200, animationX, 8f / Minecraft.getDebugFPS());
        } else {
            animationX = AnimationUtils.smooth(width - 2, animationX, 8f / Minecraft.getDebugFPS());
            wheel = 0;
            wheelAnim = 0;
        }

        RoundedUtils.drawRound((float) animationX, height / 3f, 150, 200, 1, new Color(255,255,255,180));

        float x = (float) animationX;
        float y = height / 3f;

        FontManager.font20.drawString("点击下方按钮切换背景", x + 5, y + 5, Color.BLACK.getRGB());

        floats = RoundedUtils.drawRound_Rectangle(FontManager.font20,"打开文件夹",x + (75 - floats[0] / 2), y + 175, 0, Color.WHITE, Color.BLACK, 6,4,true);

        if (isHovering(x + (75 - RoundedUtils.width / 2), y + 175, RoundedUtils.width, RoundedUtils.height, mouseX, mouseY)) {
            if (click) {
                File folder = new File(Minecraft.getMinecraft().mcDataDir, "Nattalie/Video");
                if (folder.exists() && folder.isDirectory()) {
                    try {
                        Desktop.getDesktop().open(folder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                click = false;
            }
        }

        RoundedUtils.enableRoundNoRender(x + 5, y + 7 + FontManager.font20.getHeight(), 145, 165, 0);

        {
            int j = 0;

            float x2 = x + 5;
            float y2 = y + FontManager.font20.getHeight() + 7 + wheelAnim;

            for (String string : videoList) {
                String name = string.substring(0, string.length() - 4);
                String txt = getFileExtension(string);

                RoundedUtils.drawRound(x2 + 5, y2 + j, 130, FontManager.font20.getHeight() + 4, 2, new Color(0,0,0,180));
                FontManager.font20.drawString(name, (x2 + 7) + ((130 / 2f) - (FontManager.font20.getWidth(name) / 2f)), y2 + j + 5, Color.WHITE.getRGB());

                if (isHovering(x2 + 5, y2 + j, 130, FontManager.font20.getHeight() + 4, mouseX, mouseY)) {
                    if (click) {
                        try {
                            Nattalie.instance.getPlayer().stop();
                            Nattalie.instance.getPlayer().init(new File(new File(Minecraft.getMinecraft().mcDataDir,"Nattalie/Video"), name + ".mp4"), name);
                        } catch (FFmpegFrameGrabber.Exception e) {
                            throw new RuntimeException(e);
                        }
                        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        click = false;
                    }
                }
                Color color = switch (txt) {
                    case "mp3" -> Color.GREEN;
                    case "mp4" -> Color.ORANGE;
                    default -> Color.RED;
                };

                RoundedUtils.drawRound_Rectangle(FontManager.font18, txt, x2 + 5, y2 + j - 1,1, Color.BLACK, color, 0,0,true);

                j += FontManager.font20.getHeight() + 10;
            }

            int real = Mouse.getDWheel();

            if (isHovering(x2, y + 7, 145, 165, mouseX, mouseY)) {
                if (real > 0 && wheel < 0) {
                    for (int i = 0; i < 5; i++) {
                        if (!(wheel < 0))
                            break;
                        wheel += FontManager.font20.getHeight();
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        if (!(real < 0 && wheel + j > 165))
                            break;
                        wheel -= FontManager.font20.getHeight();

                    }
                }
            }

            wheelAnim = AnimationUtils.moveUD(wheelAnim, wheel, (float) (10 * RoundedUtils.deltaTime()), (float) (7 * RoundedUtils.deltaTime()));
        }
        RoundedUtils.disableRoundNoRender();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (button.id == 5)
        {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(new GuiWorldSelection(this));
        }

        if (button.id == 2)
        {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (button.id == 69)
        {
            this.mc.displayGuiScreen(new GuiProtocolSelector(this));
        }

        if (button.id == 70)
        {
            this.mc.displayGuiScreen(new Click());
        }

        if (button.id == 4)
        {
            this.mc.shutdown();
        }

        if (button.id == 6 && Reflector.GuiModList_Constructor.exists())
        {
            this.mc.displayGuiScreen((GuiScreen)Reflector.newInstance(Reflector.GuiModList_Constructor, this));
        }

        if (button.id == 11)
        {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", WorldServerDemo.DEMO_WORLD_SETTINGS);
        }

        if (button.id == 12)
        {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

            if (worldinfo != null)
            {
                this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("selectWorld.deleteQuestion"), "'" + worldinfo.getWorldName() + "' " + I18n.format("selectWorld.deleteWarning"), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel"), 12));
            }
        }
        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            click = true;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        click = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    public static String getFileExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index > 0) {
            return filename.substring(index + 1);
        } else {
            return "???";
        }
    }
}

