package com.rodabafilms.hnsutils.items;

import com.rodabafilms.hnsutils.HNSUtilsMain;
import com.rodabafilms.hnsutils.items.init.ModItems;
import com.rodabafilms.hnsutils.util.IHasModel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel
{

	public ItemBase(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.COMBAT);
		
		ModItems.ITEMS.add(this);
	}
	
	@Override
	public void registerModels() {
		HNSUtilsMain.proxy.registerItemRenderer(this, 0, "inventory");
	}
}