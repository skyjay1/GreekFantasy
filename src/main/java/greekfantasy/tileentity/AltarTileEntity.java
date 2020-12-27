package greekfantasy.tileentity;

import greekfantasy.GFRegistry;
import greekfantasy.favor.DeityManager;
import greekfantasy.favor.IDeity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class AltarTileEntity extends StatueTileEntity {

  private static final String KEY_DEITY = "Deity";
  
  private IDeity deity = DeityManager.ZEUS;

  public AltarTileEntity() {
    super(GFRegistry.ALTAR_TE);
  }
  
  // DEITY //
 
  public void setDeity(final IDeity deityIn) { setDeity(deityIn, false); }
  
  public void setDeity(final IDeity deityIn, final boolean refresh) {
    this.deity = deityIn;
    this.markDirty();
    if(refresh) {
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }
  
  public IDeity getDeity() { return deity; }
  
  // NBT //

  public CompoundNBT buildUpdateTag(final CompoundNBT nbt) {
    nbt.putString(KEY_DEITY, deity.getName().toString());
    return super.buildUpdateTag(nbt);
  }

  public void readUpdateTag(final CompoundNBT nbt) {
    super.readUpdateTag(nbt);
    this.setDeity(DeityManager.getDeity(new ResourceLocation(nbt.getString(KEY_DEITY)))); 
  }
}
