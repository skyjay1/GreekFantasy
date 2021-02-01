package greekfantasy.deity.favor_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ConfiguredSpecialFavorEffect {
  
  public static final ConfiguredSpecialFavorEffect EMPTY = new ConfiguredSpecialFavorEffect(SpecialFavorEffect.Type.NONE.getString(), SpecialFavorEffect.EMPTY);
  
  public static final Codec<ConfiguredSpecialFavorEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.STRING.fieldOf("type").forGetter(ConfiguredSpecialFavorEffect::getTypeString),
      SpecialFavorEffect.CODEC.fieldOf("effect").forGetter(ConfiguredSpecialFavorEffect::getEffect)
    ).apply(instance, ConfiguredSpecialFavorEffect::new));
  
  private final SpecialFavorEffect.Type type;
  private final String typeString;
  private final SpecialFavorEffect effect;
  
  public ConfiguredSpecialFavorEffect(final String typeStringIn, final SpecialFavorEffect effectIn) {
    typeString = typeStringIn;
    type = SpecialFavorEffect.Type.getById(typeStringIn);
    effect = effectIn;
  }

  public SpecialFavorEffect.Type getType() { return type; }

  public String getTypeString() { return typeString; }

  public SpecialFavorEffect getEffect() { return effect; }
}
