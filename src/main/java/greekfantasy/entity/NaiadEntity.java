package greekfantasy.entity;

import java.util.Random;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;

public class NaiadEntity extends CreatureEntity {
  
  private static final DataParameter<Byte> DATA_VARIANT = EntityDataManager.createKey(NaiadEntity.class, DataSerializers.BYTE);
  private static final String KEY_VARIANT = "Variant";
    
  public NaiadEntity(final EntityType<? extends NaiadEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  //copied from DolphinEntity
  public static boolean canNaiadSpawnOn(EntityType<NaiadEntity> entity, IWorld world, SpawnReason reason, BlockPos pos,
      Random rand) {
    if (pos.getY() <= 45 || pos.getY() >= world.getSeaLevel()) {
      return false;
    }
    final Biome biome = world.getBiome(pos);
    return biome.getCategory() == Biome.Category.OCEAN || biome.getCategory() == Biome.Category.RIVER;
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
    this.setVariant(NaiadEntity.Variant.getById(compound.getByte(KEY_VARIANT)));
  }
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final Biome.Category biome = worldIn.getBiome(this.getPosition()).getCategory();
    this.setVariant(biome == Category.OCEAN ? NaiadEntity.Variant.OCEAN : NaiadEntity.Variant.RIVER);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }

  public void setVariant(final NaiadEntity.Variant variant) {
    this.getDataManager().set(DATA_VARIANT, variant.getId());
  }
  
  public NaiadEntity.Variant getVariant() {
    return NaiadEntity.Variant.getById(this.getDataManager().get(DATA_VARIANT).byteValue());
  }
  
  public static enum Variant implements IStringSerializable {
    OCEAN("ocean"),
    RIVER("river");
    
    private final String name;
    private final ResourceLocation texture;
    
    private Variant(final String nameIn) {
      name = nameIn;
      texture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/naiad/" + name + ".png");
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
      return RIVER;
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
