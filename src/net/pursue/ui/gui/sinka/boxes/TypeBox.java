package net.pursue.ui.gui.sinka.boxes;

import net.pursue.Nattalie;
import net.pursue.mode.Mode;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.category.Category;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TypeBox {
    private final List<ModuleBox> moduleBoxes = new ArrayList<>();
    private final Category type;
    private int wheel;
    private float wheelAnim;
    private float heightAnim;

    public TypeBox(Category type) {
        this.type = type;
        this.wheel = 0;
        this.wheelAnim = 0;
        for (Mode module : Nattalie.instance.getModeManager().getModes()) {
            moduleBoxes.add(new ModuleBox(module));
        }
    }

    public void draw(int mouseX, int mouseY, int typeX, boolean previousMouse) {
        int moduleButtonHeight = 0;
        for (ModuleBox moduleBox : this.moduleBoxes) {
            if (moduleBox.getModule().getCategory() != this.type) {
                continue;
            }
            moduleButtonHeight += 22 + moduleBox.getHeight();
        }
        int height = Math.min(moduleButtonHeight + 24, 366);
        RoundedUtils.drawRound(8 + typeX, 8, 120, heightAnim, 10, new Color(0, 0, 0, 120));
        FontManager.font18.drawString(this.type.name(), 8 + typeX + 16, 18, new Color(255, 255, 255, 200).getRGB());

        int moduleButtonY = 0;
        for (ModuleBox moduleBox : this.moduleBoxes) {
            if (moduleBox.getModule().getCategory() != this.type) {
                continue;
            }
            moduleBox.draw(8 + typeX, 32 + moduleButtonY + wheelAnim, mouseX, mouseY, previousMouse);
            moduleButtonY += 22 + moduleBox.getHeight();
        }

        if (Nattalie.instance.getPursueGUI().isHovering(8 + typeX, 32, 120, height - 28, mouseX, mouseY)) {
            int real = Mouse.getDWheel();
            if (real > 0 && wheel < 0) {
                for (int i = 0; i < 5; i++) {
                    if (!(wheel < 0))
                        break;
                    wheel += 10;
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    if (!(real < 0 && wheel + moduleButtonY > (height - 24)))
                        break;
                    wheel -= 10;
                }
            }
        }

        wheelAnim = AnimationUtils.moveUD(wheelAnim, wheel, (float) (10 * RenderUtils.deltaTime()), (float) (7 * RenderUtils.deltaTime()));
        heightAnim = height;
    }

    public void drawBloom(int typeX) {
        RoundedUtils.drawRound(8 + typeX, 8, 120, heightAnim, 10, new Color(0, 0, 0, 255));
    }
}
