package greekfantasy.tileentity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

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

  public void setHeadType(HeadType headTypeIn) {
    if(headType != headTypeIn) {
      this.headType = headTypeIn;
      setChanged();
    }
  }
  
  public boolean onWall() {
    return wall;
  }
  
  public void setWall(final boolean isOnWall) {
    if(wall != isOnWall) {
      wall = isOnWall;
      setChanged();
    }
  }

  // CLIENT-SERVER SYNC
  
  @Override
  public CompoundNBT getUpdateTag() {
    final CompoundNBT nbt = super.getUpdateTag();
    nbt.putByte(KEY_HEAD, headType.getId());
    nbt.putBoolean(KEY_WALL, wall);
    return nbt;
  }

  @Override
  public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    read(tag);
  }
  
  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(getBlockPos(), -1, getUpdateTag());
  }
  
  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    read(pkt.getTag());
  }
  
  // NBT / SAVING
  
  @Override
  public void load(BlockState state, CompoundNBT nbt) {
    super.load(state, nbt);
    read(nbt);
  }

  @Override
  public CompoundNBT save(CompoundNBT nbt) {
    super.save(nbt);
    nbt.putByte(KEY_HEAD, headType.getId());
    nbt.putBoolean(KEY_WALL, wall);
    return nbt;
  }
  
  protected void read(final CompoundNBT nbt) {
    setHeadType(HeadType.getById(nbt.getByte(KEY_HEAD)));
    setWall(nbt.getBoolean(KEY_WALL));
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
