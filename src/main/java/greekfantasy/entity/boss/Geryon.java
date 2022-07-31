package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.CooldownMeleeAttackGoal;
import greekfantasy.entity.ai.SummonMobGoal;
import greekfantasy.entity.monster.MadCow;
import greekfantasy.entity.util.HasCustomCooldown;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class Geryon extends Monster implements HasCustomCooldown {

    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Geryon.class, EntityDataSerializers.BYTE);
    private static final String KEY_STATE = "GeryonState";
    private static final String KEY_SPAWN_TIME = "SpawnTime";
    private static final String KEY_SMASH_TIME = "SmashTime";
    private static final String KEY_SUMMON_TIME = "SummonTime";
    // bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1;
    private static final byte SMASH = (byte) 2;
    private static final byte SUMMON_COW = (byte) 4;
    // bytes to use in Level#broadcastEntityEvent
    private static final byte START_SPAWN_EVENT = 9;
    private static final byte SMASH_EVENT = 10;
    private static final byte SUMMON_EVENT = 11;

    private static final int MAX_SPAWN_TIME = 110;
    private static final int MAX_SMASH_TIME = 42;
    private static final int MAX_SUMMON_TIME = 35;
    private static final double SMASH_RANGE = 12.0D;
    private static final int ATTACK_COOLDOWN = 38;
    private static final int STUN_DURATION = 50;

    private final ServerBossEvent bossInfo = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS)).setDarkenScreen(true);

    private int spawnTime;
    private int smashTime;
    private int summonTime0;
    private int summonTime;

    private int attackCooldown;

    public Geryon(final EntityType<? extends Geryon> type, final Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 140.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.21D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.98D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT)
                .add(Attributes.ARMOR, 12.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6F);
    }

    public static boolean canGeryonSpawnOn(final LevelAccessor level, final BlockPos pos) {
        final AABB box = new AABB(pos).expandTowards(-1, 0, -1).expandTowards(1, 5, 1);
        BlockPos.MutableBlockPos p = pos.mutable();
        // check surrounding area (if it's big enough)
        for (double x = box.minX; x < box.maxX; x++) {
            for (double y = box.minY; y < box.maxY; y++) {
                for (double z = box.minZ; z < box.maxZ; z++) {
                    p.set(x, y, z);
                    if (level.getBlockState(p).is(BlockTags.WITHER_IMMUNE)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static Geryon spawnGeryon(final Level level, final BlockPos pos, final float yaw) {
        Geryon entity = GFRegistry.EntityReg.GERYON.get().create(level);
        entity.moveTo(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
        entity.yBodyRot = yaw;
        entity.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(GFRegistry.ItemReg.IRON_CLUB.get()));
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
        this.goalSelector.addGoal(0, new Geryon.SpawningGoal());
        this.goalSelector.addGoal(1, new Geryon.SummonCowGoal(MAX_SUMMON_TIME, 440));
        this.goalSelector.addGoal(2, new Geryon.SmashAttackGoal(SMASH_RANGE, 210));
        this.goalSelector.addGoal(3, new Geryon.GeryonAttackGoal(1.0D, false, ATTACK_COOLDOWN));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // attack cooldown
        tickCustomCooldown();

        // update spawn time
        if (isSpawning() && --spawnTime <= 0) {
            // update timer
            setSpawning(false);
            if (!level.isClientSide()) {
                destroyIntersectingBlocks(0);
            }
        }

        // update smash attack
        if (this.isSmashAttack()) {
            smashTime++;
        } else if (smashTime > 0) {
            smashTime = 0;
        }

        // update summoning
        summonTime0 = summonTime;
        if (this.isSummoning()) {
            summonTime++;
            if (this.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
                this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(GFRegistry.ItemReg.HORN.get()));
            }
        } else if (summonTime > 0 && --summonTime <= 0) {
            if (this.getItemInHand(InteractionHand.OFF_HAND).is(GFRegistry.ItemReg.HORN.get())) {
                this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if(this.level.isClientSide()) {
            // add motion particles
            if (this.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F && this.random.nextInt(5) == 0) {
                addBlockParticles(2);
            }
            // add spawning particles
            if(this.isSpawning()) {
                addBlockParticles(10);
            }
        }
    }

    @Override
    public boolean doHurtTarget(final Entity entityIn) {
        if (super.doHurtTarget(entityIn)) {
            // apply extra knockback velocity when attacking (ignores knockback resistance)
            final double knockbackFactor = 0.42D;
            final Vec3 myPos = this.position();
            final Vec3 ePos = entityIn.position();
            final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
            final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
            entityIn.push(dX, knockbackFactor / 2.0D, dZ);
            entityIn.hurtMarked = true;
            return true;
        }
        return false;
    }

    @Override
    protected float getJumpPower() {
        return 1.4F * super.getJumpPower();
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
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        if(this.hasCustomName()) {
            this.bossInfo.setName(this.getCustomName());
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        return isSpawning() || source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        final SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnDataIn, dataTag);
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(GFRegistry.ItemReg.IRON_CLUB.get()));
        this.setSpawning(true);
        return data;
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.5F;
    }

    @Override
    public float getVoicePitch() {
        return 0.089F;
    }

    // NBT //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getGeryonState());
        compound.putInt(KEY_SPAWN_TIME, this.spawnTime);
        compound.putInt(KEY_SUMMON_TIME, this.summonTime);
        compound.putInt(KEY_SMASH_TIME, this.smashTime);
        saveCustomCooldown(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setGeryonState(compound.getByte(KEY_STATE));
        this.spawnTime = compound.getInt(KEY_SPAWN_TIME);
        this.summonTime = compound.getInt(KEY_SUMMON_TIME);
        this.smashTime = compound.getInt(KEY_SMASH_TIME);
        readCustomCooldown(compound);
    }

    // States //

    public byte getGeryonState() {
        return this.getEntityData().get(STATE);
    }

    public void setGeryonState(final byte state) {
        this.getEntityData().set(STATE, state);
    }

    public boolean isNoneState() {
        return getGeryonState() == NONE;
    }

    public boolean isSmashAttack() {
        return getGeryonState() == SMASH;
    }

    public void setSmashAttack(final boolean smash) {
        setGeryonState(smash ? SMASH : NONE);
    }

    public boolean isSummoning() {
        return getGeryonState() == SUMMON_COW;
    }

    public void setSummoning(final boolean summon) {
        setGeryonState(summon ? SUMMON_COW : NONE);
    }

    public boolean isSpawning() {
        return spawnTime > 0 || getGeryonState() == SPAWNING;
    }

    public void setSpawning(final boolean spawning) {
        spawnTime = spawning ? MAX_SPAWN_TIME : 0;
        setGeryonState(spawning ? SPAWNING : NONE);
        if (spawning && !this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, START_SPAWN_EVENT);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case START_SPAWN_EVENT:
                setSpawning(true);
                break;
            case SMASH_EVENT:
                // spawn particles for all nearby entities
                final List<Entity> targets = this.getCommandSenderWorld().getEntities(Geryon.this, Geryon.this.getBoundingBox().inflate(SMASH_RANGE, SMASH_RANGE / 2, SMASH_RANGE));
                for (final Entity e : targets) {
                    addSmashParticlesAt(e);
                }
                // add sound and block particles here
                level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.IRON_GOLEM_ATTACK, this.getSoundSource(), 2.0F, 0.4F, false);
                addBlockParticles(45);
                break;
            case SUMMON_EVENT:
                for (int i = 0; i < 4; i++) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.NOTE_BLOCK_DIDGERIDOO, this.getSoundSource(), 2.0F, 0.2F, false);
                }
                for (int i = 0; i < 2; i++) {
                    this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.NOTE_BLOCK_DIDGERIDOO, this.getSoundSource(), 1.8F, 0.4F, false);
                }
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Particles //

    /**
     * Adds particles using the data of the block below this entity
     *
     * @param count the number of particles to add
     **/
    private void addBlockParticles(final int count) {
        int i = Mth.floor(this.getX());
        int j = Mth.floor(this.getY() - (double) 0.2F);
        int k = Mth.floor(this.getZ());
        BlockPos pos = new BlockPos(i, j, k);
        BlockState blockstate = this.level.getBlockState(pos);
        if (!blockstate.isAir()) {
            final BlockParticleOption data = new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos);
            final double radius = this.getBbWidth();
            final double motion = 4.0D;
            for (int c = 0; c < count; c++) {
                this.level.addParticle(data,
                        this.getX() + (this.random.nextDouble() - 0.5D) * radius * 2,
                        this.getY() + 0.1D,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * radius * 2,
                        motion * (this.random.nextDouble() - 0.5D), 0.5D, (this.random.nextDouble() - 0.5D) * motion);
            }
        }
    }

    private void addSmashParticlesAt(final Entity e) {
        final double x = e.getX() + 0.5D;
        final double y = e.getY() + 0.1D;
        final double z = e.getZ() + 0.5D;
        final double motion = 0.08D;
        final double radius = e.getBbWidth();
        for (int i = 0; i < 25; i++) {
            level.addParticle(ParticleTypes.CRIT,
                    x + (level.random.nextDouble() - 0.5D) * radius,
                    y,
                    z + (level.random.nextDouble() - 0.5D) * radius,
                    (level.random.nextDouble() - 0.5D) * motion,
                    0.5D,
                    (level.random.nextDouble() - 0.5D) * motion);
        }
    }

    public float getSmashPercent(final float partialTick) {
        return Mth.lerp(partialTick, Math.max(0.0F, smashTime - 1), smashTime) / (float) MAX_SMASH_TIME;
    }

    public float getSpawnPercent(final float partialTick) {
        return 1.0F - (Mth.lerp(partialTick, Math.max(0.0F, spawnTime - 1), spawnTime) / (float)  MAX_SPAWN_TIME);
    }

    public float getSummonPercent(final float partialTick) {
        return Mth.lerp(partialTick, summonTime0, summonTime) / (float) MAX_SUMMON_TIME;
    }

    // Attacks //

    /**
     * @param entity the entity to check
     * @return whether the given entity should not be affected by smash attack
     **/
    private boolean isExemptFromSmashAttack(final Entity entity) {
        return !entity.canChangeDimensions() || entity.isNoGravity() || entity.getType() == this.getType()
                || entity.getType() == GFRegistry.EntityReg.MAD_COW.get() || entity.isSpectator()
                || (entity instanceof Player player && player.isCreative());
    }

    /**
     * Applies a smash attack to the given entity
     *
     * @param entity the target entity
     **/
    private void useSmashAttack(final Entity entity) {
        // if entitiy is touching the ground, knock it into the air and apply stun
        if (entity.isOnGround() && !isExemptFromSmashAttack(entity)) {
            entity.push(0.0D, 0.65D, 0.0D);
            entity.hurt(DamageSource.mobAttack(this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
            // stun effect (for living entities)
            if (entity instanceof LivingEntity target) {
                if (GreekFantasy.CONFIG.STUNNED_NERF.get()) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, STUN_DURATION, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, STUN_DURATION, 0));
                } else {
                    target.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.STUNNED.get(), STUN_DURATION, 0));
                }
            }
        }
    }

    /**
     * Breaks blocks within this entity's bounding box
     *
     * @param offset the forward distance to offset the bounding box
     **/
    private void destroyIntersectingBlocks(final double offset) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
        final Vec3 facing = Vec3.directionFromRotation(this.getRotationVector());
        final AABB box = this.getBoundingBox().move(facing.normalize().scale(offset));
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        BlockState s;
        for (double x = box.minX - 0.25D; x < box.maxX + 0.25D; x++) {
            for (double y = box.minY + 1.1D; y < box.maxY + 0.5D; y++) {
                for (double z = box.minZ - 0.25D; z < box.maxZ + 0.25D; z++) {
                    p.set(x, y, z);
                    s = this.level.getBlockState(p);
                    if ((s.canOcclude() || s.getMaterial().blocksMotion()) && !s.is(BlockTags.WITHER_IMMUNE)) {
                        this.level.destroyBlock(p, true);
                    }
                }
            }
        }
    }

    @Override
    public void setCustomCooldown(int cooldown) {
        this.attackCooldown = cooldown;
    }

    @Override
    public int getCustomCooldown() {
        return this.attackCooldown;
    }

    // Custom goals

    class SpawningGoal extends Goal {

        public SpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return Geryon.this.isSpawning();
        }

        @Override
        public void tick() {
            Geryon.this.getNavigation().stop();
        }
    }

    class GeryonAttackGoal extends CooldownMeleeAttackGoal<Geryon> {

        public GeryonAttackGoal(double speedIn, boolean useLongMemory, int cooldown) {
            super(Geryon.this, speedIn, useLongMemory, cooldown);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return super.getAttackReachSqr(attackTarget) - 3.0D;
        }
    }

    class SmashAttackGoal extends Goal {

        private final double range;
        private final int maxCooldown;
        private int cooldown = 90;
        private boolean isBlockSmash;

        public SmashAttackGoal(final double rangeIn, final int maxCooldownIn) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            range = rangeIn;
            maxCooldown = maxCooldownIn;
        }

        @Override
        public void start() {
            Geryon.this.setSmashAttack(true);
            Geryon.this.getNavigation().createPath(Geryon.this.getTarget(), 0);
            isBlockSmash = Geryon.this.getNavigation().isDone();
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                cooldown--;
                return false;
            }
            return Geryon.this.getTarget() != null && Geryon.this.isNoneState()
                    && Geryon.this.hasNoCustomCooldown()
                    && Geryon.this.distanceToSqr(Geryon.this.getTarget()) < (range * range);
        }

        @Override
        public void tick() {
            Geryon.this.getNavigation().stop();
            Geryon.this.getLookControl().setLookAt(Geryon.this.getTarget(), Geryon.this.getMaxHeadYRot(), Geryon.this.getMaxHeadXRot());
            if (Geryon.this.smashTime >= Geryon.MAX_SMASH_TIME) {
                // notify client (spawns particles around entities)
                Geryon.this.level.broadcastEntityEvent(Geryon.this, Geryon.SMASH_EVENT);
                // get a list of nearby entities and use smash attack on each one
                Geryon.this.level.getEntities(Geryon.this, Geryon.this.getBoundingBox().inflate(range, range / 2, range))
                        .forEach(e -> Geryon.this.useSmashAttack(e));
                Geryon.this.setCustomCooldown(ATTACK_COOLDOWN);
                // destroy nearby blocks
                if (isBlockSmash) {
                    Geryon.this.destroyIntersectingBlocks(2.5D);
                }
                // finish task
                this.stop();
            }
        }

        @Override
        public boolean canContinueToUse() {
            return Geryon.this.isSmashAttack() && Geryon.this.getTarget() != null
                    && Geryon.this.distanceToSqr(Geryon.this.getTarget()) < (range * range);
        }

        @Override
        public void stop() {
            Geryon.this.setSmashAttack(false);
            isBlockSmash = false;
            cooldown = maxCooldown;
        }
    }

    class SummonCowGoal extends SummonMobGoal<MadCow> {

        public SummonCowGoal(final int summonProgressIn, final int summonCooldownIn) {
            super(Geryon.this, summonProgressIn, summonCooldownIn, GFRegistry.EntityReg.MAD_COW.get());
        }

        @Override
        public boolean canUse() {
            return super.canUse() && Geryon.this.hasNoCustomCooldown() && Geryon.this.isNoneState();
        }

        @Override
        public void start() {
            super.start();
            Geryon.this.setSummoning(true);
        }

        @Override
        protected void onSummonMob(final MadCow mobEntity) {
            Geryon.this.level.broadcastEntityEvent(Geryon.this, SUMMON_EVENT);
        }

        @Override
        public void stop() {
            super.stop();
            Geryon.this.setCustomCooldown(ATTACK_COOLDOWN);
            Geryon.this.setSummoning(false);
        }
    }

}
