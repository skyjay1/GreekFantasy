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
  
  private static final DataParameter<String> DATA_VARIANT = EntityDataManager.createKey(NaiadEntity.class, DataSerializers.STRING);
  private static final String KEY_VARIANT = "Variant";
  private static final String KEY_AGE = "Age";
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(10, 26);
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
    this.moveController = new SwimmingMovementController<>(this);
    this.setPathPriority(PathNodeType.WATER, 0.0F);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
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
      public boolean shouldExecute() { return entity.getAttackTarget() == null && entity.getRNG().nextInt(100) == 0 && super.shouldExecute(); }
    });
    this.goalSelector.addGoal(2, new NaiadEntity.TridentAttackGoal(this, 1.0D, 36, 12));
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(4, new SwimUpGoal<NaiadEntity>(this, 1.0D, this.world.getSeaLevel()));
    this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.8D, 140) {
      @Override
      public boolean shouldExecute() { return NaiadEntity.this.isInWater() && super.shouldExecute(); }
    });
    this.goalSelector.addGoal(4, new RandomWalkingGoal(this, 0.8D, 180) {
      @Override
      public boolean shouldExecute() { return !NaiadEntity.this.isInWater() && super.shouldExecute(); }
    });
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DrownedEntity.class, false));
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
    // update age
    ++age;
    // update swimming (client)
    if(world.isRemote()) {
      // update visually swimming flag
      if(this.ticksExisted % 11 == 1) {
        final BlockState blockBelow = world.getBlockState(this.getPositionUnderneath());
        this.isVisuallySwimming = this.getMotion().getY() > -0.01D && (swimmingUp || isSwimming() || isInWater()) && blockBelow.getFluidState().getFluid().isIn(FluidTags.WATER);
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
    if(source.getTrueSource() != null && source.getTrueSource().getType() == GFRegistry.NAIAD_ENTITY) {
      return true;
    }
    return super.isInvulnerableTo(source);
  }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float amount = amountIn;
    if (!source.isDamageAbsolute() && source.getTrueSource() instanceof MobEntity
        && ((MobEntity)source.getTrueSource()).getCreatureAttribute() == CreatureAttribute.UNDEAD) {
      amount *= 0.6F;
    }
    super.damageEntity(source, amount);
  }

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putString(KEY_VARIANT, this.getDataManager().get(DATA_VARIANT));
    compound.putInt(KEY_AGE, age);
    this.writeAngerNBT(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    this.setVariant(NaiadEntity.Variant.getByName(compound.getString(KEY_VARIANT)));
    age = compound.getInt(KEY_AGE);
    this.readAngerNBT((ServerWorld)this.world, compound);
  }
  
  @Override
  public ResourceLocation getLootTable() {
    return this.getVariant().getLootTable();
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
    final NaiadEntity.Variant variant;
    if(reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER || reason == SpawnReason.DISPENSER) {
      variant = NaiadEntity.Variant.getRandom(worldIn.getRandom());
    } else {
      variant = NaiadEntity.Variant.getForBiome(worldIn.func_242406_i(this.getPosition()));
    }
    this.setVariant(variant);
    final float tridentChance = (variant == Variant.OCEAN) ? 0.25F : 0.14F;
    if(this.rand.nextFloat() < tridentChance) {
      final ItemStack trident = new ItemStack(Items.TRIDENT);
      this.setHeldItem(Hand.MAIN_HAND, trident);
    }
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  @Override
  public boolean canDespawn(final double disToPlayer) {
    return this.age > 8400 && disToPlayer > 8.0D;
  }
  
  @Override
  public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
    TridentEntity trident = new TridentEntity(this.world, this, new ItemStack(Items.TRIDENT));
    double dx = target.getPosX() - getPosX();
    double dy = target.getPosYHeight(0.33D) - trident.getPosY();
    double dz = target.getPosZ() - getPosZ();
    double dis = MathHelper.sqrt(dx * dx + dz * dz);
    trident.shoot(dx, dy + dis * 0.2D, dz, 1.6F, (14 - this.world.getDifficulty().getId() * 4));
    playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));
    this.world.addEntity(trident);
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
  public boolean isPushedByWater() { return false; }

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
  
  @OnlyIn(Dist.CLIENT)
  public float animateSwimmingPercent() {
    return visuallySwimmingPercent;
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
    public String getString() {
      return name;
    }
  }
  
  class TridentAttackGoal extends RangedAttackGoal {

    public TridentAttackGoal(final NaiadEntity entityIn, double moveSpeed, int attackInterval, float attackDistance) {
      super(entityIn, moveSpeed, attackInterval, attackDistance);
    }

    @Override
    public boolean shouldExecute() {
      return (super.shouldExecute() && NaiadEntity.this.getHeldItemMainhand().getItem() == Items.TRIDENT);
    }

    @Override
    public void startExecuting() {
      super.startExecuting();
      NaiadEntity.this.setAggroed(true);
      NaiadEntity.this.setActiveHand(Hand.MAIN_HAND);
    }

    @Override
    public void resetTask() {
      super.resetTask();
      NaiadEntity.this.resetActiveHand();
      NaiadEntity.this.setAggroed(false);
    }
  }
}
