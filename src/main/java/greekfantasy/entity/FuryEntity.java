package greekfantasy.entity;

import java.util.EnumSet;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.FollowGoal;
import greekfantasy.entity.ai.IntervalRangedAttackGoal;
import greekfantasy.entity.misc.CurseEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class FuryEntity extends MonsterEntity implements IFlyingAnimal, IRangedAttackMob {  
  public static final int MAX_AGGRO_TIME = 45;
  public float flyingTime;
  public int aggroTime;
  
  public FuryEntity(final EntityType<? extends FuryEntity> type, final World worldIn) {
    super(type, worldIn);
    this.moveController = new FlyingMovementController(this, 10, false);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.26D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.5D)
        .createMutableAttribute(Attributes.FLYING_SPEED, 1.28D);
  }
  
  @Override
  protected void registerData() {
    super.registerData();
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(3, new FollowGoal(this, 1.0D, 6.0F, 12.0F) {
      @Override
      public boolean shouldExecute() { return entity.getRNG().nextInt(110) == 0 && super.shouldExecute(); }
    });
    this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 0.9D) {
      @Override
      public boolean shouldExecute() { return FuryEntity.this.getRNG().nextInt(120) == 0 && super.shouldExecute(); }
    });
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setCallsForHelp());
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    if(GreekFantasy.CONFIG.FURY_ATTACK.get()) {
      this.goalSelector.addGoal(1, new IntervalRangedAttackGoal(this, 210, 2, 200));
    }
  }

  @Override
  protected PathNavigator createNavigator(World worldIn) {
    FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
    flyingpathnavigator.setCanOpenDoors(false);
    flyingpathnavigator.setCanSwim(true);
    flyingpathnavigator.setCanEnterDoors(true);
    return flyingpathnavigator;
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // update falling moveSpeed
    Vector3d m = getMotion();
    if (this.isServerWorld() && !this.onGround && m.y < 0.0D) {
      final double multY = this.getAttackTarget() != null ? 0.9D : 0.6D;
      setMotion(m.mul(1.0D, multY, 1.0D));
    }
    // update flying counter
    if(this.isFlying()) {
      flyingTime = Math.min(1.0F, flyingTime + 0.1F);
    } else {
      flyingTime = Math.max(0.0F, flyingTime - 0.1F);
    }
    // update aggro counter
    if(this.isAggressive()) {
      aggroTime= Math.min(aggroTime + 1, MAX_AGGRO_TIME);
    } else {
      aggroTime = Math.max(aggroTime - 1, 0);
    }
  }
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_SPIDER_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_GHAST_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_GHAST_DEATH; }
  
  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected float getSoundPitch() { return 1.0F + rand.nextFloat() * 0.2F; }
  
  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
  }

  @Override
  public boolean onLivingFall(float distance, float damageMultiplier) { return false; }

  @Override
  protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) { }

  @Override
  protected boolean makeFlySound() { return true; }

  @Override
  protected float playFlySound(float volume) {
    this.playSound(SoundEvents.ITEM_ELYTRA_FLYING, 0.25F, 0.9F);
    return volume;
  }

  public boolean isFlying() {
    return !this.onGround || this.getMotion().lengthSquared() > 0.06D;
  }
  
  public float getAggroPercent(final float partialTick) {
    if(aggroTime <= 0) {
      return 0.0F;
    }
    final float prevAggroPercent = Math.max((float)aggroTime - partialTick, 0.0F) / (float)MAX_AGGRO_TIME;
    final float aggroPercent = (float)aggroTime / (float)MAX_AGGRO_TIME;
    return MathHelper.lerp(partialTick / 8, prevAggroPercent, aggroPercent); 
  }

  //Ranged Attack //

  @Override
  public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
    if (!world.isRemote()) {
      CurseEntity healingSpell = CurseEntity.create(world, this);
      world.addEntity(healingSpell);
    }
    this.playSound(SoundEvents.ENTITY_LLAMA_SPIT, 1.2F, 1.0F);
  }
}
