package com.rodabafilms.hnsutils.event;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.rodabafilms.hnsutils.game.GameData;
import com.rodabafilms.hnsutils.util.References;
import com.rodabafilms.hnsutils.game.GameData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class EventHandler {

	@SubscribeEvent
	public void onHurt(LivingHurtEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
		
		DamageSource source = event.getSource();
		Entity root = source.getImmediateSource();

		if (root instanceof EntityPlayer) {
			EntityPlayer cause = (EntityPlayer) root;
			EntityPlayer hurt = (EntityPlayer) event.getEntityLiving();

			if (cause.getTeam() != null && hurt.getTeam() != null) {
				if (cause.getTeam().isSameTeam(hurt.getTeam())) {
					EnumHand active = cause.getActiveHand();
					ItemStack stack = cause.getHeldItem(active);

					if (stack.getItem() != null
							&& (stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_seeker")
									|| stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_hider")))
						event.setAmount(0);
				} else {
					EnumHand active = cause.getActiveHand();
					ItemStack stack = cause.getHeldItem(active);

					if (stack.getItem() != null) {
						if (stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_hider"))
							event.setAmount(2);
						else if (stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_seeker"))
							event.setAmount(10);
					}

					if (hurt.getHealth() - event.getAmount() < 1) {
						event.setCanceled(true);
						hurt.setHealth(20F);

						List<EntityPlayer> playing = hurt.world.getPlayers(EntityPlayer.class,
								pl -> pl.getTeam() != null && (pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id")
										|| pl.getTeam().getName().equalsIgnoreCase("hns_hiders_id")));

						if (hurt.getTeam().getName().equalsIgnoreCase("hns_seekers_id")) {
							for (EntityPlayer sPlayer : playing) {
								sPlayer.sendMessage(new TextComponentString(
										ChatFormatting.RED + hurt.getName() + ChatFormatting.WHITE + " got killed by "
												+ ChatFormatting.BLUE + cause.getName()));
							}
						} else if (hurt.getTeam().getName().equalsIgnoreCase("hns_hiders_id")) {
							for (EntityPlayer sPlayer : playing) {
								sPlayer.sendMessage(new TextComponentString(
										ChatFormatting.BLUE + hurt.getName() + ChatFormatting.WHITE + " got killed by "
												+ ChatFormatting.RED + cause.getName()));
							}
						}

						hurt.clearActivePotions();

						hurt.setGameType(GameType.SPECTATOR);
						((EntityPlayerMP) hurt).setSpectatingEntity(event.getSource().getImmediateSource());

						onHNSDeath(hurt);
					}
				}
			}
			return;
		}

		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer hurt = (EntityPlayer) event.getEntityLiving();

			if (hurt.getTeam() == null)
				return;

			if (hurt.getHealth() - event.getAmount() < 1) {
				event.setCanceled(true);
				hurt.setHealth(20F);

				List<EntityPlayer> playing = hurt.world.getPlayers(EntityPlayer.class,
						pl -> pl.getTeam() != null && (pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id")
								|| pl.getTeam().getName().equalsIgnoreCase("hns_hiders_id")));

				if (hurt.getTeam().getName().equalsIgnoreCase("hns_seekers_id")) {
					for (EntityPlayer sPlayer : playing) {
						sPlayer.sendMessage(new TextComponentString(
								ChatFormatting.RED + hurt.getName() + ChatFormatting.WHITE + " is out!"));
					}
				} else if (hurt.getTeam().getName().equalsIgnoreCase("hns_hiders_id")) {
					for (EntityPlayer sPlayer : playing) {
						sPlayer.sendMessage(new TextComponentString(
								ChatFormatting.BLUE + hurt.getName() + ChatFormatting.WHITE + " is out!"));
					}
				}

				hurt.clearActivePotions();
				
				List<EntityPlayer> seekers = hurt.world.getPlayers(EntityPlayer.class,
						pl -> pl.getTeam() != null && pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id") && !pl.isSpectator());

				if (seekers.size() > 0) {
					hurt.setGameType(GameType.SPECTATOR);
					((EntityPlayerMP) hurt).setSpectatingEntity(seekers.get(0));
				} else {
					GameData gData = GameData.get(hurt.world);
					BlockPos point = gData.getRespawnPoint();

					hurt.setGameType(GameType.ADVENTURE);
					hurt.setPositionAndUpdate(point.getX() + 0.5F, point.getY(), point.getZ() + 0.5F);
				}

				onHNSDeath(hurt);
			}
		}
	}

	public void onHNSDeath(EntityPlayer player) {
		List<EntityPlayer> seekers = player.world.getPlayers(EntityPlayer.class,
				pl -> pl.getTeam() != null && pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id"));
		List<EntityPlayer> hiders = player.world.getPlayers(EntityPlayer.class,
				pl -> pl.getTeam() != null && pl.getTeam().getName().equalsIgnoreCase("hns_hiders_id"));

		new Thread(() ->  {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			
			player.getServer().addScheduledTask(new Runnable() {
				
				@Override
				public void run() {
					player.extinguish();
				}
			});
		}).start();
		
		player.fallDistance = 0;
		
		int initSeekerCount = seekers.size();
		int initHiderCount = hiders.size();

		for (EntityPlayer hider : hiders) {
			if (hider.isSpectator())
				initHiderCount--;
		}
		
		for (EntityPlayer seeker : seekers) {
			if (seeker.isSpectator())
				initSeekerCount--;
		}

		String winText = "";

		if (initHiderCount < 1) {
			winText = "All " + ChatFormatting.BLUE + "hiders " + ChatFormatting.WHITE + "have been eliminated: "
					+ ChatFormatting.RED + ChatFormatting.BOLD + "Seekers " + ChatFormatting.WHITE + ChatFormatting.BOLD
					+ "WIN";
		}

		if (initSeekerCount < 1) {
			winText = "All " + ChatFormatting.RED + "seekers " + ChatFormatting.WHITE + "have been eliminated: "
					+ ChatFormatting.BLUE + ChatFormatting.BOLD + "Hiders " + ChatFormatting.WHITE + ChatFormatting.BOLD
					+ "WIN";
		}

		if (!winText.isEmpty()) {
			GameData gData = GameData.get(player.world);
			gData.setGameStarted(false);
			BlockPos point = gData.getRespawnPoint();

			for (EntityPlayer seeker : seekers) {
				seeker.inventory.clear();
				seeker.sendMessage(new TextComponentString(winText));
				((EntityPlayerMP) seeker).setGameType(GameType.ADVENTURE);
				if (!seeker.world.isRemote) {
					seeker.world.getScoreboard().removePlayerFromTeams(seeker.getName());
				}
				seeker.setPositionAndUpdate(point.getX() + 0.5F, point.getY(), point.getZ() + 0.5F);

				seeker.clearActivePotions();
			}

			for (EntityPlayer hider : hiders) {
				hider.inventory.clear();
				hider.sendMessage(new TextComponentString(winText));
				((EntityPlayerMP) hider).setGameType(GameType.ADVENTURE);

				if (!hider.world.isRemote) {
					hider.world.getScoreboard().removePlayerFromTeams(hider.getName());
				}

				hider.setPositionAndUpdate(point.getX() + 0.5F, point.getY(), point.getZ() + 0.5F);

				hider.clearActivePotions();
			}
		}
	}

	@SubscribeEvent
	public void onAttack(LivingAttackEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer
				&& event.getSource().getImmediateSource() instanceof EntityPlayer) {
			EntityPlayer p1 = (EntityPlayer) event.getSource().getImmediateSource();
			EntityPlayer p2 = (EntityPlayer) event.getEntityLiving();

			if (p1.getTeam() != null && p2.getTeam() != null) {
				if (p1.getTeam().isSameTeam(p2.getTeam())) {
					if (p1.getActiveHand() != null) {
						EnumHand active = p1.getActiveHand();
						ItemStack stack = p1.getHeldItem(active);

						if (!(stack.getItem() != null
								&& (stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_seeker")
										|| stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_hider"))))
							event.setCanceled(true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.side.isClient())
			return;

		EntityPlayer player = event.player;
		if (player.getTeam() != null) {
			if (player.getTeam().getName().equalsIgnoreCase("hns_seekers_id")
					|| player.getTeam().getName().equalsIgnoreCase("hns_hiders_id")) {
				if (player.isSpectator()) {
					if (((EntityPlayerMP)player).getSpectatingEntity().getName().equalsIgnoreCase(player.getName())) {
						player.getServer().addScheduledTask(new Runnable() {
							
							@Override
							public void run() {
								List<EntityPlayer> seekers = player.world.getPlayers(EntityPlayer.class,
										pl -> pl.getTeam() != null
												&& pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id") && !pl.isSpectator());

								if (seekers.size() > 0) {
									((EntityPlayerMP) player).setSpectatingEntity(seekers.get(0));
								} else {
									GameData data = GameData.get(player.world);

									player.setGameType(GameType.ADVENTURE);
									player.setPositionAndUpdate(data.getRespawnPoint().getX() + 0.5F,
											data.getRespawnPoint().getY(), data.getRespawnPoint().getZ() + 0.5F);
									player.world.getScoreboard().removePlayerFromTeams(player.getName());
								}
							}
						});
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemDrop(ItemTossEvent event) {
		ItemStack itemStack = event.getEntityItem().getItem();

		if (event.getEntityItem().getThrower() == null) {
			if (itemStack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_hider")) {
				event.getEntityItem().setPickupDelay(0);
			} else if (itemStack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_seeker")) {
				event.getEntityItem().setPickupDelay(0);
			}

			return;
		}

		boolean cancel = false;

		if (itemStack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_hider")) {
			cancel = true;
		} else if (itemStack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_seeker")) {
			cancel = true;
		}

		if (cancel) {
			event.getPlayer().inventory.addItemStackToInventory(itemStack);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onEntityItemPickup(EntityItemPickupEvent event) {
		if (event.getEntityPlayer().getTeam() != null) {
			ItemStack itemStack = event.getItem().getItem();

			if (event.getEntityPlayer().getTeam().getName().equalsIgnoreCase("hns_hiders_id")) {
				if (itemStack.getItem().getUnlocalizedName().equalsIgnoreCase("item.hider_finder"))
					event.setCanceled(true);
				else if (itemStack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_seeker"))
					event.setCanceled(true);
			} else if (event.getEntityPlayer().getTeam().getName().equalsIgnoreCase("hns_seekers_id")) {
				if (itemStack.getItem().getUnlocalizedName().equalsIgnoreCase("item.stick_hider"))
					event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onClientWorldJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		new Thread(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}

			try {
				HttpGet request = new HttpGet("https://mcmod.rodabafilms.nl/hnsutils/version?q=" + References.VERSION);

				request.addHeader(HttpHeaders.USER_AGENT, "HNSUtils Client HTTP UA");

				try (CloseableHttpResponse response = HttpClients.createDefault().execute(request)) {

					if (response.getStatusLine().getStatusCode() != 200
							&& (response.getStatusLine().getStatusCode() == 401
									|| response.getStatusLine().getStatusCode() == 403)) {
						HttpEntity entity = response.getEntity();

						boolean majorOutdate = response.getStatusLine().getStatusCode() == 401;

						if (entity != null) {
							String result = EntityUtils.toString(entity);

							Minecraft.getMinecraft().addScheduledTask(new Runnable() {

								@Override
								public void run() {
									EntityPlayerSP player = Minecraft.getMinecraft().player;
									
									ITextComponent minorUpdate = new TextComponentString("" + ChatFormatting.GOLD
											+ ChatFormatting.BOLD + "HNSUtils " + ChatFormatting.GREEN + ">> "
											+ ChatFormatting.AQUA + "The current version (" + References.VERSION
											+ ") is outdated");
									
									ITextComponent majorUpdate = new TextComponentString("" + ChatFormatting.RED
											+ ChatFormatting.BOLD + "HNSUtils " + ChatFormatting.GREEN + ">> "
											+ ChatFormatting.AQUA + "The current version (" 
											+ ChatFormatting.RED + ChatFormatting.BOLD + References.VERSION
											+ ChatFormatting.AQUA + ") is outdated");
									
									player.sendMessage(majorOutdate ? majorUpdate : minorUpdate);

									player.sendMessage(new TextComponentString("" + ChatFormatting.GOLD
											+ ChatFormatting.BOLD + "HNSUtils " + ChatFormatting.GREEN + ">> "
											+ ChatFormatting.AQUA + "Please consider updating to " + result + " ")
													.appendText("here")
													.setStyle(new Style().setColor(TextFormatting.AQUA).setBold(true)
															.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
																	"https://mcmod.rodabafilms.nl/hnsutils/versions/latest"))));
								}
							});
						}
					}
				}
			} catch (Exception ex) {

			}
		}).start();
	}
}