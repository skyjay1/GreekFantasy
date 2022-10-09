package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Naiad;
import greekfantasy.entity.Triton;
import greekfantasy.entity.ai.CooldownMeleeAttackGoal;
import greekfantasy.entity.ai.IntervalRangedAttackGoal;
import greekfantasy.entity.misc.WaterSpell;
import greekfantasy.entity.util.HasCustomCooldown;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class Scylla extends WaterAnimal implements Enemy, RangedAttackMob, HasCustomCooldown {

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    protected static final int MELEE_COOLDOWN = 50;
    protected int meleeCooldown;

    public Scylla(EntityType<? extends Scylla> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 40;
    }

    public static Scylla spawnScylla(final ServerLevel world, final Naiad naiad) {
        Scylla entity = GFRegistry.EntityReg.SCYLLA.get().create(world);
        entity.moveTo(naiad.getX(), naiad.getY(), naiad.getZ(), naiad.getYRot(), naiad.getXRot());
        if (naiad.hasCustomName()) {
            entity.setCustomName(naiad.getCustomName());
            entity.setCustomNameVisible(naiad.isCustomNameVisible());
        }
        entity.setPersistenceRequired();
        entity.yBodyRot = naiad.yBodyRot;
        entity.setPortalCooldown();
        world.addFreshEntityWithPassengers(entity);
        entity.finalizeSpawn(world, world.getCurrentDifficultyAt(naiad.blockPosition()), MobSpawnType.CONVERSION, null, null);
        // remove the old naiad
        naiad.discard();
        // trigger spawn for nearby players
        for (ServerPlayer player : world.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(16.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
        }
        // play sound
        entity.playSound(SoundEvents.GHAST_SCREAM, 1.2F, 1.0F);
        return entity;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 140.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.08D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 8.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new IntervalRangedAttackGoal<>(this, 30, 3, 110, 18.0F));
        this.goalSelector.addGoal(3, new CooldownMeleeAttackGoal<>(this, 1.0D, false, MELEE_COOLDOWN));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Naiad.class, true, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Triton.class, true, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Dolphin.class, false, false));
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        return type != GFRegistry.EntityReg.CHARYBDIS.get() && super.canAttackType(type);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.WITHER
                || source.getEntity() instanceof Charybdis || super.isInvulnerableTo(source);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public MobCategory getClassification(boolean forSpawnCount) {
        return MobCategory.MONSTER;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(final double disToPlayer) {
        return false;
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return false;
    }

    @Override
    protected void handleAirSupply(int air) {
    }

    @Override
    public double getFluidJumpThreshold() {
        return 1.0D;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.95F;
    }

    // Boss //

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        this.bossInfo.setName(this.hasCustomName() ? this.getCustomName() : this.getDisplayName());
        this.bossInfo.setVisible(GreekFantasy.CONFIG.showScyllaBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ELDER_GUARDIAN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ELDER_GUARDIAN_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.4F;
    }

    @Override
    public float getVoicePitch() {
        return 1.2F + random.nextFloat() * 0.2F;
    }

    // Ranged Attack

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!level.isClientSide()) {
            WaterSpell waterSpell = WaterSpell.create(level, this);
            double dx = target.getX() - waterSpell.getX();
            double dy = target.getY(0.5D) - waterSpell.getY();
            double dz = target.getZ() - waterSpell.getZ();
            double dis = Math.sqrt(dx * dx + dz * dz);
            waterSpell.shoot(dx, dy + dis * waterSpell.getGravity() * 4.0D, dz, 1.6F, 1.0F);
            this.level.addFreshEntity(waterSpell);
        }
        this.playSound(SoundEvents.LLAMA_SPIT, 1.2F, 1.2F + this.random.nextFloat() * 0.2F);
    }

    // Custom Cooldown

    @Override
    public void setCustomCooldown(int cooldown) {
        this.meleeCooldown = cooldown;
    }

    @Override
    public int getCustomCooldown() {
        return this.meleeCooldown;
    }

    // NBT //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        saveCustomCooldown(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        readCustomCooldown(compound);
    }
}
