package de.erdbeerbaerlp.betterchesting;


import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandGive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
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
				System.out.println(item);
				if(item.isEmpty()) continue;
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				itemTag.setString("id", item.getItem().getRegistryName().getResourcePath());
				itemTag.setInteger("Count", item.getCount());
				itemTag.setShort("Damage", (short) item.getItemDamage());
				if(item.hasTagCompound()) itemTag.setTag("tag", item.getTagCompound());
				itemList.appendTag(itemTag);
			}
			blockEntityTag.setTag("Items", itemList);
			tag.setTag("BlockEntityTag", blockEntityTag);
			stack.setTagCompound(tag);
			spawnAsEntity(worldIn, pos, stack);
		}
	}

}
