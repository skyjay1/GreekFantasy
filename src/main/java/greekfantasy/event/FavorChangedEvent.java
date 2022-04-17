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
  
  /**
   * @param playerIn the player whose favor is changing
   * @param deityIn the deity whose favor is changing
   * @param prevFavor the amount of favor before this event
   * @param curFavor the amount of favor after this event
   * @param sourceIn the source of the change in favor
   */
  public FavorChangedEvent(final PlayerEntity playerIn, final IDeity deityIn, 
      final long prevFavor, final long curFavor, final FavorChangedEvent.Source sourceIn) {
    super(playerIn);
    deity = deityIn;
    oldFavor = prevFavor;
    newFavor = curFavor;
    source = sourceIn;
    isLevelChange = FavorLevel.calculateLevel(curFavor) != FavorLevel.calculateLevel(prevFavor);
  }
  
  /** @return the Deity whose favor is changing **/
  public IDeity getDeity() { return deity; }
  
  /** @return the amount of favor from before this event **/
  public long getOldFavor() { return oldFavor; }
  
  /** @return the amount of favor that will be applied after this event **/
  public long getNewFavor() { return newFavor; }
  
  /**
   * @param favor the favor amount that should be applied instead
   */
  public void setNewFavor(final long favor) { 
    newFavor = favor;
    isLevelChange = FavorLevel.calculateLevel(newFavor) != FavorLevel.calculateLevel(oldFavor);
  }
  
  /** @return the Source of the change in favor **/
  public FavorChangedEvent.Source getSource() { return source; }
  
  /** @return true if this change in favor also changes the favor level **/
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
    public String getSerializedName() { return name; }
  }
}
