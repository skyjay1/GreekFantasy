package greekfantasy.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class HydraHeadEntity extends MonsterEntity {

    private static final DataParameter<Byte> PART_ID = EntityDataManager.defineId(HydraHeadEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> STATE = EntityDataManager.defineId(HydraHeadEntity.class, DataSerializers.BYTE);
    private static final String KEY_ID = "HydraHeadId";
    private static final String KEY_STATE = "HydraHeadState";
    // bytes to use in STATE
    private static final byte NORMAL = (byte) 0;
    private static final byte SEVERED = (byte) 1;
    private static final byte GROWING = (byte) 2;
    private static final byte CHARRED = (byte) 3;
    // bytes to use in World#setEntityState
    private static final byte GROWING_CLIENT = (byte) 8;
    private static final byte CHANGE_SIZE_CLIENT = (byte) 9;

    /**
     * The amount of time to spend "severed" before growing
     **/
    private final int maxSeveredTime = 100;
    private int severedTime;

    /**
     * The amount of time to spend "growing" before normal
     **/
    private final int maxGrowTime = 60;
    private int growTime;

    private final EntitySize severedSize;

    public HydraHeadEntity(final EntityType<? extends HydraHeadEntity> type, final World world) {
        super(type, world);
        severedSize = EntitySize.scalable(type.getWidth() * 0.75F, type.getHeight() * 0.25F);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 22.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(PART_ID, Byte.valueOf((byte) 0));
        this.getEntityData().define(STATE, Byte.valueOf(NORMAL));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(4, new HydraHeadEntity.BiteAttackGoal());
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, false, false));
    }

    @Override
    public void tick() {
        // remove when not linked to a hydra
        if (!hasHydra() && !level.isClientSide()) {
            remove();
            return;
        }

        // update severed timer
        if (!isCharred() && isSevered() && severedTime > 0 && ++severedTime > maxSeveredTime) {
            severedTime = 0;
            setGrowing();
        }

        // update growing timer
        if (!isCharred() && isGrowing() && growTime > 0 && ++growTime > maxGrowTime) {
            growTime = 0;
            setHealth(getMaxHealth());
            setNormal();
        }

        // recalculate size
        if (isCharred() || isSevered()) {
            refreshDimensions();
        }

        if (isCharred() && level.isClientSide() && random.nextInt(5) == 0) {
            level.addParticle(ParticleTypes.SMOKE,
                    getX() + (random.nextDouble() - 0.5D) * getBbWidth(),
                    getY() + getBbHeight(),
                    getZ() + (random.nextDouble() - 0.5D) * getBbWidth(), 0.0D, 0.0D, 0.0D);
        }

        super.tick();
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
            HydraHeadEntity head = getHydra().addHead(getHydra().getHeads());
            head.setSevered();
            level.addFreshEntity(head);
        }
        // reset health to prevent removal
        this.setHealth(1.0F);
        this.refreshDimensions();
        level.broadcastEntityEvent(this, CHANGE_SIZE_CLIENT);
    }

    @Override
    protected ActionResultType mobInteract(final PlayerEntity player, final Hand hand) {
        final ItemStack itemstack = player.getItemInHand(hand);
        // light this head on fire when flint and steel is used
        if (!itemstack.isEmpty() && itemstack.getItem() == Items.FLINT_AND_STEEL) {
            final Vector3d pos = this.position();
            this.level.playSound(player, pos.x, pos.y, pos.z, SoundEvents.FLINTANDSTEEL_USE, this.getSoundSource(), 1.0F,
                    this.random.nextFloat() * 0.4F + 0.8F);
            player.swing(hand);

            if (!this.level.isClientSide()) {
                this.setSecondsOnFire(4 + random.nextInt(3));
                itemstack.hurtAndBreak(1, player, c -> c.broadcastBreakEvent(hand));
            }
            return ActionResultType.SUCCESS;
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
        return entityIn instanceof HydraEntity;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (super.hurt(source, amount) && hasHydra()) {
            getHydra().hurt(source, amount * 0.1F);
            if (getTarget() != null) {
                getHydra().setTarget(getTarget());
            }
        }
        return false;
    }

    // Hydra body methods //

    public void setPartId(final int id) {
        getEntityData().set(PART_ID, Byte.valueOf((byte) id));
    }

    public int getPartId() {
        return getEntityData().get(PART_ID).intValue();
    }

    public boolean hasHydra() {
        return getVehicle() instanceof HydraEntity;
    }

    /**
     * @return the Hydra this head belongs to, or null if it is not found
     */
    @Nullable
    public HydraEntity getHydra() {
        if (getVehicle() instanceof HydraEntity) {
            return (HydraEntity) getVehicle();
        }
        return null;
    }

    @Override
    public void rideTick() {
        this.setDeltaMovement(Vector3d.ZERO);
        if (canUpdate()) {
            this.tick();
        }
        if (this.isPassenger() && hasHydra()) {
            HydraEntity hydra = getHydra();
            hydra.updatePassenger(this, getPartId(), Entity::setPos);
            // clamp rotation based on hydra rotation
            if (this.yHeadRot > hydra.yRot + 80) {
                this.yHeadRot = hydra.yRot + 80;
            } else if (this.yHeadRot < hydra.yRot - 80) {
                this.yHeadRot = hydra.yRot - 80;
            }
        }
    }

    //States //

    public byte getHeadState() {
        return this.getEntityData().get(STATE).byteValue();
    }

    public void setHeadState(final byte state) {
        this.getEntityData().set(STATE, Byte.valueOf(state));
        this.refreshDimensions();
        if (!level.isClientSide()) {
            level.broadcastEntityEvent(this, CHANGE_SIZE_CLIENT);
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
            this.level.broadcastEntityEvent(this, GROWING_CLIENT);
        }
    }


    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        switch (id) {
            case GROWING_CLIENT:
                setGrowing();
                refreshDimensions();
                break;
            case CHANGE_SIZE_CLIENT:
                refreshDimensions();
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    // Size //

    @Override
    public EntitySize getDimensions(Pose poseIn) {
        return this.isNormal() ? super.getDimensions(poseIn) : severedSize;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize size) {
        return this.isNormal() ? super.getStandingEyeHeight(pose, size) : severedSize.height * 0.85F;
    }

    // NBT Methods //

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_ID, (byte) getPartId());
        compound.putByte(KEY_STATE, this.getHeadState());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        setPartId(compound.getByte(KEY_ID));
        setHeadState(compound.getByte(KEY_STATE));
    }

    // render percent helpers

    @OnlyIn(Dist.CLIENT)
    public float getSpawnPercent() {
        return growTime > 0 ? (float) growTime / (float) maxGrowTime : 1.0F;
    }

    // Goals //

    protected class BiteAttackGoal extends Goal {
        private final int attackInterval = 20;

        private int swingCooldown;
        private long lastCheckTime;

        public BiteAttackGoal() {
            super();
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            long i = HydraHeadEntity.this.level.getGameTime();
            // do not execute if timer is too recent or head is severed/charred
            if (i - this.lastCheckTime < attackInterval || !HydraHeadEntity.this.isNormal()) {
                return false;
            } else {
                this.lastCheckTime = i;
                LivingEntity livingentity = HydraHeadEntity.this.getTarget();
                if (livingentity == null) {
                    return false;
                } else if (!livingentity.isAlive()) {
                    return false;
                } else {
                    return this.getAttackReachSqr(livingentity) >= HydraHeadEntity.this.distanceToSqr(livingentity.getX(),
                            livingentity.getY(), livingentity.getZ());
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity livingentity = HydraHeadEntity.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                return EntityPredicates.ATTACK_ALLOWED.test(livingentity);
            }
        }

        @Override
        public void start() {
            HydraHeadEntity.this.setAggressive(true);
            this.swingCooldown = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = HydraHeadEntity.this.getTarget();
            HydraHeadEntity.this.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            double d0 = HydraHeadEntity.this.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());

            this.swingCooldown = Math.max(this.swingCooldown - 1, 0);
            this.checkAndPerformAttack(livingentity, d0);
        }

        @Override
        public void stop() {
            LivingEntity livingentity = HydraHeadEntity.this.getTarget();
            if (!EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
                HydraHeadEntity.this.setTarget(null);
            }

            HydraHeadEntity.this.setAggressive(false);
        }

        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.swingCooldown <= 0) {
                this.swingCooldown = attackInterval;
                HydraHeadEntity.this.swing(Hand.MAIN_HAND);
                HydraHeadEntity.this.doHurtTarget(enemy);
            }

        }

        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return HydraHeadEntity.this.getBbWidth() * 4.5F * HydraHeadEntity.this.getBbWidth() * 4.5F + attackTarget.getBbWidth();
        }

    }
}
