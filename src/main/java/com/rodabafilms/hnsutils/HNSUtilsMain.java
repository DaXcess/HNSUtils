package com.rodabafilms.hnsutils;

import com.rodabafilms.hnsutils.command.HNSCommand;
import com.rodabafilms.hnsutils.gui.GuiHandler;
import com.rodabafilms.hnsutils.network.NetworkHandler;
import com.rodabafilms.hnsutils.proxy.CommonProxy;
import com.rodabafilms.hnsutils.util.Logger;
import com.rodabafilms.hnsutils.util.References;

import net.minecraft.command.CommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod(modid = References.MOD_ID, name = References.MOD_NAME, version = References.VERSION)
public class HNSUtilsMain {

	@Instance
	public static HNSUtilsMain instance;
	
	@SidedProxy(clientSide = References.CLIENT_PROXY_CLASS, serverSide = References.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@EventHandler
	public static void PreInit(FMLPreInitializationEvent event) 
	{
		Logger.SetLogger(event.getModLog());
		NetworkHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(HNSUtilsMain.instance, new GuiHandler());
		
		MinecraftForge.EVENT_BUS.register(new com.rodabafilms.hnsutils.event.EventHandler());
	}
	
	@EventHandler
	public static void init(FMLServerStartingEvent event) 
	{
		event.registerServerCommand(new HNSCommand());

		NetworkRegistry.INSTANCE.registerGuiHandler(HNSUtilsMain.instance, new GuiHandler());
	}
	
	@EventHandler
	public static void PostInit(FMLPostInitializationEvent event) 
	{
		
	}
}
