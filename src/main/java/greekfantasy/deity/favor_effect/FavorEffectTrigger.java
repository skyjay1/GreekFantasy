package greekfantasy.deity.favor_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

public class FavorEffectTrigger {
  
  public static final FavorEffectTrigger EMPTY = new FavorEffectTrigger(
      FavorEffectTrigger.Type.ENTITY_KILLED_PLAYER.getString(), new ResourceLocation("null"));
  
  public static final Codec<FavorEffectTrigger> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.STRING.fieldOf("type").forGetter(FavorEffectTrigger::getTypeString),
      ResourceLocation.CODEC.fieldOf("data").forGetter(FavorEffectTrigger::getData)
    ).apply(instance, FavorEffectTrigger::new));
  
  private final FavorEffectTrigger.Type type;
  private ResourceLocation data;
  
  public FavorEffectTrigger(String typeIn, ResourceLocation dataIn) {
    super();
    this.type = FavorEffectTrigger.Type.getById(typeIn);
    this.data = dataIn;
  }

  /** @return the trigger type **/
  public FavorEffectTrigger.Type getType() { return type; }
  
  /** @return the trigger type name (raw string) **/
  public String getTypeString() { return type.getString(); }

  /**
   * @return the data associated with this trigger.
   * Depending on the FavorEffectTrigger.Type, this could be
   * a potion id or an entity id
   */
  public ResourceLocation getData() { return data; }
  
  @Override
  public String toString() {
    final StringBuilder b = new StringBuilder("FavorEffectTrigger:");
    b.append(" type[").append(type.getString()).append("]");
    b.append(" data[").append(data.toString()).append("]");
    return b.toString();
  }

  public static enum Type implements IStringSerializable {
    PLAYER_BREAK_BLOCK("break_block"),
    EFFECTS_CHANGED("effects_changed"),
    ENTITY_HURT_PLAYER("entity_hurt_player"),
    ENTITY_KILLED_PLAYER("entity_killed_player"),
    PLAYER_HURT_ENTITY("player_hurt_entity"),
    PLAYER_KILLED_ENTITY("player_killed_entity");
    
    private final String name;
    
    private Type(final String id) {
      name = id;
    }
    
    public static FavorEffectTrigger.Type getById(final String id) {
      for(final FavorEffectTrigger.Type t : values()) {
        if(t.getString().equals(id)) {
          return t;
        }
      }
      return FavorEffectTrigger.Type.EFFECTS_CHANGED;
    }

    @Override
    public String getString() {
      return name;
    }
  }
}
