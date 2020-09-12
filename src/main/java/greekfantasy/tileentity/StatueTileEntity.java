package greekfantasy.tileentity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.util.ModelPart;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StatueTileEntity extends TileEntity implements IClearable {

  private static final String KEY_POSE = "StatuePose";
  private static final String KEY_UPPER = "Upper";

  private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

  private StatuePose statuePose = StatuePoses.NONE;
  private boolean upper = false;

  public StatueTileEntity() {
    super(GFRegistry.STATUE_TE);
  }
  
  public void setUpper(final boolean isUpper) {
    this.upper = isUpper;
    this.markDirty();
  }

  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
    if (!worldIn.isRemote()) {
      // TESTING: items
//      setItem(new ItemStack(Items.STONE_SWORD), HandSide.RIGHT);
//      setItem(new ItemStack(Items.ACACIA_LOG), HandSide.LEFT);
      // TESTING: poses
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
  
  // INVENTORY //
  
  public void setItem(final ItemStack stack, final HandSide hand) {
    int i = hand.ordinal();
    this.inventory.set(i, stack.split(1));
    this.inventoryChanged();
  }
  
  public ItemStack getItem(final HandSide hand) {
    return this.inventory.get(hand.ordinal());
  }
  
  public NonNullList<ItemStack> getInventory() {
     return this.inventory;
  }

  private void inventoryChanged() {
    this.markDirty();
    this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
  }

  public void dropAllItems() {
    if (this.world != null) {
      if (!this.world.isRemote) {
        InventoryHelper.dropItems(this.world, this.getPos(), this.getInventory());
      }

      this.inventoryChanged();
    }

  }
  
  @Override
  public void clear() {
    this.inventory.clear();
  }
  
  // NBT AND SAVING STUFF //
  
  @Override
  public CompoundNBT getUpdateTag() { return buildUpdateTag(super.getUpdateTag()); }

  @Override
  public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    this.readUpdateTag(tag);
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() { return new SUpdateTileEntityPacket(getPos(), -1, buildUpdateTag(new CompoundNBT())); }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) { readUpdateTag(pkt.getNbtCompound()); }

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    readUpdateTag(nbt);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    final CompoundNBT tag = buildUpdateTag(super.write(compound));
    return tag;
  }
  
  public CompoundNBT buildUpdateTag(final CompoundNBT tag) {
    tag.putBoolean(KEY_UPPER, this.upper);
    tag.put(KEY_POSE, this.statuePose.write(new CompoundNBT()));
    ItemStackHelper.saveAllItems(tag, this.inventory, true);
    return tag;
  }
  
  public void readUpdateTag(final CompoundNBT nbt) {
    this.upper = nbt.getBoolean(KEY_UPPER);
    this.statuePose = new StatuePose(nbt.getCompound(KEY_POSE));
    this.inventory.clear();
    ItemStackHelper.loadAllItems(nbt, this.inventory);
  }
  
  // OTHER //
  
  @OnlyIn(Dist.CLIENT)
  public double getMaxRenderDistanceSquared() {
     return 256.0D;
  }
}
