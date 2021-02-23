package greekfantasy.entity;

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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
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

  protected static final DataParameter<Boolean> FIRE = EntityDataManager.createKey(OrthusEntity.class, DataSerializers.BOOLEAN);
  protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.createKey(OrthusEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
  protected static final String KEY_FIRE = "Firing";
  protected static final String KEY_LIFE_TICKS = "LifeTicks";
  
  public static final Predicate<LivingEntity> TARGET_ENTITIES = e -> {
    return e.getType() == EntityType.STRIDER;
 };
  
  protected static final double FIRE_RANGE = 4.5D;
  protected static final int MAX_FIRE_TIME = 52;
  protected static final int FIRE_COOLDOWN = 165;
  
  protected static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(20, 39);
  protected int angerTime;
  protected UUID angerTarget;

  /** The number of ticks until the entity starts taking damage **/
  protected boolean limitedLifespan;
  protected int limitedLifeTicks;
  
  public OrthusEntity(final EntityType<? extends OrthusEntity> type, final World worldIn) {
    super(type, worldIn);
    this.setTamed(false);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.29D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.5D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(FIRE, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(2, new SitGoal(this));
    this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 4.0F, false));
    this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
    this.goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setCallsForHelp());
    this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, e -> this.func_233680_b_(e) || this.canAttackPlayer(e)));
    this.targetSelector.addGoal(5, new NonTamedTargetGoal<>(this, AnimalEntity.class, false, TARGET_ENTITIES));
    this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, AbstractSkeletonEntity.class, false));
    this.targetSelector.addGoal(7, new ResetAngerGoal<>(this, true));
    
    if(GreekFantasy.CONFIG.ORTHUS_ATTACK.get()) {
      this.goalSelector.addGoal(2, new OrthusEntity.FireAttackGoal(MAX_FIRE_TIME, FIRE_COOLDOWN, FIRE_RANGE));
    }
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // update target
    if(this.ticksExisted % 5 == 0 && getAttackTarget() instanceof PlayerEntity && !canAttackPlayer(getAttackTarget())) {
      setFireAttack(false);
      setAttackTarget(null);
      setRevengeTarget(null);
      func_241355_J__();
    }
    
    // lifespan
    if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
      this.limitedLifeTicks = 40;
      attackEntityFrom(DamageSource.STARVE, 1.0F);
    }
    
    // update fire attack
    if(this.isServerWorld() && this.isFireAttack() && this.getAttackTarget() == null) {
      this.setFireAttack(false);
    }
  
    // spawn particles
    if (world.isRemote() && this.isFireAttack()) {
      spawnFireParticles();
    }
  }
  
  public boolean canAttackPlayer(final LivingEntity e) {
    return e != null && !this.isOwner(e) && !e.getHeldItemMainhand().getItem().isIn(FOOD) && !e.getHeldItemOffhand().getItem().isIn(FOOD);
  }
  
  @Override
  public boolean isPotionApplicable(EffectInstance potioneffectIn) {
    return potioneffectIn.getPotion() != Effects.WITHER && super.isPotionApplicable(potioneffectIn);
  }

  @Override
  public boolean shouldAttackEntity(LivingEntity target, LivingEntity owner) {
    if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
      if (target instanceof TameableEntity) {
        TameableEntity tameable = (TameableEntity) target;
        return !tameable.isTamed() || tameable.getOwner() != owner;
      } else if (target instanceof IHasOwner<?>) {
        IHasOwner<?> tameable = (IHasOwner<?>) target;
        return !tameable.hasOwner() || tameable.getOwner() != owner;
      } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity
          && !((PlayerEntity) owner).canAttackPlayer((PlayerEntity) target)) {
        return false;
      } else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity) target).isTame()) {
        return false;
      } else {
        return !(target instanceof TameableEntity) || !((TameableEntity) target).isTamed();
      }
    } else {
      return false;
    }
  }
  
  @Override
  public boolean canBeLeashedTo(PlayerEntity player) {
    return !this.func_233678_J__() && super.canBeLeashedTo(player);
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
  
  // Other
  
  public void setLimitedLife(int life) {
    this.limitedLifespan = true;
    this.limitedLifeTicks = life;
  }

  @Override
  protected SoundEvent getAmbientSound() {
    if (this.rand.nextInt(3) == 0) {
      return SoundEvents.ENTITY_WOLF_AMBIENT;
    } else if (this.rand.nextInt(3) == 0) {
      return SoundEvents.ENTITY_WOLF_PANT;
    } else {
      return SoundEvents.ENTITY_WOLF_GROWL;
    }
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_WOLF_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_WOLF_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) { this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F); }
  
  @Override
  public ResourceLocation getLootTable() {
    return limitedLifespan ? LootTables.EMPTY : super.getLootTable();
  }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putBoolean(KEY_FIRE, isFireAttack());
    if (this.limitedLifespan) {
      compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
    }
    this.writeAngerNBT(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    setFireAttack(compound.getBoolean(KEY_FIRE));
    if (compound.contains(KEY_LIFE_TICKS)) {
      setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
    }
    this.readAngerNBT((ServerWorld)this.world, compound);
  }
  
  public void spawnFireParticles() {
    if(!world.isRemote()) {
      return;
    }
    Vector3d lookVec = this.getLookVec();
    Vector3d pos = this.getEyePosition(1.0F);
    final double motion = 0.06D;
    final double radius = 0.75D;
    
    for (int i = 0; i < 5; i++) {
      world.addParticle(ParticleTypes.FLAME, 
          pos.x + (world.rand.nextDouble() - 0.5D) * radius, 
          pos.y + (world.rand.nextDouble() - 0.5D) * radius, 
          pos.z + (world.rand.nextDouble() - 0.5D) * radius,
          lookVec.x * motion * FIRE_RANGE, 
          lookVec.y * motion * 0.5D,
          lookVec.z * motion * FIRE_RANGE);
    }
  }

  @Override
  public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity parent) {
    OrthusEntity baby = GFRegistry.ORTHUS_ENTITY.create(world);
    UUID uuid = this.getOwnerId();
    if (uuid != null) {
      baby.setOwnerId(uuid);
      baby.setTamed(true);
    }
    return baby;
  }

  @Override
  public void setTamed(boolean tamed) {
    super.setTamed(tamed);
    if (tamed) {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(36.0D);
      this.setHealth(36.0F);
    } else {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0D);
    }
  }
  
  @Override
  public boolean isBreedingItem(ItemStack stack) {
     return stack.getItem().isIn(FOOD);
  }
  
  @Override
  public boolean canMateWith(AnimalEntity otherAnimal) {
    if (otherAnimal == this) {
      return false;
    } else if (!this.isTamed()) {
      return false;
    } else if (!(otherAnimal instanceof OrthusEntity)) {
      return false;
    } else {
      OrthusEntity orthus = (OrthusEntity) otherAnimal;
      if (!orthus.isTamed()) {
        return false;
      } else if (orthus.isEntitySleeping()) {
        return false;
      } else {
        return this.isInLove() && orthus.isInLove();
      }
    }
  }
  
  @Override
  public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getHeldItem(hand);
    Item item = itemstack.getItem();
    if (this.world.isRemote()) {
      boolean flag = this.isOwner(player) || this.isTamed()
          || item.isIn(FOOD) && !this.isTamed();
      return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
    } else {
      if (this.isTamed()) {
        // attempt to heal entity
        if ((this.isBreedingItem(itemstack) || item == Items.BONE) && this.getHealth() < this.getMaxHealth()) {
          if (!player.abilities.isCreativeMode) {
            itemstack.shrink(1);
          }
          this.heal((float) item.getFood().getHealing());
          return ActionResultType.SUCCESS;
        }

        // attempt to udpate Sitting state
        ActionResultType actionresulttype = super.func_230254_b_(player, hand);
        if ((!actionresulttype.isSuccessOrConsume() || this.isChild()) && this.isOwner(player)) {
          this.func_233687_w_(!this.isSitting());
          this.isJumping = false;
          this.navigator.clearPath();
          this.setFireAttack(false);
          this.setAttackTarget(null);
          this.setRevengeTarget(null);
          this.func_241355_J__();
          return ActionResultType.SUCCESS;
        }
        return actionresulttype;
        
      } else if (item.isIn(FOOD)) {
        // reset anger
        if(this.func_233678_J__()) {
          this.setAngerTarget(null);
        }
        // consume the item
        if (!player.abilities.isCreativeMode) {
          itemstack.shrink(1);
        }
        // attempt to tame the entity
        if (this.rand.nextInt(4) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
          this.setTamedBy(player);
          this.setFireAttack(false);
          this.setAttackTarget(null);
          this.setRevengeTarget(null);
          this.func_241355_J__();
          this.navigator.clearPath();
          this.setAttackTarget((LivingEntity) null);
          this.func_233687_w_(true);
          this.world.setEntityState(this, (byte) 7);
        } else {
          this.world.setEntityState(this, (byte) 6);
        }

        return ActionResultType.SUCCESS;
      }

      return super.func_230254_b_(player, hand);
    }
  }
  
  public void setFireAttack(final boolean shooting) { this.dataManager.set(FIRE, shooting); }
  
  public boolean isFireAttack() { return this.getDataManager().get(FIRE).booleanValue(); }
  
  class FireAttackGoal extends ShootFireGoal {

    protected FireAttackGoal(final int fireTimeIn, final int maxCooldownIn, final double fireRange) {
      super(OrthusEntity.this, fireTimeIn, maxCooldownIn, fireRange);
    }

    @Override
    public boolean shouldExecute() {  
      return super.shouldExecute() && !OrthusEntity.this.isFireAttack() && !OrthusEntity.this.isSitting() && !OrthusEntity.this.isEntitySleeping();
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return super.shouldContinueExecuting() && OrthusEntity.this.isFireAttack();
    }
   
    @Override
    public void startExecuting() {
      super.startExecuting();
      OrthusEntity.this.setFireAttack(true);
    }
    
    @Override
    public void tick() {
      super.tick();
      OrthusEntity.this.setJumping(false);
    }
   
    @Override
    public void resetTask() {
      super.resetTask();
      OrthusEntity.this.setFireAttack(false);
    }
  }
}
