package greekfantasy.entity;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.GoToWaterGoal;
import greekfantasy.entity.ai.SwimUpGoal;
import greekfantasy.entity.ai.SwimmingMovementController;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NaiadEntity extends WaterMobEntity implements ISwimmingMob, IAngerable {
  
  private static final DataParameter<String> DATA_VARIANT = EntityDataManager.createKey(NaiadEntity.class, DataSerializers.STRING);
  private static final String KEY_VARIANT = "Variant";
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(10, 26);
  private int angerTime;
  private UUID angerTarget;

  protected boolean swimmingUp;
  
  protected final SwimmerPathNavigator waterNavigator;
  protected final GroundPathNavigator groundNavigator;
    
  public NaiadEntity(final EntityType<? extends NaiadEntity> type, final World worldIn) {
    super(type, worldIn);
    this.waterNavigator = new SwimmerPathNavigator(this, world);
    this.groundNavigator = new GroundPathNavigator(this, world);
    this.moveController = new SwimmingMovementController<>(this);
    this.setPathPriority(PathNodeType.WATER, 0.0F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  public static boolean canNaiadSpawnOn(EntityType<? extends WaterMobEntity> entity, IWorld world, SpawnReason reason, BlockPos pos,
      Random rand) {
    return SirenEntity.canSirenSpawnOn(entity, world, reason, pos, rand);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new GoToWaterGoal(this, 1.0D, 24) {
      @Override
      public boolean shouldExecute() { return entity.getAttackTarget() == null && entity.getRNG().nextInt(300) == 0 && super.shouldExecute(); }
      
    });
    this.goalSelector.addGoal(2, new SwimUpGoal<>(this, 1.0D, this.world.getSeaLevel()));
    this.goalSelector.addGoal(2, new RandomWalkingGoal(this, 0.8D, 300));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DrownedEntity.class, true));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
    this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, true));
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_VARIANT, NaiadEntity.Variant.RIVER.getString());
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putString(KEY_VARIANT, this.getDataManager().get(DATA_VARIANT));
    this.writeAngerNBT(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setVariant(NaiadEntity.Variant.getByName(compound.getString(KEY_VARIANT)));
    this.readAngerNBT((ServerWorld)this.world, compound);
  }
  
  // IAngerable methods
  
  @Override
  public void func_230258_H__() { this.setAngerTime(ANGER_RANGE.getRandomWithinRange(this.rand)); }
  @Override
  public void setAngerTime(int time) { this.angerTime = time; }
  @Override
  public int getAngerTime() { return this.angerTime; }
  @Override
  public void setAngerTarget(@Nullable UUID target) { this.angerTarget = target; }
  @Override
  public UUID getAngerTarget() { return this.angerTarget; }
 
  // End IAngerable methods
  
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final NaiadEntity.Variant variant = NaiadEntity.Variant.getForBiome(worldIn.func_242406_i(this.getPosition()));
    this.setVariant(variant);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  // Swimming methods
  
  @Override
  protected void updateAir(int air) { }
  
  @Override
  public void setSwimmingUp(boolean swimmingUp) { this.swimmingUp = swimmingUp; }

  @Override
  public boolean isSwimmingUp() { return swimmingUp; }
  
  @Override
  public void travel(final Vector3d vec) {
    if (isServerWorld() && isInWater() && isSwimmingUpCalculated()) {
      moveRelative(0.01F, vec);
      move(MoverType.SELF, getMotion());
      setMotion(getMotion().scale(0.9D));
    } else {
      super.travel(vec);
    }
  }

  @Override
  protected float getWaterSlowDown() { return 0.92F; }

  @Override
  public boolean isPushedByWater() { return !isSwimming(); }

  @Override
  public boolean isSwimmingUpCalculated() {
    if (this.swimmingUp) {
      return true;
    }
    LivingEntity e = getAttackTarget();
    return e != null && e.isInWater();
  }
  
  @Override
  public void updateSwimming() {
    if (!this.world.isRemote) {
      if (isServerWorld() && isInWater() && isSwimmingUp()) {
        this.navigator = this.waterNavigator;
        setSwimming(true);
      } else {
        this.navigator = this.groundNavigator;
        setSwimming(false);
      }
    }
  }

  // Variant

  public void setVariant(final NaiadEntity.Variant variant) {
    this.getDataManager().set(DATA_VARIANT, variant.getString());
  }
  
  public NaiadEntity.Variant getVariant() {
    return NaiadEntity.Variant.getByName(this.getDataManager().get(DATA_VARIANT));
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
    
    public static Variant getByName(final String n) {
      // check the given name against all types
      if(n != null && !n.isEmpty()) {
        for(final Variant t : values()) {
          if(t.getString().equals(n)) {
            return t;
          }
        }
      }
      // defaults to RIVER
      return RIVER;
    }
    
    public static Variant getForBiome(final Optional<RegistryKey<Biome>> biome) {
      return biome.isPresent() && Objects.equals(biome, Optional.of(Biomes.RIVER)) ? RIVER : OCEAN;
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
