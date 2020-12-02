package greekfantasy.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import greekfantasy.GreekFantasy;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class PanfluteSongs extends JsonReloadListener {
  
  private static final Gson GSON = new GsonBuilder().create();
  
  protected Map<ResourceLocation, Optional<PanfluteSong>> SONGS = new HashMap<>();
  
//  private Optional<Runnable> syncOnReloadCallback = Optional.empty();
  
  public PanfluteSongs(final String folder) {
    super(GSON, folder);
//    this.syncOnReloadCallback = Optional.of(() -> {
//      for(final Entry<ResourceLocation, Optional<PanfluteSong>> e : GreekFantasy.PROXY.PANFLUTE_SONGS.getEntries()) {
//        GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SPanfluteSongPacket(e.getKey(), e.getValue().get()));
//      }
//    });
  }
  
  /**
   * Adds a song to the map
   * @param id the resource location id
   * @param song the panflute song, or null
   **/
  public void put(final ResourceLocation id, @Nullable final PanfluteSong song) {
    SONGS.put(id, Optional.ofNullable(song));
  }
  
  /**
   * @param id a ResourceLocation name to retrieve
   * @return an Optional containing the PanfluteSong if found, otherwise empty
   **/
  public Optional<PanfluteSong> get(final ResourceLocation id) {
    if(!SONGS.containsKey(id)) {
      GreekFantasy.LOGGER.debug("Could not find song for " + id);
      put(id, null);
    }
    return SONGS.get(id);
  }
 
  /** @return a collection of all PanfluteSongs **/
  public Collection<Optional<PanfluteSong>> getValues() {
    return SONGS.values();
  }
  
  /** @return a collection of all PanfluteSongs **/
  public Set<Entry<ResourceLocation, Optional<PanfluteSong>>> getEntries() {
    return SONGS.entrySet();
  }

  public static DataResult<INBT> writeSong(final PanfluteSong song) {
    // write song to NBT
    return PanfluteSong.CODEC.encodeStart(NBTDynamicOps.INSTANCE, song);
  }
  
  public static DataResult<PanfluteSong> jsonToSong(final JsonElement json) {
    // read song from json
    return PanfluteSong.CODEC.parse(JsonOps.INSTANCE, json);
  }
  
  public static DataResult<PanfluteSong> readSong(final INBT nbt) {
    // read song from nbt
    return PanfluteSong.CODEC.parse(NBTDynamicOps.INSTANCE, nbt);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> jsons, IResourceManager manager, IProfiler profile) {
    // build the maps
    SONGS.clear();
    jsons.forEach((key, input) -> SONGS.put(key, Optional.of(GSON.fromJson(input, PanfluteSong.class))));
    // print size of the map for debugging purposes
    GreekFantasy.LOGGER.debug("Parsing PanfluteSongs map. Found " + SONGS.size() + " entries");
//    boolean isServer = GreekFantasy.PROXY instanceof greekfantasy.proxy.ServerProxy;
//    try {
//      LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
//    } catch (NullPointerException e) {
//      isServer = false;
//    }
//    // if we're on the server, send syncing packets
//    if (isServer == true) {
//      GreekFantasy.LOGGER.debug("PanfluteSongs: syncOnReloadCallback");
//      for(final Entry<ResourceLocation, Optional<PanfluteSong>> e : GreekFantasy.PROXY.PANFLUTE_SONGS.getEntries()) {
//        GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SPanfluteSongPacket(e.getKey(), e.getValue().get()));
//      }
//      this.syncOnReloadCallback.ifPresent(Runnable::run);
//    }
  }
}
