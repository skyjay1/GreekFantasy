package greekfantasy.event;

import greekfantasy.deity.IDeity;
import greekfantasy.deity.favor.FavorLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event.HasResult;

/**
 * This event is fired when a player's favor is about to change.
 * This event is not {@link Cancelable}.
 * This event does not have a result. {@link HasResult}.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
public class FavorChangedEvent extends PlayerEvent {
  
  private final IDeity deity;
  private final long oldFavor;
  private final FavorChangedEvent.Source source;
  
  private long newFavor;
  private boolean isLevelChange;
  
  public FavorChangedEvent(final PlayerEntity playerIn, final IDeity deityIn, 
      final long prevFavor, final long curFavor, final FavorChangedEvent.Source sourceIn) {
    super(playerIn);
    deity = deityIn;
    oldFavor = prevFavor;
    newFavor = curFavor;
    source = sourceIn;
    isLevelChange = FavorLevel.calculateLevel(curFavor) != FavorLevel.calculateLevel(prevFavor);
  }
  
  public IDeity getDeity() { return deity; }
  
  public long getOldFavor() { return oldFavor; }
  
  public long getNewFavor() { return newFavor; }
  
  public void setNewFavor(final long favor) { 
    newFavor = favor;
    isLevelChange = FavorLevel.calculateLevel(newFavor) != FavorLevel.calculateLevel(oldFavor);
  }
  
  public FavorChangedEvent.Source getSource() { return source; }
  
  public boolean isLevelChange() { return isLevelChange; }
  
  /**
   * This is used to indicate why the favor is changing
   * @see FavorChangedEvent
   */
  public static enum Source implements IStringSerializable {
    PASSIVE("passive"), GIVE_ITEM("item"), KILL_ENTITY("kill"), 
    ATTACK_ENTITY("attack"), COMMAND("command"), OTHER("other");
    
    private final String name;
    private Source(final String sourceName) { name = sourceName; }

    @Override
    public String getString() { return name; }
  }
}
