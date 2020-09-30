package greekfantasy.entity;

import java.util.Random;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.ClubItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class GeryonEntity extends MonsterEntity {
  
  public GeryonEntity(final EntityType<? extends GeryonEntity> type, final World worldIn) {
    super(type, worldIn);
    this.stepHeight = 1.0F;
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 120.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.30D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.5D)
        .createMutableAttribute(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT);
  }
  
  public static boolean canGeryonSpawnOn(final EntityType<? extends MobEntity> entity, final IWorld world, final SpawnReason reason, 
      final BlockPos pos, final Random rand) {
    return world.canBlockSeeSky(pos.up()) && MobEntity.canSpawnOn(entity, world, reason, pos, rand);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();

    // spawn particles
    if (horizontalMag(this.getMotion()) > (double)2.5000003E-7F && this.rand.nextInt(5) == 0) {
       int i = MathHelper.floor(this.getPosX());
       int j = MathHelper.floor(this.getPosY() - (double)0.2F);
       int k = MathHelper.floor(this.getPosZ());
       BlockPos pos = new BlockPos(i, j, k);
       BlockState blockstate = this.world.getBlockState(pos);
       if (!blockstate.isAir(this.world, pos)) {
          this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getPosX() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), this.getPosY() + 0.1D, this.getPosZ() + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D);
       }
    }
 }
  
  @Override
  protected void damageEntity(final DamageSource source, final float amountIn) {
    float amount = amountIn;
    if (GreekFantasy.CONFIG.GERYON_RESISTANCE.get()) {
      amount *= 0.6F;
    }
    super.damageEntity(source, amount);
  }
  
  @Override
  public boolean attackEntityAsMob(final Entity entityIn) {
    if (super.attackEntityAsMob(entityIn)) {
      entityIn.setMotion(entityIn.getMotion().add(0.0D, (double)0.55F, 0.0D));
      return true;
    }
    return false;
  }

  @Override
  protected float getJumpUpwardsMotion() {
    return 0.82F * this.getJumpFactor();
  }

  @Override
  public boolean canBePushed() { return false; }
  
  @Nullable
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    final ItemStack club = new ItemStack(GFRegistry.IRON_CLUB);
    this.setHeldItem(Hand.MAIN_HAND, club);
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  

  @Override
  public void writeAdditional(CompoundNBT compound) {
     super.writeAdditional(compound);
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
     super.readAdditional(compound);
  }
}
