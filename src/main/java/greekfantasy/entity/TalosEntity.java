package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.item.ClubItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
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
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class TalosEntity extends MonsterEntity implements IRangedAttackMob {

    private static final DataParameter<Byte> STATE = EntityDataManager.defineId(TalosEntity.class, DataSerializers.BYTE);
    private static final String KEY_STATE = "TalosState";
    private static final String KEY_SPAWN = "SpawnTime";
    private static final String KEY_SHOOT = "ShootTime";
    private static final String KEY_COOLDOWN = "AttackCooldown";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte SHOOT = (byte) 2;
    // bytes to use in World#setEntityState
    private static final byte SPAWN_CLIENT = 8;
    private static final byte SHOOT_CLIENT = 9;

    private static final int MAX_SPAWN_TIME = 94;
    private static final int MAX_SHOOT_TIME = 80;
    private static final int SHOOT_COOLDOWN = 188;
    private static final int MELEE_COOLDOWN = 40;

    private final ServerBossInfo bossInfo = (ServerBossInfo) (new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS)).setDarkenScreen(true);

    private int spawnTime;
    private int shootTime;
    private int attackCooldown;

    public TalosEntity(final EntityType<? extends TalosEntity> type, final World worldIn) {
        super(type, worldIn);
        this.maxUpStep = 1.0F;
        this.xpReward = 50;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.21D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.75D)
                .add(Attributes.ARMOR, 8.0D);
    }

    public static TalosEntity spawnTalos(final World world, final BlockPos pos, final float yaw) {
        TalosEntity entity = GFRegistry.EntityReg.TALOS_ENTITY.create(world);
        entity.moveTo(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
        entity.yBodyRot = yaw;
        world.addFreshEntity(entity);
        entity.setSpawning(true);
        // trigger spawn for nearby players
        for (ServerPlayerEntity player : world.getEntitiesOfClass(ServerPlayerEntity.class, entity.getBoundingBox().inflate(25.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
        }
        // play sound
        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WITHER_SPAWN, entity.getSoundSource(), 1.2F, 1.0F, false);
        return entity;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, Byte.valueOf(NONE));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TalosEntity.SpawningGoal());
        this.goalSelector.addGoal(1, new TalosEntity.RangedAttackGoal(4, 25.0F));
        this.goalSelector.addGoal(3, new TalosEntity.MeleeAttackGoal(1.0D, false));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

        // attack cooldown
        attackCooldown = Math.max(attackCooldown - 1, 0);

        // update spawn time
        if (isSpawning() || spawnTime > 0) {
            // update timer
            if (--spawnTime <= 0) {
                setSpawning(false);
            }
        }

        // update shoot time
        if (isShooting() || shootTime > 0) {
            // update timer
            if (shootTime++ >= MAX_SHOOT_TIME) {
                setShooting(false);
                shootTime = 0;
            }
        }

        // spawn particles
        if (getHorizontalDistanceSqr(this.getDeltaMovement()) > (double) 2.5000003E-7F && this.random.nextInt(3) == 0) {
            int i = MathHelper.floor(this.getX());
            int j = MathHelper.floor(this.getY() - (double) 0.2F);
            int k = MathHelper.floor(this.getZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = this.level.getBlockState(pos);
            if (!this.level.isEmptyBlock(pos)) {
                final BlockParticleData data = new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(pos);
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
            final Vector3d myPos = this.position();
            final Vector3d ePos = entityIn.position();
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
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
                || source.getDirectEntity() instanceof AbstractArrowEntity || super.isInvulnerableTo(source);
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
    protected float getVoicePitch() {
        return 0.6F + random.nextFloat() * 0.25F;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getState());
        compound.putInt(KEY_SPAWN, spawnTime);
        compound.putInt(KEY_SHOOT, shootTime);
        compound.putInt(KEY_COOLDOWN, attackCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setState(compound.getByte(KEY_STATE));
        spawnTime = compound.getInt(KEY_SPAWN);
        shootTime = compound.getInt(KEY_SHOOT);
        attackCooldown = compound.getInt(KEY_COOLDOWN);
    }

    public byte getState() {
        return this.getEntityData().get(STATE).byteValue();
    }

    public void setState(final byte state) {
        this.getEntityData().set(STATE, Byte.valueOf(state));
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
            this.level.broadcastEntityEvent(this, SPAWN_CLIENT);
        }
    }

    public void setAttackCooldown(final int cooldown) {
        attackCooldown = cooldown;
    }

    public boolean hasNoCooldown() {
        return attackCooldown <= 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        switch (id) {
            case SPAWN_CLIENT:
                setSpawning(true);
                break;
            case SHOOT_CLIENT:
                shootTime = 1;
                this.playSound(SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, 1.1F, 1.0F);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    public float getSpawnPercent(final float partialTick) {
        if (spawnTime <= 0) {
            return 1.0F;
        }
        final float prevSpawnPercent = Math.max((float) spawnTime - partialTick, 0.0F) / (float) MAX_SPAWN_TIME;
        final float spawnPercent = (float) spawnTime / (float) MAX_SPAWN_TIME;
        return 1.0F - MathHelper.lerp(partialTick / 6, prevSpawnPercent, spawnPercent);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (this.level.isClientSide() || !this.isShooting() || shootTime < (MAX_SHOOT_TIME / 4)) {
            return;
        }
        ItemStack itemstack = new ItemStack(Items.ARROW);
        AbstractArrowEntity arrow = ProjectileHelper.getMobArrow(this, itemstack, distanceFactor);
        // this is adapted from LlamaSpit code, it moves the arrow nearer to right-front of the body
        // arrow.setPosition(this.getPosX() - (this.getWidth() + 1.0F) * 0.5D * MathHelper.sin(this.renderYawOffset * 0.017453292F), this.getPosYEye() - 0.10000000149011612D, this.getPosZ() + (this.getWidth() + 1.0F) * 0.5D * MathHelper.cos(this.renderYawOffset * 0.017453292F));
        arrow.setPos(this.getX() - (this.getBbWidth()) * 0.85D * MathHelper.sin(this.yBodyRot * 0.017453292F + 1.0F), this.getEyeY() - 0.74D, this.getZ() + (this.getBbWidth()) * 0.85D * MathHelper.cos(this.yBodyRot * 0.017453292F + 1.0F));
        double dx = target.getX() - arrow.getX();
        double dy = target.getY(0.67D) - arrow.getY();
        double dz = target.getZ() - arrow.getZ();
        double dis = MathHelper.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dis * (double) 0.2F, dz, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        arrow.setBaseDamage(1.0D + this.level.getDifficulty().getId() * 0.25D);
        arrow.setOwner(this);
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(arrow);
    }

    // Boss Logic

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
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
            return TalosEntity.this.isSpawning();
        }

        @Override
        public void tick() {
            TalosEntity.this.getNavigation().stop();
            TalosEntity.this.getLookControl().setLookAt(TalosEntity.this.getX(), TalosEntity.this.getY(), TalosEntity.this.getZ());
            TalosEntity.this.setRot(0, 0);
        }
    }

    class RangedAttackGoal extends net.minecraft.entity.ai.goal.RangedAttackGoal {

        public RangedAttackGoal(int interval, float attackDistance) {
            super(TalosEntity.this, 1.0F, interval, attackDistance);
        }

        @Override
        public boolean canUse() {
            final LivingEntity target = TalosEntity.this.getTarget();
            double disSq = (target != null) ? TalosEntity.this.distanceToSqr(target) : 0.0D;
            return TalosEntity.this.isNoneState() && TalosEntity.this.hasNoCooldown() && disSq > 9.0D && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            TalosEntity.this.setAttackCooldown(SHOOT_COOLDOWN);
            TalosEntity.this.setShooting(true);
            TalosEntity.this.level.broadcastEntityEvent(TalosEntity.this, SHOOT_CLIENT);
            TalosEntity.this.shootTime = 1;
        }

        @Override
        public boolean canContinueToUse() {
            return TalosEntity.this.shootTime > 0 && TalosEntity.this.isShooting();
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Override
        public void stop() {
            TalosEntity.this.setState(NONE);
            TalosEntity.this.shootTime = 0;
            super.stop();
        }
    }

    class MeleeAttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {

        public MeleeAttackGoal(double speedIn, boolean useLongMemory) {
            super(TalosEntity.this, speedIn, useLongMemory);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (TalosEntity.this.hasNoCooldown()) {
                super.checkAndPerformAttack(enemy, distToEnemySqr);
            }
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            TalosEntity.this.setAttackCooldown(MELEE_COOLDOWN);
        }
    }
}
