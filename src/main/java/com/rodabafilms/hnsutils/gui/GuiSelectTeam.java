package com.rodabafilms.hnsutils.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Supplier;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.rodabafilms.hnsutils.game.GameData;
import com.rodabafilms.hnsutils.network.NetworkHandler;
import com.rodabafilms.hnsutils.util.Logger;
import com.rodabafilms.hnsutils.util.References;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GuiSelectTeam extends GuiContainer {

	final ResourceLocation texture = new ResourceLocation(References.MOD_ID, "textures/gui/team.png");
	public static final int GUIID = 1;

	public static class GuiContainerMod extends Container implements Supplier<Map<Integer, Slot>> {
		private IInventory internal;
		private World world;
		private EntityPlayer entity;
		private int x, y, z;
		private Map<Integer, Slot> customSlots = new HashMap<Integer, Slot>();

		public GuiContainerMod(World world, int x, int y, int z, EntityPlayer player) {
			this.world = world;
			this.entity = player;
			this.x = x;
			this.y = y;
			this.z = z;
			this.internal = new InventoryBasic("", true, 0);
		}

		public Map<Integer, Slot> get() {
			return customSlots;
		}

		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return internal.isUsableByPlayer(playerIn);
		}

		@Override
		public void onContainerClosed(EntityPlayer playerIn) {
			super.onContainerClosed(playerIn);
			if ((internal instanceof InventoryBasic) && (playerIn instanceof EntityPlayerMP)) {
				clearContainer(playerIn, playerIn.world, internal);
			}
		}
	}

	private World world;
	private int x, y, z;
	private EntityPlayer entity;

	public GuiSelectTeam(World world, int x, int y, int z, EntityPlayer entity) {
		super(new GuiContainerMod(world, x, y, z, entity));
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.entity = entity;
		this.xSize = 238;
		this.ySize = 116;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texture);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString("Select your team", 78, 22, -16777216);
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (width - 238) / 2;
		guiTop = (height - 116) / 2;
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		buttonList.add(new GuiButton(0, guiLeft + 30, guiTop + 76, 70, 20, "Seekers"));
		buttonList.add(new GuiButton(1, width - guiLeft - 100, guiTop + 76, 70, 20, "Hiders"));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		NetworkHandler.sendToServer(new GUIButtonPressedMessage(button.id, x, y, z));
		super.actionPerformed(button);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public static class GUIButtonPressedMessageHandler implements IMessageHandler<GUIButtonPressedMessage, IMessage> {
		@Override
		public IMessage onMessage(GUIButtonPressedMessage message, MessageContext context) {
			EntityPlayerMP entity = context.getServerHandler().player;
			entity.getServerWorld().addScheduledTask(() -> {
				int buttonID = message.buttonID;
				int x = message.x;
				int y = message.y;
				int z = message.z;
				handleButtonAction(entity, buttonID, x, y, z);
			});
			return null;
		}
	}

	public static class GUIButtonPressedMessage implements IMessage {
		int buttonID, x, y, z;

		public GUIButtonPressedMessage() {
		}

		public GUIButtonPressedMessage(int buttonID, int x, int y, int z) {
			this.buttonID = buttonID;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public void toBytes(io.netty.buffer.ByteBuf buf) {
			buf.writeInt(buttonID);
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
		}

		@Override
		public void fromBytes(io.netty.buffer.ByteBuf buf) {
			buttonID = buf.readInt();
			x = buf.readInt();
			y = buf.readInt();
			z = buf.readInt();
		}
	}

	private static void handleButtonAction(EntityPlayer entity, int buttonID, int x, int y, int z) {
		World world = entity.world;

		if (!world.isBlockLoaded(new BlockPos(x, y, z)))
			return;

		if (!(buttonID == 0 || buttonID == 1))
			return;

		entity.closeScreen();

		if (buttonID == 0) {
			if (!world.isRemote) {
				world.getScoreboard().addPlayerToTeam(entity.getName(), "hns_seekers_id");

				List<EntityPlayer> playing = world.getPlayers(EntityPlayer.class,
						pl -> pl.getTeam() != null && (pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id")
								|| pl.getTeam().getName().equalsIgnoreCase("hns_hiders_id")));

				int seekers = world
						.getPlayers(EntityPlayer.class,
								pl -> pl.getTeam() != null && pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id"))
						.size();
				int hiders = world
						.getPlayers(EntityPlayer.class,
								pl -> pl.getTeam() != null && pl.getTeam().getName().equalsIgnoreCase("hns_hiders_id"))
						.size();

				for (EntityPlayer player : playing) {
					if (!player.getName().equalsIgnoreCase(entity.getName()))
						player.sendMessage(
								new TextComponentString("" + ChatFormatting.RED + ChatFormatting.BOLD + entity.getName()
										+ ChatFormatting.WHITE + " has joined the " + ChatFormatting.RED + "seekers"
										+ ChatFormatting.WHITE + " team (" + ChatFormatting.RED + ChatFormatting.BOLD
										+ String.valueOf(seekers) + ChatFormatting.WHITE + "/" + ChatFormatting.BLUE
										+ ChatFormatting.BOLD + String.valueOf(hiders) + ChatFormatting.WHITE + ")"));
				}

				entity.sendMessage(new TextComponentString("" + ChatFormatting.RED + ChatFormatting.BOLD + "You"
						+ ChatFormatting.WHITE + " have joined the " + ChatFormatting.RED + "seekers"
						+ ChatFormatting.WHITE + " team (" + ChatFormatting.RED + ChatFormatting.BOLD
						+ String.valueOf(seekers) + ChatFormatting.WHITE + "/" + ChatFormatting.BLUE
						+ ChatFormatting.BOLD + String.valueOf(hiders) + ChatFormatting.WHITE + ")"));
			}
		} else {
			if (!world.isRemote) {
				world.getScoreboard().addPlayerToTeam(entity.getName(), "hns_hiders_id");

				List<EntityPlayer> playing = world.getPlayers(EntityPlayer.class,
						pl -> pl.getTeam() != null && (pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id")
								|| pl.getTeam().getName().equalsIgnoreCase("hns_hiders_id")));

				int seekers = world
						.getPlayers(EntityPlayer.class,
								pl -> pl.getTeam() != null && pl.getTeam().getName().equalsIgnoreCase("hns_seekers_id"))
						.size();
				int hiders = world
						.getPlayers(EntityPlayer.class,
								pl -> pl.getTeam() != null && pl.getTeam().getName().equalsIgnoreCase("hns_hiders_id"))
						.size();

				for (EntityPlayer player : playing) {
					if (!player.getName().equalsIgnoreCase(entity.getName()))
						player.sendMessage(new TextComponentString("" + ChatFormatting.BLUE + ChatFormatting.BOLD
								+ entity.getName() + ChatFormatting.WHITE + " has joined the " + ChatFormatting.BLUE
								+ "hiders" + ChatFormatting.WHITE + " team (" + ChatFormatting.RED + ChatFormatting.BOLD
								+ String.valueOf(seekers) + ChatFormatting.WHITE + "/" + ChatFormatting.BLUE
								+ ChatFormatting.BOLD + String.valueOf(hiders) + ChatFormatting.WHITE + ")"));
				}

				entity.sendMessage(new TextComponentString("" + ChatFormatting.BLUE + ChatFormatting.BOLD + "You"
						+ ChatFormatting.WHITE + " have joined the " + ChatFormatting.BLUE + "hiders"
						+ ChatFormatting.WHITE + " team (" + ChatFormatting.RED + ChatFormatting.BOLD
						+ String.valueOf(seekers) + ChatFormatting.WHITE + "/" + ChatFormatting.BLUE
						+ ChatFormatting.BOLD + String.valueOf(hiders) + ChatFormatting.WHITE + ")"));
			}
		}
	}
}
