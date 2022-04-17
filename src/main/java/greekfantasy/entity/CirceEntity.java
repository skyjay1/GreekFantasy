package greekfantasy.entity;

import java.util.EnumSet;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.IntervalRangedAttackGoal;
import greekfantasy.entity.misc.SwineSpellEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class CirceEntity extends MonsterEntity implements IRangedAttackMob {
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.PURPLE, BossInfo.Overlay.PROGRESS));
  
  protected static final Predicate<LivingEntity> NOT_SWINE = e -> (e != null && null == e.getEffect(GFRegistry.SWINE_EFFECT));
  
  public CirceEntity(final EntityType<? extends CirceEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 60.0D)
        .add(Attributes.MOVEMENT_SPEED, 0.25D)
        .add(Attributes.ATTACK_DAMAGE, 1.0D)
        .add(Attributes.ARMOR, 1.5D);
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(1, new IntervalRangedAttackGoal(this, 90, 1, GreekFantasy.CONFIG.getSwineWandCooldown() * 4));
    this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class, 6.0F, 1.4D, 1.2D, e -> NOT_SWINE.test(e) && e == CirceEntity.this.getTarget()));
    this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D){
      @Override
      public boolean canUse() {
        return null == CirceEntity.this.getTarget() && CirceEntity.this.random.nextInt(90) == 0 && super.canUse();
      }
    });
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this, WitchEntity.class, CirceEntity.class));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, NOT_SWINE.and(e -> GreekFantasy.CONFIG.canSwineApply(e.getType().getRegistryName().toString())).and(EntityPredicates.ATTACK_ALLOWED)));
    this.targetSelector.addGoal(4, new ResetTargetGoal());
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());    
  }
  
  @Override
  public void addAdditionalSaveData(CompoundNBT compound) {
    super.addAdditionalSaveData(compound);
  }

  @Override
  public void readAdditionalSaveData(CompoundNBT compound) {
    super.readAdditionalSaveData(compound);
  }
  
  @Nullable
  public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    this.setItemInHand(Hand.MAIN_HAND, new ItemStack(GFRegistry.SWINE_WAND));
    return data;
  }
  
  // Attack //

  @Override
  public void performRangedAttack(LivingEntity arg0, float arg1) {
    if (!level.isClientSide()) {
      SwineSpellEntity spell = SwineSpellEntity.create(level, this);
      level.addFreshEntity(spell);
    }
    this.playSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.2F, 1.0F);
    // swing arm
    this.swing(Hand.MAIN_HAND);
  }
  
  // Sounds //
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.WITCH_AMBIENT; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.WITCH_HURT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.WITCH_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }

  //Boss //

  @Override
  public boolean canChangeDimensions() { return false; }

  @Override
  public void startSeenByPlayer(ServerPlayerEntity player) {
    super.startSeenByPlayer(player);
    this.bossInfo.addPlayer(player);
    this.bossInfo.setName(this.hasCustomName() ? this.getCustomName() : this.getDisplayName());
    this.bossInfo.setVisible(GreekFantasy.CONFIG.showCirceBossBar());
  }

  @Override
  public void stopSeenByPlayer(ServerPlayerEntity player) {
    super.stopSeenByPlayer(player);
    this.bossInfo.removePlayer(player);
  }
  
  // Goals //
  
  class ResetTargetGoal extends Goal {
    
    protected int interval;
    
    public ResetTargetGoal() { this(10); }
        
    public ResetTargetGoal(int intervalIn) {
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
      interval = Math.max(1, intervalIn);
    }

    @Override
    public boolean canUse() {
      return CirceEntity.this.tickCount % interval == 0 && CirceEntity.this.isAlive() 
          && CirceEntity.this.getTarget() instanceof LivingEntity
          && ((LivingEntity)CirceEntity.this.getTarget()).getEffect(GFRegistry.SWINE_EFFECT) != null;
    }
    
    @Override
    public boolean canContinueToUse() { return false; }
    
    @Override
    public void start() {
      CirceEntity.this.setTarget(null);
    }
  }

}
