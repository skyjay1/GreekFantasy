package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Supplier;

public class Elpis extends PathfinderMob implements FlyingAnimal {
    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Elpis.class, EntityDataSerializers.BYTE);
    private static final String KEY_STATE = "ElpisState";
    private static final String KEY_HOME = "ElpisHome";
    private static final String KEY_AGE = "ElpisAge";
    private static final String KEY_DESPAWN_TIMER = "DespawnTimer";

    protected static final TagKey<Item> ELPIS_TRADE = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "elpis_trade"));
    protected static final Supplier<ItemStack> TRADE_RESULT = () -> new ItemStack(GFRegistry.ItemReg.ICHOR.get());

    public static final int wanderDistance = 8;
    private static final int maxAge = 4800;
    private static final int maxDespawnTime = 40;

    // byte flags to use with the STATE data parameter
    protected static final byte STATE_NONE = 5;
    protected static final byte STATE_TRADING = 6;
    protected static final byte STATE_DESPAWNING = 7;
    // bytes to use in World#setEntityState
    private static final byte DESPAWN_EVENT = 10;
    private static final byte TRADE_SUCCESS_EVENT = 11;

    private int despawnTime;
    private int age;

    public Elpis(final EntityType<? extends Elpis> type, final Level world) {
        super(type, world);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.4D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, STATE_NONE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new Elpis.DoNothingGoal());
        this.goalSelector.addGoal(2, new Elpis.TradeGoal(TRADE_RESULT, 80));
        this.goalSelector.addGoal(3, new Elpis.PanicGoal(1.0D));
        this.goalSelector.addGoal(4, new Elpis.ElpisWanderGoal(20, 0.75D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // update age
        ++age;
        // update despawn time
        if (despawnTime > 0) {
            if (despawnTime++ >= maxDespawnTime) {
                discard();
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
        if (this.level.isClientSide() && random.nextInt(9) == 0) {
            // add particles
            final double radius = 0.25D;
            if (isTrading()) {
                // trading particles
                level.addParticle(ParticleTypes.INSTANT_EFFECT,
                        this.getX() + (level.random.nextDouble() - 0.5D) * radius,
                        this.getEyeY() + (level.random.nextDouble() - 0.5D) * radius * 0.75D,
                        this.getZ() + (level.random.nextDouble() - 0.5D) * radius,
                        0, 0, 0);
            } else {
                // ambient particles
                level.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                        this.getX() + (level.random.nextDouble() - 0.5D) * radius,
                        this.getEyeY() + (level.random.nextDouble() - 0.5D) * radius * 0.75D,
                        this.getZ() + (level.random.nextDouble() - 0.5D) * radius,
                        1.0F, 0.60F, 0.92F);
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isNoneState() && stack.is(ELPIS_TRADE)) {
            this.setState(STATE_TRADING);
            // copy itemstack and set held item
            this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(stack.getItem()));
            // reduce stack size
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            player.setItemInHand(hand, stack);
            return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.NOTE_BLOCK_CHIME;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    @Override
    public float getBrightness() {
        return Math.min(1.0F, super.getBrightness() + 0.5F);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn) {
        return worldIn.isEmptyBlock(pos) ? 10.0F : 0.0F;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(false);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getEntityData().get(STATE).byteValue());
        compound.putInt(KEY_AGE, age);
        compound.putInt(KEY_DESPAWN_TIMER, despawnTime);
        if (this.getRestrictCenter() != BlockPos.ZERO && this.getRestrictRadius() > -1.0F) {
            compound.putInt(KEY_HOME + ".x", getRestrictCenter().getX());
            compound.putInt(KEY_HOME + ".y", getRestrictCenter().getY());
            compound.putInt(KEY_HOME + ".z", getRestrictCenter().getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.getEntityData().set(STATE, compound.getByte(KEY_STATE));
        age = compound.getInt(KEY_AGE);
        despawnTime = compound.getInt(KEY_DESPAWN_TIMER);
        if (compound.contains(KEY_HOME + ".x")) {
            final int x = compound.getInt(KEY_HOME + ".x");
            final int y = compound.getInt(KEY_HOME + ".y");
            final int z = compound.getInt(KEY_HOME + ".z");
            this.restrictTo(new BlockPos(x, y, z), wanderDistance);
        }
    }

    @Override
    public boolean removeWhenFarAway(final double disToPlayer) {
        return this.isNoneState() && this.age > maxAge && disToPlayer > 12.0D;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setState(STATE_NONE);
        this.setLeftHanded(false);
        return spawnDataIn;
    }

    // state methods

    public void setState(final byte state) {
        this.getEntityData().set(STATE, state);
        if (state == STATE_DESPAWNING) {
            despawnTime = 1;
            if (!level.isClientSide()) {
                level.broadcastEntityEvent(this, DESPAWN_EVENT);
            }
        }
    }

    public byte getState() {
        return this.getEntityData().get(STATE);
    }

    public boolean isNoneState() {
        return getState() == STATE_NONE;
    }

    public boolean isTrading() {
        return getState() == STATE_TRADING;
    }

    public boolean isDespawning() {
        return getState() == STATE_DESPAWNING;
    }

    // Client methods

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case DESPAWN_EVENT:
                setState(STATE_DESPAWNING);
                break;
            case TRADE_SUCCESS_EVENT:
                // play sound
                playSound(SoundEvents.PLAYER_LEVELUP, 0.8F, 1.0F);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    public float getAlpha(final float partialTick) {
        if(this.isRemoved()) {
            return 0.0F;
        }
        final byte state = this.getState();
        switch (state) {
            case STATE_TRADING:
                return 1.0F;
            case STATE_DESPAWNING:
                return 1.0F - getDespawnPercent(partialTick);
            default:
                return 1.0F - Math.max(0.0F, (Mth.cos((this.tickCount + partialTick) * 0.08F) - 0.35F)) * 0.45F;
        }
    }

    public float getDespawnPercent(final float partialTick) {
        return (despawnTime - partialTick) / (float) maxDespawnTime;
    }

    @Override
    public boolean isFlying() {
        return level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).isAir();
    }

    // Trading goal

    class TradeGoal extends Goal {

        final Supplier<ItemStack> result;
        final int duration;
        Player player;
        int progress;

        public TradeGoal(final Supplier<ItemStack> resultStack, final int durationIn) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            result = resultStack;
            duration = durationIn;
        }

        @Override
        public boolean canUse() {
            player = Elpis.this.level.getNearestPlayer(Elpis.this, 8.0D);
            return player != null && Elpis.this.isTrading();
        }

        @Override
        public void tick() {
            Elpis.this.getLookControl().setLookAt(Elpis.this.getEyePosition(1.0F).add(0.0D, -0.125D, 0.0D));
            Elpis.this.getNavigation().stop();
            if (progress++ >= duration) {
                // determine target position
                Vec3 tradeTarget;
                if (player != null) {
                    tradeTarget = player.position();
                } else {
                    tradeTarget = LandRandomPos.getPos(Elpis.this, 4, 2);
                    if (null == tradeTarget) {
                        tradeTarget = Elpis.this.position();
                    }
                }
                final Vec3 tradeTargetPos = tradeTarget.add(0, 1, 0);
                // finish trading and spawn an item
                BehaviorUtils.throwItem(Elpis.this, TRADE_RESULT.get(), tradeTargetPos);
                Elpis.this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                // start despawning
                Elpis.this.setState(STATE_DESPAWNING);
                level.broadcastEntityEvent(Elpis.this, DESPAWN_EVENT);
            }
        }

        @Override
        public void stop() {
            if (Elpis.this.isTrading()) {
                Elpis.this.setState(STATE_NONE);
            }
            progress = 0;
        }
    }

    // Despawning goal

    class DoNothingGoal extends Goal {
        public DoNothingGoal() {
            this.setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return Elpis.this.isDespawning();
        }
    }

    class PanicGoal extends net.minecraft.world.entity.ai.goal.PanicGoal {

        public PanicGoal(double speed) {
            super(Elpis.this, speed);
        }

        @Override
        protected boolean findRandomPosition() {
            Vec3 vec3 = AirAndWaterRandomPos.getPos(Elpis.this, 5, 4, -2, random.nextInt(10) - 5, random.nextInt(10) - 5, (double) ((float) Math.PI / 2F));
            if (vec3 == null) {
                return false;
            } else {
                this.posX = vec3.x;
                this.posY = vec3.y;
                this.posZ = vec3.z;
                return true;
            }
        }
    }

    class ElpisWanderGoal extends Goal {

        protected final double speed;
        protected final int chance;

        ElpisWanderGoal(final int chance, final double speed) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.speed = speed;
            this.chance = Math.min(1, chance);
        }

        @Override
        public boolean canUse() {
            return Elpis.this.navigation.isDone()
                    && !Elpis.this.isTrading()
                    && Elpis.this.random.nextInt(chance) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return Elpis.this.navigation.isInProgress();
        }

        @Override
        public void start() {
            Vec3 vec3 = this.findPos();
            if (vec3 != null) {
                Elpis.this.navigation.moveTo(Elpis.this.navigation.createPath(new BlockPos(vec3), 1), speed);
            }

        }

        @Nullable
        private Vec3 findPos() {
            Vec3 targetPos;
            if (!Elpis.this.isWithinRestriction()) {
                Vec3 vec31 = Vec3.atCenterOf(Elpis.this.getRestrictCenter());
                targetPos = vec31.subtract(Elpis.this.position()).normalize();
            } else {
                targetPos = Elpis.this.getViewVector(0.0F);
            }

            Vec3 hoverVec = HoverRandomPos.getPos(Elpis.this, 8, 7, targetPos.x, targetPos.z, ((float) Math.PI / 2F), 3, 1);
            return hoverVec != null ? hoverVec : AirAndWaterRandomPos.getPos(Elpis.this, 8, 4, -2, targetPos.x, targetPos.z, Math.PI / 2D);
        }
    }
}
