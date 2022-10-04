package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.ShootFireGoal;
import greekfantasy.item.ClubItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class BronzeBull extends Monster {

    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(BronzeBull.class, EntityDataSerializers.BYTE);
    private static final String KEY_STATE = "BullState";
    private static final String KEY_SPAWN = "SpawnTime";
    private static final String KEY_ATTACK_COOLDOWN = "AttackCooldown";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte FIRING = (byte) 2;
    private static final byte GORING = (byte) 3;
    // bytes to use in Level#broadcastEntityEvent
    private static final byte SPAWN_EVENT = 8;
    private static final byte FIRING_EVENT = 9;
    private static final byte GORING_EVENT = 10;

    private static final double FIRE_RANGE = 10.0D;
    private static final int MAX_SPAWN_TIME = 90;
    private static final int MAX_FIRING_TIME = 89;
    private static final int MAX_GORING_TIME = 130;
    private static final int MELEE_COOLDOWN = 50;

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    private int firingTime;
    private int spawnTime;
    private int goringTime0;
    private int goringTime;
    private int attackCooldown;

    public BronzeBull(final EntityType<? extends BronzeBull> type, final Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 230.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.75D)
                .add(Attributes.ARMOR, 12.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6F);
    }

    public static BronzeBull spawnBronzeBull(final Level level, final BlockPos pos, final float yaw) {
        BronzeBull entity = GFRegistry.EntityReg.BRONZE_BULL.get().create(level);
        entity.moveTo(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
        entity.yBodyRot = yaw;
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.addFreshEntityWithPassengers(entity);
            entity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
            // trigger spawn for nearby players
            for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(25.0D))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
            }
        }
        entity.setSpawning(true);
        // play sound
        entity.playSound(SoundEvents.WITHER_SPAWN, 1.2F, 1.0F);
        return entity;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, NONE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BronzeBullSpawningGoal());
        this.goalSelector.addGoal(1, new BronzeBullFireAttackGoal(MAX_FIRING_TIME, 150));
        this.goalSelector.addGoal(3, new BronzeBull.BronzeBullMeleeAttackGoal(1.25D, false));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // update spawn time
        if (isSpawning() || spawnTime > 0) {
            // update timer
            if (--spawnTime <= 0) {
                setSpawning(false);
            }
        }

        // update goring attack
        goringTime0 = goringTime;
        if (isGoring() || goringTime > 0) {
            // update timer
            if (--goringTime <= 0) {
                setGoring(false);
            }
        }

        // update fire attack
        if (isFiring() || firingTime > 0) {
            // update timer
            if (--firingTime <= 0) {
                setFiring(false);
            }
        }

        // update fire attack target
        if (this.isEffectiveAi() && (this.isFiring() || this.isGoring()) && this.getTarget() == null) {
            this.setFiring(false);
            this.setGoring(false);
        }

        // spawn particles
        if (level.isClientSide() && this.isFiring()) {
            spawnFireParticles();
        }

        // spawn particles
        if (this.level.isClientSide()) {
            final double x = this.getX();
            final double y = this.getY() + 1.25D;
            final double z = this.getZ();
            final double motion = 0.06D;
            final double radius = this.getBbWidth() * 1.15D;
            level.addParticle(ParticleTypes.LAVA,
                    x + (level.random.nextDouble() - 0.5D) * radius,
                    y + (level.random.nextDouble() - 0.5D) * radius,
                    z + (level.random.nextDouble() - 0.5D) * radius,
                    (level.random.nextDouble() - 0.5D) * motion,
                    (level.random.nextDouble() - 0.5D) * 0.07D,
                    (level.random.nextDouble() - 0.5D) * motion);
        }
    }

    @Override
    public boolean doHurtTarget(final Entity entity) {
        // set goring
        if (!isGoring()) {
            setGoring(true);
        }
        if (super.doHurtTarget(entity)) {
            // apply extra knockback velocity when attacking (ignores knockback resistance)
            final double knockbackFactor = 0.82D;
            final Vec3 myPos = this.position();
            final Vec3 ePos = entity.position();
            final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
            final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
            entity.push(dX, knockbackFactor / 2.0D, dZ);
            entity.hurtMarked = true;
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.6F + random.nextFloat() * 0.2F);
            return true;
        }
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        final SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnDataIn, dataTag);
        this.setSpawning(true);
        return data;
    }

    @Override
    protected float getJumpPower() {
        return 0.42F * this.getBlockJumpFactor();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void pushEntities() {
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
    public boolean isInvulnerableTo(final DamageSource source) {
        return isSpawning() || source.isMagic() || source == DamageSource.DROWN || source == DamageSource.IN_WALL || source == DamageSource.WITHER
                || source.getDirectEntity() instanceof AbstractArrow || super.isInvulnerableTo(source);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 280;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.8F;
    }

    @Override
    public float getVoicePitch() {
        return 0.6F + random.nextFloat() * 0.25F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getState());
        compound.putInt(KEY_SPAWN, spawnTime);
        compound.putInt(KEY_ATTACK_COOLDOWN, attackCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setState(compound.getByte(KEY_STATE));
        spawnTime = compound.getInt(KEY_SPAWN);
        attackCooldown = compound.getInt(KEY_ATTACK_COOLDOWN);
    }

    public void spawnFireParticles() {
        if (!level.isClientSide()) {
            return;
        }
        Vec3 lookVec = this.getLookAngle();
        Vec3 pos = this.getEyePosition(1.0F).subtract(0.0D, 0.25D, 0.0D);
        final double motion = 0.06D;
        final double radius = 0.75D;

        for (int i = 0; i < 5; i++) {
            level.addParticle(ParticleTypes.FLAME,
                    pos.x + (level.random.nextDouble() - 0.5D) * radius,
                    pos.y + (level.random.nextDouble() - 0.5D) * radius,
                    pos.z + (level.random.nextDouble() - 0.5D) * radius,
                    lookVec.x * motion * FIRE_RANGE,
                    lookVec.y * motion * 0.5D,
                    lookVec.z * motion * FIRE_RANGE);
        }
    }

    public byte getState() {
        return this.getEntityData().get(STATE);
    }

    public void setState(final byte state) {
        this.getEntityData().set(STATE, state);
    }

    public boolean isNoneState() {
        return getState() == NONE;
    }

    public boolean isSpawning() {
        return spawnTime > 0 || getState() == SPAWNING;
    }

    public boolean isFiring() {
        return getState() == FIRING || firingTime > 0;
    }

    public boolean isGoring() {
        return getState() == GORING || goringTime > 0;
    }

    public void setFiring(final boolean firing) {
        firingTime = firing ? MAX_FIRING_TIME : 0;
        setState(firing ? FIRING : NONE);
        if (firing && !this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, FIRING_EVENT);
        }
    }

    public void setGoring(final boolean goring) {
        goringTime = goring ? MAX_GORING_TIME : 0;
        setState(goring ? GORING : NONE);
        if (goring && !this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, GORING_EVENT);
            // break intersecting blocks
            destroyIntersectingBlocks(1.45F + 0.75F * random.nextFloat(), 2.0D);
        }
    }

    public void setSpawning(final boolean spawning) {
        spawnTime = spawning ? MAX_SPAWN_TIME : 0;
        setState(spawning ? SPAWNING : NONE);
        if (spawning && !this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, SPAWN_EVENT);
        }
    }

    public void setAttackCooldown(final int cooldown) {
        attackCooldown = cooldown;
    }

    public boolean hasNoCooldown() {
        return attackCooldown <= 0;
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case SPAWN_EVENT:
                setSpawning(true);
                break;
            case FIRING_EVENT:
                setFiring(true);
                break;
            case GORING_EVENT:
                setGoring(true);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    public float getSpawnPercent(final float partialTick) {
        return Math.max(0, spawnTime - partialTick) / (float) MAX_SPAWN_TIME;
    }

    public float getFiringPercent(final float partialTick) {
        return Math.max(0, firingTime - partialTick) / (float) MAX_FIRING_TIME;
    }

    public float getGoringPercent(final float partialTick) {
        return Mth.lerp(partialTick, goringTime0, goringTime) / (float) MAX_GORING_TIME;
    }

    // Boss Logic

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }


    /**
     * Breaks blocks within this entity's bounding box
     *
     * @param offset the forward distance to offset the bounding box
     **/
    private void destroyIntersectingBlocks(final float maxHardness, final double offset) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
        final Vec3 facing = Vec3.directionFromRotation(this.getRotationVector());
        final AABB box = this.getBoundingBox().move(facing.normalize().scale(offset));
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        BlockState b;
        for (double x = box.minX - 0.25D; x < box.maxX + 0.25D; x++) {
            for (double y = box.minY + 1.1D; y < box.maxY + 0.5D; y++) {
                for (double z = box.minZ - 0.25D; z < box.maxZ + 0.25D; z++) {
                    p.set(x, y, z);
                    b = this.level.getBlockState(p);
                    if ((b.canOcclude() || b.getMaterial().blocksMotion()) && b.getDestroySpeed(level, p) < maxHardness && !b.is(BlockTags.WITHER_IMMUNE)) {
                        this.level.destroyBlock(p, true);
                    }
                }
            }
        }
    }

    // Custom goals
    class BronzeBullMeleeAttackGoal extends MeleeAttackGoal {

        public BronzeBullMeleeAttackGoal(double speedIn, boolean useLongMemory) {
            super(BronzeBull.this, speedIn, useLongMemory);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (BronzeBull.this.hasNoCooldown()) {
                super.checkAndPerformAttack(enemy, distToEnemySqr);
            }
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            BronzeBull.this.setAttackCooldown(MELEE_COOLDOWN);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return super.getAttackReachSqr(attackTarget) - 4.0D;
        }
    }


    class BronzeBullSpawningGoal extends Goal {

        public BronzeBullSpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return BronzeBull.this.isSpawning();
        }

        @Override
        public void tick() {
            BronzeBull.this.getNavigation().stop();
            BronzeBull.this.getLookControl().setLookAt(BronzeBull.this.getX(), BronzeBull.this.getY(), BronzeBull.this.getZ());
            BronzeBull.this.setRot(0, 0);
        }
    }

    class BronzeBullFireAttackGoal extends ShootFireGoal {

        protected BronzeBullFireAttackGoal(final int fireTimeIn, final int maxCooldownIn) {
            super(BronzeBull.this, fireTimeIn, maxCooldownIn, FIRE_RANGE);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && BronzeBull.this.isNoneState();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && BronzeBull.this.isFiring();
        }

        @Override
        public void start() {
            super.start();
            BronzeBull.this.setFiring(true);
        }

        @Override
        public void stop() {
            super.stop();
            BronzeBull.this.setFiring(false);
        }
    }
}
