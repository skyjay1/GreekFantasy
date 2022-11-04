package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.MoveToStructureGoal;
import greekfantasy.entity.ai.SummonMobGoal;
import greekfantasy.entity.util.HasHorseVariant;
import greekfantasy.util.SongManager;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;

public class Satyr extends PathfinderMob implements NeutralMob, HasHorseVariant {

    private static final EntityDataAccessor<Byte> DATA_STATE = SynchedEntityData.defineId(Satyr.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> DATA_SHAMAN = SynchedEntityData.defineId(Satyr.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Satyr.class, EntityDataSerializers.INT);
    public static final String KEY_SHAMAN = "Shaman";
    public static final String KEY_VARIANT = "Variant";

    private static final ResourceLocation SUMMONING_SONG = new ResourceLocation(GreekFantasy.MODID, "sarias_song");

    // NONE, DANCING, and SUMMONING are values for DATA_STATE
    protected static final byte NONE = 0;
    protected static final byte DANCING = 1;
    protected static final byte SUMMONING = 2;
    // sent from server to client to trigger sound
    protected static final byte PLAY_SUMMON_SOUND = 12;

    protected static final int MAX_SUMMON_TIME = 160;
    protected static final int MAX_PANFLUTE_TIME = 10;
    /**
     * Counts up from 0 for every tick the entity is holding
     * a panflute, up to MAX_PANFLUTE_TIME
     */
    public int holdingPanfluteTime;
    /**
     * Counts up from 0 for every tick the entity is summoning,
     * up to MAX_SUMMON_TIME
     */
    public int summonTime;
    public boolean hasShamanTexture;

    protected static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    protected int angerTime;
    protected UUID angerTarget;

    public static final BiPredicate<BlockState, Boolean> IS_CAMPFIRE = (state, lit) ->
            state.is(BlockTags.CAMPFIRES) && state.hasProperty(CampfireBlock.LIT)
                    && state.getValue(CampfireBlock.LIT) == lit
                    && (!state.hasProperty(BlockStateProperties.WATERLOGGED)
                    || !state.getValue(BlockStateProperties.WATERLOGGED));

    protected BlockPos dancingAround = null;

    protected final Goal meleeAttackGoal;
    protected final Goal summonAnimalsGoal;

    public Satyr(final EntityType<? extends Satyr> type, final Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
        meleeAttackGoal = new MeleeAttackGoal(this, 1.0D, false);
        summonAnimalsGoal = new SummonWolfGoal(MAX_SUMMON_TIME, 480);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, 2.5D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_STATE, NONE);
        this.getEntityData().define(DATA_SHAMAN, Boolean.FALSE);
        this.getEntityData().define(DATA_VARIANT, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new Satyr.DancingGoal(0.75D, 880));
        this.goalSelector.addGoal(3, new Satyr.PanicGoal(1.3D));
        this.goalSelector.addGoal(4, new Satyr.StartDancingGoal(0.9D, 22, 12, 420));
        this.goalSelector.addGoal(5, new Satyr.LightCampfireGoal(0.9D, 12, 10, 60, 500));
        if(GreekFantasy.CONFIG.SATYR_SEEK_CAMP.get()) {
            this.goalSelector.addGoal(6, new MoveToStructureGoal(this, 1.0D, 2, 8, 4, new ResourceLocation(GreekFantasy.MODID, "satyr_camp"), DefaultRandomPos::getPos));
        }
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D, 160) {
            @Override
            public boolean canUse() {
                return Satyr.this.isIdleState() && Satyr.this.getTarget() == null && super.canUse();
            }
        });
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new SatyrHurtByTargetGoal());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide()) {
            // play music
            if (isSummoning()) {
                SongManager.playMusic(this, GFRegistry.ItemReg.PANFLUTE.get(), SUMMONING_SONG, summonTime, 0.92F, 0.34F);
            } else if (isDancing()) {
                SongManager.playMusic(this, GFRegistry.ItemReg.PANFLUTE.get(), GreekFantasy.CONFIG.getSatyrSong(), level.getGameTime(), 0.84F, 0.28F);
            }
        } else {
            // anger timer
            this.updatePersistentAnger((ServerLevel) this.level, true);
        }

        // dancing timer
        if (isDancing() || isSummoning()) {
            this.holdingPanfluteTime = Math.min(this.holdingPanfluteTime + 1, MAX_PANFLUTE_TIME);
        } else {
            this.holdingPanfluteTime = Math.max(this.holdingPanfluteTime - 1, 0);
        }

        // summon timer
        if (summonTime > 0) {
            summonTime = Math.min(summonTime + 1, MAX_SUMMON_TIME);
        }
    }

    @Override
    public boolean hurt(final DamageSource source, final float amount) {
        final boolean attackEntityFrom = super.hurt(source, amount);
        if (attackEntityFrom && source.getDirectEntity() instanceof LivingEntity) {
            // alert all nearby satyr shamans
            final LivingEntity target = (LivingEntity) source.getDirectEntity();
            final List<Satyr> shamans = this.getCommandSenderWorld().getEntitiesOfClass(Satyr.class, this.getBoundingBox().inflate(10.0D), e -> e.isShaman());
            for (final Satyr shaman : shamans) {
                if (shaman.getTarget() == null) {
                    shaman.setLastHurtByMob(target);
                    shaman.setPersistentAngerTarget(target.getUUID());
                    shaman.startPersistentAngerTimer();
                }
            }
        }

        return attackEntityFrom;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        if (super.isInvulnerableTo(source)) {
            return true;
        }
        // immune to being in fires
        return source == DamageSource.IN_FIRE;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        // set variant when not spawned as part of a structure
        if (spawnType != MobSpawnType.STRUCTURE) {
            // determine color variant based on spawn group data, or create new group data
            Variant color;
            if (spawnDataIn instanceof Satyr.GroupData) {
                color = ((Satyr.GroupData) spawnDataIn).variant;
            } else {
                color = Util.getRandom(Variant.values(), getRandom());
                spawnDataIn = new Satyr.GroupData(color);
            }
            this.setVariant(color);
        }

        // random chance to be a satyr shaman
        this.setShaman(getRandom().nextFloat() * 100.0F < GreekFantasy.CONFIG.SATYR_SHAMAN_CHANCE.get());
        return super.finalizeSpawn(worldIn, difficulty, spawnType, spawnDataIn, dataTag);
    }

    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key == DATA_SHAMAN) {
            // change AI task for shaman / non-shaman
            updateCombatAI();
            // update shaman flag
            this.hasShamanTexture = this.isShaman();
        } else if (key == DATA_STATE) {
            // update summon time
            if (this.isSummoning()) {
                this.summonTime = 1;
            } else {
                this.summonTime = 0;
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == PLAY_SUMMON_SOUND) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.WOLF_HOWL, this.getSoundSource(), 1.1F, 0.9F + this.getRandom().nextFloat() * 0.2F, false);
            this.summonTime = 1;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(KEY_SHAMAN, this.isShaman());
        compound.putByte(KEY_VARIANT, (byte) this.getVariant().getId());
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setShaman(compound.getBoolean(KEY_SHAMAN));
        this.setVariant(Variant.byId(compound.getByte(KEY_VARIANT)));
        this.readPersistentAngerSaveData(this.level, compound);
    }

    // NeutralMob methods

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_RANGE.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.angerTime = time;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.angerTarget = target;
    }

    @Override
    public UUID getPersistentAngerTarget() {
        return this.angerTarget;
    }

    // End NeutralMob methods

    // Idle, dancing, summoning

    public byte getSatyrState() {
        return this.getEntityData().get(DATA_STATE);
    }

    public boolean isIdleState() {
        return getSatyrState() == NONE;
    }

    public void setIdleState() {
        this.getEntityData().set(DATA_STATE, NONE);
    }

    public boolean isDancing() {
        return getSatyrState() == DANCING;
    }

    public void setDancing(final boolean dancing) {
        this.getEntityData().set(DATA_STATE, dancing ? DANCING : NONE);
    }

    public boolean isSummoning() {
        return getSatyrState() == SUMMONING;
    }

    public void setSummoning(final boolean summoning) {
        this.getEntityData().set(DATA_STATE, summoning ? SUMMONING : NONE);
    }

    // Shaman, coat colors

    public boolean isShaman() {
        return this.getEntityData().get(DATA_SHAMAN);
    }

    public void setShaman(final boolean shaman) {
        this.hasShamanTexture = shaman;
        this.getEntityData().set(DATA_SHAMAN, shaman);
        updateCombatAI();
    }

    @Override
    public void setPackedVariant(int packedColorsTypes) {
        this.getEntityData().set(DATA_VARIANT, packedColorsTypes);
    }

    @Override
    public int getPackedVariant() {
        return getEntityData().get(DATA_VARIANT);
    }

    @Override
    public Markings getMarkings() {
        return Markings.NONE;
    }

    protected void updateCombatAI() {
        if (this.isEffectiveAi()) {
            if (this.isShaman()) {
                this.goalSelector.addGoal(1, summonAnimalsGoal);
                this.goalSelector.removeGoal(meleeAttackGoal);
            } else {
                this.goalSelector.addGoal(1, meleeAttackGoal);
                this.goalSelector.removeGoal(summonAnimalsGoal);
            }
        }
    }

    public boolean hasShamanTexture() {
        return this.hasShamanTexture;
    }

    /**
     * Used in the entity model to animate arms when holding a panflute.
     *
     * @param partialTick the partial tick
     * @return the percent
     **/
    public float getArmMovementPercent(final float partialTick) {
        final float time = Mth.lerp(partialTick, this.holdingPanfluteTime, this.holdingPanfluteTime + 1);
        return Math.min(1.0F, time / (float) MAX_PANFLUTE_TIME);
    }

    /**
     * @param pos the BlockPos to check around
     * @return if the given pos is a campfire with empty space around it
     **/
    protected boolean wantsToDanceAround(@Nullable final BlockPos pos) {
        // ensure nonnull
        if (null == pos) {
            return false;
        }
        // ensure the block is a campfire
        final BlockState target = level.getBlockState(pos);
        if (!IS_CAMPFIRE.test(target, true)) {
            return false;
        }
        // ensure surrounding area is passable
        BlockPos p;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                // do not check center block
                if (x == 0 && z == 0) continue;
                // update position
                p = pos.offset(x, 0, z);
                // ensure the entity can stand at this position or the positions above/below
                if (cannotStandAt(p.below()) && cannotStandAt(p) && cannotStandAt(p.above())) {
                    return false;
                }
            }
        }
        // all the conditions passed
        return true;
    }

    /**
     * @param pos a block position
     * @return true if the position is blocked or there is no solid surface
     */
    protected boolean cannotStandAt(final BlockPos pos) {
        BlockState state1 = level.getBlockState(pos);
        BlockState state2 = level.getBlockState(pos.above());
        // check material and fluids at and above this position
        if (state1.getMaterial().blocksMotion() || !state1.getFluidState().is(Fluids.EMPTY)
                || state2.getMaterial().blocksMotion() || !state2.getFluidState().is(Fluids.EMPTY)) {
            return true;
        }
        // check no solid surface below this position
        if (!level.getBlockState(pos.below(1)).entityCanStandOn(level, pos.below(1), this)) {
            return true;
        }
        // all conditions passed, entity can stand here
        return false;
    }

    /**
     * Used to alert shaman satyrs when the entity is hurt
     */
    class SatyrHurtByTargetGoal extends HurtByTargetGoal {

        public SatyrHurtByTargetGoal() {
            super(Satyr.this);
        }

        @Override
        public void start() {
            super.start();
            alertShamans();
        }

        protected void alertShamans() {
            // alert all nearby satyr shamans
            double range = Satyr.this.getAttributeValue(Attributes.FOLLOW_RANGE);
            final LivingEntity target = Satyr.this.getLastHurtByMob();
            final List<Satyr> shamans = Satyr.this.level.getEntitiesOfClass(Satyr.class, Satyr.this.getBoundingBox().inflate(range), e -> e.isShaman());
            for (final Satyr shaman : shamans) {
                this.alertOther(shaman, target);
            }
        }
    }

    /**
     * Used to summon wolves when the entity is attacking
     */
    class SummonWolfGoal extends SummonMobGoal<Wolf> {

        public SummonWolfGoal(final int duration, final int cooldown) {
            super(Satyr.this, duration, cooldown, EntityType.WOLF, 3);
        }

        @Override
        public void start() {
            super.start();
            Satyr.this.setSummoning(true);
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Override
        public void stop() {
            super.stop();
            Satyr.this.setSummoning(false);
            Satyr.this.level.broadcastEntityEvent(Satyr.this, PLAY_SUMMON_SOUND);
        }

        @Override
        protected void onSummonMob(final Wolf mobEntity) {
            mobEntity.setRemainingPersistentAngerTime(800);
            LivingEntity target = Satyr.this.getTarget();
            if (target != null) {
                mobEntity.setPersistentAngerTarget(target.getUUID());
            }
        }

    }

    /**
     * Used to make the entity stop dancing or summoning in order to panic
     */
    class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {
        public PanicGoal(double speed) {
            super(Satyr.this, speed);
        }

        @Override
        public boolean canUse() {
            return null == Satyr.this.getLastHurtByMob() && super.canUse();
        }

        @Override
        public void tick() {
            Satyr.this.setIdleState();
            super.tick();
        }

    }

    /**
     * Used to move the entity in a square around a campfire
     */
    class DancingGoal extends Goal {

        private final int obstructedTimeout = 100;
        private final int dancingTimeout;

        private Vec3 targetPos;
        /**
         * The direction from the campfire to the target pos
         */
        private Direction targetDirection;

        protected final double moveSpeed;

        /**
         * Counts down from dancingTimeout.
         * When it reaches 0, the entity stops dancing.
         **/
        private int dancingTime;
        /**
         * Counts down from obstructedTimeout.
         * When it reaches 0, the entity stops dancing.
         * Reset when the entity reaches its destination.
         **/
        private int obstructedTime;

        public DancingGoal(final double speedIn, final int maxDancingTime) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
            this.moveSpeed = speedIn;
            this.dancingTimeout = maxDancingTime;
            this.dancingTime = maxDancingTime;
        }

        @Override
        public boolean canUse() {
            if (Satyr.this.isDancing() && Satyr.this.dancingAround != null) {
                this.targetDirection = getClosestDirection();
                this.targetPos = Vec3.atBottomCenterOf(dancingAround.relative(this.targetDirection));
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            if (targetPos != null) {
                Satyr.this.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, moveSpeed);
                //targetDirection = getClosestDirection();
                dancingTime = dancingTimeout;
                obstructedTime = obstructedTimeout;
            }
        }

        @Override
        public boolean canContinueToUse() {
            boolean isCampfireValid = true;
            if (Satyr.this.tickCount % 15 == 1) {
                isCampfireValid = Satyr.this.wantsToDanceAround(Satyr.this.dancingAround);
            }
            return isCampfireValid && this.targetPos != null && Satyr.this.getTarget() == null && Satyr.this.hurtTime == 0;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.dancingTime-- > 0 && this.obstructedTime-- > 0) {
                // if we're close to the targetPos, update targetPos and path
                if (isNearTarget(1.26D)) {
                    this.updateTarget();
                    if (Satyr.this.isOnGround()) {
                        Satyr.this.jumpFromGround();
                    }
                    Satyr.this.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, moveSpeed);
                    this.obstructedTime = obstructedTimeout;
                }
            } else {
                stop();
            }
        }

        @Override
        public void stop() {
            // move out of the way
            if (Satyr.this.dancingAround != null && dancingAround.closerThan(Satyr.this.blockPosition(), 4.0D)) {
                final Vec3 vec = LandRandomPos.getPosAway(Satyr.this, 4, 4, Vec3.atBottomCenterOf(Satyr.this.dancingAround));
                if (vec != null) {
                    Satyr.this.getNavigation().moveTo(vec.x(), vec.y(), vec.z(), this.moveSpeed);
                }
            } else {
                Satyr.this.getNavigation().stop();
            }
            // reset values
            Satyr.this.dancingAround = null;
            this.targetPos = null;
            this.dancingTime = dancingTimeout;
            this.obstructedTime = obstructedTimeout;
            Satyr.this.setDancing(false);
        }

        /**
         * Checks if a campfire has been found, and if so, updates
         * which block the entity should path toward
         *
         * @return whether there is now a targetPos to move toward
         **/
        private boolean updateTarget() {
            if (Satyr.this.dancingAround != null) {
                this.targetDirection = this.targetDirection.getClockWise();
                this.targetPos = Vec3.atBottomCenterOf(dancingAround.relative(this.targetDirection));
                return true;
            }
            return false;
        }

        /**
         * Checks each possible direction offset from a campfire
         *
         * @return the Direction offset that is closest to the entity
         **/
        private Direction getClosestDirection() {
            Direction dClosest = Direction.NORTH;
            final Vec3 curPos = Satyr.this.position();
            double dMin = 100.0D;
            if (Satyr.this.dancingAround != null) {
                for (final Direction dir : Direction.Plane.HORIZONTAL) {
                    final BlockPos dPos = dancingAround.relative(dir);
                    final Vec3 dVec = Vec3.atBottomCenterOf(dPos);
                    final double dSq = curPos.distanceToSqr(dVec);
                    if (dSq < dMin) {
                        dClosest = dir;
                        dMin = dSq;
                    }
                }
            }
            return dClosest;
        }

        private boolean isNearTarget(final double distance) {
            Vec3 position = Satyr.this.position();
            return this.targetPos != null &&
                    this.targetPos.distanceToSqr(position.x(), targetPos.y(), position.z()) < distance * distance;
        }
    }

    /**
     * Used to move the entity to a lit campfire and begin dancing
     */
    class StartDancingGoal extends MoveToBlockGoal {
        protected final int chance;

        public StartDancingGoal(final double speed, final int searchRange, final int verticalSearchRange, final int chanceIn) {
            super(Satyr.this, speed, searchRange, verticalSearchRange);
            chance = chanceIn;
        }

        @Override
        public boolean canUse() {
            return Satyr.this.getTarget() == null && Satyr.this.isIdleState() && super.canUse();
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            BlockPos p;
            // checks if the given block is within 1 block of a campfire
            for (final Direction d : Direction.Plane.HORIZONTAL) {
                p = pos.relative(d, 1);
                if (Satyr.this.wantsToDanceAround(p)) {
                    Satyr.this.dancingAround = p;
                    return true;
                }
            }
            return false;
        }

        @Override
        public double acceptedDistance() {
            return 2.1D;
        }

        @Override
        public void tick() {
            if (this.isReachedTarget()) {
                // check surrounding blocks to find the campfire
                if (Satyr.this.dancingAround != null && Satyr.this.wantsToDanceAround(Satyr.this.dancingAround)) {
                    Satyr.this.setDancing(true);
                } else {
                    Satyr.this.dancingAround = null;
                    this.nextStartTick = this.nextStartTick(Satyr.this);
                }
            }
            super.tick();
        }

        @Override
        protected int nextStartTick(PathfinderMob entity) {
            return 200 + entity.getRandom().nextInt(chance);
        }
    }

    /**
     * Used to move the entity to an unlit campfire, then light it
     */
    class LightCampfireGoal extends MoveToBlockGoal {
        protected final int maxLightCampfireTime;
        protected final int chance;

        protected int lightCampfireTimer;
        protected BlockPos lightingFireAt = null;

        public LightCampfireGoal(double speed, int searchLength, int radius, int maxLightTime, int chanceIn) {
            super(Satyr.this, speed, searchLength, radius);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
            this.maxLightCampfireTime = maxLightTime;
            this.lightCampfireTimer = 0;
            this.chance = chanceIn;
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos) {
            // checks if the given block is within 1 block of an unlit campfire
            BlockPos p;
            for (final Direction d : Direction.Plane.HORIZONTAL) {
                p = pos.relative(d, 1);
                if (IS_CAMPFIRE.test(level.getBlockState(p), false)
                        && !Satyr.this.level.isRainingAt(p)) {
                    this.lightingFireAt = p;
                    return true;
                }
            }
            return false;
        }

        @Override
        public double acceptedDistance() {
            return 2.1D;
        }

        @Override
        public boolean canUse() {
            return Satyr.this.isIdleState()
                    && Satyr.this.getTarget() == null
                    && Satyr.this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
                    && super.canUse();
        }

        @Override
        public void start() {
            this.lightCampfireTimer = maxLightCampfireTime;
            super.start();
        }

        @Override
        public void tick() {
            if (this.isReachedTarget() && this.lightingFireAt != null) {
                Satyr.this.getLookControl().setLookAt(Vec3.atBottomCenterOf(this.lightingFireAt));
                Satyr.this.setPose(Pose.CROUCHING);
                // attempt to light campfire or make flint and steel sounds until ready
                if (this.lightCampfireTimer-- <= 0) {
                    this.lightCampfire(this.lightingFireAt);
                    Satyr.this.setPose(Pose.STANDING);
                    this.lightingFireAt = null;
                } else {
                    if (Satyr.this.getRandom().nextInt(12) == 0) {
                        Satyr.this.playSound(SoundEvents.FLINTANDSTEEL_USE, 1.0F, 1.0F);
                        Satyr.this.swing(InteractionHand.MAIN_HAND, true);
                    }
                }
            }
            super.tick();
        }

        @Override
        protected int nextStartTick(PathfinderMob entity) {
            return 200 + entity.getRandom().nextInt(chance) + maxLightCampfireTime;
        }

        @Override
        public void stop() {
            super.stop();
            if (Satyr.this.getPose() == Pose.CROUCHING) {
                Satyr.this.setPose(Pose.STANDING);
            }
            this.lightingFireAt = null;
        }

        protected boolean lightCampfire(final BlockPos pos) {
            if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(Satyr.this.level, Satyr.this)) {
                final BlockState state = Satyr.this.level.getBlockState(pos);
                if (IS_CAMPFIRE.test(state, false)) {
                    Satyr.this.playSound(SoundEvents.FLINTANDSTEEL_USE, 1.0F, 1.0F);
                    Satyr.this.level.setBlock(pos, state.setValue(CampfireBlock.LIT, Boolean.TRUE), 2);
                    Satyr.this.swing(InteractionHand.MAIN_HAND, true);
                    return true;
                }
            }
            return false;
        }
    }

    public static class GroupData implements SpawnGroupData {
        public final net.minecraft.world.entity.animal.horse.Variant variant;

        public GroupData(Variant color) {
            this.variant = color;
        }
    }
}
