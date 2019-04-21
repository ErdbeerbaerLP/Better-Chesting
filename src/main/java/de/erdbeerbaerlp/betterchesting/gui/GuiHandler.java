package de.erdbeerbaerlp.betterchesting.gui;

import javax.annotation.Nullable;

import de.erdbeerbaerlp.betterchesting.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	@Nullable
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		switch (id) {
			case GuiChest.GUI_ID:
				if(world.getBlockState(pos).getBlock() instanceof BlockChest) return new ContainerChest(player.inventory, ((BlockChest)world.getBlockState(pos).getBlock()).getLockableContainer(world, pos), player);
			default:
				return null;
		}
	}
	
	@Nullable
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		switch (id) {
			case GuiChest.GUI_ID:
				if(world.getBlockState(pos).getBlock() instanceof BlockChest) return new GuiChest(player.inventory, ((BlockChest)world.getBlockState(pos).getBlock()).getLockableContainer(world, pos), pos);
			default:
				return null;
		}
	}
}