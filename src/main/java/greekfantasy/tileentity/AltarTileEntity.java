package greekfantasy.tileentity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.favor.Deity;
import greekfantasy.favor.IDeity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;

public class AltarTileEntity extends StatueTileEntity {

  private static final String KEY_DEITY = "Deity";
  
  private ResourceLocation deityName = Deity.EMPTY.getName();

  public AltarTileEntity() {
    super(GFRegistry.ALTAR_TE);
  }
  
  // NAME //
 
  public void setDeity(final ResourceLocation deityIn) { setDeity(deityIn, false); }
  
  public void setDeity(final ResourceLocation deityIn, final boolean refresh) {
    this.deityName = deityIn;
    this.markDirty();
    if(refresh) {
      this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2);
    }
  }
  
  public IDeity getDeity() { return GreekFantasy.PROXY.DEITY.get(deityName).orElse(Deity.EMPTY); }
  
  // NBT //

  public CompoundNBT buildUpdateTag(final CompoundNBT nbt) {
    nbt.putString(KEY_DEITY, deityName.toString());
    return super.buildUpdateTag(nbt);
  }

  public void readUpdateTag(final CompoundNBT nbt) {
    super.readUpdateTag(nbt);
    this.setDeity(new ResourceLocation(nbt.getString(KEY_DEITY)));
    final IDeity deity = getDeity();
    if(deity != Deity.EMPTY) {
      this.setStatueFemale(deity.isFemale());
      this.setItem(deity.getRightHandItem(), HandSide.RIGHT);
      this.setItem(deity.getLeftHandItem(), HandSide.LEFT);
    }
  }
}
