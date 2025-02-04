package net.pursue.ui.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.config.ConfigManager;
import net.pursue.event.EventManager;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.render.RoundedUtils;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Config extends GuiScreen {

    private final List<String> config = List.of(ConfigManager.getList());
    private GuiTextField textField;

    @Override
    public void initGui() {
        int j = 0;
        String name;
        if (!config.isEmpty()) {
            for (String string : config) {
                name = string.substring(0, string.length() - 5);

                String[] parts = name.split("-");

                if (parts[1].equals("HUD")) continue;

                buttonList.add(new GuiButton(string.hashCode(), width / 2 - 90, height / 2 - 150 + j, 100, 20, parts[0]));
                j += 30;
            }
        }

        textField = new GuiTextField(0, fontRendererObj, width / 2 + 30, height / 2 - 150, 100, 20);
        textField.setMaxStringLength(100);
        textField.setText(ConfigManager.configName);

        buttonList.add(new GuiButton(114514, width / 2 + 30, height / 2 - 120, 100, 20, "保存配置"));
        buttonList.add(new GuiButton(114511, width / 2 + 30, height / 2 - 90, 100, 20, "删除配置"));

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float x = width / 2f;
        float y = height / 2f;
        RoundedUtils.drawRound(x - 100,  y - 200, 240, 310, 5, new Color(189, 189, 189, 255));
        RoundedUtils.drawRound(x - 95,  y - 160, 110, 250, 5, new Color(0, 0, 0, 120));

        if (isHovered(width / 2 + 30, height / 2 - 120, 100, 20, mouseX, mouseY)) {
            RoundedUtils.drawRound_Rectangle(FontManager.font20, "保存上面框内的配置，不输入则默认保存当前配置", width / 2f + 30, height / 2f - 50, 0, new Color(255,255,255), new Color(0,0,0,255), 0, 0, true);
        } else if (isHovered(width / 2 + 30, height / 2 - 90, 100, 20, mouseX, mouseY)) {
            RoundedUtils.drawRound_Rectangle(FontManager.font20, "删除上面框内的配置，不输入则默认删除当前配置", width / 2f + 30, height / 2f - 50, 0, new Color(255,255,255), new Color(0,0,0,255), 0, 0, true);
        }

        FontManager.font40.drawString("ConfigGUI", x - 90, y - 195,new Color(0,0,0).getRGB());

        super.drawScreen(mouseX,mouseY,partialTicks);

        textField.drawTextBox();
    }

    @Override
    public void onGuiClosed() {
        EventManager.instance.unregister(this);
        super.onGuiClosed();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        textField.textboxKeyTyped(typedChar, keyCode);
    }


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        String userInput = textField.getText();
        for (String string : config) {
            if (button.id == string.hashCode()) {
                String[] parts = string.split("-");

                ConfigManager.load(parts[0]);
                DebugHelper.sendMessage("Config","配置 " + TextFormatting.YELLOW + parts[0] + TextFormatting.WHITE + " 登录成功");
                mc.displayGuiScreen(this);
            }
        }

        if (button.id == 114514) {
            ConfigManager.save(userInput);
            DebugHelper.sendMessage("Config","配置 " + TextFormatting.YELLOW + userInput + TextFormatting.WHITE + " 保存成功");
            mc.displayGuiScreen(this);
        }
        if (button.id == 114511) {
            ConfigManager.delete(userInput);
            mc.displayGuiScreen(this);
        }
        super.actionPerformed(button);
    }

    public boolean isHovered(int x, int y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
