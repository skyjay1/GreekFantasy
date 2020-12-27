package greekfantasy.favor;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class FavorInfo implements INBTSerializable<CompoundNBT> {
  
  private static final String FAVOR = "Favor";
  private static final String TIMESTAMP = "Timestamp";
  private static final String COOLDOWN = "Cooldown";
  private static final long MIN_FAVOR = 10;
  
  private long favor;
  private long effectTimestamp;
  private long effectCooldown;
  
  public FavorInfo() { }
  
  public FavorInfo(final CompoundNBT nbt) { 
    deserializeNBT(nbt);
  }
  
  public long getFavor() { return favor; }
  
  public void addFavor(long toAdd) { setFavor(favor + toAdd); }

  public void setFavor(long favorIn) { this.favor = favorIn; }

  public long getEffectTimestamp() { return effectTimestamp; }

  public void setEffectTimestamp(long timestamp) { this.effectTimestamp = timestamp; }

  public long getEffectCooldown() { return effectCooldown; }

  public void setEffectCooldown(long cooldown) { this.effectCooldown = cooldown; }

  public boolean canExecute(final long time) { 
    return Math.abs(favor) >= MIN_FAVOR && time >= (effectTimestamp + effectCooldown); 
  }
  
  public int getLevel() {
    // calculate the current level based on favor
    // TODO math
    return 0;
  }
  
  public long getFavorToNextLevel() {
    // calculate the amount of favor needed to advance to the next level
    // TODO math
    return 0;
  }
  
  @Override
  public CompoundNBT serializeNBT() {
    final CompoundNBT nbt = new CompoundNBT();
    nbt.putLong(FAVOR, favor);
    nbt.putLong(TIMESTAMP, effectTimestamp);
    nbt.putLong(COOLDOWN, effectCooldown);
    return nbt;
  }

  @Override
  public void deserializeNBT(final CompoundNBT nbt) {
    setFavor(nbt.getLong(FAVOR));
    setEffectTimestamp(nbt.getLong(TIMESTAMP));
    setEffectCooldown(nbt.getLong(COOLDOWN));
  }
}
