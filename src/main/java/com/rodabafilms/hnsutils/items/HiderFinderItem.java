package com.rodabafilms.hnsutils.items;

import java.util.List;

import com.rodabafilms.hnsutils.network.MessageHiderFinder;
import com.rodabafilms.hnsutils.network.NetworkHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class HiderFinderItem extends ItemBase {
	
	public HiderFinderItem(String name) {
		super(name);
		
		setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack item = playerIn.getHeldItem(handIn);
		
		if (worldIn.isRemote) 
		{
			NetworkHandler.sendToServer(new MessageHiderFinder(handIn));
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}
}
