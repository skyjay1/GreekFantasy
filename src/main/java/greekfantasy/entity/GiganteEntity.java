package greekfantasy.entity;

import java.util.UUID;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.ClubItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GiganteEntity extends CreatureEntity implements IAngerable {
  
  private static final int ATTACK_COOLDOWN = 32;

  private int attackCooldown;
  
  private static final RangedInteger ANGER_RANGE = TickRangeConverter.convertRange(20, 39);
  private int angerTime;
  private UUID angerTarget;
  
  public GiganteEntity(final EntityType<? extends GiganteEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
    this.experienceValue = 10;
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 100.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.22D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 5.5D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.65D);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(3, new GiganteEntity.MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.9D));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::func_233680_b_));
    this.targetSelector.addGoal(3, new ResetAngerGoal<>(this, true));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // attack cooldown
    attackCooldown = Math.max(attackCooldown - 1,  0);

    // particles
    if (world.isRemote() && horizontalMag(this.getMotion()) > (double)2.5000003E-7F && this.rand.nextInt(5) == 0) {
       int i = MathHelper.floor(this.getPosX());
       int j = MathHelper.floor(this.getPosY() - (double)0.2F);
       int k = MathHelper.floor(this.getPosZ());
       BlockPos pos = new BlockPos(i, j, k);
       BlockState blockstate = this.world.getBlockState(pos);
       if (!blockstate.isAir(this.world, pos)) {
          this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getPosX() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), this.getPosY() + 0.1D, this.getPosZ() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D);
       }
    }

    if (!this.world.isRemote()) {
       this.func_241359_a_((ServerWorld)this.world, true);
    }

 }
  
  @Override
  protected float getJumpUpwardsMotion() {
    return 0.82F * this.getJumpFactor();
  }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float amount = amountIn;
    if (!source.isDamageAbsolute() && GreekFantasy.CONFIG.GIGANTE_RESISTANCE.get()) {
      amount *= 0.6F;
    }
    super.damageEntity(source, amount);
  }
  
  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn)) {
      entityIn.setMotion(entityIn.getMotion().add(0.0D, (double)0.25F, 0.0D));
      return true;
    }
    return false;
  }
  
  @Nullable
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    if(this.rand.nextBoolean()) {
      final ItemStack club = new ItemStack(rand.nextBoolean() ? GFRegistry.STONE_CLUB : GFRegistry.WOODEN_CLUB);
      this.setHeldItem(Hand.MAIN_HAND, club);
    }
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_VILLAGER_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_VILLAGER_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_VILLAGER_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  @Override
  protected float getSoundPitch() { return 0.12F; }

  @Override
  public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
     this.writeAngerNBT(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
     this.readAngerNBT((ServerWorld)this.world, compound);
  }
  
  @Override
  public boolean canBePushed() { return false; }
  
  @Override
  public void applyEntityCollision(Entity entityIn) { 
    if(this.canBePushed()) {
      super.applyEntityCollision(entityIn);
    }
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
  
  // Cooldown methods
  
  public void setAttackCooldown() { attackCooldown = ATTACK_COOLDOWN; }
  
  public boolean hasNoCooldown() { return attackCooldown <= 0; }
  
  class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {

    public MeleeAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory) {
      super(creature, speedIn, useLongMemory);
    }
    
    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
      if(GiganteEntity.this.hasNoCooldown()) {
        super.checkAndPerformAttack(enemy, distToEnemySqr);
      }
    }
    
    @Override
    protected void func_234039_g_() {
      super.func_234039_g_();
      GiganteEntity.this.setAttackCooldown();
    }
  }

}
