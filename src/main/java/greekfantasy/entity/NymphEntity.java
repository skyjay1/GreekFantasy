package greekfantasy.entity;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class NymphEntity extends CreatureEntity {
  
  private static final DataParameter<Byte> DATA_VARIANT = EntityDataManager.createKey(NymphEntity.class, DataSerializers.BYTE);
  private static final String KEY_VARIANT = "Variant";
    
  public NymphEntity(final EntityType<? extends NymphEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_VARIANT, Byte.valueOf((byte) 0));
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putByte(KEY_VARIANT, this.getDataManager().get(DATA_VARIANT).byteValue());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setVariant(NymphEntity.Variant.getById(compound.getByte(KEY_VARIANT)));
  }
  
  @Nullable
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final NymphEntity.Variant variant = Util.getRandomObject(NymphEntity.Variant.values(), this.rand);
    this.setVariant(variant);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }

  public void setVariant(final NymphEntity.Variant variant) {
    this.getDataManager().set(DATA_VARIANT, variant.getId());
  }
  
  public NymphEntity.Variant getVariant() {
    return NymphEntity.Variant.getById(this.getDataManager().get(DATA_VARIANT).byteValue());
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
