package greekfantasy.tileentity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.util.ModelPart;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class StatueTileEntity extends TileEntity {

  private static final String KEY_POSE = "StatuePose";
  private static final String KEY_UPPER = "Upper";

  public StatuePose statuePose = StatuePoses.NONE;
  public boolean upper = false;

  public StatueTileEntity() {
    super(GFRegistry.STATUE_TE);
  }

  // #getUpdateTag() and #handleUpdateTag(CompoundNBT nbt) synchronize on chunk load

  @Override
  public CompoundNBT getUpdateTag() {
    final CompoundNBT tag = super.getUpdateTag();
    tag.put(KEY_POSE, this.statuePose.write(new CompoundNBT()));
    return tag;
  }

  @Override
  public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    this.statuePose = new StatuePose(tag.getCompound(KEY_POSE));
  }
  

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    CompoundNBT nbtTag = new CompoundNBT();
    this.statuePose.write(nbtTag);
    return new SUpdateTileEntityPacket(getPos(), -1, nbtTag);
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    CompoundNBT tag = pkt.getNbtCompound();
    this.statuePose = new StatuePose(tag);
  }

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    this.upper = nbt.getBoolean(KEY_UPPER);
    this.statuePose = new StatuePose(nbt.getCompound(KEY_POSE));
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    final CompoundNBT tag = super.write(compound);
    tag.putBoolean(KEY_UPPER, this.upper);
    tag.put(KEY_POSE, this.statuePose.write(new CompoundNBT()));
    return tag;
  }

  public void setUpper(final boolean isUpper) {
    this.upper = isUpper;
    this.markDirty();
  }

  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
    if (!worldIn.isRemote()) {
      final int poseNum = worldIn.rand.nextInt(StatuePoses.ALL_POSES.length);
      this.statuePose = StatuePoses.ALL_POSES[poseNum];
      GreekFantasy.LOGGER.info("setting statuePose: " + statuePose.getString());
      this.markDirty();
      worldIn.notifyBlockUpdate(pos, state, state, 2);
    }
    return ActionResultType.SUCCESS;
  }

  public Vector3f getRotations(final ModelPart part) {
    return statuePose.getAngles(part);
  }

}
