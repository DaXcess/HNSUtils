package com.rodabafilms.hnsutils.items.init;

import java.util.ArrayList;
import java.util.List;

import com.rodabafilms.hnsutils.items.HiderFinderItem;
import com.rodabafilms.hnsutils.items.HiderStickItem;
import com.rodabafilms.hnsutils.items.ItemBase;
import com.rodabafilms.hnsutils.items.SeekerStickItem;
import com.rodabafilms.hnsutils.items.SmokeBombItem;

import net.minecraft.item.Item;

public class ModItems 
{
	
	public static final List<Item> ITEMS = new ArrayList<Item>();

	public static final Item SMOKE_BOMB = new SmokeBombItem("smoke_bomb");
	public static final Item HIDER_FINDER = new HiderFinderItem("hider_finder");
	public static final Item HIDER_STICK = new HiderStickItem();
	public static final Item SEEKER_STICK = new SeekerStickItem();
	
}
