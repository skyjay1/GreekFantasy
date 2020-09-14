package greekfantasy.tileentity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.block.StatueBlock;
import greekfantasy.block.StatueBlock.StatueMaterial;
import greekfantasy.util.ModelPart;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

public class StatueTileEntity extends TileEntity implements IClearable, IInventory {

  private static final String KEY_POSE = "Pose";
  private static final String KEY_UPPER = "Upper";
  private static final String KEY_FEMALE = "Female";

  private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

  private StatuePose statuePose = StatuePoses.NONE;
  private boolean upper = false;
  private boolean statueFemale;

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
//      final int poseNum = worldIn.rand.nextInt(StatuePoses.ALL_POSES.length);
//      this.statuePose = StatuePoses.ALL_POSES[poseNum];
      // TODO open gui
      this.markDirty();
      worldIn.notifyBlockUpdate(pos, state, state, 2);
    }
    return ActionResultType.SUCCESS;
  }
  
  public StatuePose getStatuePose() {
    return this.statuePose;
  }
  
  public void setStatuePose(final StatuePose poseIn) {
    this.statuePose = poseIn;
    this.markDirty();
    this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 2);
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
  public CompoundNBT getUpdateTag() {
    return buildUpdateTag(super.getUpdateTag());
  }

  @Override
  public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    this.readUpdateTag(tag);
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(getPos(), -1, buildUpdateTag(new CompoundNBT()));
  }

  @Override
  public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
    readUpdateTag(pkt.getNbtCompound());
  }

  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    readUpdateTag(nbt);
  }

  @Override
  public CompoundNBT write(CompoundNBT compound) {
    return buildUpdateTag(super.write(compound));
  }

  public CompoundNBT buildUpdateTag(final CompoundNBT nbt) {
    nbt.putBoolean(KEY_UPPER, this.upper);
    nbt.putBoolean(KEY_FEMALE, statueFemale);
    nbt.put(KEY_POSE, this.statuePose.serializeNBT(new CompoundNBT()));
    ItemStackHelper.saveAllItems(nbt, this.inventory, true);
    return nbt;
  }

  public void readUpdateTag(final CompoundNBT nbt) {
    this.upper = nbt.getBoolean(KEY_UPPER);
    this.statueFemale = nbt.getBoolean(KEY_FEMALE);
    this.statuePose = new StatuePose(nbt.getCompound(KEY_POSE));
    this.inventory.clear();
    ItemStackHelper.loadAllItems(nbt, this.inventory);
  }

  // INVENTORY //

  @Override
  public int getSizeInventory() {
    return this.inventory.size();
  }

  @Override
  public boolean isEmpty() {
    return this.inventory.isEmpty();
  }

  /**
   * Returns the stack in the given slot.
   */
  public ItemStack getStackInSlot(int index) {
    return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
  }

  /**
   * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
   */
  public ItemStack decrStackSize(int index, int count) {
    return ItemStackHelper.getAndSplit(this.inventory, index, count);
  }

  /**
   * Removes a stack from the given slot and returns it.
   */
  public ItemStack removeStackFromSlot(int index) {
    return ItemStackHelper.getAndRemove(this.inventory, index);
  }

  /**
   * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
   */
  public void setInventorySlotContents(int index, ItemStack stack) {
    if (index >= 0 && index < this.inventory.size()) {
      this.inventory.set(index, stack);
    }
  }

  @Override
  public boolean isUsableByPlayer(PlayerEntity player) {
    if (this.world.getTileEntity(this.pos) != this) {
      return false;
    } else {
      return !(player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D,
          (double) this.pos.getZ() + 0.5D) > 64.0D);
    }
  }

  // OTHER //

  @OnlyIn(Dist.CLIENT)
  public double getMaxRenderDistanceSquared() {
    return 256.0D;
  }

  public StatueMaterial getStatueMaterial() {
    if (this.getBlockState().getBlock() instanceof StatueBlock) {
      return ((StatueBlock) this.getBlockState().getBlock()).getStatueMaterial();
    }
    return StatueMaterial.LIMESTONE;
  }

  public boolean isStatueFemale() {
    return statueFemale;
  }

  public void setStatueFemale(boolean statueFemale) {
    this.statueFemale = statueFemale;
  }
}
