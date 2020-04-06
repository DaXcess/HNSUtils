package com.rodabafilms.hnsutils.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class SmokeBombItem extends ItemBase {

	public SmokeBombItem(String name) {
		super(name);
		
		setMaxStackSize(16);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack item = playerIn.getHeldItem(handIn);
		
		if (!playerIn.isCreative())
			item.shrink(1);

		double particleSpeed = 0.29D;
		
		if (!worldIn.isRemote) {
			worldIn.playSound(null, playerIn.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.MASTER, 20F, 0.5F);
			((WorldServer)worldIn).spawnParticle(EnumParticleTypes.SMOKE_LARGE, playerIn.posX, playerIn.posY, playerIn.posZ, 300, 0, 0, 0, particleSpeed);
		} 
		
		playerIn.addPotionEffect(new PotionEffect(MobEffects.SPEED, 5 * 20, 2));
		playerIn.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 5 * 20, 1));
		
		if (!worldIn.isRemote) {
			List<EntityPlayer> players = worldIn.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(playerIn.posX - 4, playerIn.posY - 4, playerIn.posZ - 4, playerIn.posX + 4, playerIn.posY + 4, playerIn.posZ + 4));
			
			for(EntityPlayer player : players) {
				if (playerIn != player && !player.isOnSameTeam(playerIn)) {
					player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 6 * 20, 1));
					player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 2 * 20, 2));
				}
			}
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, item);
	}
}
