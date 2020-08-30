package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class NymphEntity extends CreatureEntity {
  
  protected NymphEntity.Variant variant = NymphEntity.Variant.OAK;
  
  public NymphEntity(final EntityType<? extends NymphEntity> type, final World worldIn) {
    super(type, worldIn);
    // set variant
    variant = NymphEntity.Variant.getByName(type.getRegistryName().getPath());
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
 }
  
  public NymphEntity.Variant getVariant() {
    return variant;
  }
  
  public static enum Variant implements IStringSerializable {
    ACACIA("acacia"),
    BIRCH("birch"),
    DARK_OAK("dark_oak"),
    JUNGLE("jungle"),
    OAK("oak"),
    SPRUCE("spruce"),
    OCEAN("ocean"),
    RIVER("river");
    
    private final String name;
    private final ResourceLocation texture;
    
    private Variant(final String nameIn) {
      name = nameIn + "_nymph";
      texture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/nymph/" + name + ".png");
    }
    
    public static Variant getById(final byte i) {
      return values()[i];
    }
    
    public static Variant getByName(final String n) {
      // check the given name against all types
      if(n != null && !n.isEmpty()) {
        for(final Variant t : values()) {
          if(t.getString().equals(n)) {
            return t;
          }
        }
      }
      // defaults to OAK
      return OAK;
    }
    
    public boolean isWaterVariant() {
      return this == OCEAN || this == RIVER;
    }
    
    public ResourceLocation getTexture() {
      return texture;
    }
  
    public byte getId() {
      return (byte) this.ordinal();
    }

    @Override
    public String getString() {
      return name;
    }
  }

}
