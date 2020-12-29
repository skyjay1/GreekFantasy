package greekfantasy.favor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import greekfantasy.GreekFantasy;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.NBTTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFavor extends INBTSerializable<CompoundNBT> {
  
  public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(GreekFantasy.MODID, "favor");
  
  static final String FAVOR_LEVELS = "FavorLevels";
  static final String NAME = "Name";
  static final String FAVOR = "Favor";
  static final String TIMESTAMP = "Timestamp";
  static final String COOLDOWN = "Cooldown";
  
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
  
  default void forEach(final BiConsumer<IDeity, FavorLevel> action) {
    for(final Entry<ResourceLocation, FavorLevel> e : getAllFavor().entrySet()) {
      action.accept(DeityManager.getDeity(e.getKey()), e.getValue());
    }
  }
  
  default Set<IDeity> getDeitySet() { 
    return getAllFavor().keySet().stream().map(i -> DeityManager.getDeity(i)).collect(Collectors.toSet()); 
  }
  
  long getEffectTimestamp();

  void setEffectTimestamp(long timestamp);

  long getEffectCooldown();

  void setEffectCooldown(long cooldown);

  default boolean canUseEffect(final FavorLevel info, final long time, final Random rand) { 
    return Math.abs(info.getFavor()) >= MIN_FAVOR 
        && time >= (getEffectTimestamp() + getEffectCooldown())
        && rand.nextDouble() < info.getPercentFavor(); 
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
    // DEBUG
    GreekFantasy.LOGGER.debug("serialize Favor - nbt is " + nbt.toString());
    GreekFantasy.LOGGER.debug("serialize Favor - Favor is " + this.toString());
    return nbt;
  }

  @Override
  default void deserializeNBT(final CompoundNBT nbt) {
    GreekFantasy.LOGGER.debug("deserialize Favor - nbt is " + nbt.toString());
    GreekFantasy.LOGGER.debug("deserialize Favor - original Favor is " + this.toString());
    GreekFantasy.LOGGER.debug("deserialize Favor - deity map has size of " + DeityManager.getDeityCollection().size());
    final ListNBT deities = nbt.getList(FAVOR_LEVELS, 10);
    for(int i = 0, l = deities.size(); i < l; i++) {
      final CompoundNBT deity = deities.getCompound(i);
      final String name = deity.getString(NAME);
      final long favor = deity.getLong(FAVOR);
      setFavor(new ResourceLocation(name), new FavorLevel(favor));
    }
    setEffectTimestamp(nbt.getLong(TIMESTAMP));
    setEffectCooldown(nbt.getLong(COOLDOWN));
    GreekFantasy.LOGGER.debug("deserialize Favor - new Favor is " + this.toString());
  }
}
