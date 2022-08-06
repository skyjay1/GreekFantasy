package greekfantasy.entity.monster;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.util.GFMobType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Gorgon extends Monster implements RangedAttackMob {

    private static final EntityDataAccessor<Boolean> MEDUSA = SynchedEntityData.defineId(Gorgon.class, EntityDataSerializers.BOOLEAN);
    private static final String KEY_MEDUSA = "Medusa";

    protected static final byte STARE_ATTACK = 9;

    protected static final int PETRIFY_DURATION = 80;

    private static final ResourceLocation MEDUSA_LOOT = new ResourceLocation(GreekFantasy.MODID, "entities/medusa");

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.PROGRESS);

    private final RangedAttackGoal rangedAttackGoal = new RangedAttackGoal(this, 1.0D, 45, 15.0F);

    public Gorgon(final EntityType<? extends Gorgon> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
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
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new StareAttackGoal(this, PETRIFY_DURATION + 20));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void thunderHit(ServerLevel world, LightningBolt bolt) {
        if (world.getDifficulty() != Difficulty.PEACEFUL && random.nextFloat() * 100.0F < GreekFantasy.CONFIG.MEDUSA_LIGHTNING_CHANCE.get()) {
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
        if (source.getEntity() != null && source.getEntity().getType() == this.getType()) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public MobType getMobType() {
        return GFMobType.SERPENT;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (this.getRandom().nextDouble() * 100.0D < GreekFantasy.CONFIG.MEDUSA_SPAWN_CHANCE.get()) {
            this.setMedusa(true);
        }
        return super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
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
            final double distance = this.getAttribute(Attributes.FOLLOW_RANGE).getValue();
            // get list of all nearby players who have been petrified
            final List<Player> list = this.level.getEntitiesOfClass(Player.class,
                    this.getBoundingBox().inflate(distance),
                    e -> e.getEffect(GFRegistry.MobEffectReg.PETRIFIED.get()) != null);
            // spawn gorgon particle for each player
            for (final Player p : list) {
                level.addParticle(GFRegistry.ParticleReg.GORGON.get(), true, p.getX(), p.getY(), p.getZ(), 0D, 0D, 0D);
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
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, i -> i instanceof BowItem)));
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, itemstack, distanceFactor);
        if (this.getMainHandItem().getItem() instanceof BowItem bow) {
            arrow = bow.customArrow(arrow);
        }
        // this is copied from LlamaSpit code, it moves the arrow nearer to the gorgon human-body
        arrow.setPos(this.getX() - (this.getBbWidth() + 1.0F) * 0.5D * Mth.sin(this.yBodyRot * 0.017453292F),
                this.getEyeY() - 0.1D,
                this.getZ() + (this.getBbWidth() + 1.0F) * 0.5D * Mth.cos(this.yBodyRot * 0.017453292F));
        double dx = target.getX() - arrow.getX();
        double dy = target.getY(0.67D) - arrow.getY();
        double dz = target.getZ() - arrow.getZ();
        double dis = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dis * (double) 0.2F, dz, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(arrow);
    }

    // Stare Attack //

    public boolean isPlayerStaring(final Player player) {
        Vec3 playerView = player.getViewVector(1.0F).normalize();
        Vec3 distanceVec = new Vec3(this.getX() - player.getX(), this.getEyeY() - player.getEyeY(),
                this.getZ() - player.getZ());
        double distance = distanceVec.length();
        distanceVec = distanceVec.normalize();
        double dot = playerView.dot(distanceVec);
        return dot > 1.0D - 0.025D / distance && this.getSensing().hasLineOfSight(player);
    }

    /**
     * @param target the entity that may or may not be immune to stare attack
     * @return true if the target entity cannot be affected by stare attack
     * (the player has a mirror, enchanted mirror shield, or mirror potion effect)
     */
    public boolean isImmuneToStareAttack(final LivingEntity target) {
        // check for applicable target
        if (target.isSpectator() || !target.canChangeDimensions() || (target instanceof Player player && player.isCreative())) {
            return true;
        }
        // check for mirror item
        if (target.getMainHandItem().is(GFRegistry.ItemReg.MIRROR.get()) || target.getOffhandItem().is(GFRegistry.ItemReg.MIRROR.get())) {
            return true;
        }
        // check for mirror potion effect
        if ((GreekFantasy.CONFIG.isMirroringEffectEnabled() && target.getEffect(GFRegistry.MobEffectReg.MIRRORING.get()) != null)) {
            return true;
        }
        // check for mirror enchantment
        if(GreekFantasy.CONFIG.isMirroringEnchantmentEnabled() &&
                (EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.MIRRORING.get(), target.getMainHandItem()) > 0)
                || EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.MIRRORING.get(), target.getOffhandItem()) > 0) {
            return true;
        }
        // target is not immune
        return false;
    }

    /**
     * Applies petrify / slowness / weakness / wither effects depending on config settings
     *
     * @param target the entity to which the effects will apply
     */
    protected void useStareAttack(final LivingEntity target) {
        // apply potion effect
        if (GreekFantasy.CONFIG.PETRIFIED_NERF.get()) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, PETRIFY_DURATION, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, PETRIFY_DURATION, 1));
        } else {
            target.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.PETRIFIED.get(), PETRIFY_DURATION, 0, false, false, true));
        }
        // medusa applies additional effect
        if (this.isMedusa()) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, PETRIFY_DURATION * 3 / 2, 0));
        }
        // update client-state
        if (this.isEffectiveAi()) {
            this.level.broadcastEntityEvent(this, STARE_ATTACK);
        }
    }

    // States //

    public void setMedusa(final boolean medusa) {
        this.getEntityData().set(MEDUSA, medusa);
        updateCombatGoal(medusa);
        if (medusa) {
            this.setCustomName(Component.translatable(this.getType().getDescriptionId().concat(".medusa")));
        }
    }

    public boolean isMedusa() {
        return this.getEntityData().get(MEDUSA);
    }

    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
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
                this.bossInfo.setVisible(GreekFantasy.CONFIG.showMedusaBossBar());
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
                    this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
                }
            } else {
                // remove bow and goal
                this.goalSelector.removeGoal(rangedAttackGoal);
                if (this.getMainHandItem().getItem() instanceof BowItem) {
                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
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
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        this.bossInfo.setName(this.hasCustomName() ? this.getCustomName() : this.getDisplayName());
        this.bossInfo.setVisible(this.isMedusa() && GreekFantasy.CONFIG.showMedusaBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // NBT methods //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(KEY_MEDUSA, isMedusa());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setMedusa(compound.getBoolean(KEY_MEDUSA));
        updateCombatGoal(isMedusa());
    }

    // Goals //

    class StareAttackGoal extends Goal {
        private final int maxCooldown;
        private int cooldown;
        private List<Player> trackedPlayers = new ArrayList<>();

        public StareAttackGoal(final Gorgon entityIn, final int cooldown) {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
            this.maxCooldown = cooldown;
            this.cooldown = cooldown / 4;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                cooldown--;
            } else {
                double range = Gorgon.this.getAttribute(Attributes.FOLLOW_RANGE).getValue();
                this.trackedPlayers = Gorgon.this.level.getEntitiesOfClass(Player.class, Gorgon.this.getBoundingBox().inflate(range),
                        e -> Gorgon.this.canAttack(e) && !Gorgon.this.isImmuneToStareAttack(e) && Gorgon.this.isPlayerStaring(e));
                return !this.trackedPlayers.isEmpty();
            }
            return false;
        }

        @Override
        public void start() {
            if (!trackedPlayers.isEmpty() && trackedPlayers.get(0) != null && cooldown <= 0) {
                Gorgon.this.getNavigation().stop();
                Gorgon.this.getLookControl().setLookAt(trackedPlayers.get(0), 100.0F, 100.0F);
                trackedPlayers.forEach(e -> Gorgon.this.useStareAttack(e));
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

    class RangedAttackGoal extends net.minecraft.world.entity.ai.goal.RangedAttackGoal {
        public RangedAttackGoal(RangedAttackMob entity, double moveSpeed, int attackInterval, float attackDistance) {
            super(entity, moveSpeed, attackInterval, attackDistance);
        }

        @Override
        public boolean canUse() {
            return (super.canUse() && Gorgon.this.getTarget() != null
                    && Gorgon.this.distanceToSqr(Gorgon.this.getTarget()) > 16.0D
                    && Gorgon.this.getMainHandItem().getItem() instanceof BowItem);
        }

        @Override
        public void start() {
            super.start();
            Gorgon.this.setAggressive(true);
        }

        @Override
        public void stop() {
            super.stop();
            Gorgon.this.setAggressive(false);
        }
    }

}
