package net.pursue.ui.client;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.pursue.Nattalie;
import optifine.Reflector;

import java.awt.*;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class MainMenu extends GuiScreen {

    float currentX = 0f;
    float currentY = 0f;

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, 40, 110, 150, 20, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, 40, 140, 150, 20, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(0, 40, 170, 150, 20, I18n.format("menu.options")));
        this.buttonList.add(new GuiButton(4, 40, 200, 150, 20, I18n.format("menu.quit")));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        float xDiff = ((mouseX - (this.height / 2.0f)) - this.currentX) / new ScaledResolution(this.mc).getScaleFactor();
        float yDiff = ((mouseY - (this.width / 2.0f)) - this.currentY) / new ScaledResolution(this.mc).getScaleFactor();

        this.currentX += xDiff * 0.3f;
        this.currentY += yDiff * 0.3f;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(new ResourceLocation("nattalie/png/back.png"));
        GlStateManager.translate(this.currentX / 30.0f, this.currentY / 15.0f, 0.0f);
        this.drawModalRectWithCustomSizedTexture(-30,-30,0,0, width + 60, height + 60, width + 60, height + 60);
        GlStateManager.translate(-this.currentX / 30.0f, -this.currentY / 15.0f, 0.0f);
        this.drawCenteredString(mc.fontRendererObj, Nattalie.instance.getClientName() + "-1.12.2 Client",2 + mc.fontRendererObj.getStringWidth(Nattalie.instance.getClientName() + "-1.12.2Client") / 2, height - 10, Color.WHITE.getRGB());
        GlStateManager.disableBlend();
        super.drawScreen(mouseX, mouseY, partialTicks);
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
}
