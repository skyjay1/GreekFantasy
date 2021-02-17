package greekfantasy.entity;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GorgonEntity extends MonsterEntity implements IRangedAttackMob {
  
  private static final DataParameter<Boolean> MEDUSA = EntityDataManager.createKey(GorgonEntity.class, DataSerializers.BOOLEAN);
  private static final String KEY_MEDUSA = "Medusa";
  
  protected static final byte STARE_ATTACK = 9;
  protected static final int PETRIFY_DURATION = 80;
  
  private static final ResourceLocation MEDUSA_LOOT = new ResourceLocation(GreekFantasy.MODID, "entities/medusa");
  
  private final ServerBossInfo bossInfo = (ServerBossInfo)(new ServerBossInfo(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS));

  private final GorgonEntity.RangedAttackGoal rangedAttackGoal = new RangedAttackGoal(this, 1.0D, 45, 15.0F);
  
  public GorgonEntity(final EntityType<? extends GorgonEntity> type, final World worldIn) {
    super(type, worldIn);
  }
  
  public static AttributeModifierMap.MutableAttribute getAttributes() {
    return MobEntity.func_233666_p_()
        .createMutableAttribute(Attributes.MAX_HEALTH, 24.0D)
        .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.26D)
        .createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D)
        .createMutableAttribute(Attributes.FOLLOW_RANGE, 24.0D);
  }
  
  @Override
  public void registerData() {
    super.registerData();
    this.getDataManager().register(MEDUSA, Boolean.valueOf(false));
  }
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(0, new SwimGoal(this));
    this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
    this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
    this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    if(GreekFantasy.CONFIG.GORGON_ATTACK.get()) {
      this.goalSelector.addGoal(2, new StareAttackGoal(this, PETRIFY_DURATION + 20));
    }
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // boss info
    this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
  }
  
  @Override
  public void func_241841_a(ServerWorld world, LightningBoltEntity bolt) { // onEntityStruckByLightning
    if (world.getDifficulty() != Difficulty.PEACEFUL && rand.nextInt(100) < GreekFantasy.CONFIG.getLightningMedusaChance()) {
      this.setMedusa(true);
      this.setHealth(this.getMaxHealth());
      this.enablePersistence();
      this.setFire(2);
    } else {
      super.func_241841_a(world, bolt);
    }
  }
  
  @Override
  public boolean isInvulnerableTo(final DamageSource source) {
    // immune to damage from other gorgons
    if(source.getTrueSource() != null && source.getTrueSource().getType() == GFRegistry.GORGON_ENTITY) {
      return true;
    }
    return super.isInvulnerableTo(source);
  }
  
  @Nullable
  public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
      @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
    if(this.getRNG().nextDouble() * 100.0D < GreekFantasy.CONFIG.getGorgonMedusaChance()) {
      this.setMedusa(true);
    }
    return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
  }
  
  @OnlyIn(Dist.CLIENT)
  public void handleStatusUpdate(byte id) {
    switch(id) {
    case STARE_ATTACK:
      spawnStareParticles();
      break;
    default:
      super.handleStatusUpdate(id);
      break;
    }
  }
  
  public void spawnStareParticles() {
    if (world.isRemote()) {
      final double motion = 0.08D;
      final double radius = 1.2D;
      for (int i = 0; i < 5; i++) {
        world.addParticle(ParticleTypes.END_ROD, 
            this.getPosX() + (world.rand.nextDouble() - 0.5D) * radius, 
            this.getPosYEye() + (world.rand.nextDouble() - 0.5D) * radius * 0.75D, 
            this.getPosZ() + (world.rand.nextDouble() - 0.5D) * radius,
            (world.rand.nextDouble() - 0.5D) * motion, 
            (world.rand.nextDouble() - 0.5D) * motion * 0.5D,
            (world.rand.nextDouble() - 0.5D) * motion);
      }
      // get list of all nearby players who have been petrified
      final List<PlayerEntity> list = this.getEntityWorld().getEntitiesWithinAABB(PlayerEntity.class, this.getBoundingBox().grow(16.0D, 16.0D, 16.0D), 
        e -> e.getActivePotionEffect(GFRegistry.PETRIFIED_EFFECT) != null);
      for(final PlayerEntity p : list) {
        world.addParticle(GFRegistry.GORGON_PARTICLE, true, p.getPosX(), p.getPosY(), p.getPosZ(), 0D, 0D, 0D);
      }
    }
  }
  
  // Sounds //
  
  @Override
  protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_CAT_HISS; }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSourceIn) { return SoundEvents.ENTITY_SPIDER_AMBIENT; }

  @Override
  protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_SPIDER_DEATH; }

  @Override
  protected float getSoundVolume() { return 0.8F; }
  
  // Ranged Attack //

  @Override
  public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
    ItemStack itemstack = this.findAmmo(this.getHeldItem(ProjectileHelper.getHandWith(this, Items.BOW)));
    AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, itemstack, distanceFactor);
    if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.BowItem)
      arrow = ((net.minecraft.item.BowItem) this.getHeldItemMainhand().getItem()).customArrow(arrow);
    // this is copied from LlamaSpit code, it moves the arrow nearer to the centaur's human-body
    arrow.setPosition(this.getPosX() - (this.getWidth() + 1.0F) * 0.5D * MathHelper.sin(this.renderYawOffset * 0.017453292F),
        this.getPosYEye() - 0.1D,
        this.getPosZ() + (this.getWidth() + 1.0F) * 0.5D * MathHelper.cos(this.renderYawOffset * 0.017453292F));
    double dx = target.getPosX() - arrow.getPosX();
    double dy = target.getPosYHeight(0.67D) - arrow.getPosY();
    double dz = target.getPosZ() - arrow.getPosZ();
    double dis = (double) MathHelper.sqrt(dx * dx + dz * dz);
    arrow.shoot(dx, dy + dis * (double) 0.2F, dz, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
    this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
    this.world.addEntity(arrow);
  }
  
  // Stare Attack //
  
  public boolean isPlayerStaring(final PlayerEntity player) {
    Vector3d vector3d = player.getLook(1.0F).normalize();
    Vector3d vector3d1 = new Vector3d(this.getPosX() - player.getPosX(), this.getPosYEye() - player.getPosYEye(),
        this.getPosZ() - player.getPosZ());
    double d0 = vector3d1.length();
    vector3d1 = vector3d1.normalize();
    double d1 = vector3d.dotProduct(vector3d1);
    return d1 > 1.0D - 0.025D / d0 ? player.canEntityBeSeen(this) : false;
  }
  
  public boolean isImmuneToStareAttack(final LivingEntity target) {
    // check for mirror potion effect
    if((GreekFantasy.CONFIG.isMirrorPotionEnabled() && target.getActivePotionEffect(GFRegistry.MIRROR_EFFECT) != null) 
        || target.isSpectator() || !target.isNonBoss() || (target instanceof PlayerEntity && ((PlayerEntity)target).isCreative())) {
      return true;
    }
    // check for mirror enchantment
    if(GreekFantasy.CONFIG.isMirrorEnabled() && EnchantmentHelper.getEnchantments(target.getHeldItem(Hand.OFF_HAND)).containsKey(GFRegistry.MIRROR_ENCHANTMENT)) {
      return true;
    }
    return false;
  }
  
  public boolean useStareAttack(final LivingEntity target) {
    // apply potion effect
    if(GreekFantasy.CONFIG.isParalysisNerf()) {
      target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, PETRIFY_DURATION, 1, false, false, true));
      target.addPotionEffect(new EffectInstance(Effects.WEAKNESS, PETRIFY_DURATION, 1, false, false, true));
    } else {
      target.addPotionEffect(new EffectInstance(GFRegistry.PETRIFIED_EFFECT, PETRIFY_DURATION, 0, false, false, true));
    }
    // apply medusa effect
    if(this.isMedusa()) {
      target.addPotionEffect(new EffectInstance(Effects.WITHER, PETRIFY_DURATION, 0));
    }
    // update client-state
    if(this.isServerWorld()) {
      this.world.setEntityState(this, STARE_ATTACK);
    }
    return false;
  }
  
  public static boolean isMirrorShield(final ItemStack stack) {
    return EnchantmentHelper.getEnchantments(stack).containsKey(GFRegistry.MIRROR_ENCHANTMENT);
  }
  
  // States //
  
  public void setMedusa(final boolean medusa) { 
    this.getDataManager().set(MEDUSA, medusa);
    updateCombatGoal(medusa);
    if(medusa) {
      this.setCustomName(new TranslationTextComponent(this.getType().getTranslationKey().concat(".medusa")));
    }
  }
  
  public boolean isMedusa() { return this.getDataManager().get(MEDUSA); }
  
  @Override
  public void notifyDataManagerChange(final DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    if(key == MEDUSA) {
      // update attributes and boss bar visibility
      if(isMedusa()) {
        // medusa attributes
        final double medusaHealth = 84.0D;
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(medusaHealth);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.30D);
        this.setHealth((float)medusaHealth);
        updateCombatGoal(true);
        if(GreekFantasy.CONFIG.showMedusaBossBar()) {
          this.bossInfo.setVisible(true);
        }
      } else {
        // non-medusa
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0D);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.26D);
        this.bossInfo.setVisible(false);
        updateCombatGoal(false);
      }
    }
  }
  
  public void updateCombatGoal(final boolean medusa) {
    if(this.isServerWorld()) {
      if(medusa) {
        // add bow and goal
        this.goalSelector.addGoal(3, rangedAttackGoal);
        if(!(this.getHeldItemMainhand().getItem() instanceof BowItem)) {
          this.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.BOW));
        }
      } else {
        // remove bow and goal
        this.goalSelector.removeGoal(rangedAttackGoal);
        if(this.getHeldItemMainhand().getItem() instanceof BowItem) {
          this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
        }
      }
    }
  }
  
  // Boss //
  
  @Override
  public boolean isNonBoss() { return !isMedusa(); }
  
  @Override
  public ResourceLocation getLootTable() {
    return isMedusa() ? MEDUSA_LOOT : super.getLootTable();
  }

  @Override
  public void addTrackingPlayer(ServerPlayerEntity player) {
    super.addTrackingPlayer(player);
    this.bossInfo.addPlayer(player);
    this.bossInfo.setName(this.hasCustomName() ? this.getCustomName() : this.getDisplayName());
    this.bossInfo.setVisible(this.isMedusa() && GreekFantasy.CONFIG.showMedusaBossBar());
  }

  @Override
  public void removeTrackingPlayer(ServerPlayerEntity player) {
    super.removeTrackingPlayer(player);
    this.bossInfo.removePlayer(player);
  }

  // NBT methods //

  @Override
  public void writeAdditional(CompoundNBT compound) {
    super.writeAdditional(compound);
    compound.putBoolean(KEY_MEDUSA, isMedusa());
  }

  @Override
  public void readAdditional(CompoundNBT compound) {
    super.readAdditional(compound);
    setMedusa(compound.getBoolean(KEY_MEDUSA));
    updateCombatGoal(isMedusa());
  }

  // Goals //
  
  public static class StareAttackGoal extends Goal {
    private final GorgonEntity entity;
    private final int maxCooldown;
    private int cooldown;
    private List<PlayerEntity> trackedPlayers = new ArrayList<>();
    
    public StareAttackGoal(final GorgonEntity entityIn, final int cooldown) {
       this.setMutexFlags(EnumSet.of(Goal.Flag.LOOK));
       this.entity = entityIn;
       this.maxCooldown = cooldown;
       this.cooldown = cooldown / 4;
    }

    @Override
    public boolean shouldExecute() {
      if(this.cooldown > 0) {
        cooldown--;
      } else {
        this.trackedPlayers = this.entity.getEntityWorld().getEntitiesWithinAABB(PlayerEntity.class, this.entity.getBoundingBox().grow(16.0D, 16.0D, 16.0D), 
            e -> this.entity.canAttack(e)&& !this.entity.isImmuneToStareAttack(e) && this.entity.isPlayerStaring((PlayerEntity)e));
        return !this.trackedPlayers.isEmpty();
      }
      return false;
    }

    @Override
    public void startExecuting() {
      if(!trackedPlayers.isEmpty() && trackedPlayers.get(0) != null && cooldown <= 0) {
        this.entity.getNavigator().clearPath();
        this.entity.getLookController().setLookPositionWithEntity(trackedPlayers.get(0), 100.0F, 100.0F);
        trackedPlayers.forEach(e -> this.entity.useStareAttack(e));
        trackedPlayers.clear();
        this.cooldown = maxCooldown;
      }
    }
    
    @Override
    public boolean shouldContinueExecuting() {
      return false;
    }
    
    @Override
    public void resetTask() {
      this.cooldown = maxCooldown;
    }
  }
  
  class RangedAttackGoal extends net.minecraft.entity.ai.goal.RangedAttackGoal {
    public RangedAttackGoal(IRangedAttackMob entity, double moveSpeed, int attackInterval, float attackDistance) {
      super(entity, moveSpeed, attackInterval, attackDistance);
    }
    
    @Override
    public boolean shouldExecute() {
      return (super.shouldExecute() && GorgonEntity.this.getDistanceSq(GorgonEntity.this.getAttackTarget()) > 16.0D
         && GorgonEntity.this.getHeldItemMainhand().getItem() instanceof BowItem);
    }
    
    @Override
    public void startExecuting() {
      super.startExecuting();
      GorgonEntity.this.setAggroed(true);
    }
    
    @Override
    public void resetTask() {
      super.resetTask();
      GorgonEntity.this.setAggroed(false);
    }
  }
  
}
