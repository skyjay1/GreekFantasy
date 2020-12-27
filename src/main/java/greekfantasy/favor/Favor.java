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
  
  protected final Map<IDeity, FavorInfo> favorMap = new HashMap<>();
  
  public Favor() { }

  @Override
  public FavorInfo getOrCreateFavor(IDeity deity) {
    if(favorMap.containsKey(deity)) {
      return favorMap.get(deity);
    } else {
      final FavorInfo favorInfo = new FavorInfo();
      favorMap.put(deity, favorInfo);
      return favorInfo;
    }
  }

  @Override
  public void setFavor(IDeity deity, FavorInfo favorInfo) {
    favorMap.put(deity, favorInfo);
  }

  @Override
  public Map<IDeity, FavorInfo> getAllFavor() {
    return favorMap;
  }
  
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
