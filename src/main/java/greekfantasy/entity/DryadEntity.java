package greekfantasy.entity;

import com.google.common.collect.ImmutableMap;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.EffectGoal;
import greekfantasy.entity.ai.FindBlockGoal;
import greekfantasy.util.BiomeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.ResetAngerGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class DryadEntity extends CreatureEntity implements IAngerable {

    protected static final DataParameter<String> DATA_VARIANT = EntityDataManager.defineId(DryadEntity.class, DataSerializers.STRING);
    protected static final String KEY_VARIANT = "Variant";
    protected static final String KEY_TREE_POS = "Tree";
    protected static final String KEY_HIDING = "HidingTime";

    protected DryadEntity.Variant variant = DryadEntity.Variant.OAK;

    protected Optional<BlockPos> treePos = Optional.empty();

    protected static final IOptionalNamedTag<Item> DRYAD_TRADES = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "dryad_trade"));
    protected Optional<PlayerEntity> tradingPlayer = Optional.empty();

    protected static final RangedInteger ANGER_RANGE = TickRangeConverter.rangeOfSeconds(4, 10);
    protected int angerTime;
    protected UUID angerTarget;

    // whether the entity is pathing to a tree
    protected boolean isGoingToTree;
    // number of ticks the entity has been hiding
    protected int hidingTime;

    public DryadEntity(final EntityType<? extends DryadEntity> type, final World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(2, new DryadEntity.TradeGoal(50 + random.nextInt(20)));
        this.goalSelector.addGoal(3, new DryadEntity.FindTreeGoal(8, 28));
        this.goalSelector.addGoal(4, new DryadEntity.HideGoal(640));
        this.goalSelector.addGoal(5, new DryadEntity.GoToTreeGoal(0.9F, 320));
        this.goalSelector.addGoal(6, new EffectGoal<>(this, () -> Effects.REGENERATION, 60, 120, 0, 1,
                EffectGoal.randomPredicate(400).and(e -> ((DryadEntity) e).isHiding())));
        this.goalSelector.addGoal(7, new AvoidEntityGoal<>(this, SatyrEntity.class, 10.0F, 1.2D, 1.1D, (entity) -> !this.isHiding() && !this.isGoingToTree && !this.tradingPlayer.isPresent()));
        this.goalSelector.addGoal(8, new DryadEntity.WalkingGoal(0.8F, 140));
        this.goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetAngerGoal<>(this, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_VARIANT, DryadEntity.Variant.OAK.getSerializedName());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // if entity has a tree position, check if it no longer exists
        if (this.tickCount % 28 == 0 && treePos.isPresent() && !isTreeAt(getCommandSenderWorld(), treePos.get(), getVariant().getLogs())) {
            // if entity was hiding, exit the tree
            this.tryExitTree();
            // update tree pos
            this.setTreePos(Optional.empty());
        }

        // anger timer
        if (!this.level.isClientSide()) {
            this.updatePersistentAnger((ServerWorld) this.level, true);
        }
    }

    @Override
    public void move(final MoverType type, final Vector3d vec) {
        super.move(type, vec);
        // copied from Vex code
        checkInsideBlocks();
    }

    @Override
    public void tick() {
        // determine how close the entity is to the tree
        final boolean isHidingInTree = isHiding() && this.isWithinDistanceOfTree(2.05D) && getNavigation().isDone();
        // set clip and gravity values
        if (isHidingInTree) {
            this.noPhysics = true;
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
            // snap to the tree's position if close enough
            final Optional<Vector3d> treeVec = getTreeVec();
            this.setPos(treeVec.get().x(), treeVec.get().y(), treeVec.get().z());
        }
        // super method
        super.tick();
        // reset values
        this.setNoGravity(isHidingInTree);
        this.noPhysics = false;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        // immune to suffocation while hiding (presumably in a tree)
        if (source == DamageSource.IN_WALL) {
            return !this.level.getBlockState(this.blockPosition().above()).is(this.getVariant().getLogs());
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean doHurtTarget(final Entity entity) {
        if (super.doHurtTarget(entity)) {
            // reset anger
            if (entity.getUUID().equals(this.getPersistentAngerTarget())) {
                this.setPersistentAngerTarget(null);
            }
            return true;
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putString(KEY_VARIANT, this.getEntityData().get(DATA_VARIANT));
        this.addPersistentAngerSaveData(compound);
        if (treePos.isPresent()) {
            compound.putInt(KEY_TREE_POS + ".x", treePos.get().getX());
            compound.putInt(KEY_TREE_POS + ".y", treePos.get().getY());
            compound.putInt(KEY_TREE_POS + ".z", treePos.get().getZ());
        }
        compound.putInt(KEY_HIDING, hidingTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(getVariantByName(compound.getString(KEY_VARIANT)));
        this.readPersistentAngerSaveData((ServerWorld) this.level, compound);
        if (compound.contains(KEY_TREE_POS + ".x")) {
            final int x = compound.getInt(KEY_TREE_POS + ".x");
            final int y = compound.getInt(KEY_TREE_POS + ".y");
            final int z = compound.getInt(KEY_TREE_POS + ".z");
            this.setTreePos(Optional.of(new BlockPos(x, y, z)));
        }
        this.hidingTime = compound.getInt(KEY_HIDING);
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
                                           @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        final DryadEntity.Variant variant;
        if (reason == SpawnReason.COMMAND || reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER || reason == SpawnReason.DISPENSER) {
            variant = DryadEntity.Variant.getRandom(worldIn.getRandom());
        } else {
            variant = DryadEntity.Variant.getForBiome(worldIn.getBiomeName(this.blockPosition()));
        }
        this.setVariant(variant);
        return data;
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return this.getVariant().getDeathLootTable();
    }

    // IAngerable methods

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_RANGE.randomValue(this.random));
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

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !treePos.isPresent() && this.tickCount > 4800;
    }

    // Trade methods

    @Override
    protected ActionResultType mobInteract(final PlayerEntity player, final Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // check if the tradingPlayer is holding a trade item and the entity is not already trading
        if (!this.level.isClientSide() && this.level instanceof ServerWorld && !this.isAggressive() && !this.tradingPlayer.isPresent()
                && this.getMainHandItem().isEmpty() && !stack.isEmpty() && getTradeTag().contains(stack.getItem())) {
            // check if the tradingPlayer is eligible to trade
            if (canPlayerTrade(player)) {
                // initiate trading
                this.setTradingPlayer(player);
                // take the item from the tradingPlayer
                this.setItemInHand(Hand.MAIN_HAND, new ItemStack(stack.getItem()));
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                player.setItemInHand(hand, stack);
                return ActionResultType.CONSUME;
            } else {
                // spawn particles
                ((ServerWorld)this.level).sendParticles(ParticleTypes.ANGRY_VILLAGER, this.getX(), this.getEyeY(), this.getZ(), 4, 0, 0, 0, 0);
            }
        }

        return super.mobInteract(player, hand);
    }

    /**
     * @param player the player
     * @return true if the given player is allowed to trade with this entity
     */
    public boolean canPlayerTrade(final PlayerEntity player) {
        if (player != null && player != this.getTarget()) {
            return false;
        }
        // allow trading if above cases do not apply
        return true;
    }

    public void setTradingPlayer(final PlayerEntity player) {
        this.tradingPlayer = Optional.ofNullable(player);
    }

    /**
     * @return an Item Tag of items to accept from the player while trading
     **/
    public IOptionalNamedTag<Item> getTradeTag() {
        return DRYAD_TRADES;
    }

    public ResourceLocation getTradeLootTable() {
        return this.getVariant().getTradeLootTable();
    }

    protected List<ItemStack> getTradeResult(final Optional<PlayerEntity> player, final ItemStack tradeItem) {
        LootTable loottable = this.level.getServer().getLootTables().get(this.getTradeLootTable());
        return loottable.getRandomItems(new LootContext.Builder((ServerWorld) this.level)
                .withRandom(this.level.random)
                .withParameter(LootParameters.THIS_ENTITY, this)
                .withParameter(LootParameters.ORIGIN, this.position())
                .withParameter(LootParameters.TOOL, tradeItem)
                .create(LootParameterSets.PIGLIN_BARTER));
    }

    /**
     * Performs a trade by depleting the tradeItem and creating a resultItem
     *
     * @param player    the player
     * @param tradeItem the item offered by the player
     */
    public void trade(final Optional<PlayerEntity> player, final ItemStack tradeItem) {
        // drop trade results as item entities
        getTradeResult(player, tradeItem).forEach(i -> spawnAtLocation(i, 1.2F));
        // shrink/remove held item
        tradeItem.shrink(1);
        this.setItemInHand(Hand.MAIN_HAND, tradeItem);
        if (tradeItem.getCount() <= 0) {
            this.setTradingPlayer(null);
        }
        // spawn xp orb
        if (random.nextInt(3) == 0) {
            this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.getX(), this.getY(), this.getZ(), 1 + random.nextInt(2)));
        }
        // send packet to spawn particles
        ((ServerWorld)this.level).sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getEyeY(), this.getZ(), 4, 0, 0, 0, 0);
    }

    // Variant methods

    public void setVariant(final DryadEntity.Variant variantIn) {
        this.variant = variantIn;
        this.getEntityData().set(DATA_VARIANT, variantIn.getSerializedName());
    }

    public DryadEntity.Variant getVariant() {
        return variant;
    }

    public DryadEntity.Variant getVariantByName(final String name) {
        return DryadEntity.Variant.getByName(name);
    }

    public void onSyncedDataUpdated(DataParameter<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(DATA_VARIANT)) {
            this.variant = getVariantByName(this.getEntityData().get(DATA_VARIANT));
        }
    }

    // Tree methods

    public Optional<BlockPos> getTreePos() {
        return treePos;
    }

    public Optional<Vector3d> getTreeVec() {
        return treePos.isPresent() ? Optional.of(new Vector3d(treePos.get().getX() + 0.5D, treePos.get().getY() + 1.0D, treePos.get().getZ() + 0.5D)) : Optional.empty();
    }

    public boolean isHiding() {
        return hidingTime > 0;
    }

    public void setHiding(final boolean hiding) {
        hidingTime = hiding ? 1 : 0;
    }

    public void setTreePos(final Optional<BlockPos> pos) {
        treePos = pos;
        if (pos.isPresent()) {
            this.restrictTo(pos.get(), (int) (this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()));
        } else {
            this.restrictTo(BlockPos.ZERO, -1);
        }
    }

    /**
     * Checks if the given position is a dirt or other plant-sustaining
     * block that is underneath a full-size tree.
     *
     * @param worldIn the world
     * @param pos     the block pos beneath the tree
     * @param logs    the blocks that are considered logs
     * @return if this block pos is supporting a likely tree
     **/
    public static boolean isTreeAt(final IWorldReader worldIn, final BlockPos pos, final ITag<Block> logs) {
        // a "tree" is considered two log blocks on top of a dirt block (or other plant-sustaining block)
        return worldIn.getBlockState(pos).canSustainPlant(worldIn, pos, Direction.UP, (IPlantable) Blocks.OAK_SAPLING)
                && worldIn.getBlockState(pos.above(1)).is(logs) && worldIn.getBlockState(pos.above(2)).is(logs);
    }

    /**
     * Attempt to exit the tree, if the entity
     * is inside a tree. Also resets hiding values.
     *
     * @return if a path was set successfully
     **/
    public boolean tryExitTree() {
        this.isGoingToTree = false;
        this.setHiding(false);
        if (this.treePos.isPresent() && this.isWithinDistanceOfTree(2.0D)) {
            if (this.getNavigation().isDone()) {
                // choose several random positions to check
                int radius = 2;
                for (int i = 0; i < 10; i++) {
                    double x = this.getX() + random.nextInt(radius * 2) - radius;
                    double y = this.getY() + random.nextInt(radius) - radius / 2;
                    double z = this.getZ() + random.nextInt(radius * 2) - radius;
                    // try to path to the position
                    if (this.getNavigation().moveTo(x, y, z, 1.0D)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param dis the maximum distance to the tree
     * @return whether the entity is near its tree
     **/
    public boolean isWithinDistanceOfTree(final double dis) {
        final Optional<Vector3d> treeVec = getTreeVec();
        if (!treeVec.isPresent()) {
            return false;
        }
        return treeVec.get().closerThan(position(), dis);
    }

    class HideGoal extends Goal {

        private final int maxHidingTime;
        private final int maxCooldown;
        private int cooldown;

        public HideGoal(final int maxHidingTimeIn) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
            this.maxHidingTime = maxHidingTimeIn;
            this.maxCooldown = 120;
        }

        @Override
        public boolean canUse() {
            if (cooldown > 0) {
                cooldown--;
            } else if (DryadEntity.this.treePos.isPresent() && DryadEntity.this.isWithinDistanceOfTree(1.5D) && DryadEntity.this.getTarget() == null) {
                return isTreeAt(DryadEntity.this.getCommandSenderWorld(), DryadEntity.this.treePos.get(), DryadEntity.this.getVariant().getLogs());
            }
            return false;
        }

        @Override
        public void start() {
            DryadEntity.this.setHiding(true);
            DryadEntity.this.isGoingToTree = false;
        }

        @Override
        public void tick() {
            super.tick();
            DryadEntity.this.getNavigation().stop();
            if (DryadEntity.this.hidingTime++ > maxHidingTime && DryadEntity.this.getRandom().nextInt(100) == 0) {
                stop();
            }
        }

        @Override
        public void stop() {
            DryadEntity.this.tryExitTree();
            cooldown = maxCooldown;
        }
    }

    class FindTreeGoal extends FindBlockGoal {

        public FindTreeGoal(int radius, int cooldown) {
            super(DryadEntity.this, radius, cooldown);
        }

        @Override
        public boolean canUse() {
            return (!DryadEntity.this.getTreePos().isPresent() || DryadEntity.this.getRandom().nextInt(500) == 0) && super.canUse();
        }

        @Override
        public boolean isTargetBlock(IWorldReader worldIn, BlockPos pos) {
            // valid block if there is a tree here and it has not been occupied by another dryad
            return isTreeAt(worldIn, pos, DryadEntity.this.getVariant().getLogs())
                    && DryadEntity.this.getCommandSenderWorld().getEntitiesOfClass(DryadEntity.class, new AxisAlignedBB(pos.above()).inflate(0.5D)).isEmpty();
        }

        @Override
        public void onFoundBlock(final IWorldReader worldIn, final BlockPos target) {
            DryadEntity.this.setTreePos(Optional.of(target));
        }
    }

    class GoToTreeGoal extends Goal {

        private final double speed;
        private final int chance;

        public GoToTreeGoal(final double speedIn, final int chanceIn) {
            setFlags(EnumSet.of(Goal.Flag.MOVE));
            speed = speedIn;
            chance = chanceIn;
        }

        @Override
        public boolean canUse() {
            return !DryadEntity.this.isHiding() && DryadEntity.this.getTreePos().isPresent()
                    && DryadEntity.this.getTarget() == null && DryadEntity.this.getRandom().nextInt(chance) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return DryadEntity.this.isGoingToTree && DryadEntity.this.getTreePos().isPresent()
                    && !DryadEntity.this.getNavigation().isDone() && DryadEntity.this.getTarget() == null;
        }

        @Override
        public void start() {
            DryadEntity.this.isGoingToTree = true;
            final Optional<Vector3d> vec = DryadEntity.this.getTreeVec();
            DryadEntity.this.getNavigation().moveTo(vec.get().x(), vec.get().y(), vec.get().z(), this.speed);
        }

        @Override
        public void stop() {
            DryadEntity.this.getNavigation().stop();
            DryadEntity.this.isGoingToTree = false;
        }
    }

    class WalkingGoal extends RandomWalkingGoal {

        public WalkingGoal(double speed, final int chance) {
            super(DryadEntity.this, speed, chance);
        }

        @Override
        public boolean canUse() {
            return !DryadEntity.this.isHiding() && !DryadEntity.this.isGoingToTree && DryadEntity.this.getTarget() == null
                    && super.canUse();
        }
    }

    class TradeGoal extends Goal {

        protected final int maxThinkingTime;
        protected int thinkingTime;

        public TradeGoal(final int maxThinkingTimeIn) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            maxThinkingTime = maxThinkingTimeIn;
            thinkingTime = 0;
        }

        @Override
        public boolean canUse() {
            return !DryadEntity.this.isAggressive()
                    && !DryadEntity.this.getMainHandItem().isEmpty()
                    && DryadEntity.this.getTradeTag().contains(DryadEntity.this.getMainHandItem().getItem());
        }

        @Override
        public boolean canContinueToUse() {
            return thinkingTime > 0 && thinkingTime <= maxThinkingTime && canUse();
        }

        @Override
        public void start() {
            thinkingTime = 1;
        }

        @Override
        public void tick() {
            // look at the tradingPlayer
            if (DryadEntity.this.tradingPlayer.isPresent()) {
                DryadEntity.this.lookControl.setLookAt(DryadEntity.this.tradingPlayer.get(), DryadEntity.this.getMaxHeadYRot(), 100.0F);
            }
            // stop moving and look down
            DryadEntity.this.getNavigation().stop();
            DryadEntity.this.getLookControl().setLookAt(DryadEntity.this.getEyePosition(1.0F).add(0.0D, -0.25D, 0.0D));
            // if enough time has elapsed, commence the trade
            if (thinkingTime++ >= maxThinkingTime) {
                trade(DryadEntity.this.tradingPlayer, DryadEntity.this.getMainHandItem());
                stop();
            }
        }

        @Override
        public void stop() {
            thinkingTime = 0;
        }
    }

    public static class Variant implements IStringSerializable {
        public static final Variant ACACIA = new Variant("acacia", () -> Blocks.ACACIA_SAPLING);
        public static final Variant BIRCH = new Variant("birch", () -> Blocks.BIRCH_SAPLING);
        public static final Variant DARK_OAK = new Variant("dark_oak", () -> Blocks.DARK_OAK_SAPLING);
        public static final Variant JUNGLE = new Variant("jungle", () -> Blocks.JUNGLE_SAPLING);
        public static final Variant OAK = new Variant("oak", () -> Blocks.OAK_SAPLING);
        public static final Variant SPRUCE = new Variant("spruce", () -> Blocks.SPRUCE_SAPLING);
        public static final Variant OLIVE = new Variant(GreekFantasy.MODID, "olive", "dryad", "logs", () -> GFRegistry.BlockReg.OLIVE_SAPLING);

        public static ImmutableMap<String, Variant> OVERWORLD = ImmutableMap.<String, Variant>builder()
                .put(ACACIA.name, ACACIA).put(BIRCH.name, BIRCH).put(DARK_OAK.name, DARK_OAK)
                .put(JUNGLE.name, JUNGLE).put(OAK.name, OAK).put(SPRUCE.name, SPRUCE)
                .put(OLIVE.name, OLIVE)
                .build();

        protected final String name;
        protected final Supplier<Block> sapling;
        protected final ResourceLocation tag;
        protected final ResourceLocation deathLootTable;
        protected final ResourceLocation tradeLootTable;

        protected Variant(final String nameIn, final Supplier<Block> saplingIn) {
            this("minecraft", nameIn, "dryad", "logs", saplingIn);
        }

        protected Variant(final String modid, final String nameIn, final String entityIn, final String tagSuffixIn, final Supplier<Block> saplingIn) {
            name = nameIn;
            sapling = saplingIn;
            tag = new ResourceLocation(modid, name + "_" + tagSuffixIn);
            deathLootTable = new ResourceLocation(GreekFantasy.MODID, "entities/" + entityIn + "/" + name);
            tradeLootTable = new ResourceLocation(GreekFantasy.MODID, "gameplay/" + entityIn + "_trade");
        }

        public static Variant getForBiome(final Optional<RegistryKey<Biome>> biome) {
            return BiomeHelper.getDryadVariantForBiome(biome);
        }

        public static Variant getRandom(final Random rand) {
            int len = OVERWORLD.size();
            return len > 0 ? OVERWORLD.entrySet().asList().get(rand.nextInt(len)).getValue() : OAK;
        }

        public static Variant getByName(final String n) {
            // check the given name in overworld and nether maps
            if (n != null && !n.isEmpty()) {
                return OVERWORLD.getOrDefault(n, OAK);
            }
            // defaults to OAK
            return OAK;
        }

        public ITag<Block> getLogs() {
            return Optional.ofNullable(BlockTags.getAllTags().getTag(tag)).orElse(BlockTags.LOGS);
        }

        public BlockState getSapling() {
            return sapling.get().defaultBlockState();
        }

        public ResourceLocation getDeathLootTable() {
            return deathLootTable;
        }

        public ResourceLocation getTradeLootTable() {
            return tradeLootTable;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
