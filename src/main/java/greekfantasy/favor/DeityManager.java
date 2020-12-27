package greekfantasy.favor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import greekfantasy.util.StatuePoses;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;

public final class DeityManager {
  
  private static final String MODID = GreekFantasy.MODID;
  
  protected static final Map<ResourceLocation, IDeity> DEITY = new HashMap<>();
  
  // init deities
  public static final IDeity ZEUS = DeityManager.addDeity(new Deity.Builder(MODID, "zeus").setPose(StatuePoses.WALKING).build());
  
  private DeityManager() { }
  
  /**
   * registers a deity
   * @param deity the deity instance
   */
  public static IDeity addDeity(final IDeity deity) {
    DEITY.put(deity.getName(), deity);
    return deity;
  }
  
//  public static boolean hasDeity(final ResourceLocation name) {
//    return DEITY.containsKey(name);
//  }

  /**
   * @param name the name of the deity
   * @return the deity if present, or null if none is found
   */
  @Nullable
  public static IDeity getDeity(final ResourceLocation name) {
    if(!DEITY.containsKey(name)) {
      GreekFantasy.LOGGER.error("Failed to retrieve deity '" + name.toString() + "' from DeityManager map!");
      return null;
    }
    return DEITY.get(name);
  }
  
  /**
   * @return the entry set of all deity
   */
  public static Set<Map.Entry<ResourceLocation, IDeity>> getAllDeity() {
    return DEITY.entrySet();
  }
}
