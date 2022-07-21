package greekfantasy.entity;

import greekfantasy.entity.util.HasHorseVariant;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.UUID;

public class Centaur extends PathfinderMob implements NeutralMob, RangedAttackMob, HasHorseVariant {

    private static final EntityDataAccessor<Byte> DATA_COLOR = SynchedEntityData.defineId(Centaur.class, EntityDataSerializers.BYTE);
    private static final String TAG_COLOR = "Color";

    private static final byte REARING_START = 6;
    private static final byte REARING_END = 7;

    private static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private int angerTime;
    private UUID angerTarget;

    private boolean isRearing;

    private int tailCounter;
    private float rearingAmount;
    private float prevRearingAmount;
    private int rearingCounter;

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
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new Centaur.RangedAttackGoal(this, 1.0D, this.hasBullHead() ? 50 : 35, 15.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_COLOR, (byte) 0);
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
        compound.putByte(TAG_COLOR, (byte) this.getVariant().getId());
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(Variant.byId(compound.getByte(TAG_COLOR)));
        this.readPersistentAngerSaveData(this.level, compound);
    }

    //IAngerable methods

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

    // End IAngerable methods

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        // set variant when not spawned as part of a structure
        if(spawnType != MobSpawnType.STRUCTURE) {
            // determine color variant based on spawn group data, or create new group data
            Variant color;
            if (spawnDataIn instanceof Satyr.GroupData) {
                color = ((Satyr.GroupData) spawnDataIn).variant;
            } else {
                color = Util.getRandom(Variant.values(), this.random);
                spawnDataIn = new Satyr.GroupData(color);
            }
            this.setVariant(color);
        }
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
            case REARING_START:
                this.isRearing = true;
                break;
            case REARING_END:
                this.isRearing = false;
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
        this.level.broadcastEntityEvent(this, rearing ? REARING_START : REARING_END);
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

    // Coat colors
    public void setVariant(final Variant color) {
        this.getEntityData().set(DATA_COLOR, (byte) color.getId());
    }

    public Variant getVariant() {
        return Variant.byId(this.getEntityData().get(DATA_COLOR).intValue());
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
}
