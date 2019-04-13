package de.erdbeerbaerlp.betterchesting;


import de.erdbeerbaerlp.betterchesting.gui.GuiChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockChest extends net.minecraft.block.BlockChest {

	protected BlockChest(Type chestTypeIn) {
		super(chestTypeIn);
		setSoundType(SoundType.WOOD);
	}
	/**
	 * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
	 */
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if (tileentity instanceof IInventory)
		{
			//InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		worldIn.removeTileEntity(pos);
	}
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity tileentity,
			ItemStack heldStack) {
		ItemStack stack = new ItemStack(this);
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.005F);
		if (tileentity instanceof TileEntityChest)
		{
			TileEntityChest te = ((TileEntityChest)tileentity);
			int slots = te.getSizeInventory();
			NBTTagCompound tag = new NBTTagCompound();
			if(te.hasCustomName()) {
				NBTTagCompound display = new NBTTagCompound();
				display.setString("Name", te.getDisplayName().getFormattedText());
				tag.setTag("display", display);
			}
			NBTTagCompound blockEntityTag = new NBTTagCompound();
			NBTTagList itemList = new NBTTagList();

			for(int i=0;i<slots;i++) {
				final ItemStack item = te.getStackInSlot(i);
				if(item.isEmpty()) continue;
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				itemTag.setString("id", item.getItem().getRegistryName().getResourcePath());
				itemTag.setInteger("Count", item.getCount());
				if(item.hasTagCompound()) itemTag.setTag("tag", item.getTagCompound());
				itemList.appendTag(itemTag);
			}
			blockEntityTag.setTag("Items", itemList);
			tag.setTag("BlockEntityTag", blockEntityTag);
			stack.setTagCompound(tag);
			spawnAsEntity(worldIn, pos, stack);
		}
	}

	/**
	 * Called when the block is right clicked by a player.
	 */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (worldIn.isRemote)
		{
			return true;
		}
		else
		{
			ILockableContainer ilockablecontainer = this.getLockableContainer(worldIn, pos);

			if (ilockablecontainer != null)
			{
				playerIn.openGui(BetterChesting.instance, GuiChest.GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());

				if (this.chestType == BlockChest.Type.BASIC)
				{
					playerIn.addStat(StatList.CHEST_OPENED);
				}
				else if (this.chestType == BlockChest.Type.TRAP)
				{
					playerIn.addStat(StatList.TRAPPED_CHEST_TRIGGERED);
				}
			}

			return true;
		}
	}

}
