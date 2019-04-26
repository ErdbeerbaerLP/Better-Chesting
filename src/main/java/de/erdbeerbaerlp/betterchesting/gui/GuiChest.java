package de.erdbeerbaerlp.betterchesting.gui;

import de.erdbeerbaerlp.betterchesting.TileEntityChest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiChest extends GuiContainer
{
	public static final int GUI_ID = 1;
	GuiButton btn = new GuiButton(0, 0, 0, "Settings");
	/** The ResourceLocation containing the chest GUI texture. */
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
	private final IInventory upperChestInventory;
	private final IInventory lowerChestInventory;
	/** window height is calculated with these values; the more rows, the heigher */
	private final int inventoryRows;
	private TileEntityChest te;
	private BlockPos pos;
	@SuppressWarnings("unused")
	public GuiChest(IInventory upperInv, IInventory lowerInv, BlockPos pos)
	{
		super(new ContainerChest(upperInv, lowerInv, Minecraft.getMinecraft().player));
		this.pos = pos;
		TileEntity te = Minecraft.getMinecraft().player.world.getTileEntity(this.pos);
		if(te instanceof TileEntityChest)
			this.te = (TileEntityChest) te;
		System.out.println(te);
		this.upperChestInventory = upperInv;
		this.lowerChestInventory = lowerInv;
		this.allowUserInput = false;
		int i = 222;
		int j = 114;
		this.inventoryRows = lowerInv.getSizeInventory() / 9;
		this.ySize = 114 + this.inventoryRows * 18;
		this.btn.width = 60;
	}
	@Override
	public void initGui() {
		this.addButton(btn);
		System.out.println(te.isLootchest());
		super.initGui();
	}
	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		btn.visible = (!te.isLootchest() && te.canEditChest(mc.player));
		btn.x = (this.width / 2)+(this.xSize/2);
		btn.y = (this.height - this.ySize)/2;
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		this.fontRenderer.drawString(this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		this.fontRenderer.drawString(this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);

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
