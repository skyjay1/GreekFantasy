package greekfantasy.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
  
  public PanfluteSongs(final String folder) {
    super(GSON, folder);
  }
  
  public Optional<PanfluteSong> get(final ResourceLocation id) {
    if(SONGS.containsKey(id)) {
      return Optional.of(SONGS.get(id));
    }
    GreekFantasy.LOGGER.debug("Could not find song for " + id);
    return Optional.empty();
  }
  
  protected PanfluteSong parse(final JsonElement json) {
    return GSON.fromJson(json, PanfluteSong.class);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> jsons, IResourceManager manager, IProfiler profile) {
    jsons.forEach((key, input) -> SONGS.put(key, parse(input)));
    final StringBuilder builder = new StringBuilder("Parsing PanfluteSongs map. Entries: {\n");
    SONGS.keySet().forEach(id -> builder.append(id.toString()).append("\n"));
    GreekFantasy.LOGGER.debug(builder.append("}").toString());
  }
}
