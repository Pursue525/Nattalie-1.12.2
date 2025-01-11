package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.pursue.Nattalie;
import net.pursue.mode.hud.Inventory;
import net.pursue.mode.player.Stealer;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiFurnace extends GuiContainer
{
    private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");

    /** The player inventory bound to this GUI. */
    private final InventoryPlayer playerInventory;
    private final IInventory tileFurnace;

    public GuiFurnace(InventoryPlayer playerInv, IInventory furnaceInv)
    {
        super(new ContainerFurnace(playerInv, furnaceInv));
        this.playerInventory = playerInv;
        this.tileFurnace = furnaceInv;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (Stealer.instance.isEnable() && Stealer.instance.silent.getValue()) {

            GuiScreen guiScreen = mc.currentScreen;
            mc.setIngameFocus();
            mc.currentScreen = guiScreen;

            float height = 40F;
            float width = 100F;

            if (Stealer.instance.currentChest == null) return;

            Vec2f base = RenderUtils.worldScreenPos(new Vec3d(
                    Stealer.instance.currentChest.getX() + 0.5D,
                    Stealer.instance.currentChest.getY() + 0.5D,
                    Stealer.instance.currentChest.getZ() + 0.5D
            ));
            if (base == null) return;
            Vec2f pos = new Vec2f(base.x, base.y);

            Color backgroundColor = new Color(20, 20, 20, 130);
            float startY = pos.y - 93;
            float fontSize = 10;

            // draw rect
            RoundedUtils.drawRound(pos.x - width / 2, startY - FontManager.font10.getHeight() - 5F, width, height,0, backgroundColor);

            // render item
            GL11.glPushMatrix();
            GL11.glTranslated(pos.x - width / 2 - 25F, startY, 0);
            RenderHelper.enableGUIStandardItemLighting();


            for (int i1 = 0; i1 < 2; i1++) {
                Slot slot = this.inventorySlots.inventorySlots.get(i1);
                ItemStack is = slot.getStack();
                this.itemRender.renderItemAndEffectIntoGUI(this.mc.player, is, (int) (slot.xDisplayPosition * 0.8), (int) ((slot.yDisplayPosition - 30) * 0.8));
            }

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

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.tileFurnace.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        if (TileEntityFurnace.isBurning(this.tileFurnace))
        {
            int k = this.getBurnLeftScaled(13);
            this.drawTexturedModalRect(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(i + 79, j + 34, 176, 14, l + 1, 16);
    }

    private int getCookProgressScaled(int pixels)
    {
        int i = this.tileFurnace.getField(2);
        int j = this.tileFurnace.getField(3);
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    private int getBurnLeftScaled(int pixels)
    {
        int i = this.tileFurnace.getField(1);

        if (i == 0)
        {
            i = 200;
        }

        return this.tileFurnace.getField(0) * pixels / i;
    }
}
