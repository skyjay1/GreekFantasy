package greekfantasy.favor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.GreekFantasy;
import net.minecraft.util.ResourceLocation;

public class FavorEffect {
  
  public static final FavorEffect EMPTY = new FavorEffect(new ResourceLocation(GreekFantasy.MODID, "null"), 0, 0, 1000);
  
  public static final Codec<FavorEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.fieldOf("function").forGetter(FavorEffect::getFunction),
      Codec.INT.fieldOf("minlevel").forGetter(FavorEffect::getMinLevel),
      Codec.INT.fieldOf("maxlevel").forGetter(FavorEffect::getMaxLevel),
      Codec.LONG.fieldOf("mincooldown").forGetter(FavorEffect::getMinCooldown)
    ).apply(instance, FavorEffect::new));
  
  private ResourceLocation function;
  private int minLevel;
  private int maxLevel;
  private long minCooldown;
  
  protected FavorEffect(final ResourceLocation functionIn, final int minLevelIn, final int maxLevelIn, final long minCooldownIn) {
    function = functionIn;
    minLevel = minLevelIn;
    maxLevel = maxLevelIn;
    minCooldown = minCooldownIn;
  }
  
  /** @return a ResourceLocation of the function to execute **/
  public ResourceLocation getFunction() { return function; }
  
  /** @return the minimum level required **/
  public int getMinLevel() { return minLevel; }
  
  /** @return the maximum level required **/
  public int getMaxLevel() { return maxLevel; }
  
  /** @return the minimum cooldown after executing **/
  public long getMinCooldown() { return minCooldown; }
  
  /** @return whether the level required is positive **/
  public boolean isPositive() { return minLevel >= 0 || maxLevel >= 0; }
  
  /** @return true if the player level is within an applicable range **/
  public boolean isInRange(final int playerLevel) {
    if(maxLevel > minLevel) {
      return playerLevel <= maxLevel && playerLevel >= minLevel;
    } else {
      return playerLevel <= minLevel && playerLevel >= maxLevel;
    }
  }
  
  @Override
  public String toString() {
    return "function[" + function.toString() + "] level[" + minLevel + ", " + maxLevel + "] cooldown[" + minCooldown + "]";
  }
}
