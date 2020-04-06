package com.rodabafilms.hnsutils.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.rodabafilms.hnsutils.game.GameData;
import com.rodabafilms.hnsutils.game.GameData;
import com.rodabafilms.hnsutils.items.HiderFinderItem;
import com.rodabafilms.hnsutils.util.Logger;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MessageHiderFinder extends MessageBase<MessageHiderFinder> {
	
	private HiderFinderMessageType packetType;
	private EnumHand initiatorHandIn;
	private String errorMessage;
	
	public MessageHiderFinder() {
		packetType = HiderFinderMessageType.UNKNOWN;
		initiatorHandIn = EnumHand.MAIN_HAND;
		errorMessage = "";
	}
	
	public MessageHiderFinder(EnumHand handIn) {
		packetType = HiderFinderMessageType.INITIATOR;
		initiatorHandIn = handIn;
		errorMessage = "";
	}
	
	public MessageHiderFinder(String errorMessage) {
		packetType = HiderFinderMessageType.INITIATOR_FAIL;
		initiatorHandIn = EnumHand.MAIN_HAND;
		this.errorMessage = errorMessage;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int enumSize = buf.readInt();
		
		byte[] array = new byte[enumSize];
		buf.readBytes(array);
		
		String enumName = new String(array);
		
		packetType = Enum.valueOf(HiderFinderMessageType.class, enumName);
		
		enumSize = buf.readInt();
		
		array = new byte[enumSize];
		buf.readBytes(array);
		
		enumName = new String(array);
		
		initiatorHandIn = Enum.valueOf(EnumHand.class, enumName);
	
		enumSize = buf.readInt();
		
		array = new byte[enumSize];
		buf.readBytes(array);
		
		errorMessage = new String(array);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(packetType.name().getBytes().length);
		buf.writeBytes(packetType.name().getBytes());
		
		buf.writeInt(initiatorHandIn.name().getBytes().length);
		buf.writeBytes(initiatorHandIn.name().getBytes());
		
		buf.writeInt(errorMessage.getBytes().length);
		buf.writeBytes(errorMessage.getBytes());
	}

	@Override
	public void handleClientSide(MessageHiderFinder message, EntityPlayer player) {
		if (message.packetType == HiderFinderMessageType.INITIATOR_FAIL) {
			player.sendMessage(new TextComponentString(message.errorMessage).setStyle(new Style().setColor(TextFormatting.RED)));
		}
	}

	@Override
	public void handleServerSide(MessageHiderFinder message, EntityPlayer player) {
		if (message.packetType != HiderFinderMessageType.INITIATOR) return;
		
		if (player.getHeldItem(initiatorHandIn) == null) return;
		if (!(player.getHeldItem(initiatorHandIn).getItem() instanceof HiderFinderItem)) return;
		
		if (player.getTeam() == null) {
			NetworkHandler.sendTo(new MessageHiderFinder("You must be a seeker to use this item"), (EntityPlayerMP)player);	
			
			return;
		}
		
		if (!player.getTeam().getName().equalsIgnoreCase("hns_seekers_id")) {
			NetworkHandler.sendTo(new MessageHiderFinder("You must be a seeker to use this item"), (EntityPlayerMP)player);
			
			return;
		}

		WorldServer worldIn = (WorldServer)player.world;
		
		GameData gData = GameData.get(worldIn);
		
		if (worldIn.getTotalWorldTime() - gData.getHFLastUseTime() < gData.getHFUsageTimeout() * 20) {
			int wait = (int)Math.floor(gData.getHFUsageTimeout() - (worldIn.getTotalWorldTime() - gData.getHFLastUseTime()) / 20);
			NetworkHandler.sendTo(new MessageHiderFinder("You must wait " + String.valueOf(wait) + " second" + (wait == 1 ? "" : "s") + " before doing that again"), (EntityPlayerMP)player);
			
			return;
		}
		
		gData.setHFLastUseTime(worldIn.getTotalWorldTime());
		
		List<EntityPlayer> hiders = worldIn.getEntities(EntityPlayer.class, p -> p.getTeam() != null && p.getTeam().getName().equalsIgnoreCase("hns_hiders_id"));
		
		for (EntityPlayer ph : hiders) {
			if (!ph.isSpectator())
				worldIn.playSound(ph, ph.getPosition(), SoundEvents.ENTITY_CAT_AMBIENT, SoundCategory.PLAYERS, 5F, 0.11F);
		}
	}
	
	public enum HiderFinderMessageType {
		UNKNOWN,
		INITIATOR,
		INITIATOR_FAIL
	}
}