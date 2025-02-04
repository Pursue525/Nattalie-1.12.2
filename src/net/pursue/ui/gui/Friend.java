package net.pursue.ui.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.pursue.Nattalie;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.RapeMasterFontManager;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.client.DebugHelper;
import net.pursue.utils.friend.FriendManager;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import net.pursue.utils.render.StencilUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

public class Friend extends GuiScreen {

    private GuiTextField textField;
    private boolean pressed = false;
    private boolean player = false;

    private int wheel;
    private float wheelAnim;

    @Override
    public void initGui() {
        textField = new GuiTextField(0, fontRendererObj, 520, 150, 120, 20);
        textField.setMaxStringLength(100);
        textField.setText(FriendManager.name);

        buttonList.add(new GuiButton(1, 520, 180, 120, 20, "添加好友"));
        buttonList.add(new GuiButton(2, 520, 210, 120, 20, "删除好友"));
        buttonList.add(new GuiButton(3, 520, 240, 120, 20, "切换列表"));
        buttonList.add(new GuiButton(4, 520, 270, 120, 20, "举报此人"));

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RoundedUtils.drawRound(200, 100, 520, 300, 2, new Color(0, 0, 0, 150));
        textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);

        RoundedUtils.drawRound(210, 115, 252, 280, 2, new Color(0, 0, 0,120));
        FontManager.font24.drawString(player ? "当前好友列表：" : "当前世界玩家列表：", 210, 102, Color.WHITE.getRGB());

        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glColor4f(1f, 1, 1, 1f);
        StencilUtils.write(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();

        {
            RoundedUtils.drawRound(210, 115, 252, 280, 2, new Color(0, 0, 0,120));
        }

        GL11.glPopMatrix();
        GlStateManager.resetColor();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        StencilUtils.erase(true);
        GL11.glPushMatrix();

        {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            int fixY = 0;
            if (!player) {
                if (mc.getConnection() != null) {
                    for (NetworkPlayerInfo playerInfo : mc.getConnection().getPlayerInfoMap()) {
                        String name = StringUtils.stripControlCodes(playerInfo.getGameProfile().getName());

                        if (name.equals(mc.player.getName()) || FriendManager.isFriend(name)) continue;

                        drawPlayer(220, (int) (120 + fixY + wheelAnim), name, playerInfo, FontManager.font36, mouseX, mouseY);
                        fixY += 40;
                    }
                }
            } else {
                if (!FriendManager.friends.isEmpty()) {
                    for (String data : FriendManager.friends) {
                        drawPlayer(220, (int) (120 + fixY + wheelAnim), data, null, FontManager.font36, mouseX, mouseY);
                        fixY += 40;
                    }
                }
            }

            int real = Mouse.getDWheel();
            if (isHovering(210, 115, 252, 280, mouseX, mouseY)) {
                if (real > 0 && wheel < 0) {
                    for (int i = 0; i < 5; i++) {
                        if (!(wheel < 0))
                            break;
                        wheel += 10;
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        if (!(real < 0))
                            break;
                        wheel -= 10;
                    }
                }
            }

            wheelAnim = AnimationUtils.moveUD(wheelAnim, wheel, (float) (10 * RenderUtils.deltaTime()), (float) (7 * RenderUtils.deltaTime()));
            GlStateManager.disableBlend();
        }
        GL11.glPopMatrix();
        GlStateManager.resetColor();
        StencilUtils.dispose();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            pressed = true;
        }
        textField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        pressed = false;

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        String userInput = textField.getText();

        if (!userInput.isEmpty()) {
            if (button.id == 1) {
                FriendManager.friends.add(userInput);
                FriendManager.name = "";
                mc.displayGuiScreen(this);
                DebugHelper.sendMessage("Friend 添加好友: " + TextFormatting.YELLOW + userInput);
            }
            if (button.id == 2) {
                if (FriendManager.friends.remove(userInput)) {
                    DebugHelper.sendMessage("Friend 删除好友: " + TextFormatting.YELLOW + userInput);
                } else {
                    DebugHelper.sendMessage("Friend 删除失败！");
                }
                FriendManager.name = "";
                mc.displayGuiScreen(this);
            }
        }

        if (!userInput.isEmpty() && !FriendManager.name.isEmpty()) {
            if (button.id == 4) {
                mc.player.sendChatMessage("/report " + FriendManager.name);
                FriendManager.name = "";
            }
        }

        if (button.id == 3) {
            player = !player;
            mc.displayGuiScreen(this);
        }
        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        textField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    private void drawPlayer(int x, int y, String name, NetworkPlayerInfo playerInfo, RapeMasterFontManager fontManager, int mouseX, int mouseY) {

        float anim;
        if (isHovered(x, y, 252, 36, mouseX, mouseY)) {
            anim = 0.98f;

            if (pressed) {
                FriendManager.name = name;
                mc.displayGuiScreen(this);
                pressed = false;
            }
        } else {
            anim = 1;
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(anim , anim, anim);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);

        RoundedUtils.drawRound(x, y,242, 36, 2, new Color(100,100,100,150));
        fontManager.drawString(name, x + 39, y + 2, Color.WHITE.getRGB());

        if (playerInfo != null) {
            RoundedUtils.drawHead(playerInfo.getLocationSkin(), x + 5, y + 2, 32, 32, 2);
        } else {
            RoundedUtils.drawHead(mc.player.getLocationSkin(), x + 5, y + 2, 32, 32, 2);
        }

        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    public boolean isHovering(float x, float y, float width, float height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
    public boolean isHovered(int x, int y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}

