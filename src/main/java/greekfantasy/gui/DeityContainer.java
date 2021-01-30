package greekfantasy.gui;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.IFavor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

public class DeityContainer extends Container {
  
  private IFavor favor;

  public DeityContainer(int id, final PlayerInventory inventory) {
    this(id, inventory, GreekFantasy.FAVOR.getDefaultInstance());
  }
  
  public DeityContainer(final int id, final PlayerInventory inventory, final IFavor favorIn) {
    super(GFRegistry.DEITY_CONTAINER, id);
    favor = favorIn;
  }

  @Override
  public boolean canInteractWith(final PlayerEntity playerIn) {
    return true;
  }
  
  public IFavor getFavor() {
    return favor;
  }
}
