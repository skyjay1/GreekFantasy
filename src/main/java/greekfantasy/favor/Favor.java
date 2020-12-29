package greekfantasy.favor;

import java.util.HashMap;
import java.util.Map;

import greekfantasy.GreekFantasy;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class Favor implements IFavor {
  
  protected final Map<IDeity, FavorLevel> favorMap = new HashMap<>();
  private long effectTimestamp;
  private long effectCooldown = 10000;
  
  public Favor() { }

  @Override
  public FavorLevel getFavor(IDeity deity) {
    if(favorMap.containsKey(deity)) {
      return favorMap.get(deity);
    } else {
      final FavorLevel favorLevel = new FavorLevel();
      favorMap.put(deity, favorLevel);
      return favorLevel;
    }
  }

  @Override
  public void setFavor(IDeity deity, FavorLevel favorLevel) {
    favorMap.put(deity, favorLevel);
  }

  @Override
  public Map<IDeity, FavorLevel> getAllFavor() {
    return favorMap;
  }
  
  public long getEffectTimestamp() { return effectTimestamp; }

  public void setEffectTimestamp(long timestamp) { this.effectTimestamp = timestamp; }

  public long getEffectCooldown() { return effectCooldown; }

  public void setEffectCooldown(long cooldown) { this.effectCooldown = cooldown; }
  
  public static class Storage implements IStorage<IFavor> {

    @Override
    public INBT writeNBT(Capability<IFavor> capability, IFavor instance, Direction side) {
      return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<IFavor> capability, IFavor instance, Direction side, INBT nbt) {
      if(nbt instanceof CompoundNBT) {
        instance.deserializeNBT((CompoundNBT) nbt);
      }
    }
  }
  
  public static class Provider implements ICapabilitySerializable<CompoundNBT> {
   
    
    public IFavor instance = GreekFantasy.FAVOR.getDefaultInstance();

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
      return cap == GreekFantasy.FAVOR ? GreekFantasy.FAVOR.orEmpty(cap, LazyOptional.of(() -> instance)) : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
      return (CompoundNBT) GreekFantasy.FAVOR.getStorage().writeNBT(GreekFantasy.FAVOR, this.instance, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
      GreekFantasy.FAVOR.getStorage().readNBT(GreekFantasy.FAVOR, this.instance, null, nbt);
    }

  }

}
