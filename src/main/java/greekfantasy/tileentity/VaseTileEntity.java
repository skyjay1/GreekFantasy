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
    this.setChanged();
    if(getLevel() != null) {
      getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
    }
  }

  public void dropAllItems() {
    if (this.level != null && !this.level.isClientSide()) {
      InventoryHelper.dropContents(this.level, this.getBlockPos(), this.getInventory());
    }
    this.inventoryChanged();
  }

  @Override
  public void clearContent() {
    this.inventory.clear();
    this.inventoryChanged();
  }

  @Override
  public int getContainerSize() {
    return this.inventory.size();
  }

  @Override
  public boolean isEmpty() {
    return this.inventory.isEmpty();
  }

  /**
   * Returns the stack in the given slot.
   */
  public ItemStack getItem(int index) {
    return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
  }

  /**
   * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
   */
  public ItemStack removeItem(int index, int count) {
    this.inventoryChanged();
    return ItemStackHelper.removeItem(this.inventory, index, count);
  }

  /**
   * Removes a stack from the given slot and returns it.
   */
  public ItemStack removeItemNoUpdate(int index) {
    this.inventoryChanged();
    return ItemStackHelper.takeItem(this.inventory, index);
  }

  /**
   * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
   */
  public void setItem(int index, ItemStack stack) {
    if (index >= 0 && index < this.inventory.size()) {
      this.inventory.set(index, stack);
      this.inventoryChanged();
    }
  }

  @Override
  public boolean stillValid(PlayerEntity player) {
    if (this.level.getBlockEntity(this.worldPosition) != this) {
      return false;
    } else {
      return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D,
          (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
    }
  }
  
  // NBT / SAVING
  
  @Override
  public void load(BlockState state, CompoundNBT nbt) {
    super.load(state, nbt);
    this.inventory.clear();
    ItemStackHelper.loadAllItems(nbt, this.inventory);
  }

  @Override
  public CompoundNBT save(CompoundNBT nbt) {
    super.save(nbt);
    ItemStackHelper.saveAllItems(nbt, this.inventory, true);
    return nbt;
  }

}
