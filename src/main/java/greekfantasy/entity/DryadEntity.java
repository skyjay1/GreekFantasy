package greekfantasy.entity;

import java.util.Optional;

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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class DryadEntity extends CreatureEntity {
  
  private static final DataParameter<Byte> DATA_VARIANT = EntityDataManager.createKey(DryadEntity.class, DataSerializers.BYTE);
  private static final String KEY_VARIANT = "Variant";
    
  public DryadEntity(final EntityType<? extends DryadEntity> type, final World worldIn) {
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
    this.setVariant(DryadEntity.Variant.getById(compound.getByte(KEY_VARIANT)));
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final DryadEntity.Variant variant = DryadEntity.Variant.getForBiome(worldIn.func_242406_i(this.getPosition()));
    this.setVariant(variant);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }

  public void setVariant(final DryadEntity.Variant variant) {
    this.getDataManager().set(DATA_VARIANT, variant.getId());
  }
  
  public DryadEntity.Variant getVariant() {
    return DryadEntity.Variant.getById(this.getDataManager().get(DATA_VARIANT).byteValue());
  }
  
  public static enum Variant implements IStringSerializable {
    ACACIA("acacia"),
    BIRCH("birch"),
    DARK_OAK("dark_oak"),
    JUNGLE("jungle"),
    OAK("oak"),
    SPRUCE("spruce");
    
    private final String name;
    private final ResourceLocation texture;
    
    private Variant(final String nameIn) {
      name = nameIn;
      texture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/dryad/" + name + ".png");
    }
    
    public static Variant getForBiome(final Optional<RegistryKey<Biome>> biome) {
      final String biomeName = biome.isPresent() ? biome.get().getRegistryName().getPath() : "";
      if(biomeName.contains("birch")) {
        return BIRCH;
      }
      if(biomeName.contains("dark_forest")) {
        return DARK_OAK;
      }
      if(biomeName.contains("taiga")) {
        return SPRUCE;
      }
      if(biomeName.contains("jungle")) {
        return JUNGLE;
      }
      if(biomeName.contains("savanna")) {
        return ACACIA;
      }
      return OAK;
    }
    
    public static Variant getById(final byte i) {
      return values()[MathHelper.clamp(i, 0, values().length)];
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
