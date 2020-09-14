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
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;

public class StatueContainer extends Container {
  
  private static final int PLAYER_INV_X = 23;
  private static final int PLAYER_INV_Y = 117;
  
  private final Slot leftSlot;
  private final Slot rightSlot;
  
  private BlockPos blockPos;
  private StatuePose statuePose;

  public StatueContainer(int id, final PlayerInventory inventory) {
    this(id, inventory, new Inventory(2), StatuePoses.NONE, BlockPos.ZERO);
  }
  
  public StatueContainer(final int id, final PlayerInventory inventory, final IInventory iinventory, 
      final StatuePose statuePoseIn, final BlockPos blockPosIn) {
    super(GFRegistry.STATUE_CONTAINER, id);
    this.statuePose = statuePoseIn;
    this.blockPos = blockPosIn;
    // add container inventory
    rightSlot = this.addSlot(new Slot(iinventory, 0, 7, 89));
    leftSlot = this.addSlot(new Slot(iinventory, 1, 43, 89));
    // add player inventory
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        this.addSlot(new Slot(inventory, j + i * 9 + 9, PLAYER_INV_X + j * 18, PLAYER_INV_Y + i * 18));
      }
    }
    for (int k = 0; k < 9; ++k) {
      this.addSlot(new Slot(inventory, k, PLAYER_INV_X + k * 18, 175));
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
  
  public StatuePose getStatuePose() {
    return this.statuePose;
  }
}
