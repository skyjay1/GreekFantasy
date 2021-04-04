package greekfantasy.deity.favor_effect;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import greekfantasy.deity.IDeity;
import greekfantasy.deity.favor.IFavor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class SpecialFavorEffect {
  
  public static final SpecialFavorEffect EMPTY = new SpecialFavorEffect(SpecialFavorEffect.Type.NONE.getString(), 
      0, 0, Optional.empty(), Optional.empty(), 0.0F, 0.0F, 100L);
  
  public static final Codec<SpecialFavorEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.STRING.fieldOf("type").forGetter(SpecialFavorEffect::getTypeString),
      Codec.INT.fieldOf("minlevel").forGetter(SpecialFavorEffect::getMinLevel),
      Codec.INT.fieldOf("maxlevel").forGetter(SpecialFavorEffect::getMaxLevel),
      CompoundNBT.CODEC.optionalFieldOf("potion").forGetter(SpecialFavorEffect::getPotion),
      Codec.FLOAT.optionalFieldOf("multiplier").forGetter(SpecialFavorEffect::getMultiplier),
      Codec.FLOAT.fieldOf("chance").forGetter(SpecialFavorEffect::getChance),
      Codec.FLOAT.fieldOf("level_multiplier").forGetter(SpecialFavorEffect::getLevelMultiplier),
      Codec.LONG.fieldOf("mincooldown").forGetter(SpecialFavorEffect::getMinCooldown)
    ).apply(instance, SpecialFavorEffect::new));
  
  private final SpecialFavorEffect.Type type;
  private final String typeString;
  private final int minLevel;
  private final int maxLevel;
  private final Optional<CompoundNBT> potion;
  private final Optional<Float> multiplier;
  private final float chance;
  private final float levelMultiplier;
  private final long minCooldown;
  
  public SpecialFavorEffect(final String typeStringIn, final int minLevelIn, final int maxLevelIn,
      final Optional<CompoundNBT> potionIn, final Optional<Float> multiplierIn, 
      final float chanceIn, final float levelMultiplierIn, final long minCooldownIn) {
    typeString = typeStringIn;
    type = SpecialFavorEffect.Type.getById(typeStringIn);
    minLevel = minLevelIn;
    maxLevel = maxLevelIn;
    potion = potionIn;
    multiplier = multiplierIn;
    chance = chanceIn;
    levelMultiplier = levelMultiplierIn;
    minCooldown = minCooldownIn;
  }
  
  public SpecialFavorEffect.Type getType() { return type; }

  public String getTypeString() { return typeString; }

  public int getMinLevel() { return minLevel; }
  
  public int getMaxLevel() { return maxLevel; }

  public float getChance() { return chance; }
  
  public float getLevelMultiplier() { return levelMultiplier; }
  
  public float getAdjustedChance(final int level) { return Math.min(chance + Math.abs(level) * levelMultiplier, 1.0F); }

  public Optional<Float> getMultiplier() { return multiplier; }

  public Optional<CompoundNBT> getPotion() { return potion; }
  
  public Optional<EffectInstance> getPotionEffect() {
    if(getPotion().isPresent()) {
      final CompoundNBT nbt = getPotion().get().copy();
      nbt.putByte("Id", (byte) Effect.getId(ForgeRegistries.POTIONS.getValue(new ResourceLocation(getPotion().get().getString("Potion")))));
      return Optional.of(EffectInstance.read(nbt));
    }
    return Optional.empty();
  }

  public long getMinCooldown() { return minCooldown; }  
  
  /**
   * @param rand a random instance
   * @return a number in the range [minCooldown, 2*minCooldown)
   */
  public long getRandomCooldown(final Random rand) { return minCooldown + (long)rand.nextInt((int)minCooldown); }
  
  /**
   * @param player the player
   * @param deity the deity to check
   * @param favor the player's favor
   * @return true if this special effect can apply to the given player
   */
  public boolean canApply(final PlayerEntity player, final IDeity deity, final IFavor favor) {
    return favor.isEnabled() && this != EMPTY && type != SpecialFavorEffect.Type.NONE 
        && deity.isEnabled() && isInFavorRange(player, deity, favor) 
        && player.getRNG().nextFloat() < getAdjustedChance(favor.getFavor(deity).getLevel());
  }
  
  /**
   * @param player the player
   * @param deity the deity to check
   * @param f the player's favor 
   * @return true if the player's favor matches this favor range
   */
  public boolean isInFavorRange(final PlayerEntity player, final IDeity deity, final IFavor f) {
    if(this == EMPTY || !f.isEnabled()) {
      return false;
    }
    final int playerLevel = f.getFavor(deity).getLevel();
    if(maxLevel > minLevel) {
      return playerLevel <= maxLevel && playerLevel >= minLevel;
    } else {
      return playerLevel <= minLevel && playerLevel >= maxLevel;
    }
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("SpecialFavorEffect:");
    b.append(" type[").append(type.getString()).append(" ]");
    b.append(" range[").append(minLevel).append(" ~ ").append(maxLevel).append("]");
    potion.ifPresent(nbt -> b.append(" potion[").append(nbt.toString()).append("]"));
    multiplier.ifPresent(f -> b.append(" multiplier[").append(f.toString()).append("]"));
    b.append(" chance[").append(chance).append("]");
    b.append(" minCooldown[").append(minCooldown).append("]");
    return b.toString();
  }
  
  public static enum Type implements IStringSerializable {
    NONE("none"),
    ARROW_DAMAGE_MULTIPLIER("arrow_damage_multiplier"),
    BREEDING_OFFSPRING_MULTIPLIER("breeding_offspring_multiplier"),
    COMBAT_START_EFFECT("combat_start_effect"),
    CROP_GROWTH_MULTIPLIER("crop_growth_multiplier"),
    CROP_HARVEST_MULTIPLIER("crop_harvest_multiplier"),
    POTION_BONUS_LENGTH("potion_bonus_length"),
    POTION_BONUS_EFFECT("potion_bonus_effect"),
    MINING_AUTOSMELT("mining_autosmelt"),
    MINING_CANCEL_ORES("mining_cancel_ores"),
    TRADING_CANCEL("trading_cancel"),
    XP_MULTIPLIER("xp_multiplier");
    
    private static final Map<String, SpecialFavorEffect.Type> valueMap = new HashMap<>();
    static {
      for(final SpecialFavorEffect.Type t : values()) {
        valueMap.put(t.getString(), t);
      }
    }
    
    public static final Codec<SpecialFavorEffect.Type> CODEC = Codec.STRING.comapFlatMap(
        s -> DataResult.success(SpecialFavorEffect.Type.getById(s)), SpecialFavorEffect.Type::getString).stable();
    
    private final String name;
    private final String translationKey;
    
    private Type(final String id) {
      name = id;
      translationKey = "favor.effect.type." + id;
    }
    
    public static SpecialFavorEffect.Type getById(final String id) {
      return valueMap.getOrDefault(id, SpecialFavorEffect.Type.NONE);
    }
    
    public String getTranslationKey() {
      return translationKey;
    }

    @Override
    public String getString() {
      return name;
    }
  }
}
