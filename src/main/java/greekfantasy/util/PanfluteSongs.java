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
  
  protected Map<ResourceLocation, Optional<PanfluteSong>> SONGS = new HashMap<>();
  
  public PanfluteSongs(final String folder) {
    super(GSON, folder);
  }
  
  /**
   * @param id a ResourceLocation name to retrieve
   * @return an Optional containing the PanfluteSong if found, otherwise empty
   **/
  public Optional<PanfluteSong> get(final ResourceLocation id) {
    if(!SONGS.containsKey(id)) {
      GreekFantasy.LOGGER.debug("Could not find song for " + id);
      SONGS.put(id, Optional.empty());
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
  
  protected PanfluteSong parse(final JsonElement json) {
    return GSON.fromJson(json, PanfluteSong.class);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> jsons, IResourceManager manager, IProfiler profile) {
    // build the maps
    SONGS.clear();
    jsons.forEach((key, input) -> SONGS.put(key, Optional.of(parse(input))));
    // print size of the map for debugging purposes
    GreekFantasy.LOGGER.debug("Parsing PanfluteSongs map. Found " + SONGS.size() + " entries");
  }
}
