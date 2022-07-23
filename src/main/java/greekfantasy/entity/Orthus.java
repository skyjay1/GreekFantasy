package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.ShootFireGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class Orthus extends TamableAnimal implements NeutralMob {

    protected static final TagKey<Item> FOOD = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "orthus_food"));

    protected static final String KEY_LIFE_TICKS = "LifeTicks";

    //bytes to use in Level#broadcastEntityEvent
    private static final byte CLIENT_START_FIRE_EVENT = 11;
    private static final byte CLIENT_STOP_FIRE_EVENT = 12;

    protected static final double FIRE_RANGE = 4.5D;
    protected static final int MAX_FIRE_TIME = 52;

    protected static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    protected int angerTime;
    protected UUID angerTarget;

    /**
     * The number of ticks until the entity starts taking damage
     **/
    protected boolean limitedLifespan;
    protected int limitedLifeTicks;

    /** The number of ticks this entity is breathing fire **/
    protected int fireTime;

    public Orthus(final EntityType<? extends Orthus> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.29D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new Orthus.FireAttackGoal(MAX_FIRE_TIME, 165, FIRE_RANGE));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 4.0F, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new Orthus.BegGoal(8.0D));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, e -> this.isAngryAt(e) || this.wantsToAttack(e, this.getOwner())));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Animal.class, false, e -> e.getType() == EntityType.STRIDER));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // update lifespan
        if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 40;
            hurt(DamageSource.STARVE, 1.0F);
        }
    }

    @Override
    public void tick() {
        super.tick();

        // spawn particles
        if (level.isClientSide() && this.isFireAttack()) {
            spawnFireParticles();
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return effectInstance.getEffect() != MobEffects.WITHER && super.canBeAffected(effectInstance);
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, @Nullable LivingEntity owner) {
        if(target instanceof Ghast) {
            return false;
        } else if (target instanceof TamableAnimal tamable) {
            return !tamable.isTame() || tamable.getOwner() != owner;
        } else if (target instanceof Player targetPlayer && owner instanceof Player ownerPlayer
                && !ownerPlayer.canHarmPlayer(targetPlayer)) {
            return false;
        } else if(target.getMainHandItem().is(FOOD) || target.getOffhandItem().is(FOOD)) {
            return false;
        } else if (target instanceof AbstractHorse horse && horse.isTamed()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return !this.isAngryAt(player) && super.canBeLeashed(player);
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

    // Other

    public float getTailAngle() {
        if (this.isAngry()) {
            return 1.5393804F;
        } else {
            return this.isTame() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * (float)Math.PI : ((float)Math.PI / 5F);
        }
    }

    public void setLimitedLife(int life) {
        this.limitedLifespan = true;
        this.limitedLifeTicks = life;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.random.nextInt(3) == 0) {
            return SoundEvents.WOLF_AMBIENT;
        } else if (this.random.nextInt(3) == 0) {
            return SoundEvents.WOLF_PANT;
        } else {
            return SoundEvents.WOLF_GROWL;
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return limitedLifespan ? BuiltInLootTables.EMPTY : super.getDefaultLootTable();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.limitedLifespan) {
            compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
        }
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KEY_LIFE_TICKS)) {
            setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
        }
        this.readPersistentAngerSaveData(this.level, compound);
    }

    public void spawnFireParticles() {
        if (!level.isClientSide()) {
            return;
        }
        Vec3 lookVec = this.getLookAngle();
        Vec3 pos = this.getEyePosition(1.0F);
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

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        Orthus baby = GFRegistry.EntityReg.ORTHUS.get().create(level);
        UUID uuid = this.getOwnerUUID();
        if (uuid != null) {
            baby.setOwnerUUID(uuid);
            baby.setTame(true);
        }
        return baby;
    }

    @Override
    public void setTame(boolean tamed) {
        super.setTame(tamed);
        if (tamed) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(36.0D);
            this.setHealth(36.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0D);
        }
    }

    @Override
    public void tame(Player player) {
        super.tame(player);
        setFireAttack(false);
        setTarget(null);
        setLastHurtByMob(null);
        forgetCurrentTargetAndRefreshUniversalAnger();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(FOOD);
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(otherAnimal instanceof Orthus)) {
            return false;
        } else {
            Orthus orthus = (Orthus) otherAnimal;
            if (!orthus.isTame()) {
                return false;
            } else if (orthus.isInSittingPose()) {
                return false;
            } else {
                return this.isInLove() && orthus.isInLove();
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (this.level.isClientSide()) {
            boolean flag = this.isOwnedBy(player) || this.isTame()
                    || heldItem.is(FOOD) && !this.isTame();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame()) {
                // attempt to heal entity
                if ((this.isFood(heldItem)) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    this.heal(heldItem.getFoodProperties(this).getNutrition());
                    return InteractionResult.SUCCESS;
                }

                // attempt to udpate Sitting state
                InteractionResult actionresulttype = super.mobInteract(player, hand);
                if ((!actionresulttype.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setFireAttack(false);
                    this.setTarget(null);
                    this.setLastHurtByMob(null);
                    this.forgetCurrentTargetAndRefreshUniversalAnger();
                    return InteractionResult.SUCCESS;
                }
                return actionresulttype;

            } else if (heldItem.is(FOOD)) {
                // reset anger
                if (this.isAngry()) {
                    this.setPersistentAngerTarget(null);
                }
                // consume the item
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
                // attempt to tame the entity
                if (this.random.nextInt(4) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.setFireAttack(false);
                    this.setTarget(null);
                    this.setLastHurtByMob(null);
                    this.forgetCurrentTargetAndRefreshUniversalAnger();
                    this.navigation.stop();
                    this.setTarget(null);
                    this.setOrderedToSit(true);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }

                return InteractionResult.SUCCESS;
            }

            return super.mobInteract(player, hand);
        }
    }

    @Override
    public void handleEntityEvent(byte event) {
        switch(event) {
            case CLIENT_START_FIRE_EVENT:
                setFireAttack(true);
                break;
            case CLIENT_STOP_FIRE_EVENT:
                setFireAttack(false);
                break;
            default:
                super.handleEntityEvent(event);
                break;
        }
    }

    public void setFireAttack(final boolean shooting) {
        if(shooting) {
            this.fireTime = MAX_FIRE_TIME;
            if(!level.isClientSide()) {
                level.broadcastEntityEvent(this, CLIENT_START_FIRE_EVENT);
            }
        } else {
            this.fireTime = 0;
            if(!level.isClientSide()) {
                level.broadcastEntityEvent(this, CLIENT_STOP_FIRE_EVENT);
            }
        }
    }

    public boolean isFireAttack() {
        return this.fireTime > 0;
    }

    class BegGoal extends Goal {

        protected static final Predicate<LivingEntity> CAUSE_BEG = e -> e.getMainHandItem().is(FOOD) || e.getOffhandItem().is(FOOD);

        protected final double range;
        protected final int interval;
        @Nullable
        protected LivingEntity player;

        protected BegGoal(final double rangeIn) {
            this(rangeIn, 10);
        }

        protected BegGoal(final double rangeIn, int intervalIn) {
            range = rangeIn;
            interval = intervalIn;
        }


        @Override
        public boolean canUse() {
            if (Orthus.this.tickCount % interval == 0) {
                // find a player within range to cause begging
                final List<Player> list = Orthus.this.level.getEntitiesOfClass(Player.class, Orthus.this.getBoundingBox().inflate(range));
                if (!list.isEmpty()) {
                    player = list.get(0);
                } else {
                    player = null;
                }
            }
            return player != null;
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void tick() {
            Orthus.this.getLookControl().setLookAt(player, Orthus.this.getMaxHeadYRot(), Orthus.this.getMaxHeadXRot());
            Orthus.this.getNavigation().stop();
            // decrease fire time
            if(Orthus.this.fireTime-- <= 0) {
                stop();
            }
        }

    }

    class FireAttackGoal extends ShootFireGoal {

        protected FireAttackGoal(final int fireTimeIn, final int maxCooldownIn, final double fireRange) {
            super(Orthus.this, fireTimeIn, maxCooldownIn, fireRange);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !Orthus.this.isFireAttack() && !Orthus.this.isOrderedToSit() && !Orthus.this.isInSittingPose();
        }

        @Override
        public boolean canContinueToUse() {
            return getTarget() != null && wantsToAttack(getTarget(), getOwner())
                    && !getTarget().fireImmune()
                    && Orthus.this.isFireAttack() && super.canContinueToUse();
        }

        @Override
        public void start() {
            super.start();
            Orthus.this.setFireAttack(true);
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Override
        public void stop() {
            super.stop();
            Orthus.this.setFireAttack(false);
        }
    }
}
