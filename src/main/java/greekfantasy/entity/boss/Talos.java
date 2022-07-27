package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.item.ClubItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class Talos extends Monster implements RangedAttackMob {

    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Talos.class, EntityDataSerializers.BYTE);
    private static final String KEY_STATE = "TalosState";
    private static final String KEY_SPAWN = "SpawnTime";
    private static final String KEY_SHOOT = "ShootTime";
    private static final String KEY_COOLDOWN = "AttackCooldown";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte SHOOT = (byte) 2;
    // bytes to use in Level#broadcastEntityEvent
    private static final byte SPAWN_EVENT = 8;
    private static final byte SHOOT_EVENT = 9;

    private static final int MAX_SPAWN_TIME = 94;
    private static final int MAX_SHOOT_TIME = 80;
    private static final int SHOOT_COOLDOWN = 188;
    private static final int MELEE_COOLDOWN = 40;

    private final ServerBossEvent bossInfo = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);

    private int spawnTime0;
    private int spawnTime;
    private int shootTime;
    private float shootAngle0;
    private float shootAngle;
    private int attackCooldown;

    public Talos(final EntityType<? extends Talos> type, final Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.75D)
                .add(Attributes.ARMOR, 20.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6F);
    }

    public static Talos spawnTalos(final Level level, final BlockPos pos, final float yaw) {
        Talos entity = GFRegistry.EntityReg.TALOS.get().create(level);
        entity.moveTo(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
        entity.yBodyRot = yaw;
        level.addFreshEntity(entity);
        entity.setSpawning(true);
        // trigger spawn for nearby players
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(25.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
        }
        // play sound
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WITHER_SPAWN, entity.getSoundSource(), 1.2F, 1.0F, false);
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
        this.goalSelector.addGoal(0, new Talos.SpawningGoal());
        this.goalSelector.addGoal(1, new Talos.TalosRangedAttackGoal(4, 25.0F));
        this.goalSelector.addGoal(3, new Talos.TalosMeleeAttackGoal(1.0D, false));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // attack cooldown
        attackCooldown = Math.max(attackCooldown - 1, 0);

        // update spawn time
        spawnTime0 = spawnTime;
        if (isSpawning() || spawnTime > 0) {
            // update timer
            if (--spawnTime <= 0) {
                setSpawning(false);
            }
        }

        // update shoot time
        shootAngle0 = shootAngle;
        if (isShooting() || shootTime > 0) {
            // update angle
            shootAngle = Math.min(1.0F, shootAngle + 0.08F);
            // update timer
            if (shootTime++ >= MAX_SHOOT_TIME) {
                setShooting(false);
                shootTime = 0;
            }
        } else {
            shootAngle = Math.max(0.0F, shootAngle - 0.08F);
        }
    }

    @Override
    public void tick() {
        super.tick();

        // spawn particles
        if (this.level.isClientSide() && this.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F && this.random.nextInt(3) == 0) {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY() - (double) 0.2F);
            int k = Mth.floor(this.getZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = this.level.getBlockState(pos);
            if (!this.level.isEmptyBlock(pos)) {
                final BlockParticleOption data = new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos);
                final double radius = this.getBbWidth();
                final double motion = 4.0D;
                this.level.addParticle(data,
                        this.getX() + (this.random.nextDouble() - 0.5D) * radius * 2,
                        this.getY() + 0.1D,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * radius * 2,
                        motion * (this.random.nextDouble() - 0.5D), 0.45D, (this.random.nextDouble() - 0.5D) * motion);
            }
        }
    }

    @Override
    public boolean doHurtTarget(final Entity entityIn) {
        if (super.doHurtTarget(entityIn)) {
            // apply extra knockback velocity when attacking (ignores knockback resistance)
            final double knockbackFactor = 0.92D;
            final Vec3 myPos = this.position();
            final Vec3 ePos = entityIn.position();
            final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
            final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
            entityIn.push(dX, knockbackFactor / 2.0D, dZ);
            entityIn.hurtMarked = true;
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
        return 1.2F * super.getJumpPower();
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
        compound.putInt(KEY_SHOOT, shootTime);
        compound.putInt(KEY_COOLDOWN, attackCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setState(compound.getByte(KEY_STATE));
        spawnTime = compound.getInt(KEY_SPAWN);
        shootTime = compound.getInt(KEY_SHOOT);
        attackCooldown = compound.getInt(KEY_COOLDOWN);
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

    public void setShooting(final boolean shoot) {
        setState(shoot ? SHOOT : NONE);
    }

    public boolean isShooting() {
        return getState() == SHOOT;
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
            case SHOOT_EVENT:
                shootTime = 1;
                this.playSound(SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, 1.1F, 1.0F);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    public float getSpawnPercent(final float partialTick) {
        return 1.0F - Mth.lerp(partialTick, spawnTime0, spawnTime) / (float) MAX_SPAWN_TIME;
    }

    public float getShootAnglePercent(final float partialTick) {
        return Mth.lerp(partialTick, shootAngle0, shootAngle);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (this.level.isClientSide() || !this.isShooting() || shootTime < (MAX_SHOOT_TIME / 4)) {
            return;
        }
        ItemStack itemstack = new ItemStack(Items.ARROW);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, itemstack, distanceFactor);
        // this is adapted from LlamaSpit code, it moves the arrow nearer to right-front of the body
        // arrow.setPosition(this.getPosX() - (this.getWidth() + 1.0F) * 0.5D * Mth.sin(this.renderYawOffset * 0.017453292F), this.getPosYEye() - 0.10000000149011612D, this.getPosZ() + (this.getWidth() + 1.0F) * 0.5D * Mth.cos(this.renderYawOffset * 0.017453292F));
        arrow.setPos(this.getX() - (this.getBbWidth()) * 0.85D * Mth.sin(this.yBodyRot * 0.017453292F + 1.0F), this.getEyeY() - 0.74D, this.getZ() + (this.getBbWidth()) * 0.85D * Mth.cos(this.yBodyRot * 0.017453292F + 1.0F));
        double dx = target.getX() - arrow.getX();
        double dy = target.getY(0.67D) - arrow.getY();
        double dz = target.getZ() - arrow.getZ();
        double dis = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dis * (double) 0.2F, dz, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        arrow.setBaseDamage(1.0D + this.level.getDifficulty().getId() * 0.25D);
        arrow.setOwner(this);
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(arrow);
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

    // Custom goals

    class SpawningGoal extends Goal {

        public SpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return Talos.this.isSpawning();
        }

        @Override
        public void tick() {
            Talos.this.getNavigation().stop();
            Talos.this.getLookControl().setLookAt(Talos.this.getX(), Talos.this.getY(), Talos.this.getZ());
            Talos.this.setRot(0, 0);
            Talos.this.setTarget(null);
        }
    }

    class TalosRangedAttackGoal extends RangedAttackGoal {

        public TalosRangedAttackGoal(int interval, float attackDistance) {
            super(Talos.this, 1.0F, interval, attackDistance);
        }

        @Override
        public boolean canUse() {
            final LivingEntity target = Talos.this.getTarget();
            double disSq = (target != null) ? Talos.this.distanceToSqr(target) : 0.0D;
            return Talos.this.isNoneState() && Talos.this.hasNoCooldown() && disSq > 9.0D && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            Talos.this.setAttackCooldown(SHOOT_COOLDOWN);
            Talos.this.setShooting(true);
            Talos.this.level.broadcastEntityEvent(Talos.this, SHOOT_EVENT);
            Talos.this.shootTime = 1;
        }

        @Override
        public boolean canContinueToUse() {
            return Talos.this.shootTime > 0 && Talos.this.isShooting();
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Override
        public void stop() {
            Talos.this.setState(NONE);
            Talos.this.shootTime = 0;
            super.stop();
        }
    }

    class TalosMeleeAttackGoal extends MeleeAttackGoal {

        public TalosMeleeAttackGoal(double speedIn, boolean useLongMemory) {
            super(Talos.this, speedIn, useLongMemory);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (Talos.this.hasNoCooldown()) {
                super.checkAndPerformAttack(enemy, distToEnemySqr);
            }
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            Talos.this.setAttackCooldown(MELEE_COOLDOWN);
        }
    }
}
