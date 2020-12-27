package greekfantasy.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import greekfantasy.GreekFantasy;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class GenericJsonReloadListener<T> extends JsonReloadListener {
  
  private final Gson GSON;
  
  private final Codec<T> codec;
  private final Consumer<GenericJsonReloadListener<T>> syncOnReload;
  private final Class<T> objClass;
  
  public GenericJsonReloadListener(final Gson gson, final String folder, final Class<T> oClass, final Codec<T> oCodec, 
      Consumer<GenericJsonReloadListener<T>> syncOnReloadConsumer) {
    super(gson, folder);
    GSON = gson;
    objClass = oClass;
    codec = oCodec;
    syncOnReload = syncOnReloadConsumer;
  }
  
  protected Map<ResourceLocation, Optional<T>> OBJECTS = new HashMap<>();
  
  
  /**
   * Adds a Deity to the map
   * @param id the resource location id
   * @param effect the favor effect, or null
   **/
  public void put(final ResourceLocation id, @Nullable final T obj) {
    OBJECTS.put(id, Optional.ofNullable(obj));
  }
  
  /**
   * @param id a ResourceLocation name to retrieve
   * @return an Optional containing the Deity if found, otherwise empty
   **/
  public Optional<T> get(final ResourceLocation id) {
    if(!OBJECTS.containsKey(id)) {
      GreekFantasy.LOGGER.debug("Could not find object for " + id + " in " + OBJECTS.getClass().toString());
      put(id, null);
    }
    return OBJECTS.get(id);
  }
 
  /** @return a collection of all Deitys **/
  public Collection<Optional<T>> getValues() {
    return OBJECTS.values();
  }
  
  /** @return a collection of all Deity entries **/
  public Set<Entry<ResourceLocation, Optional<T>>> getEntries() {
    return OBJECTS.entrySet();
  }

  public DataResult<INBT> writeObject(final T obj) {
    // write Object T to NBT
    return codec.encodeStart(NBTDynamicOps.INSTANCE, obj);
  }
  
  public DataResult<T> jsonToObject(final JsonElement json) {
    // read Object T from json
    return codec.parse(JsonOps.INSTANCE, json);
  }
  
  public DataResult<T> readObject(final INBT nbt) {
    // read Object T from nbt
    return codec.parse(NBTDynamicOps.INSTANCE, nbt);
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> jsons, IResourceManager manager, IProfiler profile) {
    // build the maps
    OBJECTS.clear();
    jsons.forEach((key, input) -> OBJECTS.put(key, Optional.of(GSON.fromJson(input, objClass))));
    // print size of the map for debugging purposes
    GreekFantasy.LOGGER.debug("Parsing Generic map of type " + objClass.getName() + " and found " + OBJECTS.size() + " entries");
    boolean isServer = true;
    try {
      LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    } catch (Exception e) {
      isServer = false;
    }
    // if we're on the server, send syncing packets
    if (isServer == true) {
      GreekFantasy.LOGGER.debug(objClass.getName() + " Map: syncOnReloadCallback");
      syncOnReload.accept(this);
    }
  }
}
