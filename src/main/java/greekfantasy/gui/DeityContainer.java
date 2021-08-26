package greekfantasy.gui;

import java.util.Optional;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.deity.Deity;
import greekfantasy.deity.favor.IFavor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;

public class DeityContainer extends Container {
  
  private IFavor favor;
  private Optional<ResourceLocation> deity;

  public DeityContainer(int id, final PlayerInventory inventory) {
    this(id, inventory, GreekFantasy.FAVOR.getDefaultInstance(), Deity.EMPTY.getName());
  }
  
  public DeityContainer(final int id, final PlayerInventory inventory, final IFavor favorIn, final ResourceLocation deityIn) {
    super(GFRegistry.DEITY_CONTAINER, id);
    favor = favorIn;
    deity = (Deity.EMPTY.getName().equals(deityIn)) ? Optional.empty() : Optional.of(deityIn);
  }

  @Override
  public boolean canInteractWith(final PlayerEntity playerIn) {
    return true;
  }
  
  public IFavor getFavor() {
    return favor;
  }
  
  public Optional<ResourceLocation> getDeity() {
    return deity;
  }
}
