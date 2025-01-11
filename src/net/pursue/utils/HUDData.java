package net.pursue.utils;


import lombok.Getter;
import lombok.Setter;
import net.pursue.ui.font.FontManager;
import org.lwjgl.input.Mouse;

import java.awt.*;

@Setter
@Getter
public class HUDData {
    private String title;
    private float x, y ,width ,height;

    // move
    public int alpha = 100;
    private float preX, preY;
    private boolean drag;

    public HUDData(String title, float x, float y, float width, float height) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void doDrag(int mouseX, int mouseY) {
        if (this.drag) {
            if (!Mouse.isButtonDown(0)) {
                this.drag = false;
            }
            this.x = (int) (mouseX - this.preX);
            this.y = (int) (mouseY - this.preY);
        }
    }

    public void mouseClick(int mouseX, int mouseY, int button) {
        if (isHovering(mouseX, mouseY)) {
            if (button == 0) {
                this.drag = true;
                this.preX = mouseX - this.x;
                this.preY = mouseY - this.y;
            }
        }
    }

    public boolean isHovering(int mouseX, int mouseY) {
        float startX = x;
        float startY = y;
        float w = width;
        float h = height;

        if (width < 0) {
            startX += width;
            w = Math.abs(w);
        }

        if (height < 0) {
            startY += height;
            h = Math.abs(h);
        }

        return mouseX >= startX && mouseX <= startX + w && mouseY >= startY && mouseY <= startY + h;
    }

    public void renderTag() {
        float textX = x;
        float textY = y;

        if (width < 0) {
            textX += width;
        }

        if (height < 0) {
            textY += height;
        }

        FontManager.font16.drawString(title, textX, textY - 2 - FontManager.font16.getHeight(), new Color(255, 255, 255, alpha).getRGB());
    }
}
