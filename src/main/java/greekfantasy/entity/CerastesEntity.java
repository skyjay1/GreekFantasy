package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.HasOwnerBegGoal;
import greekfantasy.entity.ai.HasOwnerFollowGoal;
import greekfantasy.entity.ai.HasOwnerHurtByTargetGoal;
import greekfantasy.entity.ai.HasOwnerHurtTargetGoal;
import greekfantasy.entity.misc.IHasOwner;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class CerastesEntity extends CreatureEntity implements IHasOwner<CerastesEntity> {

    protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.defineId(CerastesEntity.class, DataSerializers.OPTIONAL_UUID);

    protected static final IOptionalNamedTag<Item> FOOD = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "cerastes_food"));

    private static final byte STANDING_START = 4;
    private static final byte STANDING_END = 5;
    private static final byte HIDING_START = 6;
    private static final byte HIDING_END = 7;

    private final EntitySize hiddenSize;

    private final int MAX_TONGUE_TIME = 10;
    private final float STANDING_SPEED = 0.18F;
    private int tongueTime;
    private float standingTime;
    private float hidingTime;

    private boolean isHiding;
    private boolean isStanding;
    private boolean isGoingToSand;

    public CerastesEntity(final EntityType<? extends CerastesEntity> type, final World worldIn) {
        super(type, worldIn);
        this.hiddenSize = EntitySize.scalable(0.8F, 0.2F);
        this.setPathfindingMalus(PathNodeType.DAMAGE_CACTUS, -0.5F);
        this.setPathfindingMalus(PathNodeType.DANGER_CACTUS, -0.5F);
        this.setPathfindingMalus(PathNodeType.WATER, -1.0F);
    }

    public static boolean canCerastesSpawnOn(final EntityType<? extends MobEntity> entity, final IWorld world, final SpawnReason reason,
                                             final BlockPos pos, final Random rand) {
        final BlockPos blockpos = pos.below();
        return reason == SpawnReason.SPAWNER || world.getBlockState(blockpos).is(Blocks.SAND);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 5.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(OWNER, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new CerastesEntity.BegGoal(6.0F));
        this.goalSelector.addGoal(1, new CerastesEntity.HideGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new HasOwnerFollowGoal<>(this, 1.0D, 8.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new CerastesEntity.GoToSandGoal(10, 0.8F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D) {
            @Override
            public boolean canUse() {
                return !CerastesEntity.this.isHiding() && !CerastesEntity.this.isGoingToSand
                        && CerastesEntity.this.random.nextInt(600) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HasOwnerHurtByTargetGoal<>(this));
        this.targetSelector.addGoal(1, new HasOwnerHurtTargetGoal<>(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, false, false,
                e -> !isOwner(e) && !(e instanceof PlayerEntity && hasTamingItemInHand((PlayerEntity) e))));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, RabbitEntity.class, false, false));

    }

    @Override
    public void aiStep() {
        super.aiStep();
        // tongue-flick counter
        if (this.tongueTime == 0) {
            if (!this.isStanding() && random.nextInt(100) == 0) {
                tongueTime = 1;
            }
        } else if (++this.tongueTime > MAX_TONGUE_TIME) {
            this.tongueTime = 0;
        }
        // standing counter
        if (this.isStanding()) {
            standingTime = Math.min(1.0F, standingTime + STANDING_SPEED);
        } else if (standingTime > 0.0F) {
            standingTime = Math.max(0.0F, standingTime - STANDING_SPEED);
        }
        // hiding counter
        if (this.isHiding()) {
            hidingTime = Math.min(1.0F, hidingTime + STANDING_SPEED);
        } else if (hidingTime > 0.0F) {
            hidingTime = Math.max(0.0F, hidingTime - STANDING_SPEED);
        }
        // standing logic
        if (this.isEffectiveAi()) {
            // stand when attacked, and sometimes randomly
            if (this.getTarget() != null || this.random.nextInt(600) == 0) {
                this.setStanding(true);
            } else if (this.isStanding() && standingTime > 0.9F && this.random.nextInt(60) == 0) {
                this.setStanding(false);
            }
        }
        // isGoingToSand checker
        if (this.getTarget() != null) {
            isGoingToSand = false;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        switch (id) {
            case STANDING_START:
                this.isStanding = true;
                this.isHiding = false;
                break;
            case STANDING_END:
                this.isStanding = false;
                break;
            case HIDING_START:
                this.isHiding = true;
                this.isStanding = false;
                this.refreshDimensions();
                break;
            case HIDING_END:
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
        this.setHiding(false);
        this.setStanding(true);
        return super.hurt(source, amount);
    }

    @Override
    protected void doPush(final Entity entityIn) {
        if (entityIn instanceof LivingEntity) {
            // un-hide and stand up
            if (!this.level.isClientSide()) {
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
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).addEffect(new EffectInstance(Effects.POISON, 5 * 20, 0));
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
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
    }

    @Override
    public EntitySize getDimensions(Pose poseIn) {
        return this.isHiding() ? hiddenSize : super.getDimensions(poseIn);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize size) {
        return this.isHiding() ? hiddenSize.height * 0.85F : super.getStandingEyeHeight(pose, size);
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if (tryTameOrHeal(this, player, hand)) {
            return ActionResultType.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    // Owner methods //

    @Override
    public Optional<UUID> getOwnerID() {
        return this.getEntityData().get(OWNER);
    }

    @Override
    public void setOwner(@Nullable final UUID uuid) {
        this.getEntityData().set(OWNER, Optional.ofNullable(uuid));
    }

    @Override
    public LivingEntity getOwner() {
        if (hasOwner()) {
            return this.getCommandSenderWorld().getPlayerByUUID(getOwnerID().get());
        }
        return null;
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        if (isOwner(entity)) {
            return false;
        }
        return super.canAttack(entity);
    }

    @Override
    public boolean isTamingItem(final ItemStack item) {
        return !item.isEmpty() && FOOD.contains(item.getItem());
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)
                && this.getOwner() instanceof ServerPlayerEntity) {
            this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage(), Util.NIL_UUID);
        }
        super.die(cause);
    }

    //Team methods //

    @Override
    public Team getTeam() {
        return getOwnerTeam(super.getTeam());
    }

    @Override
    public boolean isAlliedTo(final Entity entity) {
        return isOnSameTeamAs(entity) || super.isAlliedTo(entity);
    }

    // NBT methods //

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        writeOwner(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        readOwner(compound);
    }

    // standing / hiding methods

    public void setStanding(final boolean standing) {
        this.isStanding = standing;
        if (standing) this.isHiding = false;
        this.level.broadcastEntityEvent(this, standing ? STANDING_START : STANDING_END);
    }

    public boolean isStanding() {
        return isStanding;
    }

    public float getTongueTime() {
        return ((float) this.tongueTime) / ((float) MAX_TONGUE_TIME);
    }

    public float getStandingTime(final float partialTick) {
        return standingTime;
    }

    public void setHiding(final boolean hiding) {
        this.isHiding = hiding;
        if (hiding) {
            this.isStanding = false;
        }
        this.level.broadcastEntityEvent(this, hiding ? HIDING_START : HIDING_END);
        this.refreshDimensions();
    }

    public boolean isHiding() {
        return isHiding;
    }

    public float getHidingTime(final float partialTick) {
        return hidingTime;
    }

    // Goals //

    class BegGoal extends HasOwnerBegGoal<CerastesEntity> {

        public BegGoal(float minDistance) {
            super(CerastesEntity.this, minDistance);
        }

        @Override
        public void start() {
            super.start();
            CerastesEntity.this.setStanding(false);
            CerastesEntity.this.setHiding(false);
        }
    }

    class GoToSandGoal extends MoveToBlockGoal {

        public GoToSandGoal(final int radiusIn, final double speedIn) {
            super(CerastesEntity.this, speedIn, radiusIn);
        }

        @Override
        public boolean canUse() {
            return !CerastesEntity.this.isHiding() && CerastesEntity.this.getTarget() == null && super.canUse();
        }

        @Override
        public void tick() {
            super.tick();
            if (this.isReachedTarget()) {
                CerastesEntity.this.isGoingToSand = false;
            }
        }

        @Override
        protected boolean isValidTarget(IWorldReader worldIn, BlockPos pos) {
            if (!worldIn.getBlockState(pos.above(1)).getMaterial().blocksMotion() && worldIn.getBlockState(pos).is(BlockTags.SAND)) {
                CerastesEntity.this.isGoingToSand = true;
                return true;
            }
            return false;
        }
    }

    static class HideGoal extends Goal {

        final CerastesEntity entity;
        final int MAX_HIDE_TIME = 500;
        final int MAX_COOLDOWN = 500;
        int cooldown;

        public HideGoal(final CerastesEntity entityIn) {
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
                return BlockTags.SAND.contains(blockstate.getBlock());
            }
            return false;
        }

        @Override
        public void start() {
            this.entity.setHiding(true);
            this.entity.isGoingToSand = false;
            this.cooldown = MAX_COOLDOWN + MAX_HIDE_TIME;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.entity.isHiding()) {
                if (this.entity.getTarget() != null || this.entity.getRandom().nextInt(MAX_HIDE_TIME) == 0) {
                    this.stop();
                    return;
                }
                this.entity.getNavigation().stop();
            }
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
