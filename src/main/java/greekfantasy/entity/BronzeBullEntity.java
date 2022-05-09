package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.ShootFireGoal;
import greekfantasy.item.ClubItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
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
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class BronzeBullEntity extends MonsterEntity {

    private static final DataParameter<Byte> STATE = EntityDataManager.defineId(BronzeBullEntity.class, DataSerializers.BYTE);
    private static final String KEY_STATE = "BullState";
    private static final String KEY_SPAWN = "SpawnTime";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte FIRING = (byte) 2;
    private static final byte GORING = (byte) 3;
    // bytes to use in World#setEntityState
    private static final byte SPAWN_CLIENT = 8;
    private static final byte FIRING_CLIENT = 9;
    private static final byte GORING_CLIENT = 10;

    private static final double FIRE_RANGE = 8.0D;
    private static final int MAX_SPAWN_TIME = 90;
    private static final int MAX_FIRING_TIME = 89;
    private static final int MAX_GORING_TIME = 130;

    private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

    private int firingTime;
    private int spawnTime;
    private int goringTime;

    public BronzeBullEntity(final EntityType<? extends BronzeBullEntity> type, final World worldIn) {
        super(type, worldIn);
        this.maxUpStep = 1.0F;
        this.xpReward = 50;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 150.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.75D)
                .add(Attributes.ARMOR, 8.0D);
    }

    public static BronzeBullEntity spawnBronzeBull(final World world, final BlockPos pos, final float yaw) {
        BronzeBullEntity entity = GFRegistry.EntityReg.BRONZE_BULL_ENTITY.create(world);
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
        this.goalSelector.addGoal(0, new BronzeBullEntity.SpawningGoal());
        this.goalSelector.addGoal(1, new BronzeBullEntity.FireAttackGoal(MAX_FIRING_TIME, 120));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.25D, false));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 10.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

        // update spawn time
        if (isSpawning() || spawnTime > 0) {
            // update timer
            if (--spawnTime <= 0) {
                setSpawning(false);
            }
        }

        // update goring attack
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
            final double y = this.getY() + 0.25D;
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
    public boolean doHurtTarget(final Entity entityIn) {
        // set goring
        if (!isGoring()) {
            setGoring(true);
        }
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
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setState(compound.getByte(KEY_STATE));
        spawnTime = compound.getInt(KEY_SPAWN);
    }

    public void spawnFireParticles() {
        if (!level.isClientSide()) {
            return;
        }
        Vector3d lookVec = this.getLookAngle();
        Vector3d pos = this.getEyePosition(1.0F);
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
            this.level.broadcastEntityEvent(this, FIRING_CLIENT);
        }
    }

    public void setGoring(final boolean goring) {
        goringTime = goring ? MAX_GORING_TIME : 0;
        setState(goring ? GORING : NONE);
        if (goring && !this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, GORING_CLIENT);
            // break intersecting blocks
            destroyIntersectingBlocks(1.45F + 0.75F * random.nextFloat(), 2.0D);
        }
    }

    public void setSpawning(final boolean spawning) {
        spawnTime = spawning ? MAX_SPAWN_TIME : 0;
        setState(spawning ? SPAWNING : NONE);
        if (spawning && !this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, SPAWN_CLIENT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        switch (id) {
            case SPAWN_CLIENT:
                setSpawning(true);
                break;
            case FIRING_CLIENT:
                setFiring(true);
                break;
            case GORING_CLIENT:
                setGoring(true);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    public float getSpawnPercent(final float partialTick) {
        return getPercent(spawnTime, MAX_SPAWN_TIME, partialTick);
    }

    public float getFiringPercent(final float partialTick) {
        return getPercent(firingTime, MAX_FIRING_TIME, partialTick);
    }

    public float getGoringPercent(final float partialTick) {
        return getPercent(goringTime, MAX_GORING_TIME, partialTick);
    }

    private float getPercent(final int timer, final int maxValue, final float partialTick) {
        if (timer <= 0) {
            return 0.0F;
        }
        final float prevSpawnPercent = Math.max((float) timer - partialTick, 0.0F) / (float) maxValue;
        final float spawnPercent = (float) timer / (float) maxValue;
        return 1.0F - MathHelper.lerp(partialTick / 6, prevSpawnPercent, spawnPercent);
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


    /**
     * Breaks blocks within this entity's bounding box
     *
     * @param offset the forward distance to offset the bounding box
     **/
    private void destroyIntersectingBlocks(final float maxHardness, final double offset) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
        final Vector3d facing = Vector3d.directionFromRotation(this.getRotationVector());
        final AxisAlignedBB box = this.getBoundingBox().move(facing.normalize().scale(offset));
        BlockPos p;
        BlockState s;
        for (double x = box.minX - 0.25D; x < box.maxX + 0.25D; x++) {
            for (double y = box.minY + 1.1D; y < box.maxY + 0.5D; y++) {
                for (double z = box.minZ - 0.25D; z < box.maxZ + 0.25D; z++) {
                    p = new BlockPos(x, y, z);
                    s = this.getCommandSenderWorld().getBlockState(p);
                    if ((s.canOcclude() || s.getMaterial().blocksMotion()) && s.getDestroySpeed(level, p) < maxHardness && !s.is(BlockTags.WITHER_IMMUNE)) {
                        this.getCommandSenderWorld().destroyBlock(p, true);
                    }
                }
            }
        }
    }

    // Custom goals

    class SpawningGoal extends Goal {

        public SpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return BronzeBullEntity.this.isSpawning();
        }

        @Override
        public void tick() {
            BronzeBullEntity.this.getNavigation().stop();
            BronzeBullEntity.this.getLookControl().setLookAt(BronzeBullEntity.this.getX(), BronzeBullEntity.this.getY(), BronzeBullEntity.this.getZ());
            BronzeBullEntity.this.setRot(0, 0);
        }
    }

    class FireAttackGoal extends ShootFireGoal {

        protected FireAttackGoal(final int fireTimeIn, final int maxCooldownIn) {
            super(BronzeBullEntity.this, fireTimeIn, maxCooldownIn, FIRE_RANGE);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && BronzeBullEntity.this.isNoneState();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && BronzeBullEntity.this.isFiring();
        }

        @Override
        public void start() {
            super.start();
            BronzeBullEntity.this.setFiring(true);
        }

        @Override
        public void stop() {
            super.stop();
            BronzeBullEntity.this.setFiring(false);
        }
    }
}
