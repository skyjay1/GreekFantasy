package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumSet;

public class NemeanLionEntity extends MonsterEntity {

    private static final DataParameter<Byte> STATE = EntityDataManager.defineId(NemeanLionEntity.class, DataSerializers.BYTE);
    private static final String KEY_STATE = "NemeanState";
    //bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1; // unused
    private static final byte ROARING = (byte) 2; // unused
    private static final byte ATTACKING = (byte) 3; // unused
    private static final byte SITTING = (byte) 4;
    // bytes to use in World#setEntityState
    private static final byte SITTING_START_CLIENT = 8;
    private static final byte SITTING_END_CLIENT = 9;

    private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS);

    public NemeanLionEntity(final EntityType<? extends NemeanLionEntity> type, final World worldIn) {
        super(type, worldIn);
        this.setPersistenceRequired();
        this.maxUpStep = 1.0F;
        this.xpReward = 50;
        bossInfo.setVisible(GreekFantasy.CONFIG.showNemeanLionBossBar());
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.92D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, Byte.valueOf(NONE));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new NemeanLionEntity.RunAroundLikeCrazyGoal(1.0D));
        this.goalSelector.addGoal(2, new NemeanLionEntity.SitGoal());
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.54F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.15D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.86D) {
            @Override
            public boolean canUse() {
                return !NemeanLionEntity.this.isSitting() && NemeanLionEntity.this.random.nextInt(400) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 10, false, false, e -> EntityPredicates.ATTACK_ALLOWED.test(e) && !NemeanLionEntity.this.isVehicle()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AnimalEntity.class, 10, false, false, e -> EntityPredicates.ATTACK_ALLOWED.test(e) && e.canChangeDimensions() && !e.isInWater() && !NemeanLionEntity.this.isVehicle()));

    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

        // randomly sit, or unsit if attacking
        if (!this.level.isClientSide()) {
            if (this.getTarget() == null && getPassengers().isEmpty()) {
                if (random.nextFloat() < 0.0022F) {
                    setSitting(!isSitting());
                }
            } else if (isSitting()) {
                setSitting(false);
            }
        }

        // update rotation and attack damage while being ridden
        if (isVehicle() && getPassengers().get(0) instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) getPassengers().get(0);
            this.yRot = player.yRot;
            this.yRotO = this.yRot;
            this.xRot = player.xRot * 0.5F;
            this.setRot(this.yRot, this.xRot);
            this.yBodyRot = this.yRot;
            this.yHeadRot = this.yBodyRot;
            // strangling damage
            if (this.hurtTime == 0 && !level.isClientSide()) {
                this.hurt(DamageSource.playerAttack(player), 1.0F + random.nextFloat());
                // remove regen
                if (this.getEffect(Effects.REGENERATION) != null) {
                    this.removeEffect(Effects.REGENERATION);
                }
            }
        }
    }

    // Misc //

    @Override
    protected void actuallyHurt(final DamageSource source, final float amountIn) {
        float damageAmount = amountIn;
        // cap damage at 2.0 (1 heart)
        if (!source.isBypassMagic() && !source.isMagic() && !source.isBypassArmor()) {
            damageAmount = Math.min(2.0F, amountIn);
        }
        // stop sitting when hurt
        if (!this.level.isClientSide() && this.isSitting()) {
            this.setSitting(false);
        }
        super.actuallyHurt(source, damageAmount);
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        return source == DamageSource.IN_WALL || source == DamageSource.WITHER
                || source == DamageSource.CACTUS || source == DamageSource.SWEET_BERRY_BUSH
                || source.isProjectile() || super.isInvulnerableTo(source);
    }

    @Override
    protected void doPush(final Entity entityIn) {
        // stop sitting when collided with entity
        if (entityIn instanceof LivingEntity && !this.level.isClientSide() && this.isSitting()) {
            this.setSitting(false);
        }
        super.doPush(entityIn);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    // Riding //

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() + 0.805D;
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if (!this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level.isClientSide() && this.canAddPassenger(player)) {
                // mount the player to the entity
                player.startRiding(this);
                // reset sitting
                if (isSitting()) {
                    setSitting(false);
                }
            }
            return ActionResultType.sidedSuccess(this.level.isClientSide);
        }

        return ActionResultType.FAIL;
    }

    @Override
    public void ejectPassengers() {
        if (this.getPassengers().size() > 0) {
            // give lion regen effect when player stops strangling
            addEffect(new EffectInstance(Effects.REGENERATION, 84, 0));
        }
        super.ejectPassengers();
    }

    // Boss //

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // States //

    public byte getNemeanState() {
        return this.getEntityData().get(STATE).byteValue();
    }

    public void setNemeanState(final byte state) {
        this.getEntityData().set(STATE, Byte.valueOf(state));
    }

    public boolean isNoneState() {
        return getNemeanState() == NONE;
    }

    public boolean isSitting() {
        return getNemeanState() == SITTING;
    }

    public void setSitting(final boolean sitting) {
        setNemeanState(sitting ? SITTING : NONE);
        if (!this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, sitting ? SITTING_START_CLIENT : SITTING_END_CLIENT);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        switch (id) {
            case SITTING_START_CLIENT:
                setSitting(true);
                break;
            case SITTING_END_CLIENT:
                setSitting(false);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getNemeanState());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setNemeanState(compound.getByte(KEY_STATE));
    }

    private class SitGoal extends Goal {

        public SitGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            return NemeanLionEntity.this.isSitting();
        }

        @Override
        public void tick() {
            NemeanLionEntity.this.getNavigation().stop();
        }

    }

    private class RunAroundLikeCrazyGoal extends Goal {

        private final NemeanLionEntity lion;
        private final double speed;
        private double targetX;
        private double targetY;
        private double targetZ;

        public RunAroundLikeCrazyGoal(double speedIn) {
            this.lion = NemeanLionEntity.this;
            this.speed = speedIn;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.lion.isVehicle()) {
                Vector3d vector3d = RandomPositionGenerator.getPos(this.lion, 5, 4);
                if (vector3d == null) {
                    return false;
                } else {
                    this.targetX = vector3d.x;
                    this.targetY = vector3d.y;
                    this.targetZ = vector3d.z;
                    return true;
                }
            } else {
                return false;
            }
        }

        @Override
        public void start() {
            this.lion.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speed);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.lion.getNavigation().isDone() && this.lion.isVehicle();
        }

        @Override
        public void tick() {
            // randomly remove the passenger
            if (this.lion.getRandom().nextInt(42) == 0) {
                // throw the passenger and apply attack damage
                Entity e = this.lion.getPassengers().get(0);
                this.lion.ejectPassengers();
                if (e instanceof LivingEntity) {
                    LivingEntity passenger = (LivingEntity) e;
                    passenger.knockback(2.5F + random.nextFloat() * 2.0F, random.nextDouble() * 2.0D - 1.0D, random.nextDouble() * 2.0D - 1.0D);
                    passenger.hurtMarked = true;
                    lion.doHurtTarget(passenger);
                }
            }

        }

    }
}
