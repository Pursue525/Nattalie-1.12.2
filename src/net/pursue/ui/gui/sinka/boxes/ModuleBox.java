package net.pursue.ui.gui.sinka.boxes;

import lombok.Getter;
import net.pursue.Nattalie;
import net.pursue.mode.Mode;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.render.AnimationUtils;
import net.pursue.utils.render.RenderUtils;
import net.pursue.value.Value;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModuleBox {
    private final List<ValueBox> valueBoxes = new ArrayList<>();
    @Getter
    private final Mode module;
    @Getter
    private int height;
    private double alpha;

    public ModuleBox(Mode module) {
        this.module = module;
        this.alpha = 0;

        for (Value<?> value : this.module.getValues()) {
            valueBoxes.add(new ValueBox(value));
        }
    }

    public void draw(float x, float y, int mouseX, int mouseY, boolean previousMouse) {
        RenderUtils.drawRect(x, y, 120, 22, new Color(0, 0, 0, 80));
        if (module.isEnable()) {
            alpha = AnimationUtils.moveUD((float) alpha, (float) 100, (float) (15 * RenderUtils.deltaTime()), (float) (10 * RenderUtils.deltaTime()));
        } else {
            alpha = AnimationUtils.moveUD((float) alpha, (float) 0, (float) (15 * RenderUtils.deltaTime()), (float) (10 * RenderUtils.deltaTime()));
        }
        RenderUtils.drawRect(x, y, 120, 22, new Color(86,98,246, (int) alpha));
        FontManager.font16.drawString(this.module.getName(), x + 6, y + 10, new Color(255, 255, 255, this.module.isEnable() ? 255 : 200).getRGB());
        if (Nattalie.instance.getPursueGUI().isHovering(x, y, 116, 22, mouseX, mouseY) && !Nattalie.instance.getPursueGUI().isHovering(x, 8, 116, 24, mouseX, mouseY)) {
            if (!previousMouse) {
                if (Mouse.isButtonDown(0)) {
                    this.module.setEnable(!this.module.isEnable());
                } else if (Mouse.isButtonDown(1)) {

                    if (this.module.getValues().isEmpty()) {
                        this.module.setValueset(false);
                    } else {
                        this.module.setValueset(!this.module.isValueset());
                    }
                }
            }
        }

        int valueY = 0;
        if (this.module.isValueset()) {
            for (ValueBox value : this.valueBoxes) {
                value.draw(x + 4, y + 32 + valueY, mouseX, mouseY, previousMouse);
                valueY += value.getHeight();
            }
            height = valueY;
        } else {
            height = 0;
        }
    }

}
