package de.erdbeerbaerlp.betterchesting.mixin;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mixin(BlockChest.class)
public abstract class BetterChestMixin extends BlockContainer {

	protected BetterChestMixin(Material materialIn) {
		super(materialIn);
		setSoundType(SoundType.WOOD);
	}
	// INVOKESTATIC net/minecraft/inventory/InventoryHelper.dropInventoryItems(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/inventory/IInventory;)V
	@Redirect(
			at = @At(
					value = "INVOKE",
					target= "net/minecraft/inventory/InventoryHelper.dropInventoryItems(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/inventory/IInventory;)V",
					ordinal = 0),
			method = { "breakBlock(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V"})
	private void dropInventoryItems(World worldIn, BlockPos pos, IInventory inventory)
	{
		//Do nothing!
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		ItemStack istack = new ItemStack(this);
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.005F);
		if (te instanceof TileEntityChest)
		{
			TileEntityChest tileent = ((TileEntityChest)te);
			int slots = tileent.getSizeInventory();
			NBTTagCompound rootTag = te.writeToNBT(new NBTTagCompound());
			NBTTagCompound tag = new NBTTagCompound();
			if(tileent.hasCustomName()) {
				NBTTagCompound display = new NBTTagCompound();
				display.setString("Name", tileent.getDisplayName().getFormattedText());
				tag.setTag("display", display);
			}
			NBTTagCompound blockEntityTag = new NBTTagCompound();
			NBTTagList itemList = new NBTTagList();

			for(int i=0;i<slots;i++) {
				final ItemStack item = tileent.getStackInSlot(i);
				if(item.isEmpty()) continue;
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setInteger("Slot", i);
				itemTag.setString("id", item.getItem().getRegistryName().toString());
				itemTag.setInteger("Count", item.getCount());
				itemTag.setShort("Damage", (short) item.getItemDamage());
				if(item.hasTagCompound()) itemTag.setTag("tag", item.getTagCompound());
				itemList.appendTag(itemTag);
			}
			blockEntityTag.setTag("Items", itemList);
			tag.setTag("BlockEntityTag", blockEntityTag);
			istack.setTagCompound(tag);
			if(rootTag.hasKey("type") && istack.getItem().getRegistryName().getResourceDomain().equals("quark")) {
				istack.setItemDamage(ChestType.getMeta(rootTag.getString("type")));
			}
			spawnAsEntity(worldIn, pos, istack);
		}
	}
	@SuppressWarnings("deprecation")
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
	{
		super.addInformation(stack, player, tooltip, advanced);
		NBTTagCompound nbttagcompound = stack.getTagCompound();

		if (nbttagcompound != null && nbttagcompound.hasKey("BlockEntityTag", 10))
		{
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("BlockEntityTag");

			if (nbttagcompound1.hasKey("LootTable", 8))
			{
				tooltip.add("???????");
			}

			if (nbttagcompound1.hasKey("Items", 9) && !nbttagcompound1.getTagList("Items", 10).hasNoTags())
			{
				tooltip.add(TextFormatting.AQUA+I18n.format("betterchesting.filledChest"));
				tooltip.add("");
				if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
					NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
					ItemStackHelper.loadAllItems(nbttagcompound1, nonnulllist);
					int i = 0;
					int j = 0;
					tooltip.add(I18n.format("betterchesting.itemsHeader"));
					for (ItemStack itemstack : nonnulllist)
					{
						if (!itemstack.isEmpty())
						{
							++j;

							if (i <= 10)
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
			}else {
				tooltip.add(I18n.format("betterchesting.chestEmpty"));
			}
		}else {
			tooltip.add(I18n.format("betterchesting.chestEmpty"));
		}
	}


	public enum ChestType {
		NONE(""),
		SPRUCE("spruce"),
		BIRCH("birch"),
		JUNGLE("jungle"),
		ACACIA("acacia"),
		DARK_OAK("dark_oak");

		public final String name;


		ChestType(String name) {
			this.name = name;
		}
		public static int getMeta(String type) {
			for(int i=0;i<values().length;i++) {
				if(values()[i].name.equals(type)) return i-1;
			}
			return 0;
		}
	}
}
