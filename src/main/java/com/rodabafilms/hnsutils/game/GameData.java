package com.rodabafilms.hnsutils.game;

import com.rodabafilms.hnsutils.util.References;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class GameData extends WorldSavedData {

	private int hiderFinderUsageTimeout = 60;
	private long hiderFinderLastUseTime = 0;
	private BlockPos seekerSpawn = new BlockPos(-1, -1, -1);
	private BlockPos hiderSpawn = new BlockPos(-1, -1, -1);
	private BlockPos respawnPoint = new BlockPos(-1, -1, -1);
	private boolean seekersSpawnWithHF = false;
	private int smokeBombSpawnCount = 0;
	private int seekerSpawnTimeout = 30;
	private int roundTime = 5 * 60;
	private boolean gameStarted = false;
	
	public GameData(String id) {
		super(id);
	}
	
	public GameData() {
		super(References.MOD_ID);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		hiderFinderUsageTimeout = nbt.getInteger("HF_UsageTimeout");
		hiderFinderLastUseTime = nbt.getLong("HF_LastUseTime");
		seekerSpawn = NBTUtil.getPosFromTag(nbt.getCompoundTag("BP_SeekerSpawn"));
		hiderSpawn = NBTUtil.getPosFromTag(nbt.getCompoundTag("BP_HiderSpawn"));
		respawnPoint = NBTUtil.getPosFromTag(nbt.getCompoundTag("BP_RespawnPoint"));
		seekersSpawnWithHF = nbt.getBoolean("HF_SpawnWith");
		smokeBombSpawnCount = nbt.getInteger("SB_SpawnCount");
		seekerSpawnTimeout = nbt.getInteger("SpawnTimeout");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("HF_UsageTimeout", hiderFinderUsageTimeout);
		nbt.setLong("HF_LastUseTime", hiderFinderLastUseTime);
		nbt.setTag("BP_SeekerSpawn", NBTUtil.createPosTag(seekerSpawn));
		nbt.setTag("BP_HiderSpawn", NBTUtil.createPosTag(hiderSpawn));
		nbt.setTag("BP_RespawnPoint", NBTUtil.createPosTag(respawnPoint));
		nbt.setBoolean("HF_SpawnWith", seekersSpawnWithHF);
		nbt.setInteger("SB_SpawnCount", smokeBombSpawnCount);
		nbt.setInteger("SpawnTimeout", seekerSpawnTimeout);
		
		return nbt;
	}

	public static GameData get(World world) {
		GameData data = (GameData)world.getMapStorage().getOrLoadData(GameData.class, References.MOD_ID);
		
		if (data == null) {
			data = new GameData();
			world.getMapStorage().setData(References.MOD_ID, data);
		}
		
		return data;
	}
	
	public long getHFLastUseTime() {
		return hiderFinderLastUseTime;
	}
	
	public int getHFUsageTimeout() {
		return hiderFinderUsageTimeout;
	}
	
	public boolean isSeekerSpawnSet() {
		return !(seekerSpawn.getX() == 0 && seekerSpawn.getY() == 0 && seekerSpawn.getZ() == 0);
	}
	
	public boolean isHiderSpawnSet() {
		return !(hiderSpawn.getX() == 0 && hiderSpawn.getY() == 0 && hiderSpawn.getZ() == 0);
	}
	
	public boolean isRespawnPointSet() {
		return !(respawnPoint.getX() == 0 && respawnPoint.getY() == 0 && respawnPoint.getZ() == 0);
	}
	
	public BlockPos getSeekerSpawn() {
		return seekerSpawn;
	}
	
	public BlockPos getHiderSpawn() {
		return hiderSpawn;
	}
	
	public BlockPos getRespawnPoint() {
		return respawnPoint;
	}
	
	public boolean getSeekersSpawnWithHF() {
		return seekersSpawnWithHF;
	}
	
	public int getSmokeBombSpawnCount() {
		return smokeBombSpawnCount;
	}
	
	public int getSeekerSpawnTimeout() {
		return seekerSpawnTimeout;
	}
	
	public int getRoundTime() {
		return roundTime;
	}
	
	public boolean isGameStarted() {
		return gameStarted;
	}
	
	public void setSeekerSpawn(BlockPos seekerSpawn) {
		this.seekerSpawn = seekerSpawn;
		markDirty();
	}
	
	public void setHiderSpawn(BlockPos hiderSpawn) {
		this.hiderSpawn = hiderSpawn;
		markDirty();
	}
	
	public void setRespawnPoint(BlockPos respawnPoint) {
		this.respawnPoint = respawnPoint;
		markDirty();
	}
	
	public void setHFLastUseTime(long lastUseTime) {
		hiderFinderLastUseTime = lastUseTime;
		markDirty();
	}
	
	public void setHFUsageTimeout(int usageTimeout) {
		hiderFinderUsageTimeout = usageTimeout;
		markDirty();
	}
	
	public void setSmokeBombSpawnCount(int smokeBombSpawnCount) {
		this.smokeBombSpawnCount = smokeBombSpawnCount;
		markDirty();
	}
	
	public void setSeekersSpawnWithHF(boolean seekersSpawnWithHF) {
		this.seekersSpawnWithHF = seekersSpawnWithHF;
		markDirty();
	}
	
	public void setSeekerSpawnTimeout(int seekerSpawnTimeout) {
		this.seekerSpawnTimeout = seekerSpawnTimeout;
		markDirty();
	}
	
	public void setRoundTime(int roundTime) {
		this.roundTime = roundTime;
		markDirty();
	}
	
	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
		markDirty();
	}
}
