package greekfantasy.entity;

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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class GorgonEntity extends MonsterEntity implements IRangedAttackMob {

    private static final DataParameter<Boolean> MEDUSA = EntityDataManager.defineId(GorgonEntity.class, DataSerializers.BOOLEAN);
    private static final String KEY_MEDUSA = "Medusa";

    protected static final byte STARE_ATTACK = 9;
    protected static final int PETRIFY_DURATION = 80;

    private static final ResourceLocation MEDUSA_LOOT = new ResourceLocation(GreekFantasy.MODID, "entities/medusa");

    private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS);

    private final GorgonEntity.RangedAttackGoal rangedAttackGoal = new RangedAttackGoal(this, 1.0D, 45, 15.0F);

    public GorgonEntity(final EntityType<? extends GorgonEntity> type, final World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(MEDUSA, Boolean.valueOf(false));
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
        if (GreekFantasy.CONFIG.GORGON_ATTACK.get()) {
            this.goalSelector.addGoal(2, new StareAttackGoal(this, PETRIFY_DURATION + 20));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // boss info
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void thunderHit(ServerWorld world, LightningBoltEntity bolt) { // onEntityStruckByLightning
        if (world.getDifficulty() != Difficulty.PEACEFUL && random.nextInt(100) < GreekFantasy.CONFIG.getLightningMedusaChance()) {
            this.setMedusa(true);
            this.setHealth(this.getMaxHealth());
            this.setPersistenceRequired();
            this.setSecondsOnFire(2);
        } else {
            super.thunderHit(world, bolt);
        }
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        // immune to damage from other gorgons
        if (source.getEntity() != null && source.getEntity().getType() == GFRegistry.EntityReg.GORGON_ENTITY) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
                                           @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (this.getRandom().nextDouble() * 100.0D < GreekFantasy.CONFIG.getGorgonMedusaChance()) {
            this.setMedusa(true);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        switch (id) {
            case STARE_ATTACK:
                spawnStareParticles();
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    public void spawnStareParticles() {
        if (level.isClientSide()) {
            final double motion = 0.08D;
            final double radius = 1.2D;
            for (int i = 0; i < 5; i++) {
                level.addParticle(ParticleTypes.END_ROD,
                        this.getX() + (level.random.nextDouble() - 0.5D) * radius,
                        this.getEyeY() + (level.random.nextDouble() - 0.5D) * radius * 0.75D,
                        this.getZ() + (level.random.nextDouble() - 0.5D) * radius,
                        (level.random.nextDouble() - 0.5D) * motion,
                        (level.random.nextDouble() - 0.5D) * motion * 0.5D,
                        (level.random.nextDouble() - 0.5D) * motion);
            }
            // get list of all nearby players who have been petrified
            final List<PlayerEntity> list = this.getCommandSenderWorld().getEntitiesOfClass(PlayerEntity.class, this.getBoundingBox().inflate(16.0D, 16.0D, 16.0D),
                    e -> e.getEffect(GFRegistry.MobEffectReg.PETRIFIED_EFFECT) != null);
            for (final PlayerEntity p : list) {
                level.addParticle(GFRegistry.ParticleReg.GORGON_PARTICLE, true, p.getX(), p.getY(), p.getZ(), 0D, 0D, 0D);
            }
        }
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CAT_HISS;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    // Ranged Attack //

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW)));
        AbstractArrowEntity arrow = ProjectileHelper.getMobArrow(this, itemstack, distanceFactor);
        if (this.getMainHandItem().getItem() instanceof net.minecraft.item.BowItem)
            arrow = ((net.minecraft.item.BowItem) this.getMainHandItem().getItem()).customArrow(arrow);
        // this is copied from LlamaSpit code, it moves the arrow nearer to the centaur's human-body
        arrow.setPos(this.getX() - (this.getBbWidth() + 1.0F) * 0.5D * MathHelper.sin(this.yBodyRot * 0.017453292F),
                this.getEyeY() - 0.1D,
                this.getZ() + (this.getBbWidth() + 1.0F) * 0.5D * MathHelper.cos(this.yBodyRot * 0.017453292F));
        double dx = target.getX() - arrow.getX();
        double dy = target.getY(0.67D) - arrow.getY();
        double dz = target.getZ() - arrow.getZ();
        double dis = MathHelper.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dis * (double) 0.2F, dz, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(arrow);
    }

    // Stare Attack //

    public boolean isPlayerStaring(final PlayerEntity player) {
        Vector3d vector3d = player.getViewVector(1.0F).normalize();
        Vector3d vector3d1 = new Vector3d(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(),
                this.getZ() - player.getZ());
        double d0 = vector3d1.length();
        vector3d1 = vector3d1.normalize();
        double d1 = vector3d.dot(vector3d1);
        return d1 > 1.0D - 0.025D / d0 && player.canSee(this);
    }

    /**
     * @param target the entity that may or may not be immune to stare attack
     * @return true if the target entity cannot be affected by stare attack
     * (the player has an enchanted mirror shield of the mirror potion effect)
     */
    public boolean isImmuneToStareAttack(final LivingEntity target) {
        // check for mirror potion effect
        if ((GreekFantasy.CONFIG.isMirrorPotionEnabled() && target.getEffect(GFRegistry.MobEffectReg.MIRROR_EFFECT) != null)
                || target.isSpectator() || !target.canChangeDimensions() || (target instanceof PlayerEntity && ((PlayerEntity) target).isCreative())) {
            return true;
        }
        // check for mirror enchantment
        return GreekFantasy.CONFIG.isMirrorEnabled() && EnchantmentHelper.getEnchantments(target.getItemInHand(Hand.OFF_HAND)).containsKey(GFRegistry.EnchantmentReg.MIRROR_ENCHANTMENT);
    }

    /**
     * Applies petrify / slowness / weakness / wither effects depending on config settings
     *
     * @param target the entity to which the effects will apply
     */
    protected void useStareAttack(final LivingEntity target) {
        // apply potion effect
        if (GreekFantasy.CONFIG.isParalysisNerf()) {
            target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, PETRIFY_DURATION, 1, false, false, true));
            target.addEffect(new EffectInstance(Effects.WEAKNESS, PETRIFY_DURATION, 1, false, false, true));
        } else {
            target.addEffect(new EffectInstance(GFRegistry.MobEffectReg.PETRIFIED_EFFECT, PETRIFY_DURATION, 0, false, false, true));
        }
        // apply medusa effect
        if (this.isMedusa()) {
            target.addEffect(new EffectInstance(Effects.WITHER, PETRIFY_DURATION, 0));
        }
        // update client-state
        if (this.isEffectiveAi()) {
            this.level.broadcastEntityEvent(this, STARE_ATTACK);
        }
    }

    public static boolean isMirrorShield(final ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack).containsKey(GFRegistry.EnchantmentReg.MIRROR_ENCHANTMENT);
    }

    // States //

    public void setMedusa(final boolean medusa) {
        this.getEntityData().set(MEDUSA, medusa);
        updateCombatGoal(medusa);
        if (medusa) {
            this.setCustomName(new TranslationTextComponent(this.getType().getDescriptionId().concat(".medusa")));
        }
    }

    public boolean isMedusa() {
        return this.getEntityData().get(MEDUSA);
    }

    @Override
    public void onSyncedDataUpdated(final DataParameter<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == MEDUSA) {
            // update attributes and boss bar visibility
            if (isMedusa()) {
                // medusa attributes
                final double medusaHealth = 84.0D;
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(medusaHealth);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.30D);
                this.setHealth((float) medusaHealth);
                updateCombatGoal(true);
                if (GreekFantasy.CONFIG.showMedusaBossBar()) {
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
        if (this.isEffectiveAi()) {
            if (medusa) {
                // add bow and goal
                this.goalSelector.addGoal(3, rangedAttackGoal);
                if (!(this.getMainHandItem().getItem() instanceof BowItem)) {
                    this.setItemInHand(Hand.MAIN_HAND, new ItemStack(Items.BOW));
                }
            } else {
                // remove bow and goal
                this.goalSelector.removeGoal(rangedAttackGoal);
                if (this.getMainHandItem().getItem() instanceof BowItem) {
                    this.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                }
            }
        }
    }

    // Boss //

    @Override
    public boolean canChangeDimensions() {
        return !isMedusa();
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return isMedusa() ? MEDUSA_LOOT : super.getDefaultLootTable();
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        this.bossInfo.setName(this.hasCustomName() ? this.getCustomName() : this.getDisplayName());
        this.bossInfo.setVisible(this.isMedusa() && GreekFantasy.CONFIG.showMedusaBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // NBT methods //

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(KEY_MEDUSA, isMedusa());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
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
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
            this.entity = entityIn;
            this.maxCooldown = cooldown;
            this.cooldown = cooldown / 4;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                cooldown--;
            } else {
                this.trackedPlayers = this.entity.getCommandSenderWorld().getEntitiesOfClass(PlayerEntity.class, this.entity.getBoundingBox().inflate(16.0D, 16.0D, 16.0D),
                        e -> this.entity.canAttack(e) && !this.entity.isImmuneToStareAttack(e) && this.entity.isPlayerStaring(e));
                return !this.trackedPlayers.isEmpty();
            }
            return false;
        }

        @Override
        public void start() {
            if (!trackedPlayers.isEmpty() && trackedPlayers.get(0) != null && cooldown <= 0) {
                this.entity.getNavigation().stop();
                this.entity.getLookControl().setLookAt(trackedPlayers.get(0), 100.0F, 100.0F);
                trackedPlayers.forEach(e -> this.entity.useStareAttack(e));
                trackedPlayers.clear();
                this.cooldown = maxCooldown;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void stop() {
            this.cooldown = maxCooldown;
        }
    }

    class RangedAttackGoal extends net.minecraft.entity.ai.goal.RangedAttackGoal {
        public RangedAttackGoal(IRangedAttackMob entity, double moveSpeed, int attackInterval, float attackDistance) {
            super(entity, moveSpeed, attackInterval, attackDistance);
        }

        @Override
        public boolean canUse() {
            return (super.canUse() && GorgonEntity.this.distanceToSqr(GorgonEntity.this.getTarget()) > 16.0D
                    && GorgonEntity.this.getMainHandItem().getItem() instanceof BowItem);
        }

        @Override
        public void start() {
            super.start();
            GorgonEntity.this.setAggressive(true);
        }

        @Override
        public void stop() {
            super.stop();
            GorgonEntity.this.setAggressive(false);
        }
    }

}
