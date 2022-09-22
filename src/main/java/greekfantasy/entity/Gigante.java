package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.item.ClubItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.UUID;

public class Gigante extends PathfinderMob implements NeutralMob {
    private static final int ATTACK_COOLDOWN = 32;

    private int attackCooldown;

    private static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private int angerTime;
    private UUID angerTarget;

    public Gigante(final EntityType<? extends Gigante> type, final Level level) {
        super(type, level);
        this.xpReward = 10;
        // avoid water because this entity does not swim or float
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.5D)
                .add(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.65D)
                .add(Attributes.ARMOR, 5.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new GiganteAttackGoal(1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // attack cooldown
        attackCooldown = Math.max(attackCooldown - 1, 0);

        // particles
        if (level.isClientSide() && this.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F && this.random.nextInt(5) == 0) {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY() - (double) 0.2F);
            int k = Mth.floor(this.getZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = this.level.getBlockState(pos);
            if (!blockstate.isAir()) {
                this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos), this.getX() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), this.getY() + 0.1D, this.getZ() + ((double) this.random.nextFloat() - 0.5D) * (double) this.getBbWidth(), 4.0D * ((double) this.random.nextFloat() - 0.5D), 0.5D, ((double) this.random.nextFloat() - 0.5D) * 4.0D);
            }
        }

        if (!this.level.isClientSide()) {
            this.updatePersistentAnger((ServerLevel) this.level, true);
        }

    }

    @Override
    protected float getJumpPower() {
        return 1.2F * super.getJumpPower();
    }

    @Override
    public boolean doHurtTarget(final Entity entityIn) {
        if (super.doHurtTarget(entityIn)) {
            // apply extra knockback velocity when attacking (ignores knockback resistance)
            final double knockbackFactor = 0.2D;
            final Vec3 myPos = this.position();
            final Vec3 ePos = entityIn.position();
            final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
            final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
            entityIn.push(dX, knockbackFactor / 2.0D, dZ);
            entityIn.hurtMarked = true;
            return true;
        }
        return false;
    }

    @Override
    protected void dropEquipment() {
        // damage held equipment before dropping it
        ItemStack mainhand = this.getMainHandItem();
        if (mainhand.isDamageableItem()) {
            // set item damage to some value between 30% and 50%
            int damage = Mth.floor((0.30F + 0.20F * random.nextFloat()) * mainhand.getMaxDamage());
            mainhand.setDamageValue(damage);
        }
        super.dropEquipment();
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        // note: do not call super method because this entity cannot wear armor
        if (random.nextBoolean()) {
            // determine club type
            Item club = random.nextBoolean() ? GFRegistry.ItemReg.STONE_CLUB.get() : GFRegistry.ItemReg.WOODEN_CLUB.get();
            ItemStack itemStack = new ItemStack(club);
            // randomly enchant club
            if (random.nextFloat() < 0.10F * difficulty.getSpecialMultiplier()) {
                itemStack.enchant(Enchantments.KNOCKBACK, 1);
            }
            // update held item and drop chance
            this.setItemInHand(InteractionHand.MAIN_HAND, itemStack);
            this.setDropChance(EquipmentSlot.MAINHAND, 0.165F);
        }
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficulty, MobSpawnType mobType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return super.finalizeSpawn(worldIn, difficulty, mobType, spawnDataIn, dataTag);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public float getVoicePitch() {
        return 0.12F;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double dis) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.readPersistentAngerSaveData(this.level, compound);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entityIn) {
        if (this.isPushable()) {
            super.push(entityIn);
        }
    }

    // IAngerable methods

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

    // Cooldown methods

    public void setAttackCooldown() {
        attackCooldown = ATTACK_COOLDOWN;
    }

    public boolean hasNoCooldown() {
        return attackCooldown <= 0;
    }

    class GiganteAttackGoal extends MeleeAttackGoal {

        public GiganteAttackGoal(double speedIn, boolean useLongMemory) {
            super(Gigante.this, speedIn, useLongMemory);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (Gigante.this.hasNoCooldown()) {
                super.checkAndPerformAttack(enemy, distToEnemySqr);
            }
        }

        @Override
        protected void resetAttackCooldown() {
            super.resetAttackCooldown();
            Gigante.this.setAttackCooldown();
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return super.getAttackReachSqr(attackTarget) - 3.0D;
        }
    }

}
