package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class NymphEntity extends CreatureEntity {
  
//  protected static final DataParameter<Byte> DATA_TYPE = EntityDataManager.<Byte>createKey(NymphEntity.class, DataSerializers.BYTE);
//  protected static final String KEY_TYPE = "NymphType";
  
  protected NymphEntity.Variant variant = NymphEntity.Variant.OAK;
  
  public NymphEntity(final EntityType<? extends NymphEntity> type, final World worldIn) {
    super(type, worldIn);
    // set variant
    variant = NymphEntity.Variant.getByName(type.getRegistryName().getPath());
  }
  
//  @Override
//  protected void registerData() {
//    super.registerData();
//    this.getDataManager().register(DATA_TYPE, (byte) 0);
//  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
 }
  
//  @Override
//  public void notifyDataManagerChange(DataParameter<?> key) {
//    super.notifyDataManagerChange(key);
//    // attempt to sync texture from client -> server -> other clients
//    if (DATA_TYPE.equals(key)) {
//      this.setNymphType(Variant.getById(this.getDataManager().get(DATA_TYPE)));
//    }
//  }
  
//  @Override
//  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
//    this.setNymphType(NymphEntity.Variant.BIRCH);
//    return spawnDataIn;
// }
//
//  @Override
//  public void writeAdditional(final CompoundNBT nbt) {
//    super.writeAdditional(nbt);
//    nbt.putByte(KEY_TYPE, (byte) this.getNymphType().getId());
//  }
//
//  @Override
//  public void readAdditional(final CompoundNBT nbt) {
//    super.readAdditional(nbt);
//    this.setNymphType(Variant.getById(nbt.getByte(KEY_TYPE)));
//  }
//  
  
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
