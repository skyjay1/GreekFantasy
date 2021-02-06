package greekfantasy.deity.favor;

import greekfantasy.deity.IDeity;
import greekfantasy.event.FavorChangedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;

public class FavorLevel {
  
  public static final int MAX_LEVEL = 10;
  public static final long MAX_FAVOR = calculateFavor(MAX_LEVEL + 1) - 1;
  
  public static final String FAVOR = "Favor";
    
  private long favor;
  private int level;
  
  public FavorLevel(final long f) {
    setFavor(f); 
  }
    
  /**
   * Directly modifies the favor, with some bounds-checking
   * to keep it within the min and max range. If possible,
   * use the context-aware method 
   * {@link #setFavor(PlayerEntity, IDeity, long, FavorChangedEvent.Source)}
   * @param favorIn the new favor value
   */
  public void setFavor(long favorIn) {
    if(favor != favorIn) {
      // update favor and level
      this.favor = clamp(favorIn, -MAX_FAVOR, MAX_FAVOR);
      this.level = calculateLevel(favor);
    }
  }
  
  /** @return the current favor value **/
  public long getFavor() { return favor; }
  
  /**
   * Context-aware method to add favor that also posts an event for any listeners.
   * If you don't want this, call {@link #setFavor(long)} directly.
   * @param playerIn the player whose favor is being modified
   * @param deityIn the deity for which the favor is being modified
   * @param newFavor the new favor amount
   * @param source the cause for the change in favor
   * @return the updated favor value
   */
  public long setFavor(final PlayerEntity playerIn, final IDeity deityIn, final long newFavor, final FavorChangedEvent.Source source) {
    // Post a context-aware event to allow other modifiers
    final FavorChangedEvent event = new FavorChangedEvent(playerIn, deityIn, favor, newFavor, source);
    MinecraftForge.EVENT_BUS.post(event);
    setFavor(event.getNewFavor());
    return favor;
  }
  
  /**
   * Context-aware method to add favor that also posts an event for any listeners.
   * If you don't want this, call {@link #setFavor(long)} directly.
   * @param playerIn the player whose favor is being modified
   * @param deityIn the deity for which the favor is being modified
   * @param toAdd the amount of favor to add or subtract
   * @param source the cause for the change in favor
   * @return the updated favor value
   */
  public long addFavor(final PlayerEntity playerIn, final IDeity deityIn, final long toAdd, final FavorChangedEvent.Source source) {
    // Post a context-aware event to allow other modifiers
    return setFavor(playerIn, deityIn, favor + toAdd, source);
  }
  
  /**
   * Either adds or removes favor to tend toward zero
   * @param playerIn the player whose favor is being modified
   * @param deityIn the deity for which the favor is being modified
   * @param toRemove the amount of favor to deplete (must be positive)
   * @param source the cause for the favor depletion (usually PASSIVE)
   * @return the updated favor value
   * @see #addFavor(PlayerEntity, IDeity, long, greekfantasy.event.FavorChangedEvent.Source)
   */
  public long depleteFavor(final PlayerEntity playerIn, final IDeity deityIn, final long toRemove, final FavorChangedEvent.Source source) {
    return addFavor(playerIn, deityIn, Math.min(Math.abs(favor), Math.abs(toRemove)) * -1 * (long)Math.signum(favor), source);
  }

  /** @return the current favor level **/
  public int getLevel() { return level; }
  
  /** @return the favor value required to advance to the next level **/
  public long getFavorToNextLevel() { return calculateFavor(level + (int)Math.signum(favor)); }
  
  /** @return the percent of favor that has been earned (always positive) **/
  public double getPercentFavor() { return Math.abs((double)favor / (double)MAX_FAVOR); }
  
  public int compareToAbs(FavorLevel other) { return (int) (Math.abs(this.getFavor()) - Math.abs(other.getFavor())); }

  /**
   * Sends a chat message to the player informing them of their current favor level
   * @param playerIn the player
   * @param deity the deity associated with this favor level
   */
  public void sendStatusMessage(final PlayerEntity playerIn, final IDeity deity) {
    long favorToNext = Math.min(MAX_FAVOR, getFavorToNextLevel());
    playerIn.sendStatusMessage(new TranslationTextComponent("favor.current_favor", deity.getText(), getFavor(), (favorToNext == 0 ? "--" : favorToNext), getLevel()).mergeStyle(TextFormatting.LIGHT_PURPLE), false);
  }
  
  @Override
  public String toString() {
    return Long.toString(favor) + " [" + Integer.toString(level) + "]";
  }
  
  /** @return the favor level based on amount of favor **/
  public static int calculateLevel(final long favorIn) {
    // calculate the current level based on favor
    final long f = Math.abs(favorIn);
    final int sig = (int)Math.signum(favorIn + 1);
    return sig * Math.floorDiv(-100 + (int)Math.sqrt(10000 + 40 * f), 20);
  }
  
  /** @return the maximum amount of favor for a given level **/
  public static long calculateFavor(final int lv) {
    final int l = Math.abs(lv);
    final int sig = (int)Math.signum(lv);
    return  sig * (10 * l * (l + 10));
  }
  
  /**
   * Replacement for the "MathHelper" version which is client-only
   * @param num the number to clamp
   * @param min the minimum value
   * @param max the maximum value
   * @return the number, or the min or max if num is out of range
   */
  public static long clamp(final long num, final long min, final long max) {
    if(num <= min) return min; 
    else if(num >= max) return max;
    else return num;
  }
}
