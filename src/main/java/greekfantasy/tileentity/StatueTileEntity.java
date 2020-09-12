package greekfantasy.tileentity;

import java.util.EnumMap;

import greekfantasy.GFRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.vector.Vector3f;

public class StatueTileEntity extends TileEntity {
  
  public static final EnumMap<ModelPart, Vector3f> ROTATION_MAP = new EnumMap<>(ModelPart.class);
  
  static {
    ROTATION_MAP.put(ModelPart.HEAD, new Vector3f(0, 0, 0));
    ROTATION_MAP.put(ModelPart.BODY, new Vector3f(0, 0, 0));
    ROTATION_MAP.put(ModelPart.LEFT_ARM, new Vector3f(0, 0, 0));
    ROTATION_MAP.put(ModelPart.RIGHT_ARM, new Vector3f(0, 0, 0));
    ROTATION_MAP.put(ModelPart.LEFT_LEG, new Vector3f(0, 0, 0));
    ROTATION_MAP.put(ModelPart.RIGHT_LEG, new Vector3f(0, 0, 0));
  }

  public StatueTileEntity() {
    super(GFRegistry.STATUE_TE);
  }
  
  // #getUpdateTag() and #handleUpdateTag(CompoundNBT nbt) synchronize on chunk load
  
  @Override
  public CompoundNBT getUpdateTag() {
    return super.getUpdateTag();
  }

  @Override
  public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
  }
  
  public Vector3f getRotations(final ModelPart part) {
    return ROTATION_MAP.get(part);
  }
  
  public static enum ModelPart {
    HEAD, BODY, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG;
  }
}
