package greekfantasy.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.FavorableResetTargetGoal;
import greekfantasy.entity.ai.SwimUpGoal;
import greekfantasy.entity.ai.SwimmingMovementController;
import greekfantasy.entity.misc.IFavorable;
import greekfantasy.entity.misc.ISwimmingMob;
import greekfantasy.favor.Deity;
import greekfantasy.util.FavorRange;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.BiomeDictionary;

public class SirenEntity extends WaterMobEntity implements ISwimmingMob, IFavorable {
  
  private static final DataParameter<Boolean> CHARMING = EntityDataManager.createKey(SirenEntity.class, DataSerializers.BOOLEAN); 
  private final AttributeModifier attackModifier = new AttributeModifier("Charm attack bonus", 2.0D, AttributeModifier.Operation.MULTIPLY_TOTAL);

  private static final int STUN_DURATION = 80;

  private static final Map<String, FavorRange> FAVOR_RANGE_MAP = new HashMap<>();
  static {
    FAVOR_RANGE_MAP.put(CAN_ATTACK, new FavorRange(Deity.POSEIDON, -10, 6));
  }
  
  private boolean swimmingUp;
  private float swimmingPercent;
  
  public SirenEntity(final EntityType<? extends SirenEntity> type, final World worldIn) {
    super(type, worldIn);
    this.navigator = new SwimmerPathNavigator(this, worldIn);
    this.moveController = new SwimmingMovementController<>(this);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.25D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D);
  }
  
  // copied from DolphinEntity
  public static boolean canSirenSpawnOn(final EntityType<? extends WaterMobEntity> entity, final IWorld world, final SpawnReason reason, 
      final BlockPos pos, final Random rand) {
    if (pos.getY() <= 45 || pos.getY() >= world.getSeaLevel()) {
      return false;
    }

    RegistryKey<Biome> biome = world.func_242406_i(pos).orElse(Biomes.PLAINS);
    return (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN))
        && world.getFluidState(pos).isTagged(FluidTags.WATER);
  }
  
  @Override
  protected void registerGoals() {    
    this.goalSelector.addGoal(3, new SwimUpGoal<SirenEntity>(this, 1.0D, this.world.getSeaLevel() + 2));
    this.goalSelector.addGoal(4, new LookAtGoal(this, PlayerEntity.class, 8.0F));
    this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
    // add configurable goals
    if(GreekFantasy.CONFIG.SIREN_ATTACK.get()) {
      final Predicate<LivingEntity> avoidPred = entity -> {
        return !SirenEntity.this.isCharming() && EntityPredicates.CAN_AI_TARGET.test(entity);
      };
      this.goalSelector.addGoal(2, new CharmAttackGoal(250, 100, 24));
      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, PlayerEntity.class, 10.0F, 1.2D, 1.0D, avoidPred));
      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, SpartiEntity.class, 10.0F, 1.2D, 1.0D, avoidPred));
      this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, IronGolemEntity.class, 10.0F, 1.2D, 1.0D, avoidPred));
    } else {
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
    }
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::targetFavorAttackable));
    this.targetSelector.addGoal(3, new FavorableResetTargetGoal<>(this));
  }

  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(CHARMING, Boolean.valueOf(false));
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    
    // singing
    if(this.isCharming() && rand.nextInt(7) == 0) {
      final float color = 0.065F + rand.nextFloat() * 0.025F;
      this.playSound(SoundEvents.ENTITY_GHAST_WARN, 1.8F, color * 15);
      world.addParticle(ParticleTypes.NOTE, this.getPosX(), this.getPosYEye() + 0.15D, this.getPosZ(), color, 0.0D, 0.0D);
    }
    
    // swimming
    if(this.world.isRemote()) {
      final double motionY = this.getMotion().getY();
      if(!isSwimming() && !isInWater() || this.isSwingInProgress) {
        swimmingPercent = 0;
      } else if(motionY > -0.01) {
        swimmingPercent = Math.min(swimmingPercent + 0.1F, 1.0F);
      } else {
        swimmingPercent = Math.max(swimmingPercent - 0.1F, 0.0F);
      }
    }
  }

  // IFavorable methods
  
  @Override
  public Map<String, FavorRange> getFavorRangeMap() { return FAVOR_RANGE_MAP; }
  
  // Swimming methods

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
  
  // Charming methods
  
  public void setCharming(final boolean isCharming) { this.getDataManager().set(CHARMING, isCharming); }
  
  public boolean isCharming() { return this.getDataManager().get(CHARMING); }
  
  /**
   * Applies a special attack after charming the given entity
   * @param entity the target entity
   **/
  private void useCharmingAttack(final LivingEntity target) {
    // temporarily increase attack damage
    this.getAttribute(Attributes.ATTACK_DAMAGE).applyNonPersistentModifier(attackModifier);
    this.attackEntityAsMob(target);
    this.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(attackModifier);
    // apply stunned effect
    if(GreekFantasy.CONFIG.isStunningNerf()) {
      target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, STUN_DURATION, 0));
      target.addPotionEffect(new EffectInstance(Effects.WEAKNESS, STUN_DURATION, 0));
    } else {
      target.addPotionEffect(new EffectInstance(GFRegistry.STUNNED_EFFECT, STUN_DURATION, 0));
    }
  }
  
  // Client methods
  
  @OnlyIn(Dist.CLIENT)
  public float getSwimmingPercent(final float partialTick) {
    return swimmingPercent + (partialTick < 1.0F ? partialTick * 0.1F : 0);
  }
  
  // Charming goal
  
  class CharmAttackGoal extends Goal {
    
    protected final EffectInstance nausea;
    protected final int maxProgress;
    protected final int maxCooldown;    
    protected final float range;
    
    protected int progress;
    protected int cooldown;
    
    public CharmAttackGoal(final int progressIn, final int cooldownIn, final int rangeIn) {
      this.setMutexFlags(EnumSet.noneOf(Goal.Flag.class));
      maxProgress = progressIn;
      maxCooldown = cooldownIn;
      cooldown = 60;
      range = rangeIn;
      nausea = new EffectInstance(Effects.NAUSEA, maxProgress, 0, false, false);
    }
    
    @Override
    public boolean shouldExecute() {
      if(cooldown > 0) {
        cooldown--;
      } else {
        return SirenEntity.this.getAttackTarget() != null && SirenEntity.this.isEntityInRange(SirenEntity.this.getAttackTarget(), range);
      }
      return false;
    }

    @Override
    public void startExecuting() {
      SirenEntity.this.setCharming(true);
      this.progress = 1;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return this.progress > 0 && SirenEntity.this.getAttackTarget() != null 
          && SirenEntity.this.isEntityInRange(SirenEntity.this.getAttackTarget(), range);
    }
    
    @Override
    public void tick() {
      super.tick();
      final LivingEntity target = SirenEntity.this.getAttackTarget();
      final double disSq = SirenEntity.this.getEyePosition(1.0F).distanceTo(target.getPositionVec());
      SirenEntity.this.getNavigator().clearPath();
      SirenEntity.this.getLookController().setLookPositionWithEntity(target, 100.0F, 100.0F);
      // inflict nausea
      target.addPotionEffect(nausea);
      if(disSq > 3.5D) {
        // move the target toward this entity
        // TODO force boats to move toward the entity (boats reset velocity every tick)
        final Entity attract = /* target.getRidingEntity() instanceof BoatEntity ? target.getRidingEntity() : */target;
        attractEntity(attract, disSq);
      } else {
        // attack the target
        SirenEntity.this.useCharmingAttack(target);
        this.resetTask();
      }
    }
    
    @Override
    public void resetTask() {
      this.progress = 0;
      this.cooldown = maxCooldown;
      SirenEntity.this.setCharming(false);
    }
    
    private void attractEntity(final Entity entity, final double disSq) {
      // calculate the motion strength to apply
      //final double motion = 0.12 * Math.pow(1.25, -(MathHelper.sqrt(disSq) * (range / 200.0D)));
      final double motion = 0.06D + 0.009D * (1.0D - (disSq / (range * range)));
      final Vector3d vec = SirenEntity.this.getPositionVec().subtract(entity.getPositionVec())
          .normalize().scale(motion);
      entity.setMotion(entity.getMotion().add(vec).mul(0.5D, 1.0D, 0.5D));
      entity.addVelocity(0, 0.001D, 0);
      entity.velocityChanged = true;
    }
  }
}
