package greekfantasy.events;

import greekfantasy.favor.IDeity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class FavorChangedEvent extends PlayerEvent {
  
  private final IDeity deity;
  private final long oldFavor;
  private long newFavor;
  
  public FavorChangedEvent(final PlayerEntity playerIn, final IDeity deityIn, final long prevFavor, final long curFavor) {
    super(playerIn);
    deity = deityIn;
    oldFavor = prevFavor;
    newFavor = curFavor;
  }
  
  public IDeity getDeity() { return deity; }
  
  public long getOldFavor() { return oldFavor; }
  
  public long getNewFavor() { return newFavor; }
  
  public void setNewFavor(final long favor) { newFavor = favor; }
}
