package net.pursue.ui.guiButton;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.pursue.utils.TimerUtils;

public class DelayButton extends GuiButton {

    private final int x;
    private final int y;
    private final String label;
    private final int delay;
    private final TimerUtils timer = new TimerUtils();

    private boolean done = false; // delay

    private int width = 200;
    private int height = 20;

    public DelayButton(int buttonId, int x, int y, String buttonText, int delay) {
        super(buttonId, x, y, buttonText);
        this.x = x;
        this.y = y;
        this.label = buttonText;
        this.delay = delay;
        this.done = false;
    }

    public DelayButton(int buttonId, int x, int y, String buttonText, int delay, int width, int height) {
        super(buttonId, x, y, buttonText);
        this.x = x;
        this.y = y;
        this.label = buttonText;
        this.delay = delay;
        this.width = width;
        this.height = height;
        this.done = false;
    }

    @Override
    public void func_191745_a(Minecraft p_191745_1_, int p_191745_2_, int p_191745_3_, float p_191745_4_) {
        if (!done) {
            timer.reset();
        }

        if (timer.hasTimePassed(delay)) {
            FontRenderer fontrenderer = p_191745_1_.fontRendererObj;
            p_191745_1_.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = p_191745_2_ >= this.x && p_191745_3_ >= this.y && p_191745_2_ < this.x + this.width && p_191745_3_ < this.y + this.height;
            int i = this.getHoverState(this.hovered);

            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            this.drawTexturedModalRect(x, y, 0, 46 + i * 20, this.width / 2, this.height);
            this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);

            this.mouseDragged(p_191745_1_, p_191745_2_, p_191745_3_);

            int j = 14737632;

            if (!this.enabled)
            {
                j = 10526880;
            }
            else if (this.hovered)
            {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.label, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }
}
