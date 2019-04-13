package de.erdbeerbaerlp.betterchesting;

import gui.GuiChest;
import gui.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.IInteractionObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(name=BetterChesting.NAME, modid = BetterChesting.MODID, version = BetterChesting.VERSION)
public class BetterChesting {
	public static final String NAME = "Better Chesting";
	public static final String MODID = "betterchesting";
	public static final String VERSION = "1.0.0";
	private final static Minecraft mc = Minecraft.getMinecraft();
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
	}

	public static void displayGUIChest(IInventory chestInventory, EntityPlayer p)
	{
		if(p instanceof EntityPlayerMP) p.displayGUIChest(chestInventory);
		String s = chestInventory instanceof IInteractionObject ? ((IInteractionObject)chestInventory).getGuiID() : "minecraft:container";

		if ("minecraft:chest".equals(s))
		{
			mc.displayGuiScreen(new GuiChest(p.inventory, chestInventory));
		}
		else if ("minecraft:hopper".equals(s))
		{
			mc.displayGuiScreen(new GuiHopper(p.inventory, chestInventory));
		}
		else if ("minecraft:furnace".equals(s))
		{
			mc.displayGuiScreen(new GuiFurnace(p.inventory, chestInventory));
		}
		else if ("minecraft:brewing_stand".equals(s))
		{
			mc.displayGuiScreen(new GuiBrewingStand(p.inventory, chestInventory));
		}
		else if ("minecraft:beacon".equals(s))
		{
			mc.displayGuiScreen(new GuiBeacon(p.inventory, chestInventory));
		}
		else if (!"minecraft:dispenser".equals(s) && !"minecraft:dropper".equals(s))
		{
			if ("minecraft:shulker_box".equals(s))
			{
				mc.displayGuiScreen(new GuiShulkerBox(p.inventory, chestInventory));
			}
			else
			{
				mc.displayGuiScreen(new GuiChest(p.inventory, chestInventory));
			}
		}
		else
		{
			mc.displayGuiScreen(new GuiDispenser(p.inventory, chestInventory));
		}
	}

}
