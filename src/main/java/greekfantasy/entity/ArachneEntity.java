package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.ClimberPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;

public class ArachneEntity extends MonsterEntity {
  private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(ArachneEntity.class, DataSerializers.BYTE);

  private final ServerBossInfo bossInfo = (ServerBossInfo) (new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED,
      BossInfo.Overlay.PROGRESS));

  public ArachneEntity(EntityType<? extends ArachneEntity> type, World worldIn) {
    super(type, worldIn);
  }

  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 80.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.285D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 4.5D)
        .createMutableAttribute(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(1, new SwimGoal(this));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
    this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
    this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
  }

  /**
   * Returns new PathNavigateGround instance
   */
  @Override
  protected PathNavigator createNavigator(World worldIn) {
    return new ClimberPathNavigator(this, worldIn);
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.dataManager.register(CLIMBING, (byte) 0);
  }

  /**
   * Called to update the entity's position/logic.
   */
  @Override
  public void tick() {
    super.tick();
    if (!this.world.isRemote()) {
      this.setBesideClimbableBlock(this.collidedHorizontally);
    }
  }

  @Override
  public void livingTick() {
    super.livingTick();
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
  }
  
  // Sounds //

  @Override
  protected SoundEvent getAmbientSound() {
    return SoundEvents.ENTITY_SPIDER_AMBIENT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    return SoundEvents.ENTITY_SPIDER_HURT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_SPIDER_DEATH;
  }

  @Override
  protected void playStepSound(BlockPos pos, BlockState blockIn) {
    this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
  }
  
  // Climbing //

  /**
   * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or for AI
   * reasons)
   */
  @Override
  public boolean isOnLadder() {
    return this.isBesideClimbableBlock();
  }

  @Override
  public void setMotionMultiplier(BlockState state, Vector3d motionMultiplierIn) {
    if (!state.matchesBlock(Blocks.COBWEB)) {
      super.setMotionMultiplier(state, motionMultiplierIn);
    }
  }

  /**
   * Returns true if the WatchableObject (Byte) is 0x01 otherwise returns false. The WatchableObject is updated using
   * setBesideClimableBlock.
   */
  public boolean isBesideClimbableBlock() {
    return (this.dataManager.get(CLIMBING) & 1) != 0;
  }

  /**
   * Updates the WatchableObject (Byte) created in entityInit(), setting it to 0x01 if par1 is true or 0x00 if it is false.
   */
  public void setBesideClimbableBlock(boolean climbing) {
    byte b0 = this.dataManager.get(CLIMBING);
    if (climbing) {
      b0 = (byte) (b0 | 1);
    } else {
      b0 = (byte) (b0 & -2);
    }

    this.dataManager.set(CLIMBING, b0);
  }
  
  // Misc //

  @Override
  public CreatureAttribute getCreatureAttribute() {
    return CreatureAttribute.ARTHROPOD;
  }

  @Override
  public boolean isPotionApplicable(EffectInstance potioneffectIn) {
    if (potioneffectIn.getPotion() == Effects.POISON) {
      net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent event = new net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent(
          this, potioneffectIn);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
      return event.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW;
    }
    return super.isPotionApplicable(potioneffectIn);
  }

  //Boss //

  @Override
  public boolean canChangeDimension() {
    return false;
  }

  @Override
  public void addTrackingPlayer(ServerPlayerEntity player) {
    super.addTrackingPlayer(player);
    this.bossInfo.addPlayer(player);
    this.bossInfo.setName(this.hasCustomName() ? this.getCustomName() : this.getDisplayName());
    this.bossInfo.setVisible(GreekFantasy.CONFIG.showArachneBossBar());
  }

  @Override
  public void removeTrackingPlayer(ServerPlayerEntity player) {
    super.removeTrackingPlayer(player);
    this.bossInfo.removePlayer(player);
  }

  @Override
  public boolean canDespawn(double distanceToClosestPlayer) {
    return false;
  }

}
