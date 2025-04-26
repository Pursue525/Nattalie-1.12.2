package net.pursue.ui.gui.sinka;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.pursue.Nattalie;
import net.pursue.mode.client.ClickGUI;
import net.pursue.ui.font.FontManager;
import net.pursue.ui.font.FontUtils;
import net.pursue.ui.gui.sinka.boxes.TypeBox;
import net.pursue.utils.MathUtils;
import net.pursue.utils.category.Category;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.GaussianBlur;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.StencilUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.pursue.utils.render.StencilUtils.checkSetupFBO;
import static org.lwjgl.opengl.GL11.*;

public class SinkaClick extends GuiScreen {
    private final List<TypeBox> typeBoxes = new ArrayList<>();
    private boolean previousMouse = true;

    private int wheel;
    private float wheelAnim;

    @Override
    public void initGui() {
        typeBoxes.clear();
        for (Category type : Category.values()) {
            typeBoxes.add(new TypeBox(type));
        }

        wheel = 120;
        wheelAnim = 120;

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (typeBoxes.isEmpty()) {
            return;
        }

        if (ClickGUI.instance.blur.getValue()) {
            mc.getFramebuffer().bindFramebuffer(false);
            checkSetupFBO(mc.getFramebuffer());
            glClear(GL_STENCIL_BUFFER_BIT);
            glEnable(GL_STENCIL_TEST);
            glStencilFunc(GL_ALWAYS, 1, 1);
            glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
            glColorMask(false, false, false, false);
            {
                int typeX = 0;
                for (TypeBox type : typeBoxes) {
                    type.drawBloom((int) (typeX + wheelAnim));
                    typeX += 128;
                }
            }
            glColorMask(true, true, true, true);
            glStencilFunc(GL_EQUAL, 1, 1);
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
            GaussianBlur.renderBlur(ClickGUI.instance.radius.getValue().floatValue());
            glDisable(GL_STENCIL_TEST);
        }

        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glColor4f(1f, 1, 1, 1f);
        StencilUtils.write(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        {
            int typeX = 0;
            for (TypeBox type : typeBoxes) {
                type.drawBloom((int) (typeX + wheelAnim));
                typeX += 128;
            }
        }
        GL11.glPopMatrix();
        GlStateManager.resetColor();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        StencilUtils.erase(true);
        GL11.glPushMatrix();
        {
            int typeX = 0;
            for (TypeBox type : typeBoxes) {
                type.draw(mouseX, mouseY, (int) (typeX + wheelAnim), previousMouse);
                typeX += 128;
            }
        }
        GL11.glPopMatrix();
        GlStateManager.resetColor();
        StencilUtils.dispose();
        GL11.glPopMatrix();
        GL11.glPushMatrix();


        draw(0, 100, 50, height - 200, mouseX, mouseY, true);
        draw(width - 50, 100, 50, height - 200, mouseX, mouseY, false);

        wheelAnim = AnimationUtils.moveUD(wheelAnim, wheel, (float) (10 * RenderUtils.deltaTime()), (float) (7 * RenderUtils.deltaTime()));

        if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
            previousMouse = true;
        }

        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            previousMouse = false;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void draw(float x, float y, float width, float height, int mouseX, int mouseY, boolean add) {
        Color color = new Color(0,0,0,200);

        FontUtils font64 = FontManager.font64;


        if (add) {
            if (Nattalie.instance.getPursueGUI().isHovering(x, y, width, height, mouseX, mouseY)) {

                color = new Color(255,255,255,100);

                if (wheel < 120) wheel += 5;
            }

            RenderUtils.drawRect(x,y,width,height,color);

            font64.drawString("<", x + MathUtils.centre(width, font64.getWidth("<")), y + MathUtils.centre(height, font64.getHeight()) - 2, Color.WHITE);
        } else {
            if (Nattalie.instance.getPursueGUI().isHovering(x, y, width, height, mouseX, mouseY)) {

                color = new Color(255,255,255,100);

                if (wheel > -225) wheel -= 5;
            }

            RenderUtils.drawRect(x,y,width,height,color);
            font64.drawString(">", x + MathUtils.centre(width, font64.getWidth(">")), y + MathUtils.centre(height, font64.getHeight()) - 2, Color.WHITE);
        }
    }
}
