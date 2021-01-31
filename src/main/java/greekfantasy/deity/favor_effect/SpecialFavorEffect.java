package greekfantasy.deity.favor_effect;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;

public class SpecialFavorEffect {
  
  public static final SpecialFavorEffect EMPTY = new SpecialFavorEffect(FavorRange.EMPTY, Optional.empty(), Optional.empty(), 0.0F, 100L);
  
  public static final Codec<SpecialFavorEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      FavorRange.CODEC.fieldOf("favor_range").forGetter(SpecialFavorEffect::getFavorRange),
      CompoundNBT.CODEC.optionalFieldOf("potion").forGetter(SpecialFavorEffect::getPotion),
      Codec.FLOAT.optionalFieldOf("multiplier").forGetter(SpecialFavorEffect::getMultiplier),
      Codec.FLOAT.fieldOf("chance").forGetter(SpecialFavorEffect::getChance),
      Codec.LONG.fieldOf("mincooldown").forGetter(SpecialFavorEffect::getMinCooldown)
    ).apply(instance, SpecialFavorEffect::new));
  
  private final FavorRange favorRange;
  private final Optional<CompoundNBT> potion;
  private final Optional<Float> multiplier;
  private final float chance;
  private final long minCooldown;
  
  public SpecialFavorEffect(final FavorRange favorRangeIn,
      final Optional<CompoundNBT> potionIn, final Optional<Float> multiplierIn, 
      final float chanceIn, final long minCooldownIn) {
    favorRange = favorRangeIn;
    potion = potionIn;
    chance = chanceIn;
    multiplier = multiplierIn;
    minCooldown = minCooldownIn;
  }

  public float getChance() { return chance; }

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

  public FavorRange getFavorRange() { return favorRange; }

  public long getMinCooldown() { return minCooldown; }
  
  public boolean canApply(final PlayerEntity player) {
    return this != EMPTY && favorRange.isInFavorRange(player) && player.getRNG().nextFloat() < chance;
  }

  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("SpecialFavorEffect:");
    b.append(" range[").append(favorRange.toString()).append("]");
    potion.ifPresent(nbt -> b.append(" potion[").append(nbt.toString()).append("]"));
    multiplier.ifPresent(f -> b.append(" multiplier[").append(f.toString()).append("]"));
    b.append(" chance[").append(chance).append("]");
    b.append(" minCooldown[").append(minCooldown).append("]");
    return b.toString();
  }
  
  public static enum Type implements IStringSerializable {
    NONE("none"),
    NEARBY_CROPS_GROW("nearby_crops_grow"),
    NEARBY_CROPS_UNGROW("nearby_crops_ungrow"),
    BREEDING_OFFSPRING_MULTIPLIER("breeding_offspring_multiplier"),
    CROP_HARVEST_MULTIPLIER("crop_harvest_multiplier"),
    POTION_LENGTH_BONUS("potion_bonus_length"),
    POTION_NAUSEA_CHANCE("potion_nausea_chance"),
    MINING_AUTOSMELT("mining_autosmelt"),
    MINING_CANCEL_ORES("mining_cancel_ores"),
    XP_MULTIPLIER("xp_multiplier"),
    COMBAT_START_EFFECT("combat_start_effect"),
    TRADING_CANCEL("trading_cancel");
    
    private static final Map<String, SpecialFavorEffect.Type> valueMap = new HashMap<>();
    static {
      for(final SpecialFavorEffect.Type t : values()) {
        valueMap.put(t.getString(), t);
      }
    }
    
    public static final Codec<SpecialFavorEffect.Type> CODEC = Codec.STRING.comapFlatMap(
        s -> DataResult.success(SpecialFavorEffect.Type.getById(s)), SpecialFavorEffect.Type::getString).stable();
    
    private final String name;
    
    private Type(final String id) {
      name = id;
    }
    
    public static SpecialFavorEffect.Type getById(final String id) {
      return valueMap.getOrDefault(id, SpecialFavorEffect.Type.NONE);
    }

    @Override
    public String getString() {
      return name;
    }
  }
}
