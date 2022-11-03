package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.entity.util.HasHorseVariant;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

public class Pegasus extends AbstractHorse implements FlyingAnimal, HasHorseVariant {

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Pegasus.class, EntityDataSerializers.INT);
    private static final String KEY_VARIANT = "Variant";

    private static final int FLYING_INTERVAL = 8;
    protected int flyingTime;

    protected boolean isFlying;

    protected GroundPathNavigation groundNavigation;
    protected FlyingPathNavigation flyingNavigation;

    public Pegasus(EntityType<? extends Pegasus> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHorse.createBaseHorseAttributes()
                .add(Attributes.ARMOR, 1.0D)
                .add(ForgeMod.ENTITY_GRAVITY.get(), 0.04D)
                .add(Attributes.FLYING_SPEED, 1.32F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        groundNavigation = new GroundPathNavigation(this, level);
        flyingNavigation = new FlyingPathNavigation(this, level);
        return groundNavigation;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(VARIANT, 0);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new Pegasus.AvoidPlayersGoal(this));
    }

    @Override
    public PathNavigation getNavigation() {
        if (this.isPassenger() && this.getVehicle() instanceof Mob) {
            Mob mob = (Mob)this.getVehicle();
            return mob.getNavigation();
        } else if(this.isFlying()) {
            return this.flyingNavigation;
        } else {
            return this.groundNavigation;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // update flying
        if (flyingTime > 0) {
            flyingTime--;
        }
        if (!isVehicle()) {
            isFlying = false;
        }
        // setting this to true here allows smooth client-side motion
        this.onGround = true;
        // fall slowly when being ridden
        if (isVehicle() && this.getDeltaMovement().y < -0.1D) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.95D, 1.0));
        }

        // take damage when too high
        if (this.position().y > this.level.getHeight() + 16) {
            this.hurt(DamageSource.OUT_OF_WORLD, 2.0F);
        }
    }

    @Override
    public boolean requiresCustomPersistence() {
        return this.isTamed() || super.requiresCustomPersistence();
    }


    // CALLED FROM ON INITIAL SPAWN //

    @Override
    protected void randomizeAttributes(RandomSource random) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.generateRandomMaxHealth(random));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed(random));
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength(random));
        this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(this.getAttributeValue(Attributes.MOVEMENT_SPEED) + 1.15D);
    }

    @Override
    protected float generateRandomMaxHealth(RandomSource random) {
        return super.generateRandomMaxHealth(random) + 10.0F;
    }

    @Override
    protected double generateRandomJumpStrength(RandomSource random) {
        return super.generateRandomJumpStrength(random) + 0.20F;
    }

    @Override
    protected double generateRandomSpeed(RandomSource random) {
        return super.generateRandomSpeed(random);
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficulty, MobSpawnType mobSpawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        Variant color;
        if (spawnDataIn instanceof Horse.HorseGroupData) {
            color = ((Horse.HorseGroupData) spawnDataIn).variant;
        } else {
            color = Util.getRandom(Variant.values(), worldIn.getRandom());
            spawnDataIn = new Horse.HorseGroupData(color);
        }
        // set color and type
        this.setVariant(color, Util.getRandom(Markings.values(), worldIn.getRandom()));
        return super.finalizeSpawn(worldIn, difficulty, mobSpawnType, spawnDataIn, dataTag);
    }

    // FLYING //

    @Override
    public boolean canJump() {
        return super.canJump() && flyingTime <= 0;
    }

    @Override
    public void onPlayerJump(int jumpPowerIn) {
        // Do nothing
    }

    public void flyingJump() {
        if (flyingTime <= 0 && canJump()) {
            // move upward
            float jumpMotion = (float) this.getCustomJump() + 0.82F;
            this.push(0, jumpMotion, 0);
            this.markHurt();
            this.onGround = true;
            // reset flying time
            flyingTime = FLYING_INTERVAL;
            isFlying = true;
        }
    }

    @Override
    public void handleStartJump(int jumpPower) {
        //super.handleStartJump(jumpPower);
        if (!this.onGround) {
            this.setStanding(false);
        }
    }

    @Override
    public boolean isJumping() {
        return false;
    }

    @Override
    public boolean isFlying() {
        final double flyingMotion = isBaby() ? 0.02D : 0.06D;
        return !this.onGround || this.getDeltaMovement().lengthSqr() > flyingMotion;
    }

    @Override
    protected int calculateFallDamage(float distance, float damageMultiplier) {
        return 0;
    }

    @Override
    public void travel(final Vec3 vec) {
        super.travel(vec);
    }

    // MISC //

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.385D;
    }

    @Override
    protected void playGallopSound(SoundType sound) {
        super.playGallopSound(sound);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.HORSE_BREATHE, sound.getVolume() * 0.6F, sound.getPitch());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        super.getAmbientSound();
        return SoundEvents.HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        super.getDeathSound();
        return SoundEvents.HORSE_DEATH;
    }

    @Override
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        super.getAngrySound();
        return SoundEvents.HORSE_ANGRY;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isBaby()) {
            if (this.isTamed() && player.isSecondaryUseActive()) {
                this.openCustomInventoryScreen(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide());
            }

            if (this.isVehicle()) {
                return super.mobInteract(player, hand);
            }

            if ((itemstack.isEmpty() && this.isTamed()) || itemstack.is(GFRegistry.ItemReg.GOLDEN_BRIDLE.get())) {
                this.doPlayerRide(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide());
            }
        }

        if (!itemstack.isEmpty()) {
            if (this.isFood(itemstack)) {
                return this.fedFood(player, itemstack);
            }

            InteractionResult actionresulttype = itemstack.interactLivingEntity(player, this, hand);
            if (actionresulttype.consumesAction()) {
                return actionresulttype;
            }

            if (!this.isTamed()) {
                this.makeMad();
                return InteractionResult.sidedSuccess(this.level.isClientSide());
            }

            boolean isUsableSaddle = !this.isBaby() && !this.isSaddled() && itemstack.is(Items.SADDLE);
            if (this.isArmor(itemstack) || isUsableSaddle) {
                this.openCustomInventoryScreen(player);
                return InteractionResult.sidedSuccess(this.level.isClientSide());
            }
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public int getMaxTemper() {
        return 150;
    }

    // Mate

    @Override
    public boolean canMate(final Animal otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else {
            return otherAnimal instanceof Pegasus && this.canParent() && ((Pegasus) otherAnimal).canParent();
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob mate) {
        Pegasus child;
        Pegasus pegasusMate = (Pegasus) mate;
        child = GFRegistry.EntityReg.PEGASUS.get().create(world);
        int i = this.random.nextInt(9);
        Variant coatcolors;
        if (i < 4) {
            coatcolors = this.getVariant();
        } else if (i < 8) {
            coatcolors = pegasusMate.getVariant();
        } else {
            coatcolors = Util.getRandom(Variant.values(), this.random);
        }

        int j = this.random.nextInt(5);
        Markings coattypes;
        if (j < 2) {
            coattypes = this.getMarkings();
        } else if (j < 4) {
            coattypes = pegasusMate.getMarkings();
        } else {
            coattypes = Util.getRandom(Markings.values(), this.random);
        }

        child.setVariant(coatcolors, coattypes);

        this.setOffspringAttributes(mate, child);
        return child;
    }

    // Color

    @Override
    public void setPackedVariant(int packedColorsTypes) {
        this.entityData.set(VARIANT, packedColorsTypes);
    }

    @Override
    public int getPackedVariant() {
        return this.entityData.get(VARIANT);
    }

    // NBT

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(KEY_VARIANT, this.getPackedVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setPackedVariant(compound.getInt(KEY_VARIANT));
    }

    class AvoidPlayersGoal extends AvoidEntityGoal<Player> {

        public AvoidPlayersGoal(final Pegasus pegasus) {
            super(pegasus, Player.class, 16.0F, 1.2D, 1.1D, (entity) -> !entity.isDiscrete() && !pegasus.isVehicle()
                    && (!pegasus.isTamed() || pegasus.getOwnerUUID() == null || !entity.getUUID().equals(pegasus.getOwnerUUID())));
        }

        @Override
        public boolean canUse() {
            // access tranformer exposes this field to update frequently
            this.pathNav = this.mob.getNavigation();
            return super.canUse();
        }
    }
}
