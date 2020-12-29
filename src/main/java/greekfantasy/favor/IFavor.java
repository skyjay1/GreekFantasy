package greekfantasy.favor;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import greekfantasy.GreekFantasy;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFavor extends INBTSerializable<CompoundNBT> {
  
  public static final ResourceLocation REGISTRY_NAME = new ResourceLocation(GreekFantasy.MODID, "favor");
  
  static final String TIMESTAMP = "Timestamp";
  static final String COOLDOWN = "Cooldown";
  
  static final long MIN_FAVOR = 10;

  /**
   * @param deity the IDeity
   * @return the FavorLevel associated with the given IDeity
   */
  FavorLevel getFavor(final IDeity deity);
  
  /**
   * Updates the favor info for the given diety
   * @param deity the IDeity
   * @param favorLevel the new FavorLevel
   */
  void setFavor(final IDeity deity, final FavorLevel favorLevel);
  
  /** @return a map of all Deity and favor info objects **/
  Map<IDeity, FavorLevel> getAllFavor();
  
  default Set<IDeity> getAllDeity() { return getAllFavor().keySet(); }
  
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
    for(final Map.Entry<IDeity, FavorLevel> entry : getAllFavor().entrySet()) {
      final String name = entry.getKey().getName().toString();
      nbt.putLong(name, entry.getValue().getFavor());
    }
    nbt.putLong(TIMESTAMP, getEffectTimestamp());
    nbt.putLong(COOLDOWN, getEffectCooldown());
    return nbt;
  }

  @Override
  default void deserializeNBT(final CompoundNBT nbt) {
    for(final Entry<ResourceLocation, IDeity> entry : DeityManager.getDeityEntries()) {
      final String name = entry.getKey().toString();
      if(nbt.contains(entry.getKey().toString())) {
        final FavorLevel favorLevel = new FavorLevel(nbt.getLong(name));
        setFavor(entry.getValue(), favorLevel);
      }
    }
    setEffectTimestamp(nbt.getLong(TIMESTAMP));
    setEffectCooldown(nbt.getLong(COOLDOWN));
  }
  
}
