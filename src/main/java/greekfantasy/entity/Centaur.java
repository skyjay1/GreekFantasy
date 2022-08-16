package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.util.HasHorseVariant;
import greekfantasy.util.Quest;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class Centaur extends PathfinderMob implements NeutralMob, RangedAttackMob, HasHorseVariant {

    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Centaur.class, EntityDataSerializers.INT);
    public static final String KEY_VARIANT = "Variant";

    private static final byte START_REARING_EVENT = 6;
    private static final byte STOP_REARING_EVENT = 7;
    private static final byte FINISH_TRADE_EVENT = 8;

    protected static final TagKey<Item> CENTAUR_TRADE = ItemTags.create(new ResourceLocation(GreekFantasy.MODID, "centaur_trade"));

    private static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private int angerTime;
    private UUID angerTarget;

    private boolean isRearing;

    private int tailCounter;
    private float rearingAmount;
    private float prevRearingAmount;
    private int rearingCounter;

    private Player tradingPlayer;

    public Centaur(final EntityType<? extends Centaur> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.MAX_HEALTH, 34.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ARMOR, 1.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new Centaur.RangedAttackGoal(this, 1.0D, this.hasBullHead() ? 50 : 35, 15.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, true));
        addTradeGoal();
    }

    protected void addTradeGoal() {
        this.goalSelector.addGoal(2, new Centaur.TradeGoal(40 + random.nextInt(20)));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_VARIANT, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // tail motion timer
        if (this.random.nextInt(200) == 0) {
            this.moveTail();
        }
        // anger timer
        if (!this.level.isClientSide()) {
            this.updatePersistentAnger((ServerLevel) this.level, true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        // tail movement logic
        if (this.tailCounter > 0 && ++this.tailCounter > 8) {
            this.tailCounter = 0;
        }
        // rearing logic
        this.prevRearingAmount = this.rearingAmount;
        if (this.isRearing()) {
            this.rearingAmount += (1.0F - this.rearingAmount) * 0.4F + 0.05F;
            if (this.rearingAmount > 1.0F) {
                this.rearingAmount = 1.0F;
            }
        } else {
            this.rearingAmount += (0.8F * this.rearingAmount * this.rearingAmount * this.rearingAmount - this.rearingAmount) * 0.6F
                    - 0.05F;
            if (this.rearingAmount < 0.0F) {
                this.rearingAmount = 0.0F;
            }
        }
        if (this.isEffectiveAi() && this.rearingCounter > 0 && ++this.rearingCounter > 20) {
            this.rearingCounter = 0;
            this.setRearing(false);
        }
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        // immune to damage from other centaurs
        if (source.getEntity() != null && source.getEntity().getType() == this.getType()) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    // Ranged Attack methods

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
        return item instanceof BowItem;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof BowItem)));
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, itemstack, distanceFactor);
        if (this.getMainHandItem().getItem() instanceof BowItem) {
            arrow = ((BowItem) this.getMainHandItem().getItem()).customArrow(arrow);
        }
        // this is copied from LlamaSpit code, it moves the arrow nearer to the centaur's human-body
        arrow.setPos(this.getX() - (this.getBbWidth() + 1.0F) * 0.5D * Mth.sin(this.yBodyRot * 0.017453292F), this.getEyeY() - 0.10000000149011612D, this.getZ() + (this.getBbWidth() + 1.0F) * 0.5D * Mth.cos(this.yBodyRot * 0.017453292F));
        double dx = target.getX() - arrow.getX();
        double dy = target.getY(0.67D) - arrow.getY();
        double dz = target.getZ() - arrow.getZ();
        double dis = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dis * (double) 0.2F, dz, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(arrow);
    }

    // NBT Methods

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_VARIANT, (byte) this.getVariant().getId());
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
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

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        // set variant when not spawned as part of a structure
        Variant color = this.getVariant();
        if(spawnType != MobSpawnType.STRUCTURE) {
            // determine color variant based on spawn group data, or create new group data
            if (spawnDataIn instanceof Centaur.GroupData) {
                color = ((Centaur.GroupData) spawnDataIn).variant;
            } else {
                color = Util.getRandom(Variant.values(), this.random);
                spawnDataIn = new Centaur.GroupData(color);
            }
        }
        // set markings
        this.setVariant(color, Util.getRandom(Markings.values(), this.random));
        if (this.random.nextInt(3) > 0) {
            this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOW));
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnDataIn, dataTag);
    }

    @Override
    protected int calculateFallDamage(float distance, float damageMultiplier) {
        return Mth.ceil((distance * 0.5F - 3.0F) * damageMultiplier);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.random.nextInt(3) == 0) {
            this.makeRear();
        }

        return null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(10) == 0 && !this.isImmobile()) {
            this.makeRear();
        }
        return null;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 400;
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case START_REARING_EVENT:
                this.isRearing = true;
                break;
            case STOP_REARING_EVENT:
                this.isRearing = false;
                break;
            case FINISH_TRADE_EVENT:
                // swing arm and play sound
                this.swing(InteractionHand.OFF_HAND, true);
                this.playSound(SoundEvents.BOOK_PAGE_TURN, this.getSoundVolume(), this.getVoicePitch());
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Rearing and tail-movement methods

    public void makeRear() {
        if (this.isEffectiveAi()) {
            this.rearingCounter = 1;
            this.setRearing(true);
        }
    }

    public void setRearing(final boolean rearing) {
        this.isRearing = rearing;
        this.level.broadcastEntityEvent(this, rearing ? START_REARING_EVENT : STOP_REARING_EVENT);
    }

    @Override
    public boolean isRearing() {
        return this.isRearing;
    }

    public void moveTail() {
        this.tailCounter = 1;
    }

    @Override
    public float getRearingAmount(float partialTick) {
        return partialTick > 0.99F ? rearingAmount : Mth.lerp(partialTick, prevRearingAmount, rearingAmount);
    }

    @Override
    public int getTailCounter() {
        return tailCounter;
    }

    // Trading

    @Override
    protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // check if the tradingPlayer is holding a trade item and the entity is not already trading
        if (!this.level.isClientSide() && this.level instanceof ServerLevel
                && canPlayerTrade(player) && !this.isAggressive() && !isTrading()
                && this.getOffhandItem().isEmpty() && !stack.isEmpty() && stack.is(getTradeTag())) {
            // initiate trading
            this.setTradingPlayer(player);
            // take the item from the tradingPlayer
            this.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(stack.getItem()));
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            player.setItemInHand(hand, stack);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.sidedSuccess(!this.level.isClientSide());
    }

    protected TagKey<Item> getTradeTag() {
        return CENTAUR_TRADE;
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
                tradeTarget = this.position();
            }
        }
        final Vec3 tradeTargetPos = tradeTarget.add(0, 1, 0);
        // determine trade result
        ItemStack quest = Quest.createQuestItemStack(Quest.getRandomQuestId(this.random));
        // drop trade result as item entities
        BehaviorUtils.throwItem(this, quest, tradeTargetPos);
        // shrink/remove held item
        tradeItem.shrink(1);
        this.setItemInHand(InteractionHand.OFF_HAND, tradeItem);
        if (tradeItem.getCount() <= 0) {
            this.setTradingPlayer(null);
        }
        this.level.broadcastEntityEvent(this, FINISH_TRADE_EVENT);
    }

    // Color

    @Override
    public void setPackedVariant(int packedColorsTypes) {
        this.entityData.set(DATA_VARIANT, packedColorsTypes);
    }

    @Override
    public int getPackedVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    /**
     * @return whether to render using a bull-headed texture
     **/
    public boolean hasBullHead() {
        return false;
    }

    class RangedAttackGoal extends net.minecraft.world.entity.ai.goal.RangedAttackGoal {
        public RangedAttackGoal(RangedAttackMob entity, double moveSpeed, int attackInterval, float attackDistance) {
            super(entity, moveSpeed, attackInterval, attackDistance);
        }

        @Override
        public boolean canUse() {
            return (super.canUse() && Centaur.this.getMainHandItem().getItem() instanceof BowItem);
        }

        @Override
        public void start() {
            super.start();
            Centaur.this.setAggressive(true);
        }

        @Override
        public void stop() {
            super.stop();
            Centaur.this.setAggressive(false);
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
            return !Centaur.this.isAggressive()
                    && !Centaur.this.getOffhandItem().isEmpty()
                    && Centaur.this.getOffhandItem().is(Centaur.this.getTradeTag());
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
            Centaur.this.getNavigation().stop();
            Centaur.this.getLookControl().setLookAt(Centaur.this.getEyePosition(1.0F).add(0.0D, -0.25D, 0.0D));
            // if enough time has elapsed, commence the trade
            if (thinkingTime++ >= maxThinkingTime) {
                trade(Centaur.this.tradingPlayer, Centaur.this.getOffhandItem());
                stop();
            }
        }

        @Override
        public void stop() {
            thinkingTime = 0;
        }
    }

    public static class GroupData implements SpawnGroupData {
        public final net.minecraft.world.entity.animal.horse.Variant variant;

        public GroupData(Variant color) {
            this.variant = color;
        }
    }
}
