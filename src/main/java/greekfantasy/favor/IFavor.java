package greekfantasy.favor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import greekfantasy.GreekFantasy;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFavor extends INBTSerializable<CompoundNBT> {
  
  public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(GreekFantasy.MODID, "favor");
  
  static final String FAVOR_LEVELS = "FavorLevels";
  static final String NAME = "Name";
  static final String FAVOR = "Favor";
  static final String TIMESTAMP = "Timestamp";
  static final String COOLDOWN = "Cooldown";
  static final String TRIGGERED_TIMESTAMP = "TriggeredTimestamp";
  static final String TRIGGERED_COOLDOWN = "TriggeredCooldown";
  
  public static final long MIN_FAVOR = 10;

  /**
   * Helper method to get the favor info for the given IDeity
   * @param deity the IDeity
   * @return the FavorLevel associated with the given IDeity
   * @see #getFavor(ResourceLocation)
   */
  default FavorLevel getFavor(final IDeity deity) { 
    return getFavor(deity.getName());
  }
  
  /**
   * Gets the FavorLevel for the given Deity
   * @param deity the IDeity
   * @return the FavorLevel associated with the given IDeity
   */
  FavorLevel getFavor(final ResourceLocation deity);
  
  /**
   * Helper method to update favor info for the given IDeity
   * @param deity the IDeity
   * @param favorLevel the new FavorLevel
   * @see #setFavor(ResourceLocation, FavorLevel)
   */
  default void setFavor(final IDeity deity, final FavorLevel favorLevel) {
    setFavor(deity.getName(), favorLevel);
  }
  
  /**
   * Updates the favor info for the given diety
   * @param deity the IDeity
   * @param favorLevel the new FavorLevel
   */
  void setFavor(final ResourceLocation deity, final FavorLevel favorLevel);
  
  /** @return a map of all Deity and favor info objects **/
  Map<ResourceLocation, FavorLevel> getAllFavor();
  
  /**
   * Performs an action for each IDeity/FavorLevel pair that is registered
   * @param action the action to perform
   */
  default void forEach(final BiConsumer<Deity, FavorLevel> action) {
    for(final Entry<ResourceLocation, FavorLevel> e : getAllFavor().entrySet()) {
      GreekFantasy.PROXY.DEITY.get(e.getKey()).ifPresent(d -> action.accept(d, e.getValue()));
    }
  }
  
  /** @return a set of all registered IDeity objects **/
  default Set<IDeity> getDeitySet() {
    return getAllFavor().keySet().stream().map(i -> GreekFantasy.PROXY.DEITY.get(i).orElse(Deity.EMPTY)).collect(Collectors.toSet()); 
  }
  
  /** @return the time of the last favor effect **/
  long getEffectTimestamp();

  /** @param timestamp the current time **/
  void setEffectTimestamp(long timestamp);

  /** @return the time until the next favor effect **/
  long getEffectCooldown();

  /** @param cooldown the amount of time until the next favor effect **/
  void setEffectCooldown(long cooldown);
  
  /** @return the time of the last favor effect **/
  long getTriggeredTimestamp();

  /** @param timestamp the current time **/
  void setTriggeredTimestamp(long timestamp);

  /** @return the time until the next favor effect **/
  long getTriggeredCooldown();

  /** @param cooldown the amount of time until the next favor effect **/
  void setTriggeredCooldown(long cooldown);

  /**
   * @param info the favor level
   * @param time the current time
   * @param rand a random instance
   * @return true if a FavorEffect should be chosen and executed
   */
  default boolean canUseEffect(final FavorLevel info, final long time, final Random rand) { 
    return Math.abs(info.getFavor()) >= MIN_FAVOR 
        && time >= (getEffectTimestamp() + getEffectCooldown())
        && rand.nextDouble() * 0.7D < info.getPercentFavor(); 
  }
  
  /**
   * @param info the favor level
   * @param time the current time
   * @return true if a TriggeredFavorEffect should be chosen and executed
   */
  default boolean canUseTriggeredEffect(final FavorLevel info, final long time) { 
    return Math.abs(info.getFavor()) >= MIN_FAVOR 
        && time >= (getTriggeredTimestamp() + getTriggeredCooldown()); 
  }
  
  @Override
  default CompoundNBT serializeNBT() {
    final CompoundNBT nbt = new CompoundNBT();
    final ListNBT deities = new ListNBT();
    for(final Entry<ResourceLocation, FavorLevel> entry : getAllFavor().entrySet()) {
      final CompoundNBT deityTag = new CompoundNBT();
      deityTag.putString(NAME, entry.getKey().toString());
      deityTag.putLong(FAVOR, entry.getValue().getFavor());
      deities.add(deityTag);
    }
    nbt.put(FAVOR_LEVELS, deities);
    nbt.putLong(TIMESTAMP, getEffectTimestamp());
    nbt.putLong(COOLDOWN, getEffectCooldown());
    nbt.putLong(TRIGGERED_TIMESTAMP, getTriggeredTimestamp());
    nbt.putLong(TRIGGERED_COOLDOWN, getTriggeredCooldown());
    return nbt;
  }

  @Override
  default void deserializeNBT(final CompoundNBT nbt) {
    final ListNBT deities = nbt.getList(FAVOR_LEVELS, 10);
    for(int i = 0, l = deities.size(); i < l; i++) {
      final CompoundNBT deity = deities.getCompound(i);
      final String name = deity.getString(NAME);
      final long favor = deity.getLong(FAVOR);
      setFavor(new ResourceLocation(name), new FavorLevel(favor));
    }
    setEffectTimestamp(nbt.getLong(TIMESTAMP));
    setEffectCooldown(nbt.getLong(COOLDOWN));
    setTriggeredTimestamp(nbt.getLong(TRIGGERED_TIMESTAMP));
    setTriggeredCooldown(nbt.getLong(TRIGGERED_COOLDOWN));
  }
}
