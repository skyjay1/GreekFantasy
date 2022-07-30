package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.GFBegGoal;
import greekfantasy.entity.boss.Hydra;
import greekfantasy.entity.misc.Curse;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Random;

public class Cerastes extends TamableAnimal {

    protected static final TagKey<Item> FOOD = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "cerastes_food"));

    private static final byte STANDING_START_EVENT = 4;
    private static final byte STANDING_END_EVENT = 5;
    private static final byte HIDING_START_EVENT = 6;
    private static final byte HIDING_END_EVENT = 7;

    private final EntityDimensions hiddenSize;

    private final int MAX_TONGUE_TIME = 10;
    private int tongueTime0;
    private int tongueTime;
    private final int MAX_STANDING_TIME = 8;
    private int standingTime0;
    private int standingTime;
    private final int MAX_HIDING_TIME = 10;
    private int hidingTime0;
    private int hidingTime;

    private boolean isHiding;
    private boolean isStanding;
    private boolean isGoingToSand;

    public Cerastes(final EntityType<? extends Cerastes> type, final Level worldIn) {
        super(type, worldIn);
        this.hiddenSize = EntityDimensions.scalable(0.8F, 0.2F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_CACTUS, -0.5F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_CACTUS, -0.5F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public static boolean checkCerastesSpawnRules(EntityType<? extends PathfinderMob> entityType, ServerLevelAccessor level, MobSpawnType mobSpawnType, BlockPos pos, Random rand) {
        return Mob.checkMobSpawnRules(entityType, level, mobSpawnType, pos, rand) && level.getBlockState(pos.below()).is(BlockTags.SAND);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 5.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new Cerastes.CerastesBegGoal(6.0F));
        this.goalSelector.addGoal(2, new Cerastes.HideGoal(this));
        this.goalSelector.addGoal(3, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 8.0F, 2.0F, false));
        this.goalSelector.addGoal(6, new Cerastes.GoToSandGoal(10, 0.8F));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D) {
            @Override
            public boolean canUse() {
                return !Cerastes.this.isHiding() && !Cerastes.this.isGoingToSand
                        && Cerastes.this.random.nextInt(600) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 4.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false,
                e -> !isOwnedBy(e) && !(e.getMainHandItem().is(FOOD) || e.getOffhandItem().is(FOOD))));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Rabbit.class, 10, false, false, e -> !this.isHiding() || this.distanceToSqr(e) < 5.0D));

    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        return null;
    }

    @Override
    public boolean canMate(Animal animal) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(FOOD);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // standing logic
        if (this.isEffectiveAi()) {
            // stand when attacked, and sometimes randomly
            if (!this.isHiding && (this.getTarget() != null || this.random.nextInt(600) == 0)) {
                this.setStanding(true);
            } else if (this.isStanding() && standingTime > 0.9F && this.random.nextInt(60) == 0) {
                this.setStanding(false);
            }
        }
        // isGoingToSand check
        if (this.getTarget() != null) {
            isGoingToSand = false;
        }
    }

    @Override
    public void tick() {
        super.tick();

        // tongue-flick counter
        tongueTime0 = tongueTime;
        if (this.tongueTime == 0) {
            if (!this.isStanding() && random.nextInt(100) == 0) {
                tongueTime = 1;
            }
        } else if (++this.tongueTime > MAX_TONGUE_TIME) {
            this.tongueTime0 = 0;
            this.tongueTime = 0;
        }

        // standing counter
        standingTime0 = standingTime;
        if (this.isStanding()) {
            standingTime = Math.min(standingTime + 1, MAX_STANDING_TIME);
        } else if (standingTime > 0.0F) {
            standingTime = Math.max(0, standingTime - 1);
        }

        // hiding counter
        hidingTime0 = hidingTime;
        if (this.isHiding()) {
            hidingTime = Math.min(hidingTime + 1, MAX_HIDING_TIME);
        } else if (hidingTime > 0.0F) {
            hidingTime = Math.max(0, hidingTime - 1);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case STANDING_START_EVENT:
                this.isStanding = true;
                this.isHiding = false;
                break;
            case STANDING_END_EVENT:
                this.isStanding = false;
                break;
            case HIDING_START_EVENT:
                this.isHiding = true;
                this.isStanding = false;
                this.refreshDimensions();
                break;
            case HIDING_END_EVENT:
                this.isHiding = false;
                this.refreshDimensions();
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    @Override
    public boolean hurt(final DamageSource source, final float amount) {
        boolean hurt = super.hurt(source, amount);
        this.setHiding(false);
        this.setStanding(true);
        if(hurt && source.getDirectEntity() instanceof Curse && this.level instanceof ServerLevel) {
            // cause explosion and summon hydra
            level.explode(this, this.getX(), this.getY(), this.getZ(), 2.5F, Explosion.BlockInteraction.DESTROY);
            Hydra.spawnHydra((ServerLevel) this.level, this);
        }
        return hurt;
    }

    @Override
    protected void doPush(final Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            // un-hide and stand up
            if (!this.level.isClientSide() && random.nextInt(10) == 0) {
                this.setHiding(false);
                this.setStanding(true);
            }
        }
        super.doPush(entityIn);
    }

    @Override
    public boolean canAttackType(EntityType<?> typeIn) {
        if (typeIn == this.getType() || typeIn == EntityType.CREEPER) {
            return false;
        }
        return super.canAttackType(typeIn);
    }

    @Override
    public boolean doHurtTarget(final Entity entity) {
        if (super.doHurtTarget(entity)) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 6 * 20, 0));
                // chance to stun target
                if(this.random.nextFloat() < 0.15D) {
                    livingEntity.addEffect(new MobEffectInstance(GFRegistry.MobEffectReg.STUNNED.get(), 2 * 20, 0));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.isHiding() ? hiddenSize : super.getDimensions(pose);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return this.isHiding() ? hiddenSize.height * 0.85F : super.getStandingEyeHeight(pose, size);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.level.isClientSide()) {
            boolean consume = this.isOwnedBy(player) || this.isTame() || isFood(itemstack) && !this.isTame();
            return consume ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.isCreative()) {
                        itemstack.shrink(1);
                    }

                    this.heal((float)itemstack.getFoodProperties(this).getNutrition());
                    this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                    return InteractionResult.SUCCESS;
                }

                InteractionResult interactionresult = super.mobInteract(player, hand);
                if ((!interactionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
                    this.setOrderedToSit(!this.isOrderedToSit());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                    return InteractionResult.SUCCESS;
                }

                return interactionresult;

            } else if (itemstack.is(FOOD)) {
                if (!player.isCreative()) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.setOrderedToSit(true);
                    this.level.broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte)6);
                }

                return InteractionResult.SUCCESS;
            }

            return super.mobInteract(player, hand);
        }
    }

    // standing / hiding methods

    public void setStanding(final boolean standing) {
        this.isStanding = standing;
        if (standing) {
            this.isHiding = false;
        }
        if(!level.isClientSide()) {
            this.level.broadcastEntityEvent(this, standing ? STANDING_START_EVENT : STANDING_END_EVENT);
        }
    }

    public boolean isStanding() {
        return isStanding;
    }

    public float getTonguePercent(final float partialTick) {
        return Mth.lerp(partialTick, tongueTime0, tongueTime) / (float) MAX_TONGUE_TIME;
    }

    public float getStandingPercent(final float partialTick) {
        return Mth.lerp(partialTick, standingTime0, standingTime) / (float) MAX_STANDING_TIME;
    }

    public void setHiding(final boolean hiding) {
        this.isHiding = hiding;
        if (hiding) {
            this.isStanding = false;
        }
        this.level.broadcastEntityEvent(this, hiding ? HIDING_START_EVENT : HIDING_END_EVENT);
        this.refreshDimensions();
    }

    public boolean isHiding() {
        return isHiding;
    }

    public float getHidingPercent(final float partialTick) {
        return Mth.lerp(partialTick, hidingTime0, hidingTime) / (float) MAX_HIDING_TIME;
    }

    // Goals //

    class CerastesBegGoal extends GFBegGoal {

        public CerastesBegGoal(float minDistance) {
            super(Cerastes.this, minDistance, 10, item -> item.is(FOOD));
        }

        @Override
        public void start() {
            super.start();
            Cerastes.this.setStanding(false);
            Cerastes.this.setHiding(false);
        }
    }

    class GoToSandGoal extends MoveToBlockGoal {

        public GoToSandGoal(final int radiusIn, final double speedIn) {
            super(Cerastes.this, speedIn, radiusIn);
        }

        @Override
        public boolean canUse() {
            return !Cerastes.this.isHiding() && !Cerastes.this.isOrderedToSit()
                    && Cerastes.this.getTarget() == null && super.canUse();
        }

        @Override
        public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                Cerastes.this.isGoingToSand = false;
            }
        }

        @Override
        protected boolean isValidTarget(LevelReader worldIn, BlockPos pos) {
            if (!worldIn.getBlockState(pos).getMaterial().blocksMotion() && worldIn.getBlockState(pos.below()).is(BlockTags.SAND)) {
                Cerastes.this.isGoingToSand = true;
                return true;
            }
            return false;
        }
    }

    static class HideGoal extends Goal {

        final Cerastes entity;
        final int MAX_HIDE_TIME = 600;
        final int MAX_COOLDOWN = 500;
        int cooldown;

        public HideGoal(final Cerastes entityIn) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            this.entity = entityIn;
            this.cooldown = entityIn.getRandom().nextInt(MAX_COOLDOWN);
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                cooldown--;
            } else if (this.entity.getTarget() != null || !this.entity.getNavigation().isDone() || this.entity.isHiding()) {
                return false;
            } else if (this.entity.getRandom().nextInt(10) == 0) {
                BlockPos blockpos = (new BlockPos(this.entity.getX(), this.entity.getY() - 0.5D, this.entity.getZ()));
                BlockState blockstate = this.entity.level.getBlockState(blockpos);
                return blockstate.is(BlockTags.SAND);
            }
            return false;
        }

        @Override
        public void start() {
            this.entity.setHiding(true);
            this.entity.isGoingToSand = false;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.entity.getTarget() != null || this.entity.getRandom().nextInt(MAX_HIDE_TIME) == 0) {
                this.stop();
                return;
            }
            this.entity.getNavigation().stop();
        }

        @Override
        public boolean canContinueToUse() {
            return this.entity.isHiding();
        }

        @Override
        public void stop() {
            super.stop();
            this.entity.setHiding(false);
            this.cooldown = MAX_COOLDOWN;
        }

    }

}
