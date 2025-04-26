package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.pursue.mode.player.Stealer;
import net.pursue.ui.font.FontManager;
import net.pursue.utils.render.RenderUtils;
import net.pursue.utils.render.RoundedUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiBrewingStand extends GuiContainer
{
    private static final ResourceLocation BREWING_STAND_GUI_TEXTURES = new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[] {29, 24, 20, 16, 11, 6, 0};

    /** The player inventory bound to this GUI. */
    private final InventoryPlayer playerInventory;
    private final IInventory tileBrewingStand;

    public GuiBrewingStand(InventoryPlayer playerInv, IInventory p_i45506_2_)
    {
        super(new ContainerBrewingStand(playerInv, p_i45506_2_));
        this.playerInventory = playerInv;
        this.tileBrewingStand = p_i45506_2_;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (Stealer.instance.isEnable() && Stealer.instance.silent.getValue()) {
            ContainerBrewingStand brewingStand = (ContainerBrewingStand) mc.player.openContainer;

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

            RoundedUtils.drawRound(pos.x - width / 2, startY - FontManager.font10.getHeight() - 5F, width, height,0, backgroundColor);

            GL11.glPushMatrix();
            GL11.glTranslated(pos.x - width / 2 - 25F, startY, 0);
            RenderHelper.enableGUIStandardItemLighting();

            for (int i1 = 0; i1 <= 5; i1++) {
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
        String s = this.tileBrewingStand.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BREWING_STAND_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        int k = this.tileBrewingStand.getField(1);
        int l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18);

        if (l > 0)
        {
            this.drawTexturedModalRect(i + 60, j + 44, 176, 29, l, 4);
        }

        int i1 = this.tileBrewingStand.getField(0);

        if (i1 > 0)
        {
            int j1 = (int)(28.0F * (1.0F - (float)i1 / 400.0F));

            if (j1 > 0)
            {
                this.drawTexturedModalRect(i + 97, j + 16, 176, 0, 9, j1);
            }

            j1 = BUBBLELENGTHS[i1 / 2 % 7];

            if (j1 > 0)
            {
                this.drawTexturedModalRect(i + 63, j + 14 + 29 - j1, 185, 29 - j1, 12, j1);
            }
        }
    }
}
