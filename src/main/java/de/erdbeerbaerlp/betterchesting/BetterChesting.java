package de.erdbeerbaerlp.betterchesting;

import de.erdbeerbaerlp.betterchesting.gui.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(name=BetterChesting.NAME, modid = BetterChesting.MODID, version = BetterChesting.VERSION)
public class BetterChesting {
	public static final String NAME = "Better Chesting";
	public static final String MODID = "betterchesting";
	public static final String VERSION = "1.0.0";
	public static BetterChesting instance;
	public BetterChesting() {
		instance = this;
	}
	@EventHandler
	public void preInit(FMLPreInitializationEvent ev) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	@EventHandler
	public void init(FMLInitializationEvent ev) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent ev) {
	}
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(new BlockChest(BlockChest.Type.BASIC).setRegistryName("minecraft", "chest").setUnlocalizedName("chest").setHardness(2.5F), new BlockChest(BlockChest.Type.TRAP).setRegistryName("minecraft", "trapped_chest").setUnlocalizedName("chestTrap").setHardness(2.5F));
		GameRegistry.registerTileEntity(TileEntityChest.class, new ResourceLocation(BetterChesting.MODID, "betterTileEntityChest"));
	}


}
