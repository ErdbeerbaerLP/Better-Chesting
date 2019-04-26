package de.erdbeerbaerlp.betterchesting.gui;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nullable;

import de.erdbeerbaerlp.betterchesting.BetterChesting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ContainerChest extends Container
{
    private final IInventory lowerChestInventory;
    private final int numRows;

    public ContainerChest(IInventory playerInventory, IInventory chestInventory, EntityPlayer player)
    {
        this.lowerChestInventory = chestInventory;
        this.numRows = chestInventory.getSizeInventory() / 9;
        chestInventory.openInventory(player);
        int i = (this.numRows - 4) * 18;
        for (int j = 0; j < this.numRows; ++j)
        {
            for (int k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(chestInventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.lowerChestInventory.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            System.out.println("transfer");
            if(isFilledChest(itemstack1)) {
            	return ItemStack.EMPTY;
            }
            itemstack = itemstack1.copy();

            if (index < this.numRows * 9)
            {
                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.lowerChestInventory.closeInventory(playerIn);
    }

    /**
     * Return this chest container's lower chest inventory.
     */
    public IInventory getLowerChestInventory()
    {
        return this.lowerChestInventory;
    }
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        InventoryPlayer inventoryplayer = player.inventory;
        System.out.println(slotId);
        System.out.println(this.lowerChestInventory.getSizeInventory()-1);
        System.out.println(isFilledChest(inventoryplayer.getItemStack()));
        if(isFilledChest(inventoryplayer.getItemStack()) && slotId <= this.lowerChestInventory.getSizeInventory()-1 && slotId!= -999) {
        	return ItemStack.EMPTY;
        }
        System.out.println(clickTypeIn);
        if (clickTypeIn == ClickType.QUICK_CRAFT)
        {
            int j1 = this.dragEvent;
            this.dragEvent = getDragEvent(dragType);

            if ((j1 != 1 || this.dragEvent != 2) && j1 != this.dragEvent)
            {
                this.resetDrag();
            }
            else if (inventoryplayer.getItemStack().isEmpty())
            {
                this.resetDrag();
            }
            else if (this.dragEvent == 0)
            {
                this.dragMode = extractDragMode(dragType);

                if (isValidDragMode(this.dragMode, player))
                {
                    this.dragEvent = 1;
                    this.dragSlots.clear();
                }
                else
                {
                    this.resetDrag();
                }
            }
            else if (this.dragEvent == 1)
            {
                Slot slot7 = this.inventorySlots.get(slotId);
                ItemStack itemstack12 = inventoryplayer.getItemStack();

                if (slot7 != null && canAddItemToSlot(slot7, itemstack12, true) && slot7.isItemValid(itemstack12) && (this.dragMode == 2 || itemstack12.getCount() > this.dragSlots.size()) && this.canDragIntoSlot(slot7))
                {
                    this.dragSlots.add(slot7);
                }
            }
            else if (this.dragEvent == 2)
            {
                if (!this.dragSlots.isEmpty())
                {
                    ItemStack itemstack9 = inventoryplayer.getItemStack().copy();
                    int k1 = inventoryplayer.getItemStack().getCount();

                    for (Slot slot8 : this.dragSlots)
                    {
                        ItemStack itemstack13 = inventoryplayer.getItemStack();

                        if (slot8 != null && canAddItemToSlot(slot8, itemstack13, true) && slot8.isItemValid(itemstack13) && (this.dragMode == 2 || itemstack13.getCount() >= this.dragSlots.size()) && this.canDragIntoSlot(slot8))
                        {
                            ItemStack itemstack14 = itemstack9.copy();
                            int j3 = slot8.getHasStack() ? slot8.getStack().getCount() : 0;
                            computeStackSize(this.dragSlots, this.dragMode, itemstack14, j3);
                            int k3 = Math.min(itemstack14.getMaxStackSize(), slot8.getItemStackLimit(itemstack14));

                            if (itemstack14.getCount() > k3)
                            {
                                itemstack14.setCount(k3);
                            }

                            k1 -= itemstack14.getCount() - j3;
                            slot8.putStack(itemstack14);
                        }
                    }

                    itemstack9.setCount(k1);
                    inventoryplayer.setItemStack(itemstack9);
                }

                this.resetDrag();
            }
            else
            {
                this.resetDrag();
            }
        }
        else if (this.dragEvent != 0)
        {
            this.resetDrag();
        }
        else if ((clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) && (dragType == 0 || dragType == 1))
        {
            if (slotId == -999)
            {
                if (!inventoryplayer.getItemStack().isEmpty())
                {
                    if (dragType == 0)
                    {
                        player.dropItem(inventoryplayer.getItemStack(), true);
                        inventoryplayer.setItemStack(ItemStack.EMPTY);
                    }

                    if (dragType == 1)
                    {
                        player.dropItem(inventoryplayer.getItemStack().splitStack(1), true);
                    }
                }
            }
            else if (clickTypeIn == ClickType.QUICK_MOVE)
            {
                if (slotId < 0)
                {
                    return ItemStack.EMPTY;
                }

                Slot slot5 = this.inventorySlots.get(slotId);

                if (slot5 == null || !slot5.canTakeStack(player))
                {
                    return ItemStack.EMPTY;
                }

                for (ItemStack itemstack7 = this.transferStackInSlot(player, slotId); !itemstack7.isEmpty() && ItemStack.areItemsEqual(slot5.getStack(), itemstack7); itemstack7 = this.transferStackInSlot(player, slotId))
                {
                    itemstack = itemstack7.copy();
                }
            }
            else
            {
                if (slotId < 0)
                {
                    return ItemStack.EMPTY;
                }

                Slot slot6 = this.inventorySlots.get(slotId);

                if (slot6 != null)
                {
                    ItemStack itemstack8 = slot6.getStack();
                    ItemStack itemstack11 = inventoryplayer.getItemStack();

                    if (!itemstack8.isEmpty())
                    {
                        itemstack = itemstack8.copy();
                    }

                    if (itemstack8.isEmpty())
                    {
                        if (!itemstack11.isEmpty() && slot6.isItemValid(itemstack11))
                        {
                            int i3 = dragType == 0 ? itemstack11.getCount() : 1;

                            if (i3 > slot6.getItemStackLimit(itemstack11))
                            {
                                i3 = slot6.getItemStackLimit(itemstack11);
                            }

                            slot6.putStack(itemstack11.splitStack(i3));
                        }
                    }
                    else if (slot6.canTakeStack(player))
                    {
                        if (itemstack11.isEmpty())
                        {
                            if (itemstack8.isEmpty())
                            {
                                slot6.putStack(ItemStack.EMPTY);
                                inventoryplayer.setItemStack(ItemStack.EMPTY);
                            }
                            else
                            {
                                int l2 = dragType == 0 ? itemstack8.getCount() : (itemstack8.getCount() + 1) / 2;
                                inventoryplayer.setItemStack(slot6.decrStackSize(l2));

                                if (itemstack8.isEmpty())
                                {
                                    slot6.putStack(ItemStack.EMPTY);
                                }

                                slot6.onTake(player, inventoryplayer.getItemStack());
                            }
                        }
                        else if (slot6.isItemValid(itemstack11))
                        {
                            if (itemstack8.getItem() == itemstack11.getItem() && itemstack8.getMetadata() == itemstack11.getMetadata() && ItemStack.areItemStackTagsEqual(itemstack8, itemstack11))
                            {
                                int k2 = dragType == 0 ? itemstack11.getCount() : 1;

                                if (k2 > slot6.getItemStackLimit(itemstack11) - itemstack8.getCount())
                                {
                                    k2 = slot6.getItemStackLimit(itemstack11) - itemstack8.getCount();
                                }

                                if (k2 > itemstack11.getMaxStackSize() - itemstack8.getCount())
                                {
                                    k2 = itemstack11.getMaxStackSize() - itemstack8.getCount();
                                }

                                itemstack11.shrink(k2);
                                itemstack8.grow(k2);
                            }
                            else if (itemstack11.getCount() <= slot6.getItemStackLimit(itemstack11))
                            {
                                slot6.putStack(itemstack11);
                                inventoryplayer.setItemStack(itemstack8);
                            }
                        }
                        else if (itemstack8.getItem() == itemstack11.getItem() && itemstack11.getMaxStackSize() > 1 && (!itemstack8.getHasSubtypes() || itemstack8.getMetadata() == itemstack11.getMetadata()) && ItemStack.areItemStackTagsEqual(itemstack8, itemstack11) && !itemstack8.isEmpty())
                        {
                            int j2 = itemstack8.getCount();

                            if (j2 + itemstack11.getCount() <= itemstack11.getMaxStackSize())
                            {
                                itemstack11.grow(j2);
                                itemstack8 = slot6.decrStackSize(j2);

                                if (itemstack8.isEmpty())
                                {
                                    slot6.putStack(ItemStack.EMPTY);
                                }

                                slot6.onTake(player, inventoryplayer.getItemStack());
                            }
                        }
                    }

                    slot6.onSlotChanged();
                }
            }
        }
        else if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9)
        {
            Slot slot4 = this.inventorySlots.get(slotId);
            ItemStack itemstack6 = inventoryplayer.getStackInSlot(dragType);
            if(isFilledChest(itemstack6)) return ItemStack.EMPTY;
            ItemStack itemstack10 = slot4.getStack();

            if (!itemstack6.isEmpty() || !itemstack10.isEmpty())
            {
                if (itemstack6.isEmpty())
                {
                    if (slot4.canTakeStack(player))
                    {
                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
//                        slot4.onSwapCraft(itemstack10.getCount());
                        try {
							ReflectionHelper.findMethod(slot4.getClass(), "onSwapCraft", "b", int.class).invoke(itemstack10.getCount());
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        slot4.putStack(ItemStack.EMPTY);
                        slot4.onTake(player, itemstack10);
                    }
                }
                else if (itemstack10.isEmpty())
                {
                    if (slot4.isItemValid(itemstack6))
                    {
                        int l1 = slot4.getItemStackLimit(itemstack6);

                        if (itemstack6.getCount() > l1)
                        {
                            slot4.putStack(itemstack6.splitStack(l1));
                        }
                        else
                        {
                            slot4.putStack(itemstack6);
                            inventoryplayer.setInventorySlotContents(dragType, ItemStack.EMPTY);
                        }
                    }
                }
                else if (slot4.canTakeStack(player) && slot4.isItemValid(itemstack6))
                {
                    int i2 = slot4.getItemStackLimit(itemstack6);

                    if (itemstack6.getCount() > i2)
                    {
                        slot4.putStack(itemstack6.splitStack(i2));
                        slot4.onTake(player, itemstack10);

                        if (!inventoryplayer.addItemStackToInventory(itemstack10))
                        {
                            player.dropItem(itemstack10, true);
                        }
                    }
                    else
                    {
                        slot4.putStack(itemstack6);
                        inventoryplayer.setInventorySlotContents(dragType, itemstack10);
                        slot4.onTake(player, itemstack10);
                    }
                }
            }
        }
        else if (clickTypeIn == ClickType.CLONE && player.capabilities.isCreativeMode && inventoryplayer.getItemStack().isEmpty() && slotId >= 0)
        {
            Slot slot3 = this.inventorySlots.get(slotId);

            if (slot3 != null && slot3.getHasStack())
            {
                ItemStack itemstack5 = slot3.getStack().copy();
                itemstack5.setCount(itemstack5.getMaxStackSize());
                inventoryplayer.setItemStack(itemstack5);
            }
        }
        else if (clickTypeIn == ClickType.THROW && inventoryplayer.getItemStack().isEmpty() && slotId >= 0)
        {
            Slot slot2 = this.inventorySlots.get(slotId);

            if (slot2 != null && slot2.getHasStack() && slot2.canTakeStack(player))
            {
                ItemStack itemstack4 = slot2.decrStackSize(dragType == 0 ? 1 : slot2.getStack().getCount());
                slot2.onTake(player, itemstack4);
                player.dropItem(itemstack4, true);
            }
        }
        else if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0)
        {
            Slot slot = this.inventorySlots.get(slotId);
            ItemStack itemstack1 = inventoryplayer.getItemStack();

            if (!itemstack1.isEmpty() && (slot == null || !slot.getHasStack() || !slot.canTakeStack(player)))
            {
                int i = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
                int j = dragType == 0 ? 1 : -1;

                for (int k = 0; k < 2; ++k)
                {
                    for (int l = i; l >= 0 && l < this.inventorySlots.size() && itemstack1.getCount() < itemstack1.getMaxStackSize(); l += j)
                    {
                        Slot slot1 = this.inventorySlots.get(l);

                        if (slot1.getHasStack() && canAddItemToSlot(slot1, itemstack1, true) && slot1.canTakeStack(player) && this.canMergeSlot(itemstack1, slot1))
                        {
                            ItemStack itemstack2 = slot1.getStack();

                            if (k != 0 || itemstack2.getCount() != itemstack2.getMaxStackSize())
                            {
                                int i1 = Math.min(itemstack1.getMaxStackSize() - itemstack1.getCount(), itemstack2.getCount());
                                ItemStack itemstack3 = slot1.decrStackSize(i1);
                                itemstack1.grow(i1);

                                if (itemstack3.isEmpty())
                                {
                                    slot1.putStack(ItemStack.EMPTY);
                                }

                                slot1.onTake(player, itemstack3);
                            }
                        }
                    }
                }
            }

            this.detectAndSendChanges();
        }

        return itemstack;
    }
    /**
     * Checks if it's possible to add the given itemstack to the given slot.
     */
    public static boolean canAddItemToSlot(@Nullable Slot slotIn, ItemStack stack, boolean stackSizeMatters)
    {
        boolean flag = slotIn == null || !slotIn.getHasStack();
        if(isFilledChest(stack)) return false;
        if (!flag && stack.isItemEqual(slotIn.getStack()) && ItemStack.areItemStackTagsEqual(slotIn.getStack(), stack))
        {
            return slotIn.getStack().getCount() + (stackSizeMatters ? 0 : stack.getCount()) <= stack.getMaxStackSize();
        }
        else
        {
            return flag;
        }
    }
    /**
     Check if the chest is a chest with items stored
     */
    private static boolean isFilledChest(ItemStack stack) {
    	if(stack.isEmpty()) return false;
    	System.out.println("not empty");
    	if(!(stack.isItemEqual(new ItemStack(BetterChesting.chest)) || stack.isItemEqual(new ItemStack(BetterChesting.trappedchest)))) return false;
    	else {
    		if(stack.hasTagCompound()) {
    			System.out.println("has tag");
    			System.out.println(stack.getTagCompound());
    			if(stack.getTagCompound().hasKey("Items")) {
    				System.out.println("Items tag");
					NBTTagList item = stack.getTagCompound().getTagList("Items", 10);
					if(item.hasNoTags()) return false;
					else return true;
    			}else return false;
    		}else return false;
    	}
    }
}