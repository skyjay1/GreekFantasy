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
  
  public static final int PLAYER_INV_X = 31;
  public static final int PLAYER_INV_Y = 125;
  
  private final Slot leftSlot;
  private final Slot rightSlot;
  
  private BlockPos blockPos;
  private StatuePose statuePose;
  private boolean isFemale;
  private String textureName;

  public StatueContainer(int id, final PlayerInventory inventory) {
    this(id, inventory, new Inventory(2), StatuePoses.NONE, false, "", BlockPos.ZERO);
  }
  
  public StatueContainer(final int id, final PlayerInventory inventory, final IInventory iinventory, 
      final StatuePose statuePoseIn, final boolean isFemaleIn, final String textureNameIn, final BlockPos blockPosIn) {
    super(GFRegistry.STATUE_CONTAINER, id);
    this.statuePose = statuePoseIn;
    this.blockPos = blockPosIn;
    this.isFemale = isFemaleIn;
    this.textureName = textureNameIn;
    // add container inventory
    leftSlot = this.addSlot(new Slot(iinventory, 0, 8, 90));
    rightSlot = this.addSlot(new Slot(iinventory, 1, 44, 90));
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
  
  public boolean isStatueFemale() {
    return this.isFemale;
  }
  
  public String getTextureName() {
    return this.textureName;
  }
}
