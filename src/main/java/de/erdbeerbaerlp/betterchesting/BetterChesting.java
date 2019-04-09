package de.erdbeerbaerlp.betterchesting;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(name=BetterChesting.NAME, modid = BetterChesting.MODID, version = BetterChesting.VERSION)
public class BetterChesting {
	public static final String NAME = "Better Chesting";
	public static final String MODID = "betterchesting";
	public static final String VERSION = "1.0.0";
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent ev) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	@EventHandler
	public void init(FMLInitializationEvent ev) {
		
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent ev) {
	}
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
	    event.getRegistry().registerAll(new BlockChest(BlockChest.Type.BASIC).setRegistryName("minecraft", "chest").setUnlocalizedName("chest").setHardness(2.5F), new BlockChest(BlockChest.Type.TRAP).setRegistryName("minecraft", "trapped_chest").setUnlocalizedName("chestTrap").setHardness(2.5F));
	}

}