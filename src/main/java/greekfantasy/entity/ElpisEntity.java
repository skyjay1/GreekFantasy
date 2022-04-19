package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ElpisEntity extends CreatureEntity implements IFlyingAnimal {
    private static final DataParameter<Byte> STATE = EntityDataManager.defineId(ElpisEntity.class, DataSerializers.BYTE);
    private static final String KEY_STATE = "ElpisState";
    private static final String KEY_HOME = "ElpisHome";
    private static final String KEY_AGE = "ElpisAge";
    private static final String KEY_DESPAWN_TIMER = "DespawnTimer";

    protected static final IOptionalNamedTag<Item> ELPIS_TRADE = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "elpis_trade"));

    private static final Predicate<Item> TRADE_ITEM = i -> ELPIS_TRADE.contains(i);
    private static final Supplier<ItemStack> TRADE_RESULT = () -> new ItemStack(GFRegistry.ICHOR);
    public static final int wanderDistance = 8;
    private static final int maxAge = 4800;
    private static final int maxDespawnTime = 40;

    // byte flags to use with the STATE data parameter
    protected static final byte STATE_NONE = 5;
    protected static final byte STATE_TRADING = 6;
    protected static final byte STATE_DESPAWNING = 7;
    // bytes to use in World#setEntityState
    private static final byte DESPAWN_CLIENT = 10;

    private int despawnTime;
    private int age;

    public ElpisEntity(final EntityType<? extends CreatureEntity> type, final World world) {
        super(type, world);
        this.moveControl = new FlyingMovementController(this, 20, true);
        this.setPathfindingMalus(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0F);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, 0.5D)
                .add(Attributes.FLYING_SPEED, 0.4D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, Byte.valueOf(STATE_NONE));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new ElpisEntity.DoNothingGoal());
        this.goalSelector.addGoal(2, new ElpisEntity.TradeGoal(TRADE_RESULT, 80));
        this.goalSelector.addGoal(3, new ElpisEntity.PanicGoal(1.0D));
        this.goalSelector.addGoal(4, new ElpisEntity.MoveRandomGoal(20, wanderDistance, 0.75D));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide() && random.nextInt(12) == 0) {
            spawnParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, true);
        }
        // update age
        ++age;
        // update despawn time
        if (despawnTime > 0) {
            if (despawnTime > (maxDespawnTime / 2) && random.nextInt(3) == 0) {
                spawnParticle(ParticleTypes.PORTAL, false);
            }
            if (despawnTime++ >= maxDespawnTime) {
                remove();
            }
        }

        if (this.isTrading()) {
            // spawn particles when trading
            spawnParticle(ParticleTypes.HAPPY_VILLAGER, false);
        } else if (TRADE_ITEM.test(this.getItemInHand(Hand.OFF_HAND).getItem())) {
            // update held item when done trading
            this.setItemInHand(Hand.OFF_HAND, ItemStack.EMPTY);
        }
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
    }

    @Override
    protected ActionResultType mobInteract(final PlayerEntity player, final Hand hand) { // processInteract
        ItemStack stack = player.getItemInHand(hand);
        if (this.isNoneState() && TRADE_ITEM.test(stack.getItem())) {
            this.setState(STATE_TRADING);
            // copy itemstack and set held item
            this.setItemInHand(Hand.OFF_HAND, new ItemStack(stack.getItem()));
            // reduce stack size
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            player.setItemInHand(hand, stack);
            return ActionResultType.CONSUME;
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

//  @Override
//  protected float getSoundPitch() { return 1.2F + rand.nextFloat() * 0.2F; }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, IWorldReader worldIn) {
        return worldIn.isEmptyBlock(pos) ? 10.0F : 0.0F;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected PathNavigator createNavigation(World worldIn) {
        FlyingPathNavigator flyingpathnavigator = new FlyingPathNavigator(this, worldIn);
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(false);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
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
    public void readAdditionalSaveData(CompoundNBT compound) {
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
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
                                           @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        spawnDataIn = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setLeftHanded(false);
        this.setState(STATE_NONE);
        return spawnDataIn;
    }

    public BlockPos getWanderCenter() {
        final BlockPos home = this.getRestrictCenter();
        return this.getRestrictRadius() > -1.0F && home != BlockPos.ZERO ? home : this.blockPosition();
    }

    protected void spawnParticle(final IParticleData particle, final boolean colored) {
        if (level.isClientSide()) {
            final double motion = 0.09D;
            final double radius = 0.25D;
            level.addParticle(particle,
                    this.getX() + (level.random.nextDouble() - 0.5D) * radius,
                    this.getEyeY() + (level.random.nextDouble() - 0.5D) * radius * 0.75D,
                    this.getZ() + (level.random.nextDouble() - 0.5D) * radius,
                    colored ? 1.0F : (level.random.nextDouble() - 0.5D) * motion,
                    colored ? 0.60F : (level.random.nextDouble() - 0.5D) * motion * 0.5D,
                    colored ? 0.92F : (level.random.nextDouble() - 0.5D) * motion);
        }
    }

    // state methods

    public void setState(final byte state) {
        this.getEntityData().set(STATE, state);
        if (state == STATE_DESPAWNING) {
            despawnTime = 1;
            if (!level.isClientSide()) {
                level.broadcastEntityEvent(this, DESPAWN_CLIENT);
            }
        }
    }

    public byte getState() {
        return this.getEntityData().get(STATE).byteValue();
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

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == DESPAWN_CLIENT) {
            setState(STATE_DESPAWNING);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getAlpha(final float partialTick) {
        final byte state = this.getState();
        switch (state) {
            case STATE_TRADING:
                return 1.0F;
            case STATE_DESPAWNING:
                return 1.0F - getDespawnPercent(partialTick);
            case STATE_NONE:
            default:
                final float minAlpha = 0.14F;
                final float cosAlpha = 0.5F + 0.5F * MathHelper.cos((this.getId() + this.tickCount + partialTick) * 0.025F);
                return MathHelper.clamp(cosAlpha, minAlpha, 1.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getDespawnPercent(final float partialTick) {
        return (float) despawnTime / (float) maxDespawnTime;
    }

    // Trading goal

    class TradeGoal extends Goal {

        final Supplier<ItemStack> result;
        final int duration;
        PlayerEntity player;
        int progress;

        public TradeGoal(final Supplier<ItemStack> resultStack, final int durationIn) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            result = resultStack;
            duration = durationIn;
        }

        @Override
        public boolean canUse() {
            player = ElpisEntity.this.level.getNearestPlayer(ElpisEntity.this, 8.0D);
            return player != null && ElpisEntity.this.isTrading();
        }

        @Override
        public void tick() {
            ElpisEntity.this.getLookControl().setLookAt(player, ElpisEntity.this.getMaxHeadYRot(), ElpisEntity.this.getMaxHeadXRot());
            ElpisEntity.this.getNavigation().stop();
            if (progress++ >= duration) {
                // finish trading and spawn an item
                final ItemEntity item = new ItemEntity(ElpisEntity.this.level, ElpisEntity.this.getX(),
                        ElpisEntity.this.getY(), ElpisEntity.this.getZ(), result.get());
                ElpisEntity.this.getCommandSenderWorld().addFreshEntity(item);
                ElpisEntity.this.playSound(SoundEvents.PLAYER_LEVELUP, 0.8F, 1.0F);
                // start despawning
                ElpisEntity.this.despawnTime = 1;
                ElpisEntity.this.setState(STATE_DESPAWNING);
            }
        }

        @Override
        public void stop() {
            if (ElpisEntity.this.isTrading()) {
                ElpisEntity.this.setState(STATE_NONE);
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
            return ElpisEntity.this.isDespawning();
        }
    }

    // TODO not doing anything...
    class PanicGoal extends net.minecraft.entity.ai.goal.PanicGoal {

        public PanicGoal(double speed) {
            super(ElpisEntity.this, speed);
        }

    }

    class MoveRandomGoal extends Goal {

        private final int chance;
        private final int radius;
        private final double speed;

        public MoveRandomGoal(final int chanceIn, final int radiusIn, final double speedIn) {
            setFlags(EnumSet.of(Goal.Flag.MOVE));
            chance = chanceIn;
            radius = radiusIn;
            speed = speedIn;
        }

        @Override
        public boolean canUse() {
            return (ElpisEntity.this.getNavigation().isDone() && ElpisEntity.this.random.nextInt(chance) == 0);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos pos = ElpisEntity.this.getWanderCenter();

            for (int checks = 0; checks < 3; checks++) {
                BlockPos posCheck = pos.offset(ElpisEntity.this.random.nextInt(radius * 2) - radius, ElpisEntity.this.random.nextInt(radius) - (radius / 2), ElpisEntity.this.random.nextInt(radius * 2) - radius);
                if (ElpisEntity.this.level.isEmptyBlock(posCheck)) {
                    ElpisEntity.this.getNavigation().moveTo(posCheck.getX() + 0.5D, posCheck.getY() + 0.5D, posCheck.getZ() + 0.5D, speed);
                    //ElpisEntity.this.moveController.setMoveTo(posCheck.getX() + 0.5D, posCheck.getY() + 0.5D, posCheck.getZ() + 0.5D, speed);
                    if (ElpisEntity.this.getTarget() == null) {
                        ElpisEntity.this.getLookControl().setLookAt(posCheck.getX() + 0.5D, posCheck.getY() + 0.5D, posCheck.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }
}
