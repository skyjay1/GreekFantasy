package greekfantasy.deity.favor_effect;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class FavorEffect {
  
  public static final FavorEffect EMPTY = new FavorEffect(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), 0, 0, 1000);
  
  public static final Codec<FavorEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      ResourceLocation.CODEC.optionalFieldOf("function").forGetter(FavorEffect::getFunction),
      CompoundNBT.CODEC.optionalFieldOf("potion").forGetter(FavorEffect::getPotion),
      CompoundNBT.CODEC.optionalFieldOf("summon").forGetter(FavorEffect::getSummon),
      ItemStack.CODEC.optionalFieldOf("item").forGetter(FavorEffect::getItem),
      Codec.STRING.optionalFieldOf("biome").forGetter(FavorEffect::getBiome),
      Codec.LONG.optionalFieldOf("favor").forGetter(FavorEffect::getFavor),
      Codec.INT.fieldOf("minlevel").forGetter(FavorEffect::getMinLevel),
      Codec.INT.fieldOf("maxlevel").forGetter(FavorEffect::getMaxLevel),
      Codec.LONG.fieldOf("mincooldown").forGetter(FavorEffect::getMinCooldown)
    ).apply(instance, FavorEffect::new));
  
  private final Optional<ResourceLocation> function;
  private final Optional<CompoundNBT> potion;
  private final Optional<CompoundNBT> summon;
  private final Optional<ItemStack> item;
  private final Optional<String> biome;
  private final Optional<Long> favor;
  private final int minLevel;
  private final int maxLevel;
  private final long minCooldown;
  
  private final String translationKey;
  
  protected FavorEffect(final Optional<ResourceLocation> functionIn, final Optional<CompoundNBT> potionIn, 
      final Optional<CompoundNBT> summonIn, final Optional<ItemStack> itemIn, final Optional<String> dataIn,
      final Optional<Long> favorIn, final int minLevelIn, final int maxLevelIn, final long minCooldownIn) {
    function = functionIn;
    potion = potionIn;
    summon = summonIn;
    item = itemIn;
    biome = dataIn;
    favor = favorIn;
    minLevel = minLevelIn;
    maxLevel = maxLevelIn;
    minCooldown = Math.max(1, minCooldownIn);
    
    if(function.isPresent()) {
      translationKey = "favor.effect.function";
    } else if(potion.isPresent()) {
      translationKey = "favor.effect.potion";
    } else if(summon.isPresent()) {
      translationKey = "favor.effect.summon";
    } else if(item.isPresent() ) {
      translationKey = "favor.effect.item";
    } else if(favor.isPresent()) {
      translationKey = "favor.effect.favor";
    } else {
      translationKey = "favor.effect.none";
    }
  }
  
  /** @return a ResourceLocation of the function to execute **/
  public Optional<ResourceLocation> getFunction() { return function; }
  
  /** @return a CompoundNBT of the potion effect to apply **/
  public Optional<CompoundNBT> getPotion() { return potion; }
  
  /** @return a CompoundNBT of the entity to summon **/
  public Optional<CompoundNBT> getSummon() { return summon; }
  
  /** @return an ItemStack to give the player **/
  public Optional<ItemStack> getItem() { return item; }

  /** @return a String to represent a biome (or biome type) predicate **/
  public Optional<String> getBiome() { return biome; }
  
  /** @return an amount of favor to give the player **/
  public Optional<Long> getFavor() { return favor; }
  
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
  
  /** 
   * @param playerLevel the player's favor level
   * @return true if the player level is within an applicable range
   **/
  public boolean isInRange(final int playerLevel) {
    if(maxLevel > minLevel) {
      return playerLevel <= maxLevel && playerLevel >= minLevel;
    } else {
      return playerLevel <= minLevel && playerLevel >= maxLevel;
    }
  }
  
  public boolean isInBiome(final World world, final BlockPos pos) {
    // if biome data is present for this effect, make sure the biome is correct
    if(biome.isPresent()) {
      final Optional<RegistryKey<Biome>> curBiome = world.getBiomeName(pos);
      if(biome.get().contains(":")) {
        // interpret as a ResourceLocation
        // if the biome name does not match, the effect cannot run
        if(curBiome.isPresent() && !curBiome.get().getRegistryName().toString().equals(biome.get())) {
          // GreekFantasy.LOGGER.debug("denied effect in biome " + curBiome + " (needs to be " + biome.get() + ")");
          return false;
        }
      } else {
        // interpret as a BiomeDictionary.TYPE
        final BiomeDictionary.Type type = BiomeDictionary.Type.getType(biome.get());
        // if the biome does not match the given type, the effect cannot run
        if(curBiome.isPresent() && !BiomeDictionary.hasType(curBiome.get(), type)) {
          // GreekFantasy.LOGGER.debug("denied effect in biome " + curBiome + " (needs to be type " + biome.get() + ")");
          return false;
        }
      }
    }
    // if the biome tag was not present OR it passed the tests, the effect can run
    return true;
  }
  
  public String getTranslationKey() { return translationKey; }
  
  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("FavorEffect:");
    function.ifPresent(r -> b.append(" function[").append(r.toString()).append("]"));
    potion.ifPresent(r -> b.append(" potion[").append(r.toString()).append("]"));
    summon.ifPresent(r -> b.append(" summon[").append(r.toString()).append("]"));
    item.ifPresent(r -> b.append(" item[").append(r.toString()).append("]"));
    favor.ifPresent(r -> b.append(" favor[").append(r.toString()).append("]"));
    b.append(" level[").append(minLevel).append(",").append(maxLevel).append("]");
    b.append(" cooldown[").append(minCooldown).append("]");
    return b.toString();
  }
}
