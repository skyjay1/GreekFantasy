package greekfantasy.deity.favor_effect;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FavorEffect {
  
  public static final FavorEffect EMPTY = new FavorEffect(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 0, 0, 1000);
  
  public static final Codec<FavorEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.optionalFieldOf("function").forGetter(FavorEffect::getFunction),
      CompoundNBT.CODEC.optionalFieldOf("potion").forGetter(FavorEffect::getPotion),
      CompoundNBT.CODEC.optionalFieldOf("summon").forGetter(FavorEffect::getSummon),
      ItemStack.CODEC.optionalFieldOf("item").forGetter(FavorEffect::getItem),
      Codec.INT.fieldOf("minlevel").forGetter(FavorEffect::getMinLevel),
      Codec.INT.fieldOf("maxlevel").forGetter(FavorEffect::getMaxLevel),
      Codec.LONG.fieldOf("mincooldown").forGetter(FavorEffect::getMinCooldown)
    ).apply(instance, FavorEffect::new));
  
  private final Optional<ResourceLocation> function;
  private final Optional<CompoundNBT> potion;
  private final Optional<CompoundNBT> summon;
  private final Optional<ItemStack> item;
  private final int minLevel;
  private final int maxLevel;
  private final long minCooldown;
  
  protected FavorEffect(final Optional<ResourceLocation> functionIn, final Optional<CompoundNBT> potionIn, 
      final Optional<CompoundNBT> summonIn, final Optional<ItemStack> itemIn, 
      final int minLevelIn, final int maxLevelIn, final long minCooldownIn) {
    function = functionIn;
    potion = potionIn;
    summon = summonIn;
    item = itemIn;
    minLevel = minLevelIn;
    maxLevel = maxLevelIn;
    minCooldown = minCooldownIn;
  }
  
  /** @return a ResourceLocation of the function to execute **/
  public Optional<ResourceLocation> getFunction() { return function; }
  
  /** @return a CompoundNBT of the potion effect to apply **/
  public Optional<CompoundNBT> getPotion() { return potion; }
  
  /** @return a CompoundNBT of the entity to summon **/
  public Optional<CompoundNBT> getSummon() { return summon; }
  
  /** @return an ItemStack to give the player **/
  public Optional<ItemStack> getItem() { return item; }
  
  /** @return the minimum level required **/
  public int getMinLevel() { return minLevel; }
  
  /** @return the maximum level required **/
  public int getMaxLevel() { return maxLevel; }
  
  /** @return the minimum cooldown after executing **/
  public long getMinCooldown() { return minCooldown; }
  
  /**
   * @param rand a random instance
   * @return a number in the range [minCooldown, 2*minCooldown)
   */
  public long getRandomCooldown(final Random rand) { return minCooldown + (long)rand.nextInt((int)minCooldown); }
  
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
    final StringBuilder b = new StringBuilder("FavorEffect:");
    function.ifPresent(r -> b.append(" function[").append(r.toString()).append("]"));
    potion.ifPresent(r -> b.append(" potion[").append(r.toString()).append("]"));
    summon.ifPresent(r -> b.append(" summon[").append(r.toString()).append("]"));
    item.ifPresent(r -> b.append(" item[").append(r.toString()).append("]"));
    b.append(" level[").append(minLevel).append(",").append(maxLevel).append("]");
    b.append(" cooldown[").append(minCooldown).append("]");
    return b.toString();
  }
}
