package greekfantasy.favor;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFavor extends INBTSerializable<CompoundNBT> {
  
  static final String FAVOR_INFO = "FavorInfo";

  /**
   * @param deity the IDeity
   * @return the FavorInfo associated with the given IDeity
   */
  FavorInfo getOrCreateFavor(final IDeity deity);
  
  void setFavor(final IDeity deity, final FavorInfo favorInfo);
  
  Map<IDeity, FavorInfo> getAllFavor();
  
  
  
  @Override
  default CompoundNBT serializeNBT() {
    final CompoundNBT nbt = new CompoundNBT();
    for(final Map.Entry<IDeity, FavorInfo> entry : getAllFavor().entrySet()) {
      final String name = entry.getKey().getName().toString();
      final CompoundNBT entrynbt = new CompoundNBT();
      entrynbt.put(FAVOR_INFO, entry.getValue().serializeNBT());
      nbt.put(name, entrynbt);
    }
    return nbt;
  }

  @Override
  default void deserializeNBT(final CompoundNBT nbt) {
    for(final Entry<ResourceLocation, IDeity> entry : DeityManager.getAllDeity()) {
      final String name = entry.getKey().toString();
      if(nbt.contains(entry.getKey().toString())) {
        final CompoundNBT entrynbt = nbt.getCompound(name);
        final FavorInfo favorInfo = new FavorInfo(entrynbt.getCompound(FAVOR_INFO));
        setFavor(entry.getValue(), favorInfo);
      }
    }
  }
  
}
