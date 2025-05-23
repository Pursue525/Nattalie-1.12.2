package net.pursue.ui.guiButton;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.pursue.utils.TimerUtils;
import net.pursue.utils.render.RoundedUtils;

import java.awt.*;

public class DelayTextField extends GuiTextField {
    private final FontRenderer fontRendererInstance;
    private final int width;
    private final int height;

    private int id;

    private final int delay;
    private final TimerUtils timer = new TimerUtils();

    private boolean delays;
    private Color color;
    private final boolean fix;

    public DelayTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, int delay, boolean fix)
    {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        this.fontRendererInstance = fontrendererObj;
        this.xPosition = x;
        this.yPosition = y;
        this.width = par5Width;
        this.height = par6Height;
        this.delay = delay;
        this.id = componentId;
        this.fix = fix;
        delays = false;
    }

    public DelayTextField(int componentId, String string, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height, int delay, Color color, boolean fix)
    {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        setText(string);
        this.fontRendererInstance = fontrendererObj;
        this.xPosition = x;
        this.yPosition = y;
        this.width = par5Width;
        this.height = par6Height;
        this.delay = delay;
        this.id = componentId;
        this.fix = fix;
        this.color = color;
        delays = false;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void drawTextBox() {
        if (!delays) {
            timer.reset();
            delays = true;
        }

        if (timer.hasTimePassed(delay)) {
            if (this.getVisible())
            {
                if (color != null) {
                    RoundedUtils.drawRound(this.xPosition, this.yPosition + 6, this.width, this.height - 9, 0, color);
                }
                RoundedUtils.drawRound(this.xPosition, this.yPosition + this.height - 3, this.width, 1,0, new Color(255,255,255));

                int i = this.isEnabled ? this.enabledColor : this.disabledColor;
                int j = this.getCursorPosition() - this.lineScrollOffset;
                int k = this.getSelectionEnd() - this.lineScrollOffset;
                String s = this.fontRendererInstance.trimStringToWidth(this.getText().substring(this.lineScrollOffset), this.getWidth());

                if (fix) {
                    s = new String(new char[this.getText().length()]).replace('\0', '*');
                }

                boolean flag = j >= 0 && j <= s.length();
                boolean flag1 = this.isFocused() && this.cursorCounter / 6 % 2 == 0 && flag;
                int l = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
                int i1 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
                int j1 = l;

                if (k > s.length())
                {
                    k = s.length();
                }

                if (!s.isEmpty())
                {
                    String s1 = flag ? s.substring(0, j) : s;
                    j1 = this.fontRendererInstance.drawStringWithShadow(s1, (float)l, (float)i1, i);
                }

                boolean flag2 = this.getCursorPosition() < this.getText().length() || this.getText().length() >= this.getMaxStringLength();
                int k1 = j1;

                if (!flag)
                {
                    k1 = j > 0 ? l + this.width : l;
                }
                else if (flag2)
                {
                    k1 = j1 - 1;
                    --j1;
                }

                if (!s.isEmpty() && flag && j < s.length())
                {
                    j1 = this.fontRendererInstance.drawStringWithShadow(s.substring(j), (float)j1, (float)i1, i);
                }

                if (flag1)
                {
                    if (flag2)
                    {
                        Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
                    }
                    else
                    {
                        this.fontRendererInstance.drawStringWithShadow("_", (float)k1, (float)i1, i);
                    }
                }

                if (k != j)
                {
                    int l1 = l + this.fontRendererInstance.getStringWidth(s.substring(0, k));
                    this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRendererInstance.FONT_HEIGHT);
                }
            }
        }
    }
}
