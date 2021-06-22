package greekfantasy.tileentity;

import greekfantasy.GFRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

public class VaseTileEntity extends TileEntity implements IClearable, IInventory {

  private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

  public VaseTileEntity() {
    super(GFRegistry.VASE_TE);
  }
  
  // CLIENT-SERVER SYNC
  
  @Override
  public CompoundNBT getUpdateTag() {
    return ItemStackHelper.saveAllItems(super.getUpdateTag(), inventory);
  }

  @Override
  public void handleUpdateTag(final BlockState state, final CompoundNBT tag) {
    super.handleUpdateTag(state, tag);
    ItemStackHelper.loadAllItems(tag, inventory);
  }
 
  //INVENTORY //

  public NonNullList<ItemStack> getInventory() {
    return this.inventory;
  }

  private void inventoryChanged() {
    this.markDirty();
    if(getWorld() != null) {
      getWorld().notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 2);
    }
  }

  public void dropAllItems() {
    if (this.world != null && !this.world.isRemote()) {
      InventoryHelper.dropItems(this.world, this.getPos(), this.getInventory());
    }
    this.inventoryChanged();
  }

  @Override
  public void clear() {
    this.inventory.clear();
    this.inventoryChanged();
  }

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
    this.inventoryChanged();
    return ItemStackHelper.getAndSplit(this.inventory, index, count);
  }

  /**
   * Removes a stack from the given slot and returns it.
   */
  public ItemStack removeStackFromSlot(int index) {
    this.inventoryChanged();
    return ItemStackHelper.getAndRemove(this.inventory, index);
  }

  /**
   * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
   */
  public void setInventorySlotContents(int index, ItemStack stack) {
    if (index >= 0 && index < this.inventory.size()) {
      this.inventory.set(index, stack);
      this.inventoryChanged();
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
  
  // NBT / SAVING
  
  @Override
  public void read(BlockState state, CompoundNBT nbt) {
    super.read(state, nbt);
    this.inventory.clear();
    ItemStackHelper.loadAllItems(nbt, this.inventory);
  }

  @Override
  public CompoundNBT write(CompoundNBT nbt) {
    super.write(nbt);
    ItemStackHelper.saveAllItems(nbt, this.inventory, true);
    return nbt;
  }

}
