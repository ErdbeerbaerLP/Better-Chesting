package de.erdbeerbaerlp.betterchesting;

import java.util.ArrayList;

import de.erdbeerbaerlp.betterchesting.gui.ContainerChest;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
@SuppressWarnings("unused")
public class TileEntityChest extends net.minecraft.tileentity.TileEntityChest{


	private ChestUser owner;
	private ArrayList<ChestUser> whitelisted = new ArrayList<ChestUser>();
	private boolean isLootchest = false;
	public TileEntityChest() {
		// TODO Auto-generated constructor stub
	}
	public TileEntityChest(BlockChest.Type typeIn)
	{
		super(typeIn);
	}
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if(compound.hasKey("chesting_info", 10)){
			NBTTagCompound info = compound.getCompoundTag("chesting_info");
			if(info.hasKey("lootchest")) this.isLootchest = info.getBoolean("lootchest");
			else if (this.lootTable != null) this.isLootchest = true;
			if(this.isLootchest) {
				this.owner = ChestUser.getPublicChestUser();
			}else {
				System.out.println(info);
				System.out.println(this.owner);
				if(info.hasKey("owner", 10)) {
					this.owner = ChestUser.fromNBT(info.getCompoundTag("owner"));
					System.out.println(this.owner);
				}
				if(info.hasKey("whitelist", 9))
					for(NBTBase l : info.getTagList("whitelist", 8)) {
						//TODO
					}
			}
		}

	}
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		readFromNBT(tag);
	}
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		// TODO Auto-generated method stub
		return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound()));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{

		super.writeToNBT(compound);
		NBTTagCompound info = new NBTTagCompound();
		if (this.lootTable != null) this.isLootchest = true;
		info.setBoolean("lootchest", this.isLootchest);

		if(!this.isLootchest) {
			if(owner != null) info.setTag("owner", this.owner.toNBT());

			NBTTagList whitelist = new NBTTagList();
			//TODO  Fill whitelist
			info.setTag("whitelist", whitelist);
		}

		compound.setTag("chesting_info", info);
		return compound;
	}

	public final void setOwner(ChestUser chestUser) {
		this.owner = chestUser;
		this.markDirty();
	}

	public ChestUser getOwner() {
		return this.owner;
	}

	public void addToWhitelist(ChestUser u) {
		this.whitelisted.add(u);
		this.markDirty();
	}

	public boolean removeFromWhitelist(EntityPlayer p) {
		for(ChestUser u : this.whitelisted) {
			if(p.getUniqueID().toString().equals(u.getUuid())) {
				whitelisted.remove(u);
				markDirty();
				return true;
			}
		}
		return false;
	}

	public void setWhitelist(NBTTagList tagList) {
		for(int i=0;i<tagList.tagCount();i++) {
			this.whitelisted.add(ChestUser.fromNBT(tagList.getCompoundTagAt(i)));
		}
	}

	public boolean canOpenChest(EntityPlayer p) {
		if((this.owner != null && this.owner.isPublic() )|| this.isLootchest) return true;
		else {
			if(p.getUniqueID().toString().equals(this.owner.getUuid())) return true;
			else return false;
		}
	}

	public boolean canEditChest(EntityPlayer player) {
		return true;
	}

	public boolean isLootchest() {
		return this.isLootchest;
	}


	@Override
	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update()
	{
		this.checkForAdjacentChests();
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		++this.ticksSinceSync;

		if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0)
		{
			this.numPlayersUsing = 0;
			float f = 5.0F;

			for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0F), (double)((float)j - 5.0F), (double)((float)k - 5.0F), (double)((float)(i + 1) + 5.0F), (double)((float)(j + 1) + 5.0F), (double)((float)(k + 1) + 5.0F))))
			{
				if (entityplayer.openContainer instanceof ContainerChest)
				{
					IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();

					if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this))
					{
						++this.numPlayersUsing;
					}
				}
			}
		}

		this.prevLidAngle = this.lidAngle;
		float f1 = 0.1F;

		if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null)
		{
			double d1 = (double)i + 0.5D;
			double d2 = (double)k + 0.5D;

			if (this.adjacentChestZPos != null)
			{
				d2 += 0.5D;
			}

			if (this.adjacentChestXPos != null)
			{
				d1 += 0.5D;
			}

			this.world.playSound((EntityPlayer)null, d1, (double)j + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F)
		{
			float f2 = this.lidAngle;

			if (this.numPlayersUsing > 0)
			{
				this.lidAngle += 0.1F;
			}
			else
			{
				this.lidAngle -= 0.1F;
			}

			if (this.lidAngle > 1.0F)
			{
				this.lidAngle = 1.0F;
			}

			float f3 = 0.5F;

			if (this.lidAngle < 0.5F && f2 >= 0.5F && this.adjacentChestZNeg == null && this.adjacentChestXNeg == null)
			{
				double d3 = (double)i + 0.5D;
				double d0 = (double)k + 0.5D;

				if (this.adjacentChestZPos != null)
				{
					d0 += 0.5D;
				}

				if (this.adjacentChestXPos != null)
				{
					d3 += 0.5D;
				}

				this.world.playSound((EntityPlayer)null, d3, (double)j + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F)
			{
				this.lidAngle = 0.0F;
			}
		}
	}
	@Override
	public void fillWithLoot(EntityPlayer player) {
		if (this.lootTable != null)
		{
			this.isLootchest = true;
			markDirty();
		}
		super.fillWithLoot(player);

	}


}