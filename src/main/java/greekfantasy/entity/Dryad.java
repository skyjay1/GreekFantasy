package greekfantasy.entity;

import com.google.common.collect.ImmutableMap;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.FindBlockGoal;
import greekfantasy.entity.util.NymphVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class Dryad extends PathfinderMob implements NeutralMob {

    protected static final EntityDataAccessor<String> DATA_VARIANT = SynchedEntityData.defineId(Dryad.class, EntityDataSerializers.STRING);
    protected static final String KEY_VARIANT = "Variant";
    protected static final String KEY_TREE_POS = "Tree";
    protected static final String KEY_HIDING = "HidingTime";

    protected static final TagKey<Item> DRYAD_TRADES = ItemTags.create(new ResourceLocation(GreekFantasy.MODID, "dryad_trade"));

    private static final byte FINISH_TRADE_EVENT = 9;

    protected NymphVariant variant = Variant.OAK;
    protected BlockPos treePos = null;
    protected Player tradingPlayer = null;

    protected static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(4, 10);
    protected int angerTime;
    protected UUID angerTarget;

    // whether the entity is pathing to a tree
    protected boolean isGoingToTree;
    // number of ticks the entity has been hiding
    protected int hidingTime;

    public Dryad(final EntityType<? extends Dryad> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(2, new Dryad.TradeGoal(50 + random.nextInt(20)));
        this.goalSelector.addGoal(3, new Dryad.FindTreeGoal(8, 28));
        this.goalSelector.addGoal(4, new Dryad.HideGoal(640));
        this.goalSelector.addGoal(5, new Dryad.GoToTreeGoal(0.9F, 320));
        this.goalSelector.addGoal(7, new AvoidEntityGoal<>(this, Satyr.class, 10.0F, 1.2D, 1.1D, (entity) -> !this.isHiding() && !this.isGoingToTree && this.tradingPlayer != null));
        this.goalSelector.addGoal(8, new Dryad.RandomStrollWhenNotHidingGoal(0.8F, 140));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_VARIANT, Variant.OAK.getSerializedName());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // if entity has a tree position, check if it no longer exists
        if (this.tickCount % 28 == 0 && treePos != null && !isTreeAt(level, treePos, getVariant().getLogs())) {
            // if entity was hiding, exit the tree
            this.tryExitTree();
            this.isGoingToTree = false;
            this.setHiding(false);
            // update tree pos
            this.setTreePos(null);
        }

        // regeneration
        if (this.treePos != null && this.isHiding() && random.nextInt(400) == 1) {
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60));
        }

        // anger timer
        if (!this.level.isClientSide()) {
            this.updatePersistentAnger((ServerLevel) this.level, true);
        }
    }

    @Override
    public void move(final MoverType type, final Vec3 vec) {
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
            final Vec3 treeVec = getTreeVec();
            this.setPos(treeVec.x(), treeVec.y(), treeVec.z());
        }
        // super method
        super.tick();
        // reset values
        this.setNoGravity(isHidingInTree);
        this.noPhysics = false;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        // immune to suffocation while hiding
        if (source == DamageSource.IN_WALL && this.isHiding()) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean doHurtTarget(final Entity entity) {
        if (super.doHurtTarget(entity)) {
            // reset anger after successful attack
            if (entity.getUUID().equals(this.getPersistentAngerTarget())) {
                this.setPersistentAngerTarget(null);
            }
            return true;
        }
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString(KEY_VARIANT, this.getEntityData().get(DATA_VARIANT));
        this.addPersistentAngerSaveData(compound);
        if (treePos != null) {
            compound.putInt(KEY_TREE_POS + ".x", treePos.getX());
            compound.putInt(KEY_TREE_POS + ".y", treePos.getY());
            compound.putInt(KEY_TREE_POS + ".z", treePos.getZ());
        }
        compound.putInt(KEY_HIDING, hidingTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(getVariantByName(compound.getString(KEY_VARIANT)));
        this.readPersistentAngerSaveData(this.level, compound);
        if (compound.contains(KEY_TREE_POS + ".x")) {
            final int x = compound.getInt(KEY_TREE_POS + ".x");
            final int y = compound.getInt(KEY_TREE_POS + ".y");
            final int z = compound.getInt(KEY_TREE_POS + ".z");
            this.setTreePos(new BlockPos(x, y, z));
        }
        this.hidingTime = compound.getInt(KEY_HIDING);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType mobType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, mobType, spawnDataIn, dataTag);
        final NymphVariant variant;
        if (mobType == MobSpawnType.COMMAND || mobType == MobSpawnType.SPAWN_EGG || mobType == MobSpawnType.SPAWNER || mobType == MobSpawnType.DISPENSER) {
            variant = getRandomVariant();
        } else {
            variant = getVariantForBiome(worldIn.getBiome(this.blockPosition()));
        }
        this.setVariant(variant);
        return data;
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return this.getVariant().getDeathLootTable();
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

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return null == treePos && this.tickCount > 4800;
    }

    // Trade methods

    @Override
    protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // check if the tradingPlayer is holding a trade item and the entity is not already trading
        if (!this.level.isClientSide() && this.level instanceof ServerLevel && !this.isAggressive() && !isTrading()
                && this.getMainHandItem().isEmpty() && !stack.isEmpty() && stack.is(getTradeTag())) {
            // check if the tradingPlayer is eligible to trade
            if (canPlayerTrade(player)) {
                // initiate trading
                this.setTradingPlayer(player);
                // take the item from the tradingPlayer
                this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(stack.getItem()));
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                player.setItemInHand(hand, stack);
                return InteractionResult.CONSUME;
            } else {
                // spawn particles
                ((ServerLevel) this.level).sendParticles(ParticleTypes.ANGRY_VILLAGER, this.getX(), this.getEyeY(), this.getZ(), 4, 0, 0, 0, 0);
            }
        }

        return InteractionResult.sidedSuccess(!level.isClientSide());
    }

    /**
     * @param player the player
     * @return true if the given player is allowed to trade with this entity
     */
    public boolean canPlayerTrade(final Player player) {
        return player != null && player != this.getTarget();
    }

    public void setTradingPlayer(@Nullable final Player player) {
        this.tradingPlayer = player;
    }

    public boolean isTrading() {
        return this.tradingPlayer != null;
    }

    /**
     * @return an Item Tag of items to accept from the player while trading
     **/
    public TagKey<Item> getTradeTag() {
        return DRYAD_TRADES;
    }

    public ResourceLocation getTradeLootTable() {
        return this.getVariant().getTradeLootTable();
    }

    protected List<ItemStack> getTradeResult(@Nullable final Player player, final ItemStack tradeItem) {
        LootTable loottable = this.level.getServer().getLootTables().get(this.getTradeLootTable());
        return loottable.getRandomItems(new LootContext.Builder((ServerLevel) this.level)
                .withRandom(this.level.random)
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.TOOL, tradeItem)
                .create(LootContextParamSets.PIGLIN_BARTER));
    }

    /**
     * Performs a trade by depleting the tradeItem and creating a resultItem
     *
     * @param player    the player
     * @param tradeItem the item offered by the player
     */
    public void trade(@Nullable final Player player, final ItemStack tradeItem) {
        // determine target position
        Vec3 tradeTarget;
        if (player != null) {
            tradeTarget = player.position();
        } else {
            tradeTarget = LandRandomPos.getPos(this, 4, 2);
            if (null == tradeTarget) {
                tradeTarget = Dryad.this.position();
            }
        }
        final Vec3 tradeTargetPos = tradeTarget.add(0, 1, 0);
        // determine list of trade results
        // drop trade results as item entities
        getTradeResult(player, tradeItem).forEach(item -> BehaviorUtils.throwItem(this, item, tradeTargetPos));
        // shrink/remove held item
        tradeItem.shrink(1);
        this.setItemInHand(InteractionHand.MAIN_HAND, tradeItem);
        if (tradeItem.getCount() <= 0) {
            this.setTradingPlayer(null);
        }
        // spawn xp orb
        if (player != null && random.nextInt(3) == 0) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.getX(), this.getY(), this.getZ(), 1 + random.nextInt(2)));
        }
        this.level.broadcastEntityEvent(this, FINISH_TRADE_EVENT);
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case FINISH_TRADE_EVENT:
                // swing arm and play sound
                this.swing(InteractionHand.MAIN_HAND, true);
                for(int i = 0; i < 4; i++) {
                    this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getX() + 0.5D * (random.nextDouble() - 0.5D), this.getEyeY() + 0.5D * (random.nextDouble() - 0.5D), this.getZ() + 0.5D  * (random.nextDouble() - 0.5D), 0, 0, 0);
                }
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Variant methods

    public void setVariant(final NymphVariant variantIn) {
        this.variant = variantIn;
        this.getEntityData().set(DATA_VARIANT, variantIn.getSerializedName());
    }

    public NymphVariant getVariant() {
        return variant;
    }

    public NymphVariant getVariantByName(final String name) {
        return Variant.getByName(name);
    }

    public NymphVariant getRandomVariant() {
        return Variant.getRandom(level.getRandom());
    }

    public NymphVariant getVariantForBiome(final Holder<Biome> biome) {
        return Variant.getForBiome(biome);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(DATA_VARIANT)) {
            this.variant = getVariantByName(this.getEntityData().get(DATA_VARIANT));
        }
    }

    // Tree methods

    public BlockPos getTreePos() {
        return treePos;
    }

    public Vec3 getTreeVec() {
        if (treePos != null) {
            return Vec3.atBottomCenterOf(treePos.above());
        }
        return null;
    }

    public boolean isHiding() {
        return hidingTime > 0 || this.level.getBlockState(this.blockPosition().above()).is(this.getVariant().getLogs());
    }

    public void setHiding(final boolean hiding) {
        hidingTime = hiding ? 1 : 0;
    }

    public void setTreePos(@Nullable final BlockPos pos) {
        treePos = pos;
        if (pos != null) {
            this.restrictTo(pos, (int) (this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue()));
        } else {
            this.restrictTo(BlockPos.ZERO, -1);
        }
    }

    /**
     * Checks if the given position is a dirt or other plant-sustaining
     * block that is underneath a full-size tree.
     *
     * @param level the world
     * @param pos   the block pos beneath the tree
     * @param logs  the blocks that are considered logs
     * @return if this block pos is supporting a likely tree
     **/
    public static boolean isTreeAt(final BlockGetter level, final BlockPos pos, final TagKey<Block> logs) {
        // a "tree" is considered two log blocks on top of a dirt block (or other plant-sustaining block)
        final BlockState soil = level.getBlockState(pos);
        return (soil.is(BlockTags.DIRT)) && level.getBlockState(pos.above(1)).is(logs) && level.getBlockState(pos.above(2)).is(logs);
    }

    /**
     * Attempt to exit the tree, if the entity
     * is inside a tree. Also resets hiding values.
     *
     * @return if a path was set successfully
     **/
    public boolean tryExitTree() {
        if (this.treePos != null && this.isWithinDistanceOfTree(2.0D) && this.getNavigation().isDone()) {
            // choose several random positions to check
            int radius = 2;
            for (int i = 0; i < 10; i++) {
                double x = this.getX() + random.nextInt(radius * 2) - radius;
                double y = this.getY() + random.nextInt(radius) - radius / 2.0D;
                double z = this.getZ() + random.nextInt(radius * 2) - radius;
                // try to path to the position
                if (level.noCollision(this, this.getType().getAABB(x, y, z))) {
                    this.getNavigation().moveTo(x, y, z, 1.0D);
                    return true;
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
        final Vec3 treeVec = getTreeVec();
        if (treeVec != null) {
            return treeVec.closerThan(position(), dis);
        }
        return false;
    }

    /**
     * Moves the entity to the tree position and disables navigation for a while
     */
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
            } else if (Dryad.this.treePos != null && Dryad.this.isWithinDistanceOfTree(1.5D) && Dryad.this.getTarget() == null) {
                return isTreeAt(Dryad.this.level, Dryad.this.treePos, Dryad.this.getVariant().getLogs());
            }
            return false;
        }

        @Override
        public void start() {
            Dryad.this.setHiding(true);
            Dryad.this.isGoingToTree = false;
        }

        @Override
        public void tick() {
            super.tick();
            Dryad.this.getNavigation().stop();
            Dryad.this.hidingTime = Math.min(Dryad.this.hidingTime + 1, maxHidingTime);
            if (Dryad.this.hidingTime >= maxHidingTime
                    && Dryad.this.getRandom().nextInt(100) == 0
                    && Dryad.this.tryExitTree()) {
                cooldown = maxCooldown;
            }
        }

        @Override
        public void stop() {
            Dryad.this.tryExitTree();
            cooldown = maxCooldown;
        }
    }

    /**
     * Searches for an unclaimed tree, then assigns the tree to this entity
     */
    class FindTreeGoal extends FindBlockGoal {

        public FindTreeGoal(int radius, int cooldown) {
            super(Dryad.this, radius, cooldown);
        }

        @Override
        public boolean canUse() {
            return (null == Dryad.this.getTreePos() || Dryad.this.getRandom().nextInt(500) == 0) && super.canUse();
        }

        @Override
        public boolean isTargetBlock(LevelReader worldIn, BlockPos pos) {
            // ensure the target block is a tree
            if (!isTreeAt(worldIn, pos, Dryad.this.getVariant().getLogs())) {
                return false;
            }
            // ensure the block is not claimed by nearby dryads
            List<Dryad> dryads = Dryad.this.level.getEntitiesOfClass(Dryad.class, Dryad.this.getBoundingBox().inflate(10.0D), e ->
                    pos.equals(e.treePos));
            // if any dryads were found with the given tree, reject this position
            return dryads.isEmpty();
        }

        @Override
        public void onFoundBlock(final LevelReader worldIn, final BlockPos target) {
            Dryad.this.setTreePos(target);
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
            return !Dryad.this.isHiding() && Dryad.this.getTreePos() != null
                    && Dryad.this.getTarget() == null && Dryad.this.getRandom().nextInt(chance) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return Dryad.this.isGoingToTree && Dryad.this.getTreePos() != null
                    && !Dryad.this.getNavigation().isDone() && Dryad.this.getTarget() == null;
        }

        @Override
        public void start() {
            Dryad.this.isGoingToTree = true;
            final Vec3 vec = Dryad.this.getTreeVec();
            Dryad.this.getNavigation().moveTo(vec.x(), vec.y(), vec.z(), this.speed);
        }

        @Override
        public void stop() {
            Dryad.this.getNavigation().stop();
            Dryad.this.isGoingToTree = false;
        }
    }

    class RandomStrollWhenNotHidingGoal extends RandomStrollGoal {

        public RandomStrollWhenNotHidingGoal(double speed, final int chance) {
            super(Dryad.this, speed, chance);
        }

        @Override
        public boolean canUse() {
            return !Dryad.this.isHiding() && !Dryad.this.isGoingToTree && Dryad.this.getTarget() == null
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
            return !Dryad.this.isAggressive()
                    && !Dryad.this.getMainHandItem().isEmpty()
                    && Dryad.this.getMainHandItem().is(Dryad.this.getTradeTag());
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
            // stop moving and look down
            Dryad.this.getNavigation().stop();
            Dryad.this.getLookControl().setLookAt(Dryad.this.getEyePosition(1.0F).add(0.0D, -0.25D, 0.0D));
            // if enough time has elapsed, commence the trade
            if (thinkingTime++ >= maxThinkingTime) {
                trade(Dryad.this.tradingPlayer, Dryad.this.getMainHandItem());
                stop();
            }
        }

        @Override
        public void stop() {
            thinkingTime = 0;
        }
    }

    public static class Variant implements NymphVariant {
        public static final Variant ACACIA = new Variant("acacia", new ResourceLocation("forge", "is_savanna"), () -> Blocks.ACACIA_SAPLING);
        public static final Variant BIRCH = new Variant("birch", new ResourceLocation("forge", "is_birch"), () -> Blocks.BIRCH_SAPLING);
        public static final Variant DARK_OAK = new Variant("dark_oak", new ResourceLocation("forge", "is_spooky"), () -> Blocks.DARK_OAK_SAPLING);
        public static final Variant JUNGLE = new Variant("jungle", new ResourceLocation("minecraft", "is_jungle"), () -> Blocks.JUNGLE_SAPLING);
        public static final Variant OAK = new Variant("oak", new ResourceLocation("forge", "is_plains"), () -> Blocks.OAK_SAPLING);
        public static final Variant SPRUCE = new Variant("spruce", new ResourceLocation("minecraft", "is_taiga"), () -> Blocks.SPRUCE_SAPLING);
        public static final Variant OLIVE = new Variant(GreekFantasy.MODID, "olive", new ResourceLocation("forge", "is_olive"), () -> GFRegistry.BlockReg.OLIVE_SAPLING.get());

        public static ImmutableMap<String, Variant> OVERWORLD = ImmutableMap.<String, Variant>builder()
                .put(ACACIA.name, ACACIA).put(BIRCH.name, BIRCH).put(DARK_OAK.name, DARK_OAK)
                .put(JUNGLE.name, JUNGLE).put(OAK.name, OAK).put(SPRUCE.name, SPRUCE)
                .put(OLIVE.name, OLIVE)
                .build();

        protected final String name;
        protected final Supplier<Block> sapling;
        protected final TagKey<Block> tag;
        protected final TagKey<Biome> biomeTag;
        protected final ResourceLocation deathLootTable;
        protected final ResourceLocation tradeLootTable;

        protected Variant(final String nameIn, final ResourceLocation biomeTag, final Supplier<Block> saplingIn) {
            this("minecraft", nameIn, ForgeRegistries.BIOMES.tags().createTagKey(biomeTag), "dryad", "logs", saplingIn);
        }

        protected Variant(final String modid, final String nameIn, final ResourceLocation biomeTag, final Supplier<Block> saplingIn) {
            this(modid, nameIn, ForgeRegistries.BIOMES.tags().createTagKey(biomeTag), "dryad", "logs", saplingIn);
        }

        protected Variant(final String modid, final String nameIn, final TagKey<Biome> biome,
                          final String entityIn, final String tagSuffixIn,
                          final Supplier<Block> saplingIn) {
            this.name = nameIn;
            this.biomeTag = biome;
            this.sapling = saplingIn;
            this.deathLootTable = new ResourceLocation(GreekFantasy.MODID, "entities/" + entityIn + "/" + name);
            this.tradeLootTable = new ResourceLocation(GreekFantasy.MODID, "gameplay/" + entityIn + "_trade");
            ResourceLocation tagId = new ResourceLocation(modid, name + "_" + tagSuffixIn);
            this.tag = ForgeRegistries.BLOCKS.tags().createTagKey(tagId);
        }

        public static Variant getForBiome(final Holder<Biome> biome) {
            for (Variant variant : OVERWORLD.values()) {
                if (biome.is(variant.biomeTag)) {
                    return variant;
                }
            }
            return Variant.OAK;
        }

        public static Variant getRandom(final Random rand) {
            int len = OVERWORLD.size();
            return len > 0 ? OVERWORLD.entrySet().asList().get(rand.nextInt(len)).getValue() : OAK;
        }

        public static Variant getByName(final String n) {
            return OVERWORLD.getOrDefault(n, OAK);
        }

        @Override
        public TagKey<Block> getLogs() {
            return tag;
        }

        @Override
        public TagKey<Biome> getBiome() {
            return biomeTag;
        }

        @Override
        public BlockState getSapling() {
            return sapling.get().defaultBlockState();
        }

        @Override
        public ResourceLocation getDeathLootTable() {
            return deathLootTable;
        }

        @Override
        public ResourceLocation getTradeLootTable() {
            return tradeLootTable;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
