package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.entity.util.GFMobType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class HydraHead extends Monster {

    private static final EntityDataAccessor<Byte> PART_ID = SynchedEntityData.defineId(HydraHead.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(HydraHead.class, EntityDataSerializers.BYTE);
    private static final String KEY_ID = "HydraHeadId";
    private static final String KEY_STATE = "HydraHeadState";
    // bytes to use in STATE
    private static final byte NORMAL = (byte) 0;
    private static final byte SEVERED = (byte) 1;
    private static final byte GROWING = (byte) 2;
    private static final byte CHARRED = (byte) 3;
    // bytes to use in Level#broadcastEntityEvent
    private static final byte GROWING_EVENT = (byte) 8;
    private static final byte CHANGE_SIZE_EVENT = (byte) 9;
    private static final byte ATTACK_EVENT = (byte) 10;

    /**
     * The amount of time to spend "severed" before growing
     **/
    private final int maxSeveredTime = 100;
    private int severedTime;

    /**
     * The amount of time to spend "growing" before normal
     **/
    private final int MAX_GROW_TIME = 60;
    private int growTime;

    private final EntityDimensions severedSize;
    private boolean markForSizeChange;

    public HydraHead(final EntityType<? extends HydraHead> type, final Level level) {
        super(type, level);
        severedSize = EntityDimensions.scalable(type.getWidth() * 0.75F, type.getHeight() * 0.25F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 22.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(PART_ID, (byte) 0);
        this.getEntityData().define(STATE, NORMAL);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new HydraHead.BiteAttackGoal());
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, false, false));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // remove when not linked to a hydra
        if (!hasHydra() && !level.isClientSide()) {
            discard();
            return;
        }

        if (!level.isClientSide() && getTarget() != null && null == getHydra().getTarget()) {
            getHydra().setTarget(getTarget());
        }

        // update severed timer
        if (!isCharred() && isSevered() && severedTime > 0 && ++severedTime > maxSeveredTime) {
            severedTime = 0;
            setGrowing();
        }

        // update growing timer
        if (!isCharred() && isGrowing() && growTime > 0 && ++growTime > MAX_GROW_TIME) {
            growTime = 0;
            setHealth(getMaxHealth());
            setNormal();
        }
    }

    @Override
    public void tick() {
        super.tick();
        // recalculate size
        if (markForSizeChange || (this.level.isClientSide() && !this.isNormal())) {
            refreshDimensions();
            markForSizeChange = false;
        }

        if (level.isClientSide() && isCharred() && random.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.SMOKE,
                    getX() + (random.nextDouble() - 0.5D) * getBbWidth(),
                    getY() + getBbHeight(),
                    getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        return ((isSevered() || isGrowing()) && !source.isFire())
                || isCharred() || source == DamageSource.IN_WALL || source == DamageSource.WITHER
                || super.isInvulnerableTo(source);
    }

    @Override
    public void die(DamageSource cause) {
        // set state to "charred" or "severed" depending on fire timer
        if (this.getRemainingFireTicks() > 0) {
            setNoAi(true);
            setCharred();
        } else {
            // set this head to "severed"
            this.setSevered();
            // create another head that is currently "severed"
            HydraHead head = getHydra().addHead(getHydra().getHeads());
            head.setSevered();
            level.addFreshEntity(head);
        }
        // reset health to prevent removal
        this.setHealth(1.0F);
        this.markForSizeChange = true;
        this.refreshDimensions();
        level.broadcastEntityEvent(this, CHANGE_SIZE_EVENT);
    }

    @Override
    protected InteractionResult mobInteract(final Player player, final InteractionHand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        // light this head on fire when flint and steel is used
        if (!itemstack.isEmpty() && itemstack.is(Items.FLINT_AND_STEEL)) {
            final Vec3 pos = this.position();
            this.level.playSound(player, pos.x, pos.y, pos.z, SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F,
                    this.random.nextFloat() * 0.4F + 0.8F);
            player.swing(hand);

            if (!this.level.isClientSide()) {
                this.setSecondsOnFire(4 + random.nextInt(3));
                itemstack.hurtAndBreak(1, player, c -> c.broadcastBreakEvent(hand));
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return isNormal();
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return entityIn instanceof Hydra;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (super.hurt(source, amount) && hasHydra()) {
            getHydra().hurt(source, amount * 0.1F);
        }
        return false;
    }

    @Override
    public boolean doHurtTarget(final Entity entity) {
        if (super.doHurtTarget(entity)) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 5 * 20, 0));
            }
            return true;
        }
        return false;
    }

    @Override
    public MobType getMobType() {
        return GFMobType.SERPENT;
    }

    // Hydra body methods //

    public void setPartId(final int id) {
        getEntityData().set(PART_ID, (byte) id);
    }

    public int getPartId() {
        return getEntityData().get(PART_ID).intValue();
    }

    public boolean hasHydra() {
        return getVehicle() instanceof Hydra;
    }

    /**
     * @return the Hydra this head belongs to, or null if it is not found
     */
    @Nullable
    public Hydra getHydra() {
        if (getVehicle() instanceof Hydra) {
            return (Hydra) getVehicle();
        }
        return null;
    }

    @Override
    public void rideTick() {
        this.setDeltaMovement(Vec3.ZERO);
        if (canUpdate()) {
            this.tick();
        }
        if (this.isPassenger() && hasHydra()) {
            Hydra hydra = getHydra();
            hydra.updatePassenger(this, getPartId(), Entity::setPos);
            // clamp rotation based on hydra rotation
            if (this.yHeadRot > hydra.getYRot() + 80) {
                this.yHeadRot = hydra.getYRot() + 80;
            } else if (this.yHeadRot < hydra.getYRot() - 80) {
                this.yHeadRot = hydra.getYRot() - 80;
            }
        }
    }

    //States //

    public byte getHeadState() {
        return this.getEntityData().get(STATE);
    }

    public void setHeadState(final byte state) {
        this.getEntityData().set(STATE, state);
        this.markForSizeChange = true;
        if (!level.isClientSide()) {
            level.broadcastEntityEvent(this, CHANGE_SIZE_EVENT);
        }
    }

    public boolean isNormal() {
        return getHeadState() == NORMAL;
    }

    public boolean isSevered() {
        return severedTime > 0 || getHeadState() == SEVERED;
    }

    public boolean isGrowing() {
        return growTime > 0 || getHeadState() == GROWING;
    }

    public boolean isCharred() {
        return getHeadState() == CHARRED;
    }

    public void setNormal() {
        setHeadState(NORMAL);
    }

    public void setCharred() {
        setHeadState(CHARRED);
    }

    public void setSevered() {
        setHeadState(SEVERED);
        severedTime = 1;
    }

    public void setGrowing() {
        setHeadState(GROWING);
        growTime = 1;
        if (!this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, GROWING_EVENT);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case GROWING_EVENT:
                setGrowing();
                markForSizeChange = true;
                break;
            case CHANGE_SIZE_EVENT:
                this.markForSizeChange = true;
                break;
            case ATTACK_EVENT:
                swing(InteractionHand.MAIN_HAND);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Size //

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        if(this.isNormal()) {
            return super.getDimensions(poseIn);
        }
        return severedSize;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
        return this.isNormal() ? super.getStandingEyeHeight(pose, size) : severedSize.height * 0.85F;
    }

    // NBT Methods //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_ID, (byte) getPartId());
        compound.putByte(KEY_STATE, this.getHeadState());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setPartId(compound.getByte(KEY_ID));
        setHeadState(compound.getByte(KEY_STATE));
    }

    // render percent helpers

    public float getSpawnPercent(final float partialTick) {
        if(growTime <= 0) {
            return 1.0F;
        }
        return Mth.lerp(partialTick, Math.max(0, growTime - 1), growTime) / (float) MAX_GROW_TIME;
    }

    // Goals //

    class BiteAttackGoal extends Goal {
        private final int attackInterval = 20;
        private final TargetingConditions targetingConditions;

        private int swingCooldown;
        private long lastCheckTime;

        public BiteAttackGoal() {
            super();
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
            this.targetingConditions = TargetingConditions.forCombat().ignoreLineOfSight();
        }

        @Override
        public boolean canUse() {
            long i = HydraHead.this.level.getGameTime();
            // do not execute if timer is too recent or head is severed/charred
            if (i - this.lastCheckTime < attackInterval || !HydraHead.this.isNormal()) {
                return false;
            } else {
                this.lastCheckTime = i;
                LivingEntity livingentity = HydraHead.this.getTarget();
                if (livingentity == null) {
                    return false;
                } else if (!livingentity.isAlive()) {
                    return false;
                } else {
                    return this.getAttackReachSqr(livingentity) >= HydraHead.this.distanceToSqr(livingentity.getX(),
                            livingentity.getY(), livingentity.getZ());
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = HydraHead.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                return targetingConditions.test(HydraHead.this, livingentity);
            }
        }

        @Override
        public void start() {
            HydraHead.this.setAggressive(true);
            this.swingCooldown = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = HydraHead.this.getTarget();
            HydraHead.this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = HydraHead.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            this.swingCooldown = Math.max(this.swingCooldown - 1, 0);
            this.checkAndPerformAttack(livingentity, d0);
        }

        @Override
        public void stop() {
            LivingEntity livingentity = HydraHead.this.getTarget();
            if(null == livingentity || !livingentity.isAlive() || !targetingConditions.test(HydraHead.this, livingentity)) {
                HydraHead.this.setTarget(null);
            }
            HydraHead.this.setAggressive(false);
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.swingCooldown <= 0) {
                this.swingCooldown = attackInterval;
                HydraHead.this.swing(InteractionHand.MAIN_HAND);
                HydraHead.this.level.broadcastEntityEvent(HydraHead.this, ATTACK_EVENT);
                HydraHead.this.doHurtTarget(enemy);
            }
        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return HydraHead.this.getBbWidth() * 4.5F * HydraHead.this.getBbWidth() * 4.5F + attackTarget.getBbWidth();
        }

    }
}
