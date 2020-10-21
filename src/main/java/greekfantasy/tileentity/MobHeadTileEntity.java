package greekfantasy.tileentity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MobHeadTileEntity extends TileEntity {
  
  private static final String KEY_HEAD = "HeadType";
  private static final String KEY_WALL = "Wall";
  
  private HeadType headType = HeadType.GIGANTE;
  private boolean wall = false;

  public MobHeadTileEntity() {
    super(GFRegistry.BOSS_HEAD_TE);
  }
  
  public HeadType getHeadType() {
    return headType;
  }

  public void setHeadType(HeadType headType) {
    this.headType = headType;
    markDirty();
  }
  
  public boolean onWall() {
    return wall;
  }
  
  public void setWall(final boolean isOnWall) {
    wall = isOnWall;
    markDirty();
  }

  // CLIENT-SERVER SYNC
  
  @Override
  public CompoundNBT getUpdateTag() {
    final CompoundNBT nbt = new CompoundNBT();
    nbt.putByte(KEY_HEAD, headType.getId());
    nbt.putBoolean(KEY_WALL, wall);
    return nbt;
  }

  @Override
  public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    headType = HeadType.getById(tag.getByte(KEY_HEAD));
    wall = tag.getBoolean(KEY_WALL);
  }
  
  // NBT / SAVING
  
  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    headType = HeadType.getById(nbt.getByte(KEY_HEAD));
    wall = nbt.getBoolean(KEY_WALL);
  }

  @Override
  public CompoundNBT write(CompoundNBT nbt) {
    super.write(nbt);
    nbt.putByte(KEY_HEAD, headType.getId());
    nbt.putBoolean(KEY_WALL, wall);
    return nbt;
  }
  
  // OTHER //

  @OnlyIn(Dist.CLIENT)
  public double getMaxRenderDistanceSquared() {
    return 256.0D;
  }
  
  // HEAD TYPE //  
  public enum HeadType {
    GIGANTE((byte)0, new ResourceLocation(GreekFantasy.MODID, "textures/entity/gigante.png")),
    ORTHUS((byte)1, new ResourceLocation(GreekFantasy.MODID, "textures/entity/orthus/orthus.png")),
    CERBERUS((byte)2, new ResourceLocation(GreekFantasy.MODID, "textures/entity/cerberus/cerberus.png"));
    
    private final byte id;
    public final ResourceLocation texture;
    
    private HeadType(final byte typeID, final ResourceLocation headTexture) {
      id = typeID;
      texture = headTexture;
    }
    
    public byte getId() {
      return id;
    }
    
    public ResourceLocation getTexture() {
      return texture;
    }

    public static HeadType getById(final byte b) {
      for(final HeadType h : values()) {
        if(h.getId() == b) {
          return h;
        }
      }
      return GIGANTE;
    }
  }

}
