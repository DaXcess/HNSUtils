package com.rodabafilms.hnsutils.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.rodabafilms.hnsutils.HNSUtilsMain;
import com.rodabafilms.hnsutils.game.GameData;
import com.rodabafilms.hnsutils.gui.GuiSelectTeam;
import com.rodabafilms.hnsutils.items.HiderStickItem;
import com.rodabafilms.hnsutils.items.SeekerStickItem;
import com.rodabafilms.hnsutils.items.init.ModItems;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandTitle;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import scala.actors.threadpool.Arrays;

public class HNSCommand extends CommandBase {

	@Override
	public String getName() {
		return "hns";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.hns.usage";
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
			
		
		if (args.length == 1) {
			String command = args[0];
			
			String[] allPossibles = new String[] {
					"hiderSpawn", 
					"seekerSpawn", 
					"respawnPoint", 
					"hiderFinderTimeout",
					"smokeBombSpawnCount",
					"seekerSpawnTimeout",
					"seekersSpawnWithHiderFinder",
					"setTeams",
					"roundTime",
					"start",
					"stop"
			};
			
			if (command.isEmpty())
				return Arrays.asList(allPossibles);
			
			List<String> possibles = new ArrayList<String>();
			
			for (String possibleEntry : allPossibles) {
				if (possibleEntry.toLowerCase().startsWith(command.toLowerCase()))
					possibles.add(possibleEntry);
			}
			
			return possibles;
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("seekersSpawnWithHiderFinder")) {
				String command = args[1];
				
				String[] allPossibles = new String[] {
						"true",
						"false"
				};
				
				List<String> possibles = new ArrayList<String>();
				
				for (String possibleEntry : allPossibles) {
					if (possibleEntry.toLowerCase().startsWith(command.toLowerCase()))
						possibles.add(possibleEntry);
				}
				
				return possibles;
			}
		}
		
		return new ArrayList<String>();
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayer)) {
			sender.sendMessage(new TextComponentString("Only players may execute this command"));
			return;
		}

		EntityPlayer player = (EntityPlayer) sender;

		if (args != null && args.length > 0) {
			if (args[0].equalsIgnoreCase("hiderSpawn")) {
				GameData gData = GameData.get(player.world);
				gData.setHiderSpawn(player.getPosition());

				sender.sendMessage(new TextComponentString("Set hider's spawn point to " + player.getPosition().getX()
						+ " " + player.getPosition().getY() + " " + player.getPosition().getZ()));
			} else if (args[0].equalsIgnoreCase("seekerSpawn")) {
				GameData gData = GameData.get(player.world);
				gData.setSeekerSpawn(player.getPosition());

				sender.sendMessage(new TextComponentString("Set seeker's spawn point to " + player.getPosition().getX()
						+ " " + player.getPosition().getY() + " " + player.getPosition().getZ()));
			} else if (args[0].equalsIgnoreCase("respawnPoint")) {
				GameData gData = GameData.get(player.world);
				gData.setRespawnPoint(player.getPosition());

				sender.sendMessage(new TextComponentString("Set respawn point to " + player.getPosition().getX() + " "
						+ player.getPosition().getY() + " " + player.getPosition().getZ()));
			} else if (args[0].equalsIgnoreCase("hiderFinderTimeout")) {
				if (args.length < 2) {
					GameData gData = GameData.get(player.world);
					int secTimeout = gData.getHFUsageTimeout();

					sender.sendMessage(new TextComponentString("Hider finder timeout is currently set to "
							+ String.valueOf(secTimeout) + " second" + (secTimeout == 1 ? "" : "s")));

					return;
				}

				try {
					int secTimeout = Integer.parseInt(args[1]);

					if (secTimeout < 1 || secTimeout > 600) {
						sender.sendMessage(new TextComponentString("Hider finder timeout must be between 1 and 600")
								.setStyle(new Style().setColor(TextFormatting.RED)));
						return;
					}

					GameData gData = GameData.get(player.world);
					gData.setHFUsageTimeout(secTimeout);

					sender.sendMessage(new TextComponentString("Set hider finder item usage timeout to " + args[1]
							+ " second" + (secTimeout == 1 ? "" : "s")));
				} catch (Exception ex) {
					sender.sendMessage(
							new TextComponentString("Hider finder timeout must be a valid number between 1 and 600")
									.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}
			} else if (args[0].equalsIgnoreCase("roundTime")) {
				if (args.length < 2) {
					GameData gData = GameData.get(player.world);
					int roundTime = gData.getRoundTime();

					sender.sendMessage(new TextComponentString("Round time is currently set to "
							+ (roundTime == -1 ? "forever" : String.valueOf(roundTime) + " second" + (roundTime == 1 ? "" : "s"))));

					return;
				}

				try {
					int roundTime = Integer.parseInt(args[1]);

					if ((roundTime < 1 || roundTime > 60 * 30) && roundTime != -1) {
						sender.sendMessage(new TextComponentString("Round time must be between 1 and 1800 or -1 (forever)")
								.setStyle(new Style().setColor(TextFormatting.RED)));
						return;
					}

					GameData gData = GameData.get(player.world);
					gData.setRoundTime(roundTime);

					sender.sendMessage(new TextComponentString("Set round time to " + 
							(roundTime == -1 ? "forever" : args[1] + " second" + (roundTime == 1 ? "" : "s"))));
				} catch (Exception ex) {
					sender.sendMessage(
							new TextComponentString("Round time must be a valid number between 1 and 1800 or -1 (forever)")
									.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}
			} else if (args[0].equalsIgnoreCase("smokeBombSpawnCount")) {
				if (args.length < 2) {
					GameData gData = GameData.get(player.world);
					int count = gData.getSmokeBombSpawnCount();

					sender.sendMessage(new TextComponentString(
							"Smoke bomb spawn count is currently set to " + String.valueOf(count)));

					return;
				}

				try {
					int count = Integer.parseInt(args[1]);

					if (count < 0 || count > 16) {
						sender.sendMessage(new TextComponentString("Smoke bomb spawn count must be between 0 and 16")
								.setStyle(new Style().setColor(TextFormatting.RED)));
						return;
					}

					GameData gData = GameData.get(player.world);
					gData.setSmokeBombSpawnCount(count);

					sender.sendMessage(new TextComponentString("Set smoke bomb spawn count to " + args[1]));
				} catch (Exception ex) {
					sender.sendMessage(
							new TextComponentString("Smoke bomb spawn count must be a valid number between 0 and 16")
									.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}
			} else if (args[0].equalsIgnoreCase("seekerSpawnTimeout")) {
				if (args.length < 2) {
					GameData gData = GameData.get(player.world);
					int count = gData.getSeekerSpawnTimeout();

					sender.sendMessage(new TextComponentString(
							"Seeker spawn timeout is currently set to " + String.valueOf(count)
							+ " second" + (count == 1 ? "" : "s")));

					return;
				}

				try {
					int count = Integer.parseInt(args[1]);

					if (count < 1 || count > 300) {
						sender.sendMessage(new TextComponentString("Seeker spawn timeout must be between 1 and 300")
								.setStyle(new Style().setColor(TextFormatting.RED)));
						return;
					}

					GameData gData = GameData.get(player.world);
					gData.setSeekerSpawnTimeout(count);

					sender.sendMessage(new TextComponentString("Set seeker spawn timeout to " + args[1]
							+ " second" + (count == 1 ? "" : "s")));
				} catch (Exception ex) {
					sender.sendMessage(
							new TextComponentString("Seeker spawn timeout must be a valid number between 1 and 300")
									.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}
			} else if (args[0].equalsIgnoreCase("seekersSpawnWithHiderFinder")) {
				if (args.length < 2) {
					GameData gData = GameData.get(player.world);
					boolean isSet = gData.getSeekersSpawnWithHF();

					sender.sendMessage(new TextComponentString(
							"Seekers spawn with hider finder is currently set to " + (isSet ? "true" : "false")));

					return;
				}

				try {
					if (!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
						sender.sendMessage(
								new TextComponentString("Seekers spawn with hider finder must be true or false")
										.setStyle(new Style().setColor(TextFormatting.RED)));
						return;
					}

					GameData gData = GameData.get(player.world);
					gData.setSeekersSpawnWithHF(args[1].equalsIgnoreCase("true"));

					sender.sendMessage(
							new TextComponentString("Seekers spawn with hider finder set to " + args[1].toLowerCase()));
				} catch (Exception ex) {
					sender.sendMessage(new TextComponentString("Seekers spawn with hider finder must be true or false")
							.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}
			} else if (args[0].equalsIgnoreCase("setTeams")) {
				if (player.world.isRemote)
					return;

				List<EntityPlayer> players = player.world.getPlayers(EntityPlayer.class, p -> true);

				for (EntityPlayer p : players) {
					p.openGui(HNSUtilsMain.instance, GuiSelectTeam.GUIID, player.world, player.getPosition().getX(),
							player.getPosition().getY(), player.getPosition().getZ());
				}
			} else if (args[0].equalsIgnoreCase("reset")) {
				List<EntityPlayer> playing = player.world.getPlayers(EntityPlayer.class,
						pl -> pl.getTeam() != null && (pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id")
								|| pl.getTeam().getName().equalsIgnoreCase("hns_hiders_id")));

				for (PotionEffect pot : player.getActivePotionEffects()) {
					player.removePotionEffect(pot.getPotion());
				}

				player.setGameType(GameType.ADVENTURE);
				player.world.getScoreboard().removePlayerFromTeams(player.getName());

				GameData data = GameData.get(player.world);

				data.setHFLastUseTime(0);
				data.setHFUsageTimeout(60);
				data.setHiderSpawn(new BlockPos(0, 0, 0));
				data.setSeekerSpawn(new BlockPos(0, 0, 0));
				data.setRespawnPoint(new BlockPos(0, 0, 0));
				data.setSmokeBombSpawnCount(0);
				data.setSeekersSpawnWithHF(false);
				data.setSeekerSpawnTimeout(30);
				data.setRoundTime(5 * 60);
				
				player.sendMessage(new TextComponentString(
						"All Hide and Seek settings have been reverted to their original values"));
			} else if (args[0].equalsIgnoreCase("stop")) {
				GameData gData = GameData.get(player.world);
				
				if (!gData.isGameStarted()) {
					sender.sendMessage(new TextComponentString("There is no game in progress at the moment")
							.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}
				
				List<EntityPlayer> playing = player.world.getPlayers(EntityPlayer.class,
						pl -> pl.getTeam() != null && pl.getTeam().getName().equalsIgnoreCase("hns_hiders_id"));
				
				for (EntityPlayer pl : playing) {
					if (!pl.isSpectator())
						pl.attackEntityFrom(DamageSource.MAGIC, 20F);
				}
			} else if (args[0].equalsIgnoreCase("start")) {
				GameData gData = GameData.get(player.world);
				
				if (gData.isGameStarted()) {
					sender.sendMessage(new TextComponentString("A game is already in progress")
							.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}

				if (!gData.isHiderSpawnSet()) {
					sender.sendMessage(new TextComponentString("You have not set the hider's spawn point")
							.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}

				if (!gData.isSeekerSpawnSet()) {
					sender.sendMessage(new TextComponentString("You have not set the seeker's spawn point")
							.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}

				if (!gData.isRespawnPointSet()) {
					sender.sendMessage(new TextComponentString("You have not set the respawn point")
							.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}

				List<EntityPlayer> hiders = player.world.getPlayers(EntityPlayer.class,
						p -> p.getTeam() != null && p.getTeam().getName().equalsIgnoreCase("hns_hiders_id"));
				List<EntityPlayer> seekers = player.world.getPlayers(EntityPlayer.class,
						p -> p.getTeam() != null && p.getTeam().getName().equalsIgnoreCase("hns_seekers_id"));

				if (hiders.size() < 1 || seekers.size() < 1) {
					sender.sendMessage(new TextComponentString("Make sure both teams have at least one player in them")
							.setStyle(new Style().setColor(TextFormatting.RED)));
					return;
				}

				gData.setGameStarted(true);
				
				for (EntityPlayer hider : hiders) {
					hider.inventory.clear();
					hider.inventory.addItemStackToInventory(new ItemStack(ModItems.HIDER_STICK));

					if (gData.getSmokeBombSpawnCount() > 0) {
						hider.inventory.addItemStackToInventory(
								new ItemStack(ModItems.SMOKE_BOMB, gData.getSmokeBombSpawnCount()));
					}

					hider.setHealth(20F);

					hider.clearActivePotions();

					BlockPos point = gData.getHiderSpawn();
					BlockPos respawn = gData.getRespawnPoint();

					hider.setPositionAndUpdate(point.getX() + 0.5F, point.getY(), point.getZ() + 0.5F);
					hider.setGameType(GameType.ADVENTURE);
					hider.setSpawnPoint(new BlockPos(respawn.getX() + 0.5F, respawn.getY(), respawn.getZ() + 0.5F),
							true);
				}

				for (EntityPlayer seeker : seekers) {
					seeker.inventory.clear();
					seeker.inventory.addItemStackToInventory(new ItemStack(ModItems.SEEKER_STICK));

					if (gData.getSeekersSpawnWithHF()) {
						seeker.inventory.addItemStackToInventory(new ItemStack(ModItems.HIDER_FINDER));
					}

					if (gData.getSmokeBombSpawnCount() > 0) {
						seeker.inventory.addItemStackToInventory(
								new ItemStack(ModItems.SMOKE_BOMB, gData.getSmokeBombSpawnCount()));
					}

					seeker.addPotionEffect(new PotionEffect(MobEffects.HEALTH_BOOST, 1000000, 9, false, false));
					seeker.setHealth(60F);

					BlockPos point = gData.getSeekerSpawn();
					BlockPos respawn = gData.getRespawnPoint();

					seeker.setPositionAndUpdate(respawn.getX() + 0.5F, respawn.getY(), respawn.getZ() + 0.5F);
					seeker.setGameType(GameType.ADVENTURE);
					seeker.setSpawnPoint(new BlockPos(respawn.getX() + 0.5F, respawn.getY(), respawn.getZ() + 0.5F),
							true);
				}
				
				int spawnTimeout = gData.getSeekerSpawnTimeout();
				int roundTime = gData.getRoundTime();
				BlockPos point = gData.getSeekerSpawn();
				
				new Thread(() -> {						
					if (spawnTimeout > 0) {							
						for (int i = 0; i < spawnTimeout; i++) {
							if (!gData.isGameStarted()) {
								return;
							}
							
							int timeLeft = spawnTimeout - i - 1;
							
							int mins = (int) Math.floor(timeLeft / 60);
							int secs = timeLeft % 60;
							
							SPacketTitle titlePacket = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, new TextComponentString("Seekers released -> 0" + String.valueOf(mins) + ":" + (secs < 10 ? "0" + String.valueOf(secs) : String.valueOf(secs))));
							
							for (EntityPlayer seeker : seekers) {
								((EntityPlayerMP)seeker).connection.sendPacket(titlePacket);
							}
							
							for (EntityPlayer hider : hiders) {
								((EntityPlayerMP)hider).connection.sendPacket(titlePacket);
							}
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
					if (!gData.isGameStarted()) {
						return;
					}
					
					SPacketTitle titlePacket = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, new TextComponentString("" + ChatFormatting.RED + ChatFormatting.BOLD + "The seekers have been released!"));
					
					for (EntityPlayer seeker : seekers) {
						((EntityPlayerMP)seeker).connection.sendPacket(titlePacket);
					}
					
					for (EntityPlayer hider : hiders) {
						((EntityPlayerMP)hider).connection.sendPacket(titlePacket);
					}
					
					player.world.getMinecraftServer().addScheduledTask(new Runnable() {
						
						@Override
						public void run() {
							player.setPositionAndUpdate(point.getX() + 0.5F, point.getY(), point.getZ() + 0.5F);
							
							for (EntityPlayer hider : hiders) {
								hider.playSound(SoundEvents.ENTITY_ELDER_GUARDIAN_CURSE, 1F, 1F);
							}
						}
					});
					
					new Thread(() -> {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
						}
						
						if (roundTime < 0) {
							for (int i = 3; ; i++) {
								if (!gData.isGameStarted()) {
									return;
								}
								
								int mins = (int) Math.floor(i / 60);
								int secs = i % 60;
								
								SPacketTitle tPacket = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, new TextComponentString("Game duration: 0" + (mins < 10 ? "0" + String.valueOf(mins) : String.valueOf(mins)) + ":" + (secs < 10 ? "0" + String.valueOf(secs) : String.valueOf(secs))));
								
								for (EntityPlayer seeker : seekers) {
									((EntityPlayerMP)seeker).connection.sendPacket(tPacket);
								}
								
								for (EntityPlayer hider : hiders) {
									((EntityPlayerMP)hider).connection.sendPacket(tPacket);
								}
								
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						} else {
							for (int i = 3; i < roundTime; i++) {
								if (!gData.isGameStarted()) {
									return;
								}
								
								int timeLeft = roundTime - i - 1;
								
								int mins = (int) Math.floor(timeLeft / 60);
								int secs = timeLeft % 60;
								
								SPacketTitle tPacket = new SPacketTitle(SPacketTitle.Type.ACTIONBAR, new TextComponentString("Time left: 0" + String.valueOf(mins) + ":" + (secs < 10 ? "0" + String.valueOf(secs) : String.valueOf(secs))));
								
								for (EntityPlayer seeker : seekers) {
									((EntityPlayerMP)seeker).connection.sendPacket(tPacket);
								}
								
								for (EntityPlayer hider : hiders) {
									((EntityPlayerMP)hider).connection.sendPacket(tPacket);
								}
								
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							
							if (!gData.isGameStarted()) {
								return;
							}

							// End game -> hiders win
							player.world.getMinecraftServer().addScheduledTask(new Runnable() {
								
								@Override
								public void run() {
									for (EntityPlayer seeker : seekers) {
										if (!seeker.isSpectator())
											seeker.attackEntityFrom(DamageSource.MAGIC, 60F);
									}
								}
							});							
						}
					}).start();
				}).start();
			}
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
}
