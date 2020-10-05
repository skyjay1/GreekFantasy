package greekfantasy.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import greekfantasy.GreekFantasy;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class PanfluteSongs extends JsonReloadListener {
  
  private static final Gson GSON = new GsonBuilder().create();
  
  protected Map<ResourceLocation, PanfluteSong> SONGS = new HashMap<>();
  protected Map<PanfluteSong, ResourceLocation> SONG_LOOKUP = new HashMap<>();
  
  public PanfluteSongs(final String folder) {
    super(GSON, folder);
  }
  
  /**
   * @param id a ResourceLocation name to retrieve
   * @return an Optional containing the PanfluteSong if found, otherwise empty
   **/
  public Optional<PanfluteSong> get(final ResourceLocation id) {
    if(SONGS.containsKey(id)) {
      return Optional.of(SONGS.get(id));
    }
    GreekFantasy.LOGGER.debug("Could not find song for " + id);
    return Optional.empty();
  }
  
  /**
   * @param song a PanfluteSong
   * @return an Optional containing the ID of the given song if registered, otherwise empty
   **/
  public Optional<ResourceLocation> get(final PanfluteSong song) {
    if(SONG_LOOKUP.containsKey(song)) {
      return Optional.of(SONG_LOOKUP.get(song));
    }
    GreekFantasy.LOGGER.debug("Could not find ID for " + song.getTranslationKey() + ", this means it was not registered!");
    return Optional.empty();
  }
  
  /** @return a collection of all PanfluteSongs **/
  public Collection<PanfluteSong> getValues() {
    return SONGS.values();
  }
  
  /** @return a collection of all PanfluteSongs **/
  public Set<Entry<ResourceLocation, PanfluteSong>> getEntries() {
    return SONGS.entrySet();
  }
  
  protected PanfluteSong parse(final JsonElement json) {
    return GSON.fromJson(json, PanfluteSong.class);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> jsons, IResourceManager manager, IProfiler profile) {
    // build the maps
    SONGS.clear();
    jsons.forEach((key, input) -> SONGS.put(key, parse(input)));
    SONG_LOOKUP.clear();
    SONGS.forEach((rl, s) -> SONG_LOOKUP.put(s, rl));
    // print contents of the map
    final StringBuilder builder = new StringBuilder("Parsing PanfluteSongs map. Entries: {\n");
    SONGS.keySet().forEach(id -> builder.append(id.toString()).append("\n"));
    GreekFantasy.LOGGER.debug(builder.append("}").toString());
  }
}
