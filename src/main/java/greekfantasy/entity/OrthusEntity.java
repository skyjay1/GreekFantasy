package greekfantasy.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.ShootFireGoal;
import greekfantasy.entity.misc.IHasOwner;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.NonTamedTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtByTargetGoal;
import net.minecraft.entity.ai.goal.OwnerHurtTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public class OrthusEntity extends TameableEntity implements IMob, IAngerable {
  
  protected static final IOptionalNamedTag<Item> FOOD = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "orthus_food"));

  protected static final DataParameter<Boolean> FIRE = EntityDataManager.defineId(OrthusEntity.class, DataSerializers.BOOLEAN);
  protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.defineId(OrthusEntity.class, DataSerializers.OPTIONAL_UUID);
  protected static final String KEY_FIRE = "Firing";
  protected static final String KEY_LIFE_TICKS = "LifeTicks";
  
  public static final Predicate<LivingEntity> TARGET_ENTITIES = e -> {
    return e.getType() == EntityType.STRIDER;
 };
  
  protected static final double FIRE_RANGE = 4.5D;
  protected static final int MAX_FIRE_TIME = 52;
  protected static final int FIRE_COOLDOWN = 165;
  
  protected static final RangedInteger ANGER_RANGE = TickRangeConverter.rangeOfSeconds(20, 39);
  protected int angerTime;
  protected UUID angerTarget;

  /** The number of ticks until the entity starts taking damage **/
  protected boolean limitedLifespan;
  protected int limitedLifeTicks;
  
  public OrthusEntity(final EntityType<? extends OrthusEntity> type, final World worldIn) {
    super(type, worldIn);
    this.setTame(false);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 24.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.29D)
        .add(Attributes.ATTACK_DAMAGE, 4.5D);
  }
  
  @Override
  public void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(FIRE, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new SitGoal(this));
    this.goalSelector.addGoal(3, new OrthusEntity.BegGoal(this, 8.0D));
    this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
    this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 4.0F, false));
    this.goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(8, new BreedGoal(this, 1.0D));
    this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
    this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, e -> this.isAngryAt(e) || this.canAttackPlayer(e)));
    this.targetSelector.addGoal(5, new NonTamedTargetGoal<>(this, AnimalEntity.class, false, TARGET_ENTITIES));
    this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, AbstractSkeletonEntity.class, false));
    this.targetSelector.addGoal(7, new ResetAngerGoal<>(this, true));
    
    if(GreekFantasy.CONFIG.ORTHUS_ATTACK.get()) {
      this.goalSelector.addGoal(2, new OrthusEntity.FireAttackGoal(MAX_FIRE_TIME, FIRE_COOLDOWN, FIRE_RANGE));
    }
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
    
    // update target
    if(this.tickCount % 5 == 0 && getTarget() instanceof PlayerEntity && !canAttackPlayer(getTarget())) {
      setFireAttack(false);
      setTarget(null);
      setLastHurtByMob(null);
      forgetCurrentTargetAndRefreshUniversalAnger();
    }
    
    // lifespan
    if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
      this.limitedLifeTicks = 40;
      hurt(DamageSource.STARVE, 1.0F);
    }
    
    // update fire attack
    if(this.isEffectiveAi() && this.isFireAttack() && this.getTarget() == null) {
      this.setFireAttack(false);
    }
  
    // spawn particles
    if (level.isClientSide() && this.isFireAttack()) {
      spawnFireParticles();
    }
  }
  
  public boolean canAttackPlayer(final LivingEntity e) {
    return e != null && !this.isOwnedBy(e) && !e.getMainHandItem().getItem().is(FOOD) && !e.getOffhandItem().getItem().is(FOOD);
  }
  
  @Override
  public boolean canBeAffected(EffectInstance potioneffectIn) {
    return potioneffectIn.getEffect() != Effects.WITHER && super.canBeAffected(potioneffectIn);
  }

  @Override
  public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
    if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
      if (target instanceof TameableEntity) {
        TameableEntity tameable = (TameableEntity) target;
        return !tameable.isTame() || tameable.getOwner() != owner;
      } else if (target instanceof IHasOwner<?>) {
        IHasOwner<?> tameable = (IHasOwner<?>) target;
        return !tameable.hasOwner() || tameable.getOwner() != owner;
      } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity
          && !((PlayerEntity) owner).canHarmPlayer((PlayerEntity) target)) {
        return false;
      } else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity) target).isTamed()) {
        return false;
      } else {
        return !(target instanceof TameableEntity) || !((TameableEntity) target).isTame();
      }
    } else {
      return false;
    }
  }
  
  @Override
  public boolean canBeLeashed(PlayerEntity player) {
    return !this.isAngry() && super.canBeLeashed(player);
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
  
  // Other
  
  public void setLimitedLife(int life) {
    this.limitedLifespan = true;
    this.limitedLifeTicks = life;
  }

  @Override
  protected SoundEvent getAmbientSound() {
    if (this.random.nextInt(3) == 0) {
      return SoundEvents.WOLF_AMBIENT;
    } else if (this.random.nextInt(3) == 0) {
      return SoundEvents.WOLF_PANT;
    } else {
      return SoundEvents.WOLF_GROWL;
    }
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.WOLF_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.WOLF_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) { this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F); }
  
  @Override
  public ResourceLocation getDefaultLootTable() {
    return limitedLifespan ? LootTables.EMPTY : super.getDefaultLootTable();
  }
  
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
    compound.putBoolean(KEY_FIRE, isFireAttack());
    if (this.limitedLifespan) {
      compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
    }
    this.addPersistentAngerSaveData(compound);
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
    setFireAttack(compound.getBoolean(KEY_FIRE));
    if (compound.contains(KEY_LIFE_TICKS)) {
      setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
    }
    this.readPersistentAngerSaveData((ServerWorld)this.level, compound);
  }
  
  public void spawnFireParticles() {
    if(!level.isClientSide()) {
      return;
    }
    Vector3d lookVec = this.getLookAngle();
    Vector3d pos = this.getEyePosition(1.0F);
    final double motion = 0.06D;
    final double radius = 0.75D;
    
    for (int i = 0; i < 5; i++) {
      level.addParticle(ParticleTypes.FLAME, 
          pos.x + (level.random.nextDouble() - 0.5D) * radius, 
          pos.y + (level.random.nextDouble() - 0.5D) * radius, 
          pos.z + (level.random.nextDouble() - 0.5D) * radius,
          lookVec.x * motion * FIRE_RANGE, 
          lookVec.y * motion * 0.5D,
          lookVec.z * motion * FIRE_RANGE);
    }
  }

  @Override
  public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity parent) {
    OrthusEntity baby = GFRegistry.ORTHUS_ENTITY.create(world);
    UUID uuid = this.getOwnerUUID();
    if (uuid != null) {
      baby.setOwnerUUID(uuid);
      baby.setTame(true);
    }
    return baby;
  }

  @Override
  public void setTame(boolean tamed) {
    super.setTame(tamed);
    if (tamed) {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(36.0D);
      this.setHealth(36.0F);
    } else {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0D);
    }
  }
  
  @Override
  public boolean isFood(ItemStack stack) {
     return stack.getItem().is(FOOD);
  }
  
  @Override
  public boolean canMate(AnimalEntity otherAnimal) {
    if (otherAnimal == this) {
      return false;
    } else if (!this.isTame()) {
      return false;
    } else if (!(otherAnimal instanceof OrthusEntity)) {
      return false;
    } else {
      OrthusEntity orthus = (OrthusEntity) otherAnimal;
      if (!orthus.isTame()) {
        return false;
      } else if (orthus.isInSittingPose()) {
        return false;
      } else {
        return this.isInLove() && orthus.isInLove();
      }
    }
  }
  
  @Override
  public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getItemInHand(hand);
    Item item = itemstack.getItem();
    if (this.level.isClientSide()) {
      boolean flag = this.isOwnedBy(player) || this.isTame()
          || item.is(FOOD) && !this.isTame();
      return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
    } else {
      if (this.isTame()) {
        // attempt to heal entity
        if ((this.isFood(itemstack) || item == Items.BONE) && this.getHealth() < this.getMaxHealth()) {
          if (!player.abilities.instabuild) {
            itemstack.shrink(1);
          }
          this.heal((float) item.getFoodProperties().getNutrition());
          return ActionResultType.SUCCESS;
        }

        // attempt to udpate Sitting state
        ActionResultType actionresulttype = super.mobInteract(player, hand);
        if ((!actionresulttype.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
          this.setOrderedToSit(!this.isOrderedToSit());
          this.jumping = false;
          this.navigation.stop();
          this.setFireAttack(false);
          this.setTarget(null);
          this.setLastHurtByMob(null);
          this.forgetCurrentTargetAndRefreshUniversalAnger();
          return ActionResultType.SUCCESS;
        }
        return actionresulttype;
        
      } else if (item.is(FOOD)) {
        // reset anger
        if(this.isAngry()) {
          this.setPersistentAngerTarget(null);
        }
        // consume the item
        if (!player.abilities.instabuild) {
          itemstack.shrink(1);
        }
        // attempt to tame the entity
        if (this.random.nextInt(4) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
          this.tame(player);
          this.setFireAttack(false);
          this.setTarget(null);
          this.setLastHurtByMob(null);
          this.forgetCurrentTargetAndRefreshUniversalAnger();
          this.navigation.stop();
          this.setTarget((LivingEntity) null);
          this.setOrderedToSit(true);
          this.level.broadcastEntityEvent(this, (byte) 7);
        } else {
          this.level.broadcastEntityEvent(this, (byte) 6);
        }

        return ActionResultType.SUCCESS;
      }

      return super.mobInteract(player, hand);
    }
  }
  
  public void setFireAttack(final boolean shooting) { this.entityData.set(FIRE, shooting); }
  
  public boolean isFireAttack() { return this.getEntityData().get(FIRE).booleanValue(); }
  
  static class BegGoal extends Goal {
    
    protected static final Predicate<LivingEntity> CAUSE_BEG = e -> e.getMainHandItem().getItem().is(FOOD) || e.getOffhandItem().getItem().is(FOOD);

    protected final CreatureEntity creature;
    protected final double range;
    protected final int interval;
    @Nullable
    protected LivingEntity player;
    
    protected BegGoal(final CreatureEntity entity, final double rangeIn) { this(entity, rangeIn, 10); }
    
    protected BegGoal(final CreatureEntity entity, final double rangeIn, int intervalIn) {
      creature = entity;
      range = rangeIn;
      interval = intervalIn;
    }
    
    
    @Override
    public boolean canUse() {
      if(creature.tickCount % interval == 0) {
        // find a player within range to cause begging
        final List<LivingEntity> list = creature.getCommandSenderWorld().getEntitiesOfClass(PlayerEntity.class, creature.getBoundingBox().inflate(range));
        if(!list.isEmpty()) {
          player = list.get(0);
        } else {
          player = null;
        }
      }
      return player != null;
    }
    
    @Override
    public boolean canContinueToUse() {
      return canUse();
    }
    
    @Override
    public void tick() {
      creature.getLookControl().setLookAt(player, creature.getMaxHeadYRot(), creature.getMaxHeadXRot());
      creature.getNavigation().stop();
    }
    
  }
  
  class FireAttackGoal extends ShootFireGoal {

    protected FireAttackGoal(final int fireTimeIn, final int maxCooldownIn, final double fireRange) {
      super(OrthusEntity.this, fireTimeIn, maxCooldownIn, fireRange);
    }

    @Override
    public boolean canUse() {  
      return super.canUse() && !OrthusEntity.this.isFireAttack() && !OrthusEntity.this.isOrderedToSit() && !OrthusEntity.this.isInSittingPose();
    }
    
    @Override
    public boolean canContinueToUse() {
      return super.canContinueToUse() && OrthusEntity.this.isFireAttack();
    }
   
    @Override
    public void start() {
      super.start();
      OrthusEntity.this.setFireAttack(true);
    }
    
    @Override
    public void tick() {
      super.tick();
      OrthusEntity.this.setJumping(false);
    }
   
    @Override
    public void stop() {
      super.stop();
      OrthusEntity.this.setFireAttack(false);
    }
  }
}
