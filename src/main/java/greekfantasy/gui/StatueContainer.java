package greekfantasy.gui;

import greekfantasy.GFRegistry;
import greekfantasy.util.StatuePose;
import greekfantasy.util.StatuePoses;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;

public class StatueContainer extends Container {
  
  public static final int PLAYER_INV_X = 32;
  public static final int PLAYER_INV_Y = 120;
  
  private Slot leftSlot;
  private Slot rightSlot;
  
  private BlockPos blockPos;
  private Direction facing;
  private StatuePose statuePose;
  private boolean isFemale;
  private String profile;

  public StatueContainer(int id, final PlayerInventory inventory) {
    this(id, inventory, new Inventory(2), StatuePoses.NONE, false, "", BlockPos.ZERO, Direction.NORTH);
  }
  
  public StatueContainer(final int id, final PlayerInventory inventory, final IInventory iinventory, 
      final StatuePose statuePoseIn, final boolean isFemaleIn, final String profileIn, 
      final BlockPos blockPosIn, final Direction facingIn) {
    super(GFRegistry.STATUE_CONTAINER, id);
    this.statuePose = statuePoseIn;
    this.blockPos = blockPosIn;
    this.facing = facingIn;
    this.isFemale = isFemaleIn;
    this.profile = profileIn;
    // add container inventory
    rightSlot = this.addSlot(new Slot(iinventory, 0, 44, 90));
    leftSlot = this.addSlot(new Slot(iinventory, 1, 8, 90));
    // add player inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        this.addSlot(new Slot(inventory, j + i * 9 + 9, PLAYER_INV_X + j * 18, PLAYER_INV_Y + i * 18));
      }
    }
    for (int k = 0; k < 9; ++k) {
      this.addSlot(new Slot(inventory, k, PLAYER_INV_X + k * 18, 178));
    }
  }

  @Override
  public boolean canInteractWith(final PlayerEntity playerIn) {
    return isWithinUsableDistance(IWorldPosCallable.of(playerIn.getEntityWorld(), getBlockPos()), playerIn, GFRegistry.LIMESTONE_STATUE) ||
           isWithinUsableDistance(IWorldPosCallable.of(playerIn.getEntityWorld(), getBlockPos()), playerIn, GFRegistry.MARBLE_STATUE);
  }

  public BlockPos getBlockPos() {
    return this.blockPos;
  }
  
  public Direction getBlockRotation() {
    return this.facing;
  }
  
  public StatuePose getStatuePose() {
    return this.statuePose;
  }
  
  public boolean isStatueFemale() {
    return this.isFemale;
  }
  
  public String getProfile() {
    return this.profile;
  }
  
  public ItemStack getItemLeft() {
    return this.leftSlot != null ? this.leftSlot.getStack() : ItemStack.EMPTY;
  }
  
  public ItemStack getItemRight() {
    return this.rightSlot != null ? this.rightSlot.getStack() : ItemStack.EMPTY;
  }
  
  /**
   * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
   * inventory and the other inventory(s).
   */
  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
     ItemStack itemstack = ItemStack.EMPTY;
     Slot slot = this.inventorySlots.get(index);
     if (slot != null && slot.getHasStack()) {
        ItemStack itemstack1 = slot.getStack();
        itemstack = itemstack1.copy();
        if (index < 2) {
           if (!this.mergeItemStack(itemstack1, 2, this.inventorySlots.size(), false)) {
              return ItemStack.EMPTY;
           }
        } else if (!this.mergeItemStack(itemstack1, 0, 2, true)) {
           return ItemStack.EMPTY;
        }

        if (itemstack1.isEmpty()) {
           slot.putStack(ItemStack.EMPTY);
        } else {
           slot.onSlotChanged();
        }
     }

     return itemstack;
  }
}
