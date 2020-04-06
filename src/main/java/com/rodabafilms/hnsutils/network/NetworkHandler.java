package com.rodabafilms.hnsutils.network;

import com.rodabafilms.hnsutils.gui.GuiSelectTeam.GUIButtonPressedMessage;
import com.rodabafilms.hnsutils.gui.GuiSelectTeam.GUIButtonPressedMessageHandler;
import com.rodabafilms.hnsutils.util.References;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler 
{
	private static SimpleNetworkWrapper INSTANCE;
	
	public static void init() {
		INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(References.MOD_ID);
		
		registerPackets();
	}
	
	private static void registerPackets() {
		INSTANCE.registerMessage(MessageHiderFinder.class, MessageHiderFinder.class, 0, Side.SERVER);
		INSTANCE.registerMessage(MessageHiderFinder.class, MessageHiderFinder.class, 1, Side.CLIENT);

		INSTANCE.registerMessage(GUIButtonPressedMessageHandler.class, GUIButtonPressedMessage.class, 0, Side.SERVER);
	}
	
	public static void sendToServer(IMessage message) {
		INSTANCE.sendToServer(message);
	}
	
	public static void sendTo(IMessage message, EntityPlayerMP player) {
		INSTANCE.sendTo(message, player);
	}
}