package de.erdbeerbaerlp.betterchesting;

import java.util.logging.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(name=BetterChesting.NAME, modid = BetterChesting.MODID, version = BetterChesting.VERSION)
public class BetterChesting {
	public static final String NAME = "Better Chesting";
	public static final String MODID = "betterchesting";
	public static final String VERSION = "1.1.0";
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent ev) {
		Logger.getLogger(MODID).info("Crafting chests...");
	}
	@EventHandler
	public void init(FMLInitializationEvent ev) {
		Logger.getLogger(MODID).info("Placing chests...");
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent ev) {
		Logger.getLogger(MODID).info("Filling chests...");
	}
	@EventHandler
	public void complete(FMLLoadCompleteEvent ev) {
		Logger.getLogger(MODID).info("Breaking chests again...");
	}

}
