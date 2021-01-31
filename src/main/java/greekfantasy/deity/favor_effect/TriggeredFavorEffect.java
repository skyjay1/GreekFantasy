package greekfantasy.deity.favor_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TriggeredFavorEffect {
  
  public static final TriggeredFavorEffect EMPTY = new TriggeredFavorEffect(FavorEffectTrigger.EMPTY, 0.0F, 0.0F, FavorEffect.EMPTY);
  
  public static final Codec<TriggeredFavorEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      FavorEffectTrigger.CODEC.fieldOf("trigger").forGetter(TriggeredFavorEffect::getTrigger),
      Codec.FLOAT.fieldOf("chance").forGetter(TriggeredFavorEffect::getChance),
      Codec.FLOAT.fieldOf("level_multiplier").forGetter(TriggeredFavorEffect::getLevelMultiplier),
      FavorEffect.CODEC.fieldOf("effect").forGetter(TriggeredFavorEffect::getEffect)
    ).apply(instance, TriggeredFavorEffect::new));
  
  private final FavorEffectTrigger trigger;
  private final float chance;
  private final float levelMultiplier;
  private final FavorEffect effect;
  
  public TriggeredFavorEffect(final FavorEffectTrigger triggerIn, final float chanceIn, 
      final float levelMultiplierIn, final FavorEffect effectIn) {
    trigger = triggerIn;
    chance = chanceIn;
    levelMultiplier = levelMultiplierIn;
    effect = effectIn;
  }

  public FavorEffectTrigger getTrigger() { return trigger; }

  public float getChance() { return chance; }
  
  public float getLevelMultiplier() { return levelMultiplier; }
  
  public float getAdjustedChance(final int level) { return Math.min(chance + Math.abs(level) * levelMultiplier, 1.0F); }

  public FavorEffect getEffect() { return effect; }
  
  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("TriggeredFavorEffect:");
    b.append(" ").append(trigger.toString());
    b.append(" chance[").append(chance).append(" + level*").append(levelMultiplier).append("]");
    b.append(" ").append(effect.toString());
    return b.toString();
  }
}
