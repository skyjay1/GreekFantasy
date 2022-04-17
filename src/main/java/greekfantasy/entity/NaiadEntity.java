package greekfantasy.entity;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.GoToWaterGoal;
import greekfantasy.entity.ai.SwimUpGoal;
import greekfantasy.entity.ai.SwimmingMovementController;
import greekfantasy.entity.misc.ISwimmingMob;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

public class NaiadEntity extends WaterMobEntity implements ISwimmingMob, IAngerable, IRangedAttackMob {
  
  private static final DataParameter<String> DATA_VARIANT = EntityDataManager.defineId(NaiadEntity.class, DataSerializers.STRING);
  private static final String KEY_VARIANT = "Variant";
  private static final String KEY_AGE = "Age";
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.rangeOfSeconds(10, 26);
  private int angerTime;
  private UUID angerTarget;

  protected int age;
  protected boolean swimmingUp;
  protected boolean isVisuallySwimming;
  protected float visuallySwimmingPercent;
  
  protected final SwimmerPathNavigator waterNavigator;
  protected final GroundPathNavigator groundNavigator;
    
  public NaiadEntity(final EntityType<? extends NaiadEntity> type, final World worldIn) {
    super(type, worldIn);
    this.waterNavigator = new SwimmerPathNavigator(this, worldIn);
    this.groundNavigator = new GroundPathNavigator(this, worldIn);
    this.moveControl = new SwimmingMovementController<>(this);
    this.setPathfindingMalus(PathNodeType.WATER, 0.0F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 24.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.25D)
        .add(Attributes.ATTACK_DAMAGE, 3.0D);
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
      public boolean canUse() { return entity.getTarget() == null && entity.getRandom().nextInt(100) == 0 && super.canUse(); }
    });
    this.goalSelector.addGoal(2, new NaiadEntity.TridentAttackGoal(this, 1.0D, 36, 12));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(4, new SwimUpGoal<NaiadEntity>(this, 1.0D, this.level.getSeaLevel()));
    this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.8D, 140) {
      @Override
      public boolean canUse() { return NaiadEntity.this.isInWater() && super.canUse(); }
    });
    this.goalSelector.addGoal(4, new RandomWalkingGoal(this, 0.8D, 180) {
      @Override
      public boolean canUse() { return !NaiadEntity.this.isInWater() && super.canUse(); }
    });
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DrownedEntity.class, false));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
    this.targetSelector.addGoal(4, new ResetAngerGoal<>(this, true));
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(DATA_VARIANT, NaiadEntity.Variant.RIVER.getSerializedName());
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
    // update age
    ++age;
    // update swimming (client)
    if(level.isClientSide()) {
      // update visually swimming flag
      if(this.tickCount % 11 == 1) {
        final BlockState blockBelow = level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement());
        this.isVisuallySwimming = this.getDeltaMovement().y() > -0.01D && (swimmingUp || isSwimming() || isInWater()) && blockBelow.getFluidState().getType().is(FluidTags.WATER);
      }
      // visually swimming percent
      if(isVisuallySwimming) {
        this.visuallySwimmingPercent = Math.min(visuallySwimmingPercent + 0.09F, 1.0F);
      } else {
        this.visuallySwimmingPercent = Math.max(visuallySwimmingPercent - 0.09F, 0.0F);
      }
    }
  }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    // immune to damage from other naiads
    if(source.getEntity() != null && source.getEntity().getType() == GFRegistry.NAIAD_ENTITY) {
      return true;
    }
    return super.isInvulnerableTo(source);
  }
  
  @Override
  protected void actuallyHurt(final DamageSource source, final float amountIn) {
    float amount = amountIn;
    if (!source.isBypassMagic() && source.getEntity() instanceof MobEntity
        && ((MobEntity)source.getEntity()).getMobType() == CreatureAttribute.UNDEAD) {
      amount *= 0.6F;
    }
    super.actuallyHurt(source, amount);
  }

  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
    compound.putString(KEY_VARIANT, this.getEntityData().get(DATA_VARIANT));
    compound.putInt(KEY_AGE, age);
    this.addPersistentAngerSaveData(compound);
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
    this.setVariant(NaiadEntity.Variant.getByName(compound.getString(KEY_VARIANT)));
    age = compound.getInt(KEY_AGE);
    this.readPersistentAngerSaveData((ServerWorld)this.level, compound);
  }
  
  @Override
  public ResourceLocation getDefaultLootTable() {
    return this.getVariant().getLootTable();
  }
  
  // IAngerable methods
  
  @Override
  public void startPersistentAngerTimer() { this.setRemainingPersistentAngerTime(ANGER_RANGE.randomValue(this.random)); }
  @Override
  public void setRemainingPersistentAngerTime(int time) { this.angerTime = time; }
  @Override
  public int getRemainingPersistentAngerTime() { return this.angerTime; }
  @Override
  public void setPersistentAngerTarget(@Nullable UUID target) { this.angerTarget = target; }
  @Override
  public UUID getPersistentAngerTarget() { return this.angerTarget; }

  @Override
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final NaiadEntity.Variant variant;
    if(reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER || reason == SpawnReason.DISPENSER) {
      variant = NaiadEntity.Variant.getRandom(worldIn.getRandom());
    } else {
      variant = NaiadEntity.Variant.getForBiome(worldIn.getBiomeName(this.blockPosition()));
    }
    this.setVariant(variant);
    final float tridentChance = (variant == Variant.OCEAN) ? 0.25F : 0.14F;
    if(this.random.nextFloat() < tridentChance) {
      final ItemStack trident = new ItemStack(Items.TRIDENT);
      this.setItemInHand(Hand.MAIN_HAND, trident);
    }
    return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  @Override
  public boolean removeWhenFarAway(final double disToPlayer) {
    return this.age > 8400 && disToPlayer > 8.0D;
  }
  
  @Override
  public void performRangedAttack(LivingEntity target, float distanceFactor) {
    TridentEntity trident = new TridentEntity(this.level, this, new ItemStack(Items.TRIDENT));
    double dx = target.getX() - getX();
    double dy = target.getY(0.33D) - trident.getY();
    double dz = target.getZ() - getZ();
    double dis = MathHelper.sqrt(dx * dx + dz * dz);
    trident.shoot(dx, dy + dis * 0.2D, dz, 1.6F, (14 - this.level.getDifficulty().getId() * 4));
    playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
    this.level.addFreshEntity(trident);
  }
  
  // Swimming methods
  
  @Override
  protected void handleAirSupply(int air) { }
  
  @Override
  public void setSwimmingUp(boolean swimmingUp) { this.swimmingUp = swimmingUp; }

  @Override
  public boolean isSwimmingUp() { return swimmingUp; }
  
  @Override
  public void travel(final Vector3d vec) {
    if (isEffectiveAi() && isInWater() && isSwimmingUpCalculated()) {
      moveRelative(0.01F, vec);
      move(MoverType.SELF, getDeltaMovement());
      setDeltaMovement(getDeltaMovement().scale(0.9D));
    } else {
      super.travel(vec);
    }
  }

  @Override
  public boolean isPushedByFluid() { return false; }

  @Override
  public boolean isSwimmingUpCalculated() {
    if (this.swimmingUp) {
      return true;
    }
    LivingEntity e = getTarget();
    return e != null && e.isInWater();
  }
  
  @Override
  public void updateSwimming() {
    if (!this.level.isClientSide) {
      if (isEffectiveAi() && isInWater() && isSwimmingUp()) {
        this.navigation = this.waterNavigator;
        setSwimming(true);
      } else {
        this.navigation = this.groundNavigator;
        setSwimming(false);
      }
    }
  }
  
  @OnlyIn(Dist.CLIENT)
  public float animateSwimmingPercent() {
    return visuallySwimmingPercent;
  }

  // Variant

  public void setVariant(final NaiadEntity.Variant variant) {
    this.getEntityData().set(DATA_VARIANT, variant.getSerializedName());
  }
  
  public NaiadEntity.Variant getVariant() {
    return NaiadEntity.Variant.getByName(this.getEntityData().get(DATA_VARIANT));
  }  
  
  public static enum Variant implements IStringSerializable {
    OCEAN("ocean"),
    RIVER("river");
    
    private final String name;
    private final ResourceLocation texture;
    private final ResourceLocation lootTable;
    
    private Variant(final String nameIn) {
      name = nameIn;
      texture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/naiad/" + name + ".png");
      lootTable = new ResourceLocation(GreekFantasy.MODID, "entities/naiad/" + name);
    }
    
    public static Variant getByName(final String n) {
      // check the given name against all types
      if(n != null && !n.isEmpty()) {
        for(final Variant t : values()) {
          if(t.getSerializedName().equals(n)) {
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
    
    public static Variant getRandom(final Random rand) {
      int len = values().length;
      return values()[rand.nextInt(len)];
    }

    public ResourceLocation getTexture() {
      return texture;
    }
    
    public ResourceLocation getLootTable() {
      return lootTable;
    }
  
    public byte getId() {
      return (byte) this.ordinal();
    }

    @Override
    public String getSerializedName() {
      return name;
    }
  }
  
  class TridentAttackGoal extends RangedAttackGoal {

    public TridentAttackGoal(final NaiadEntity entityIn, double moveSpeed, int attackInterval, float attackDistance) {
      super(entityIn, moveSpeed, attackInterval, attackDistance);
    }

    @Override
    public boolean canUse() {
      return (super.canUse() && NaiadEntity.this.getMainHandItem().getItem() == Items.TRIDENT);
    }

    @Override
    public void start() {
      super.start();
      NaiadEntity.this.setAggressive(true);
      NaiadEntity.this.startUsingItem(Hand.MAIN_HAND);
    }

    @Override
    public void stop() {
      super.stop();
      NaiadEntity.this.stopUsingItem();
      NaiadEntity.this.setAggressive(false);
    }
  }
}
