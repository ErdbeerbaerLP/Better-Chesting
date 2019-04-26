package de.erdbeerbaerlp.betterchesting;


import java.util.List;

import org.lwjgl.input.Keyboard;

import de.erdbeerbaerlp.betterchesting.gui.GuiChest;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BlockChest extends net.minecraft.block.BlockChest {

	protected BlockChest(Type chestTypeIn) {
		super(chestTypeIn);
		setSoundType(SoundType.WOOD);
		
	}
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityChest();
	}
	@Override
	/**
     * Checks if this block can be placed exactly at the given position.
     */
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
//		return true;
        int i = 0;
        BlockPos blockpos = pos.west();
        BlockPos blockpos1 = pos.east();
        BlockPos blockpos2 = pos.north();
        BlockPos blockpos3 = pos.south();

        if (worldIn.getBlockState(blockpos).getBlock() == this)
        {
            if (this.isDoubleChest(worldIn, blockpos))
            {
                return false;
            }

            ++i;
        }

        if (worldIn.getBlockState(blockpos1).getBlock() == this)
        {
            if (this.isDoubleChest(worldIn, blockpos1))
            {
                return false;
            }

            ++i;
        }

        if (worldIn.getBlockState(blockpos2).getBlock() == this)
        {
            if (this.isDoubleChest(worldIn, blockpos2))
            {
                return false;
            }

            ++i;
        }

        if (worldIn.getBlockState(blockpos3).getBlock() == this)
        {
            if (this.isDoubleChest(worldIn, blockpos3))
            {
                return false;
            }

            ++i;
        }

        return i <= 1;
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
		if (tileentity instanceof net.minecraft.tileentity.TileEntityChest)
		{
			TileEntityChest te = ((TileEntityChest)tileentity);
			int slots = te.getSizeInventory();
			NBTTagCompound tag = new NBTTagCompound();
			if(te.hasCustomName()) {
				NBTTagCompound display = new NBTTagCompound();
				display.setString("Name", te.getDisplayName().getFormattedText());
				tag.setTag("display", display);
			}
			NBTTagList itemList = new NBTTagList();

			for(int i=0;i<slots;i++) {
				final ItemStack item = te.getStackInSlot(i);
				if(item.isEmpty()) continue;
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				itemTag.setString("id", item.getItem().getRegistryName().getResourcePath());
				itemTag.setInteger("Count", item.getCount());
				itemTag.setShort("Damage", (short) item.getItemDamage());
				if(item.hasTagCompound()) itemTag.setTag("tag", item.getTagCompound());
				itemList.appendTag(itemTag);
			}
			tag.setTag("Items", itemList);
			NBTTagCompound blockData = te.writeToNBT(new NBTTagCompound());
			if(blockData.hasKey("chesting_info", 10)) {
				tag.setTag("chesting_info", blockData.getTag("chesting_info"));
			}
			if(te.writeToNBT(new NBTTagCompound()).hasKey("chesting_info", 10))tag.setTag("chesting_info", te.writeToNBT(new NBTTagCompound()).getTag("chesting_info"));
			stack.setTagCompound(tag);
			System.out.println(tag);
			spawnAsEntity(worldIn, pos, stack);
		}else super.harvestBlock(worldIn, player, pos, state, tileentity, stack);
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
			TileEntity tile = worldIn.getTileEntity(pos);
			if((tile instanceof net.minecraft.tileentity.TileEntityChest) && !(tile instanceof TileEntityChest)) {
				NBTTagCompound tag = tile.writeToNBT(new NBTTagCompound());
				TileEntityChest te = new TileEntityChest();
				worldIn.removeTileEntity(pos);
				worldIn.setTileEntity(pos, te);
				te.readFromNBT(tag);
				te.markDirty();
				worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 2);
				playerIn.sendStatusMessage(new TextComponentString(I18n.format("betterchesting.updated")), true);
				return true;
			}
			if(tile instanceof TileEntityChest) {
				TileEntityChest te = (TileEntityChest)tile;
				System.out.println(te.writeToNBT(new NBTTagCompound()));
				if(te.getOwner() == null && !te.isLootchest()) {
					te.setOwner(new ChestUser(playerIn, ChestPermission.all())); 
					playerIn.sendStatusMessage(new TextComponentTranslation("betterchesting.ownerSet", te.getOwner().getName()), true);
				}

				if(!te.canOpenChest(playerIn)) {
					playerIn.sendStatusMessage(new TextComponentTranslation("betterchesting.notAllowed").setStyle(new Style().setColor(TextFormatting.RED)), true);
					return true;
				}

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
			}
			return true;
		}
	}
	
	/**
	 * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
	 * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
	 * block, etc.
	 */
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		System.out.println("changed");
		TileEntity tileentity = worldIn.getTileEntity(pos);
		TileEntity te = worldIn.getTileEntity(fromPos);
		if (tileentity instanceof TileEntityChest && te instanceof TileEntityChest){
			BlockChest thisBlock = (BlockChest) worldIn.getBlockState(pos).getBlock();
			BlockChest otherBlock = (BlockChest) worldIn.getBlockState(fromPos).getBlock();
			if(thisBlock.chestType != otherBlock.chestType) return;
			NBTTagCompound nbt = tileentity.writeToNBT(new NBTTagCompound());
			NBTTagCompound nbt2 = te.writeToNBT(new NBTTagCompound());
			if(nbt.hasKey("chesting_info", 10)) {
				if(nbt2.hasKey("chesting_info", 10) && nbt.getCompoundTag("chesting_info").equals(nbt2.getCompoundTag("chesting_info"))){
					System.out.println(true);
					return;
				}
					System.out.println(false);
				nbt2.setTag("chesting_info", nbt.getCompoundTag("chesting_info"));
				te.readFromNBT(nbt2);
				te.markDirty();
				worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 1);
				
			}
			
		}

	}
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		TileEntityChest te = (TileEntityChest) worldIn.getTileEntity(pos);
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("chesting_info", 10)) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag("chesting_info");
			if(tag.hasKey("owner", 10))
				te.setOwner(ChestUser.fromNBT(tag.getCompoundTag("owner")));
			if(tag.hasKey("whitelist", 9))
				te.setWhitelist(tag.getTagList("whitelist", 10));
			System.out.println(tag.hasKey("Items", 9));
			if(stack.getTagCompound().hasKey("Items", 9)) {
				NBTTagList list = stack.getTagCompound().getTagList("Items", 10);
				for(int i=0;i<list.tagCount();i++) {
					final NBTTagCompound item = list.getCompoundTagAt(i);
					System.out.println(item);
					te.setInventorySlotContents(item.getInteger("Slot"), new ItemStack(item));
				}
			}
		}
	}

}
