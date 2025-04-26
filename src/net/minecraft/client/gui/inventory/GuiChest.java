package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.pursue.mode.player.Stealer;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.player.InvUtils;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiChest extends GuiContainer
{
    /** The ResourceLocation containing the chest GUI texture. */
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final IInventory upperChestInventory;
    private final IInventory lowerChestInventory;

    /**
     * window height is calculated with these values; the more rows, the heigher
     */
    private final int inventoryRows;

    public GuiChest(IInventory upperInv, IInventory lowerInv)
    {
        super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().player));
        this.upperChestInventory = upperInv;
        this.lowerChestInventory = lowerInv;
        this.allowUserInput = false;
        int i = 222;
        int j = 114;
        this.inventoryRows = lowerInv.getSizeInventory() / 9;
        this.ySize = 114 + this.inventoryRows * 18;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {

        if (Stealer.instance.isEnable() && Stealer.instance.silent.getValue()) {
            ContainerChest chest = (ContainerChest) mc.player.openContainer;

            if (Stealer.instance.count == -1) {
                Stealer.instance.count = InvUtils.count(chest);
                Stealer.instance.progress.update(0);
                Stealer.instance.progress.set(0);
            }

            int prog = Stealer.instance.count - InvUtils.count(chest);
            if (prog > Stealer.instance.progress.target) {
                Stealer.instance.progress.update(prog);
            }

            GuiScreen guiScreen = mc.currentScreen;
            mc.setIngameFocus();
            mc.currentScreen = guiScreen;

            float height = 75F;
            float width = 174F;

            if (Stealer.instance.currentChest == null) return;

            Vec2f base = RenderUtils.worldScreenPos(new Vec3d(
                    Stealer.instance.currentChest.getX() + 0.5D,
                    Stealer.instance.currentChest.getY() + 0.5D,
                    Stealer.instance.currentChest.getZ() + 0.5D
            ));
            if (base == null) return;
            Vec2f pos = new Vec2f(base.x, base.y);

            Color backgroundColor = new Color(20, 20, 20, 130);
            float startY = pos.y - 120;

            // draw rect
            RoundedUtils.drawRound(pos.x - width / 2, startY - FontManager.font10.getHeight() - 5F, width , height, 0, backgroundColor);
            String str = chest.getLowerChestInventory().getDisplayName().toString();
            FontManager.font10.drawString(str, (width / 2F) - (FontManager.font10.getStringWidth(str) * 0.5F), -(FontManager.font10.getHeight()), Color.WHITE.getRGB());


            float bar = 154F;
            RoundedUtils.drawRound(pos.x - bar / 2, startY - 5F, bar, 2, 1, Color.WHITE);

            // render item
            GL11.glPushMatrix();
            GL11.glTranslated(pos.x - width / 2, startY + 20F, 0);
            RenderHelper.enableGUIStandardItemLighting();
            renderInv(0, 8, 6, -12, chest, chest);
            renderInv(9, 17, 6, 6, chest, chest);
            renderInv(18, 26, 6, 24, chest, chest);

            RenderHelper.disableStandardItemLighting();
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GL11.glPopMatrix();


            return;
        }

        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.func_191948_b(mouseX, mouseY);
    }

    private boolean renderInv(int slot, int endSlot, int x, int y, Container container, ContainerChest chest) {
        int xOffset = x;

        boolean isE = true;

        for (int i = slot; i <= endSlot; i++) {
            xOffset += 18;
            if (container.getSlot(i).getStack() != null) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(container.getSlot(i).getStack(), xOffset - 18, y);
                mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, container.getSlot(i).getStack(), xOffset - 18, y);
            }

            if (!chest.getLowerChestInventory().getStackInSlot(i).func_190926_b()) isE = false;
        }
        return isE;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRendererObj.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
