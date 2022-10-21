package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.Whirl;
import greekfantasy.entity.misc.WaterSpell;
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
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class Charybdis extends WaterAnimal implements Enemy {

    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Charybdis.class, EntityDataSerializers.BYTE);
    private static final String KEY_STATE = "CharybdisState";
    private static final String KEY_SPAWN_TIME = "SpawnTime";
    private static final String KEY_SWIRL_TIME = "SwirlTime";
    private static final String KEY_THROW_TIME = "ThrowTime";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte SWIRLING = (byte) 2;
    private static final byte THROWING = (byte) 4;
    //bytes to use in Level#broadcastEntityEvent
    private static final byte NONE_CLIENT = 8;
    private static final byte SPAWN_CLIENT = 9;
    private static final byte SWIRL_CLIENT = 10;
    private static final byte THROW_CLIENT = 11;

    // other constants for attack, spawn, etc.
    private static final double RANGE = 15.0D;
    private static final int SPAWN_TIME = 50;
    private static final int SWIRL_TIME = 240;
    private static final int THROW_TIME = 34;

    protected static final Predicate<Entity> CAN_TARGET = e -> e.isInWaterOrBubble()
            && !e.isSpectator() && !(e instanceof Player p && p.isCreative())
            && !(e.getType() == GFRegistry.EntityReg.SCYLLA.get() || e.getType() == GFRegistry.EntityReg.WHIRL.get()
            && (e instanceof LivingEntity || e instanceof Boat || e instanceof ItemEntity));

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);

    private int spawnTime0;
    private int spawnTime;
    private int swirlTime0;
    private int swirlTime;
    private int throwTime0;
    private int throwTime;

    public Charybdis(final EntityType<? extends Charybdis> type, final Level worldIn) {
        super(type, worldIn);
        this.xpReward = 50;
    }

    public static Charybdis spawnCharybdis(final ServerLevel level, final Whirl whirl) {
        Charybdis entity = GFRegistry.EntityReg.CHARYBDIS.get().create(level);
        entity.moveTo(whirl.getX(), whirl.getY() - 2.8D, whirl.getZ(), whirl.getYRot(), whirl.getXRot());
        if (whirl.hasCustomName()) {
            entity.setCustomName(whirl.getCustomName());
            entity.setCustomNameVisible(whirl.isCustomNameVisible());
        }
        entity.setPersistenceRequired();
        entity.yBodyRot = whirl.yBodyRot;
        entity.setPortalCooldown();
        level.addFreshEntityWithPassengers(entity);
        entity.finalizeSpawn(level, level.getCurrentDifficultyAt(whirl.blockPosition()), MobSpawnType.CONVERSION, null, null);
        entity.setState(SPAWNING);
        // remove the old whirl
        whirl.discard();
        // trigger spawn for nearby players
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(16.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
        }
        // play sound
        entity.playSound(SoundEvents.WITHER_SPAWN, 1.2F, 1.0F);
        // attempt to summon scylla
        if (level.getRandom().nextFloat() * 100.0F < GreekFantasy.CONFIG.SCYLLA_SPAWN_CHANCE.get()) {
            Scylla scylla = entity.spawnScylla(level);
        }
        return entity;
    }

    protected Scylla spawnScylla(final ServerLevel level) {
        final Scylla entity = GFRegistry.EntityReg.SCYLLA.get().create(level);
        final BlockPos entityPos = new BlockPos(position());
        final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        final int radius = Mth.ceil(getBbWidth() * 1.5F);
        AABB aabb;
        // locate spawn position
        for (int tries = 0, maxTries = 24; tries < maxTries; tries++) {
            blockPos.setWithOffset(entityPos,
                    level.getRandom().nextInt(radius * 2) - radius,
                    level.getRandom().nextInt(4) - 2,
                    level.getRandom().nextInt(radius * 2) - radius);
            // create bounding box
            aabb = GFRegistry.EntityReg.SCYLLA.get().getDimensions().makeBoundingBox(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            // check if scylla can spawn here
            if (level.isWaterAt(blockPos) && level.noCollision(entity, aabb)) {
                // spawn scylla
                entity.moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0, 0);
                entity.setPersistenceRequired();
                entity.setPortalCooldown();
                level.addFreshEntityWithPassengers(entity);
                entity.finalizeSpawn(level, level.getCurrentDifficultyAt(blockPos), MobSpawnType.MOB_SUMMONED, null, null);
                // play sound
                entity.playSound(SoundEvents.GHAST_SCREAM, 1.2F, 1.0F);
                return entity;
            }
        }
        // no checks passed
        entity.discard();
        return null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 180.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.10D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, NONE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new Charybdis.SwirlGoal(this, SWIRL_TIME, 90, RANGE));
        this.goalSelector.addGoal(3, new Charybdis.ThrowGoal(THROW_TIME, 130, RANGE * 0.75D));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // update spawn time
        spawnTime0 = spawnTime;
        if (isSpawning()) {
            // update timer
            spawnTime = Math.max(spawnTime - 1, 0);
            if (spawnTime <= 0) {
                setState(NONE);
            }
        }

        // update swirl attack
        swirlTime0 = swirlTime;
        if (isSwirling()) {
            swirlTime = Math.min(swirlTime + 1, SWIRL_TIME);
            this.setYBodyRot(this.yBodyRot + 5);
        } else if (swirlTime > 0) {
            swirlTime--;
        }

        // update throw attack
        throwTime0 = throwTime;
        if (isThrowing()) {
            throwTime = Math.min(throwTime + 1, THROW_TIME);
        } else if (throwTime > 0) {
            throwTime--;
        }
    }

    @Override
    public void tick() {
        super.tick();

        // spawn particles
        if (this.level.isClientSide() && tickCount % 3 == 0 && this.isInWaterOrBubble()) {
            // spawn particles at targeted entities
            getEntitiesInRange(RANGE).forEach(e -> bubbles(e.getX(), e.getY(), e.getZ(), e.getBbWidth(), 5));
            // spawn particles in spiral
            final float spawnPercent = this.getSpawnPercent(0.0F);
            float maxY = this.getBbHeight() * spawnPercent * 1.65F;
            float y = 0;
            float nY = 120 * spawnPercent;
            float dY = maxY / nY;
            double posX = this.getX();
            double posY = this.getY();
            double posZ = this.getZ();
            // for each y-position, increase the angle and spawn particle here
            for (float a = 0, nA = 28 + random.nextInt(4), dA = (2 * (float) Math.PI) / nA; y < maxY; a += dA) {
                float radius = y * 0.5F;
                float cosA = Mth.cos(a) * radius;
                float sinA = Mth.sin(a) * radius;
                //bubbles(posX + cosA, posY + y, posZ + sinA, 0.125D, 1);
                level.addParticle(ParticleTypes.BUBBLE, posX + cosA, posY + y - (maxY * 0.4), posZ + sinA, 0.0D, 0.085D, 0.0D);
                y += dY;
            }
        }

    }

    // Misc //

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnDataIn, dataTag);
        this.setState(SPAWNING);
        return data;
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
    public boolean isInvulnerableTo(final DamageSource source) {
        return isSpawning() || source == DamageSource.IN_WALL || source == DamageSource.WITHER
                || source.getDirectEntity() instanceof AbstractArrow
                || source.getDirectEntity() instanceof WaterSpell
                || super.isInvulnerableTo(source);
    }

    @Override
    protected void handleAirSupply(int air) {
    }

    @Override
    public double getFluidJumpThreshold() {
        return getBbHeight() - 0.2D;
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.89F;
    }

    // Prevent entity collisions //

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
    }

    // Boss //

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
        return 0.6F + random.nextFloat() * 0.2F;
    }

    // NBT //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getState());
        compound.putInt(KEY_SPAWN_TIME, spawnTime);
        compound.putInt(KEY_SWIRL_TIME, swirlTime);
        compound.putInt(KEY_THROW_TIME, throwTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setState(compound.getByte(KEY_STATE));
        spawnTime0 = spawnTime = compound.getInt(KEY_SPAWN_TIME);
        swirlTime0 = swirlTime = compound.getInt(KEY_SWIRL_TIME);
        throwTime0 = throwTime = compound.getInt(KEY_THROW_TIME);
    }

    // Swimming //

    @Override
    public void travel(final Vec3 vec) {
        if (isEffectiveAi() && isInWater() && !this.isEyeInFluid(FluidTags.WATER)) {
            moveRelative(-0.02F, vec);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().scale(0.9D));
        } else {
            super.travel(vec);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    // States //

    public void setState(final byte state) {
        this.getEntityData().set(STATE, state);
        byte clientFlag = NONE_CLIENT;
        switch (state) {
            case NONE:
                break;
            case SPAWNING:
                spawnTime = SPAWN_TIME;
                clientFlag = SPAWN_CLIENT;
                break;
            case SWIRLING:
                clientFlag = SWIRL_CLIENT;
                break;
            case THROWING:
                clientFlag = THROW_CLIENT;
                break;
        }
        if (!level.isClientSide()) {
            level.broadcastEntityEvent(this, clientFlag);
        }
    }

    public byte getState() {
        return this.getEntityData().get(STATE);
    }

    public boolean isNoneState() {
        return getState() == NONE;
    }

    public boolean isSpawning() {
        return getState() == SPAWNING;
    }

    public boolean isSwirling() {
        return getState() == SWIRLING;
    }

    public boolean isThrowing() {
        return getState() == THROWING;
    }

    public float getSpawnPercent(final float partialTick) {
        return 1.0F - Mth.lerp(partialTick, spawnTime0, spawnTime) / (float) SPAWN_TIME;
    }

    public float getSwirlPercent(final float partialTick) {
        return Mth.clamp(Mth.lerp(partialTick, swirlTime0, swirlTime) / 50.0F, 0.0F, 1.0F);
    }

    public float getThrowPercent(final float partialTick) {
        return Mth.lerp(partialTick, throwTime0, throwTime) / (float) THROW_TIME;
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case NONE:
                setState(NONE);
                break;
            case SPAWN_CLIENT:
                setState(SPAWNING);
                break;
            case SWIRL_CLIENT:
                setState(SWIRLING);
                break;
            case THROW_CLIENT:
                setState(THROWING);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Misc //

    public List<Entity> getEntitiesInRange(final double range) {
        return level.getEntities(this, getBoundingBox().inflate(range, range / 2, range), CAN_TARGET);
    }

    public void bubbles(final double posX, final double posY, final double posZ, final double radius, final int count) {
        final double motion = 0.08D;
        for (int i = 0; i < count; i++) {
            level.addParticle(ParticleTypes.BUBBLE,
                    posX + (level.random.nextDouble() - 0.5D) * radius,
                    posY,
                    posZ + (level.random.nextDouble() - 0.5D) * radius,
                    (level.random.nextDouble() - 0.5D) * motion,
                    0.5D,
                    (level.random.nextDouble() - 0.5D) * motion);
        }
    }

    // Goals //

    private static class SwirlGoal extends greekfantasy.entity.ai.SwirlGoal {
        private final Charybdis entity;

        public SwirlGoal(final Charybdis entity, final int duration, final int cooldown, final double range) {
            super(entity, duration, cooldown, range, 0.12F, true, e ->
                    !(e.getType() == GFRegistry.EntityReg.WHIRL.get()
                            || e.getType() == GFRegistry.EntityReg.CHARYBDIS.get()
                            || e.getType() == GFRegistry.EntityReg.SCYLLA.get()));
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && (entity.isNoneState() || entity.isSwirling());
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && entity.isSwirling();
        }

        @Override
        public void start() {
            super.start();
            entity.setState(SWIRLING);
        }

        @Override
        public void tick() {
            super.tick();
            final float attack = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 0.25F;
            for (Entity e : trackedEntities) {
                if (e instanceof LivingEntity livingEntity) {
                    // give living entities slow swim
                    livingEntity.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.SLOW_SWIM.get(), 10, 0));
                    // periodically hurt living entities
                    if (livingEntity.hurtTime == 0 && livingEntity.tickCount % 20 == 0) {
                        livingEntity.hurt(DamageSource.mobAttack(entity), attack);
                    }
                }
            }
        }

        @Override
        public void stop() {
            super.stop();
            entity.setState(NONE);
        }

        @Override
        protected void onCollideWith(Entity e) {
            // attack the entity and steal some health
            final float attack = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            if (e.hurt(DamageSource.mobAttack(entity), attack)) {
                entity.heal(Math.abs(attack * 0.25F));
            }
        }
    }

    class ThrowGoal extends Goal {

        protected final Charybdis entity;
        protected final int duration;
        protected final int cooldown;
        protected final double range;

        protected List<Entity> trackedEntities = new ArrayList<>();
        protected int progressTime;
        protected int cooldownTime;

        /**
         * @param lDuration the maximum amount of time this goal will run
         * @param lCooldown the minimum amount of time before this goal runs again
         * @param lRange    the distance at which entities should be swirled
         **/
        public ThrowGoal(final int lDuration, final int lCooldown, final double lRange) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            entity = Charybdis.this;
            duration = lDuration;
            cooldown = lCooldown;
            range = lRange;
            cooldownTime = 50;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            if (cooldownTime > 0) {
                cooldownTime--;
            } else if (entity.isNoneState() || entity.isThrowing()) {
                trackedEntities = entity.getEntitiesInRange(range);
                return trackedEntities.size() > 0;
            }
            return false;
        }

        @Override
        public void start() {
            entity.setState(THROWING);
            entity.throwTime = 1;
            progressTime = 1;
        }

        @Override
        public boolean canContinueToUse() {
            return progressTime > 0 && entity.isThrowing();
        }

        @Override
        public void tick() {
            // goal timer
            if (progressTime++ >= duration) {
                final double widthSq = entity.getBbWidth() * entity.getBbWidth();
                // move tracked entities
                trackedEntities = entity.getEntitiesInRange(range);
                for (final Entity target : trackedEntities) {
                    // distance math
                    double dx = entity.getX() - target.position().x;
                    double dz = entity.getZ() - target.position().z;
                    final double horizDisSq = dx * dx + dz * dz;
                    // throw the entity upward
                    if (horizDisSq > widthSq) {
                        // calculate the amount of motion to apply based on distance
                        final double motion = 1.08D + 0.31D * (1.0D - (horizDisSq / (range * range)));
                        target.push(0, motion, 0);
                        target.hurtMarked = true;
                        // damage boats and other rideable entities
                        if (target instanceof Boat || !target.getPassengers().isEmpty()) {
                            target.hurt(DamageSource.mobAttack(entity), 6.0F);
                        }
                    }
                }
                stop();
            }
        }

        @Override
        public void stop() {
            entity.setState(NONE);
            entity.throwTime = 0;
            progressTime = 0;
            cooldownTime = cooldown;
            trackedEntities.clear();
        }
    }

}
