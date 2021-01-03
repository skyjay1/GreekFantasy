package greekfantasy.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

import greekfantasy.GreekFantasy;
import greekfantasy.block.StatueBlock.StatueMaterial;
import greekfantasy.tileentity.StatueTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class PalladiumSavedData extends WorldSavedData {
  
  private static Map<ChunkPos, Set<BlockPos>> palladiumMap = new HashMap<>();
  private static final String KEY_MAP = "PalladiumMap";
  private static final String KEY_SET = "Positions";
  private static final String KEY_CHUNK_X = "ChunkX";
  private static final String KEY_CHUNK_Z = "ChunkZ";
  private static final String KEY_X = "X";
  private static final String KEY_Y = "Y";
  private static final String KEY_Z = "Z";

  public PalladiumSavedData(String name) {
    super(name);
  }
  
  public static PalladiumSavedData getOrCreate(final ServerWorld server) {
    return server.getSavedData().getOrCreate(() -> new PalladiumSavedData(GreekFantasy.MODID), GreekFantasy.MODID);
  }
  
  /**
   * Get or create a collection of palladium positions
   * @param chunk the chunk position
   * @return an immutable copy of the palladium block positions
   */
  public Set<BlockPos> getPalladium(final World world, final ChunkPos chunk) {
// this would occasionally hard-refresh the set, but it's prob not necessary
//    if(Math.random() < 0.02D) {
//      putAllPalladium(chunk, fillPalladiumList(world, chunk));
//    } else 
    if(!palladiumMap.containsKey(chunk)) {
      // add a new palladium set
      addAllPalladium(chunk, fillPalladiumList(world, chunk));
    }
    return ImmutableSet.copyOf(palladiumMap.get(chunk));
  }
  
  /**
   * Appends the given position to the existing set
   * @param chunk the chunk
   * @param pos the palladium position to add
   */
  public void addPalladium(final ChunkPos chunk, final BlockPos pos) {
    if(!palladiumMap.containsKey(chunk)) {
      palladiumMap.put(chunk, new HashSet<>());
    }
    palladiumMap.get(chunk).add(pos);
    this.markDirty();
  }
  
  /**
   * Appends the given positions to the existing set
   * @param chunk the chunk
   * @param pos the palladium positions to add
   */
  public void addAllPalladium(final ChunkPos chunk, final Set<BlockPos> pos) {
    if(!palladiumMap.containsKey(chunk)) {
      palladiumMap.put(chunk, new HashSet<>());
    }
    palladiumMap.get(chunk).addAll(pos);
    this.markDirty();
  }
  
  /**
   * Replaces the current set of positions with the given set
   * @param chunk the chunk
   * @param pos the palladium positions to set
   */
  public void putAllPalladium(final ChunkPos chunk, final Set<BlockPos> pos) {
    palladiumMap.put(chunk, pos);
    this.markDirty();
  }
  
  /**
   * Removes the given position from the existing set, if present
   * @param chunk the chunk
   * @param pos the palladium position to remove
   */
  public void removePalladium(final ChunkPos chunk, final BlockPos pos) {
    if(!palladiumMap.containsKey(chunk)) {
      palladiumMap.put(chunk, new HashSet<>());
    }
    palladiumMap.get(chunk).remove(pos);
    this.markDirty();
  }
  
  /** @return if the given position contains a palladium tile entity **/
  public static boolean validate(final World world, final BlockPos pos) {
    return validate(world.getTileEntity(pos));
  }
  
  /** @return if the given tile entity is a palladium **/
  public static boolean validate(final @Nullable TileEntity te) {
    return (te instanceof StatueTileEntity && ((StatueTileEntity)te).getStatueMaterial() == StatueMaterial.WOOD);
  }
  
  /**
   * Scans all tile entities in the chunk to get a list of palladium locations
   * @param world the world
   * @param chunkPos the chunk
   * @return the set of positions containing palladium blocks
   */
  private static Set<BlockPos> fillPalladiumList(final World world, final ChunkPos chunkPos) {
    // iterate through all tile entities in this chunk and fill a list with Palladium entries
    Set<BlockPos> palladiumSet = new HashSet<>();
    try {
      Map<BlockPos, TileEntity> chunkTEMap = world.getChunk(chunkPos.x, chunkPos.z).getTileEntityMap();
      for(final Entry<BlockPos, TileEntity> e : chunkTEMap.entrySet()) {
        if(validate(e.getValue())) {
          palladiumSet.add(e.getKey());
        }
      }
    } catch(final Exception e) {
      GreekFantasy.LOGGER.error("Encountered an error trying to fill palladium list (PalladiumSavedData)");
    }
    return palladiumSet;
  }

  @Override
  public void read(CompoundNBT nbt) {
    if(nbt.contains(KEY_MAP)) {
      final ListNBT chunkMap = nbt.getList(KEY_MAP, 10);
      for(int i = 0, il = chunkMap.size(); i < il; i++) {
        // chunk contains chunkX, chunkY, and posList
        final CompoundNBT chunk = chunkMap.getCompound(i);
        final int chunkX = chunk.getInt(KEY_CHUNK_X);
        final int chunkZ = chunk.getInt(KEY_CHUNK_Z);
        // each block pos is stored in posList
        final ListNBT posList = chunk.getList(KEY_SET, 10);
        final Set<BlockPos> blockPosSet = new HashSet<>();
        for(int j = 0, jl = posList.size(); j < jl; j++) {
          final CompoundNBT posNBT = posList.getCompound(j);
          blockPosSet.add(new BlockPos(posNBT.getInt(KEY_X), posNBT.getInt(KEY_Y), posNBT.getInt(KEY_Z)));
        }
        // that's all the data we need from the compound tag, add it to the map
        palladiumMap.put(new ChunkPos(chunkX, chunkZ), blockPosSet);
      }
    }
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    final ListNBT chunkMap = new ListNBT();
    for(final Entry<ChunkPos, Set<BlockPos>> entry : palladiumMap.entrySet()) {
      // chunk contains chunkX, chunkY, and posList
      final CompoundNBT chunk = new CompoundNBT();
      chunk.putInt(KEY_CHUNK_X, entry.getKey().x);
      chunk.putInt(KEY_CHUNK_Z, entry.getKey().z);
      // each block pos is stored in posList
      final ListNBT posList = new ListNBT();
      for(final BlockPos p : entry.getValue()) {
        final CompoundNBT posNBT = new CompoundNBT();
        posNBT.putInt(KEY_X, p.getX());
        posNBT.putInt(KEY_Y, p.getY());
        posNBT.putInt(KEY_Z, p.getZ());
      }
      chunk.put(KEY_SET, posList);
      // chunk has all the NBT it needs, add it to the map
      chunkMap.add(chunk);
    }
    compound.put(KEY_MAP, chunkMap);
    return compound;
  }

}
