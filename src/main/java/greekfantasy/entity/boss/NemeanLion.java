package greekfantasy.entity.boss;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.CooldownMeleeAttackGoal;
import greekfantasy.entity.ai.MoveToStructureGoal;
import greekfantasy.entity.util.HasCustomCooldown;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.EnumSet;

public class NemeanLion extends Monster implements HasCustomCooldown {

    private static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(NemeanLion.class, EntityDataSerializers.BYTE);
    private static final String KEY_STATE = "NemeanState";
    private static final String KEY_ATTACK_TIME = "AttackTime";
    private static final String KEY_SITTING_TIME = "SittingTime";
    //bytes to use in STATE
    private static final byte NONE = (byte) 0;
    private static final byte SPAWNING = (byte) 1; // unused
    private static final byte ROARING = (byte) 2; // unused
    private static final byte ATTACKING = (byte) 3;
    private static final byte SITTING = (byte) 4;
    // bytes to use in Level#broadcastEntityEvent
    private static final byte SITTING_START_EVENT = 8;
    private static final byte SITTING_END_EVENT = 9;

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);
    private static final int ATTACK_COOLDOWN = 45;
    private int attackCooldown;

    private static final int MAX_ATTACK_TIME = 6;
    private int attackTime0;
    private int attackTime;

    private static final int MAX_SITTING_TIME = 10;
    private int sittingTime0;
    private int sittingTime;

    public NemeanLion(final EntityType<? extends NemeanLion> type, final Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.92D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ARMOR, 5.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, NONE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new NemeanLion.RunAroundLikeCrazyGoal(1.0D));
        this.goalSelector.addGoal(2, new NemeanLion.SitGoal());
        this.goalSelector.addGoal(3, new NemeanLion.NemeanLionAttackGoal(1.15D, true, ATTACK_COOLDOWN));
        this.goalSelector.addGoal(4, new MoveToStructureGoal(this, 1.0D, 3, 8, 4, new ResourceLocation(GreekFantasy.MODID, "lion_den"), DefaultRandomPos::getPos));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.86D) {
            @Override
            public boolean canUse() {
                return !NemeanLion.this.isSitting() && NemeanLion.this.random.nextInt(400) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, e -> !NemeanLion.this.isVehicle()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, 10, false, false, e -> e.canChangeDimensions() && !e.isInWater() && !NemeanLion.this.isVehicle()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, 10, false, false, e -> !NemeanLion.this.isVehicle()));

    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        // attack cooldown
        attackCooldown = Math.max(attackCooldown - 1, 0);

        // update attacking time
        attackTime0 = attackTime;
        if(this.isAttacking()) {
            // stop attacking when timer reaches max
            if(attackTime++ >= MAX_ATTACK_TIME) {
                this.setAttacking(false);
            }
        } else {
            attackTime = Math.max(0, attackTime - 1);
        }

        boolean sitting = isSitting();
        // update sitting time
        sittingTime0 = sittingTime;
        if(sitting) {
            sittingTime = Math.min(sittingTime + 1, MAX_SITTING_TIME);
        } else {
            sittingTime = Math.max(sittingTime - 1, 0);
        }

        // randomly change sitting position when not attacking or wanting to attack
        if(!this.level.isClientSide()) {
            if(sitting && (this.isAggressive() || this.getTarget() != null || !getPassengers().isEmpty())) {
                setSitting(false);
            } else if(random.nextFloat() < 0.0009F) {
                setSitting(!sitting);
            }
        }

        // update rotation and attack damage while being ridden
        if (isVehicle() && getPassengers().get(0) instanceof Player) {
            Player player = (Player) getPassengers().get(0);
            this.setRot(player.getYRot(), player.getXRot() * 0.5F);
            // strangling damage
            if (this.hurtTime == 0 && !level.isClientSide()) {
                this.hurt(DamageSource.playerAttack(player), 1.0F + random.nextFloat());
                // remove regen
                if (this.getEffect(MobEffects.REGENERATION) != null) {
                    this.removeEffect(MobEffects.REGENERATION);
                }
            }
        }
    }

    // Misc //

    @Override
    public boolean doHurtTarget(final Entity entity) {
        if (super.doHurtTarget(entity)) {
            setAttacking(true);
            return true;
        }
        return false;
    }

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
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level.isClientSide() && this.canAddPassenger(player)) {
                // mount the player to the entity
                player.startRiding(this);
                // reset sitting
                if (isSitting()) {
                    setSitting(false);
                }
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }

        return InteractionResult.FAIL;
    }

    @Override
    public void ejectPassengers() {
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
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        if(this.hasCustomName()) {
            bossInfo.setName(this.getCustomName());
        }
        bossInfo.setVisible(GreekFantasy.CONFIG.showNemeanLionBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // States //

    public byte getNemeanState() {
        return this.getEntityData().get(STATE);
    }

    public void setNemeanState(final byte state) {
        this.getEntityData().set(STATE, state);
    }

    public boolean isNoneState() {
        return getNemeanState() == NONE;
    }

    public boolean isAttacking() {
        return getNemeanState() == ATTACKING;
    }

    public void setAttacking(boolean attacking) {
        if(attacking) {
            setNemeanState(ATTACKING);
        } else {
            setNemeanState(NONE);
        }
    }

    public boolean isSitting() {
        return getNemeanState() == SITTING;
    }

    public void setSitting(final boolean sitting) {
        if(sitting) {
            setNemeanState(SITTING);
            this.level.broadcastEntityEvent(this, SITTING_START_EVENT);
        } else {
            setNemeanState(NONE);
            this.level.broadcastEntityEvent(this, SITTING_END_EVENT);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case SITTING_START_EVENT:
                setSitting(true);
                break;
            case SITTING_END_EVENT:
                setSitting(false);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    public float getAttackPercent(final float partialTick) {
        return Mth.lerp(partialTick, attackTime0, attackTime) / (float) MAX_ATTACK_TIME;
    }

    public float getSittingPercent(final float partialTick) {
        return Mth.lerp(partialTick, sittingTime0, sittingTime) / (float) MAX_SITTING_TIME;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getNemeanState());
        compound.putInt(KEY_ATTACK_TIME, this.attackTime);
        compound.putInt(KEY_SITTING_TIME, this.sittingTime);
        saveCustomCooldown(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setNemeanState(compound.getByte(KEY_STATE));
        this.attackTime = compound.getInt(KEY_ATTACK_TIME);
        this.sittingTime = compound.getInt(KEY_SITTING_TIME);
        readCustomCooldown(compound);
    }

    @Override
    public void setCustomCooldown(int cooldown) {
        attackCooldown = cooldown;
    }

    public int getCustomCooldown() {
        return attackCooldown;
    }

    class SitGoal extends Goal {

        public SitGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            return NemeanLion.this.isSitting();
        }

        @Override
        public void start() {
            super.start();
            NemeanLion.this.setPose(Pose.CROUCHING);
        }

        @Override
        public void tick() {
            NemeanLion.this.getNavigation().stop();
        }

        @Override
        public void stop() {
            super.stop();
            if(NemeanLion.this.getPose() == Pose.CROUCHING) {
                NemeanLion.this.setPose(Pose.STANDING);
            }
        }
    }

    class NemeanLionAttackGoal extends CooldownMeleeAttackGoal<NemeanLion> {

        public NemeanLionAttackGoal(double speedIn, boolean useLongMemory, int cooldown) {
            super(NemeanLion.this, speedIn, useLongMemory, cooldown);
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return super.getAttackReachSqr(attackTarget) - 2.0D;
        }
    }

    class RunAroundLikeCrazyGoal extends Goal {

        private final NemeanLion lion;
        private final double speed;
        private double targetX;
        private double targetY;
        private double targetZ;

        public RunAroundLikeCrazyGoal(double speedIn) {
            this.lion = NemeanLion.this;
            this.speed = speedIn;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            if (this.lion.isVehicle()) {
                Vec3 vector3d = DefaultRandomPos.getPos(this.lion, 5, 4);
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
                // throw the passenger
                Entity e = this.lion.getPassengers().get(0);
                this.lion.ejectPassengers();
                if (e instanceof LivingEntity) {
                    LivingEntity passenger = (LivingEntity) e;
                    passenger.knockback(random.nextDouble() * 2.0F - 0.5D, random.nextDouble() * 2.0D - 0.75D, random.nextDouble() * 2.0D - 0.5D);
                    passenger.hurtMarked = true;
                }
            }

        }

    }
}
