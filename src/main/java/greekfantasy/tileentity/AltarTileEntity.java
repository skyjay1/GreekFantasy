package greekfantasy.tileentity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.favor.Deity;
import greekfantasy.favor.DeityManager;
import greekfantasy.favor.IDeity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class AltarTileEntity extends StatueTileEntity {

  private static final String KEY_DEITY = "Deity";
  
  private ResourceLocation deityName = DeityManager.ZEUS;
  private IDeity deity = Deity.EMPTY;

  public AltarTileEntity() {
    super(GFRegistry.ALTAR_TE);
  }
  
  // DEITY //
 
  public void setDeity(final ResourceLocation deityIn) { setDeity(deityIn, false); }
  
  public void setDeity(final ResourceLocation deityIn, final boolean refresh) {
    this.deityName = deityIn;
    this.deity = GreekFantasy.PROXY.DEITY.get(deityName).orElse(Deity.EMPTY);
    this.markDirty();
    if(refresh) {
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }
  
  public IDeity getDeity() { return deity; }
  
  // NBT //

  public CompoundNBT buildUpdateTag(final CompoundNBT nbt) {
    nbt.putString(KEY_DEITY, deityName.toString());
    return super.buildUpdateTag(nbt);
  }

  public void readUpdateTag(final CompoundNBT nbt) {
    super.readUpdateTag(nbt);
    this.setDeity(new ResourceLocation(nbt.getString(KEY_DEITY))); 
  }
}
