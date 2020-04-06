package com.rodabafilms.hnsutils.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GuiSelectTeam.GUIID)
			return new GuiSelectTeam.GuiContainerMod(world, x, y, z, player);
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GuiSelectTeam.GUIID)
			return new GuiSelectTeam(world, x, y, z, player);
		return null;
	}
}
