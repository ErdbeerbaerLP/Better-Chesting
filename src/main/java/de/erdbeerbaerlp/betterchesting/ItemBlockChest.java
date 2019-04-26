package de.erdbeerbaerlp.betterchesting;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest.Type;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockChest extends ItemBlock {

	public ItemBlockChest(Block block) {
		super(block);
	}
	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		// TODO Auto-generated method stub
		if(stack.hasTagCompound()) {
			if(stack.getTagCompound().hasKey("Items") && !stack.getTagCompound().getTagList("Items", 10).hasNoTags()) {
				tooltip.add(TextFormatting.AQUA+I18n.format("betterchesting.filledChest"));
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {

					NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
					ItemStackHelper.loadAllItems(stack.getTagCompound(), nonnulllist);
					int i = 0;
					int j = 0;
					tooltip.add(I18n.format("betterchesting.itemsHeader"));
					for (ItemStack itemstack : nonnulllist)
					{
						if (!itemstack.isEmpty())
						{
							++j;

							if (i <= 11)
							{
								++i;
								tooltip.add(String.format("%s x%d", itemstack.getDisplayName(), itemstack.getCount()));
							}
						}
					}

					if (j - i > 0)
					{
						tooltip.add(String.format(TextFormatting.ITALIC + net.minecraft.util.text.translation.I18n.translateToLocal("container.shulkerBox.more"), j - i));
					}
				}else {
					tooltip.add(I18n.format("betterchesting.shiftForMore"));
				}
			}}
		super.addInformation(stack, player, tooltip, advanced);
	}
	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack)
	{
		Block block = worldIn.getBlockState(pos).getBlock();

		if (block == Blocks.SNOW_LAYER && block.isReplaceable(worldIn, pos))
		{
			side = EnumFacing.UP;
		}
		else if (!block.isReplaceable(worldIn, pos))
		{
			pos = pos.offset(side);
		}
		System.out.println(pos);
		for(EnumFacing facing : EnumFacing.values()) {
			if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) continue;
			IBlockState state = worldIn.getBlockState(pos.offset(facing));
			System.out.println(state);
			if(state.getBlock() != BetterChesting.chest && state.getBlock() != BetterChesting.trappedchest && state.getBlock() != Blocks.CHEST && state.getBlock() != Blocks.TRAPPED_CHEST) continue;
			else {
				
				final BlockChest b = (BlockChest)state.getBlock();
				final Type type = b.chestType;
				final Type type2 = ((BlockChest)((ItemBlockChest)stack.getItem()).getBlock()).chestType;
				if(type != null && type2 != null) {
				}
				if(!b.isDoubleChest(worldIn, pos.offset(facing))) {
					TileEntity te = worldIn.getTileEntity(pos.offset(facing));
					if(te instanceof TileEntityChest) {
						
						NBTTagCompound nbt = ((TileEntityChest) te).writeToNBT(new NBTTagCompound());
						System.out.println(nbt);
						ChestUser o = ChestUser.fromNBT(nbt.getCompoundTag("chesting_info").getCompoundTag("owner"));
						System.out.println(o);
						if(o == null) break;
						if(o.getUuid().equals(player.getUniqueID().toString())) {
							System.out.println("OK");
							break;
						}else {
							if(type == type2) {
							player.sendStatusMessage(new TextComponentString(TextFormatting.RED+I18n.format("betterchesting.cannotDouble", o.getName())), true);
							return false;
							}
						}
					}
				}else {
					player.sendStatusMessage(new TextComponentString(TextFormatting.RED+I18n.format("betterchesting.doubleChest")), true);
				}
			}
		}
		return worldIn.mayPlace(this.block, pos, false, side, (Entity)null);
	}
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();

		if (!block.isReplaceable(worldIn, pos))
		{
			pos = pos.offset(facing);
		}

		ItemStack itemstack = player.getHeldItem(hand);
		System.out.println("may place:"+worldIn.mayPlace(this.block, pos, false, facing, (Entity)null));
		if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack) && worldIn.mayPlace(this.block, pos, false, facing, (Entity)null))
		{
			int i = this.getMetadata(itemstack.getMetadata());
			IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i, player, hand);

			if (placeBlockAt(itemstack, player, worldIn, pos, facing, hitX, hitY, hitZ, iblockstate1))
			{
				iblockstate1 = worldIn.getBlockState(pos);
				SoundType soundtype = iblockstate1.getBlock().getSoundType(iblockstate1, worldIn, pos, player);
				worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				itemstack.shrink(1);
				TileEntity te = worldIn.getTileEntity(pos);
				System.out.println(te.getClass());
				if(te instanceof TileEntityChest && ((TileEntityChest) te).getOwner() == null) {
					((TileEntityChest) te).setOwner(new ChestUser(player, ChestPermission.all()));
					player.sendStatusMessage(new TextComponentTranslation("betterchesting.ownerSet", ((TileEntityChest) te).getOwner().getName()), true);

				}
			}

			return EnumActionResult.SUCCESS;
		}
		else
		{
			System.out.println("FAILED!");
			return EnumActionResult.FAIL;
		}
	}
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, IBlockState newState) {
		// TODO Auto-generated method stub
		System.out.println("PLACE!!!!");
		return super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
	}

}
