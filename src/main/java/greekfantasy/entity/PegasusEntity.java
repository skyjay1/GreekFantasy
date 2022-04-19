package greekfantasy.entity;

import greekfantasy.GFRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.CoatColors;
import net.minecraft.entity.passive.horse.CoatTypes;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;

public class PegasusEntity extends AbstractHorseEntity implements IFlyingAnimal {

    private static final DataParameter<Integer> DATA_COLOR = EntityDataManager.defineId(PegasusEntity.class, DataSerializers.INT);
    private static final String KEY_COLOR = "Color";

    private static final int FLYING_INTERVAL = 8;
    protected int flyingTime;
    protected int navigatorTimer;

    protected boolean isFlying;

//  protected final GroundPathNavigator groundNavigator;
//  protected final FlyingPathNavigator flyingNavigator;

    public PegasusEntity(EntityType<? extends PegasusEntity> type, World worldIn) {
        super(type, worldIn);
//    groundNavigator = new GroundPathNavigator(this, worldIn);
//    flyingNavigator = new FlyingPathNavigator(this, worldIn);
//    flyingNavigator.setCanSwim(true);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractHorseEntity.createBaseHorseAttributes()
                .add(Attributes.ARMOR, 1.0D)
                .add(ForgeMod.ENTITY_GRAVITY.get(), 0.04D)
                .add(Attributes.FLYING_SPEED, 1.32F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_COLOR, 0);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new AvoidPlayersGoal(this));
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
        if (this.position().y > 300) {
            this.hurt(DamageSource.OUT_OF_WORLD, 2.0F);
        }
    }
    @Override
    public boolean requiresCustomPersistence() {
        return this.isTamed() || super.requiresCustomPersistence();
    }


    // CALLED FROM ON INITIAL SPAWN //

    @Override
    protected void randomizeAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.generateRandomMaxHealth());
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed());
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
        this.getAttribute(Attributes.FLYING_SPEED).setBaseValue(this.getAttributeValue(Attributes.MOVEMENT_SPEED) + 1.15D);
    }

    @Override
    protected float generateRandomMaxHealth() {
        return super.generateRandomMaxHealth() + 10.0F;
    }

    @Override
    protected double generateRandomJumpStrength() {
        return super.generateRandomJumpStrength() + 0.20F;
    }

    @Override
    protected double generateRandomSpeed() {
        return super.generateRandomSpeed();
    }

    @Nullable
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
                                           @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        CoatColors color;
        if (spawnDataIn instanceof HorseEntity.HorseData) {
            color = ((HorseEntity.HorseData) spawnDataIn).variant;
        } else {
            color = Util.getRandom(CoatColors.values(), this.random);
            spawnDataIn = new HorseEntity.HorseData(color);
        }
        // set color and type
        this.setCoatColor(color, Util.getRandom(CoatTypes.values(), this.random));
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    // FLYING //

    @Override
    public boolean canJump() {
        return super.canJump() && flyingTime <= 0;
    }

    @OnlyIn(Dist.CLIENT)
    public void onPlayerJump(int jumpPowerIn) {
        // Do nothing
    }

    @OnlyIn(Dist.CLIENT)
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
    protected int calculateFallDamage(float distance, float damageMultiplier) {
        return 0; //MathHelper.ceil((distance * 0.5F - 3.0F) * damageMultiplier);
    }

    @Override
    public void travel(final Vector3d vec) {
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
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.isBaby()) {
            if (this.isTamed() && player.isSecondaryUseActive()) {
                this.openInventory(player);
                return ActionResultType.sidedSuccess(this.level.isClientSide());
            }

            if (this.isVehicle()) {
                return super.mobInteract(player, hand);
            }

            if ((itemstack.isEmpty() && this.isTamed()) || itemstack.getItem() == GFRegistry.GOLDEN_BRIDLE) {
                this.doPlayerRide(player);
                return ActionResultType.sidedSuccess(this.level.isClientSide());
            }
        }

        if (!itemstack.isEmpty()) {
            if (this.isFood(itemstack)) {
                return this.fedFood(player, itemstack);
            }

            ActionResultType actionresulttype = itemstack.interactLivingEntity(player, this, hand);
            if (actionresulttype.consumesAction()) {
                return actionresulttype;
            }

            if (!this.isTamed()) {
                this.makeMad();
                return ActionResultType.sidedSuccess(this.level.isClientSide());
            }

            boolean flag = !this.isBaby() && !this.isSaddled() && itemstack.getItem() == Items.SADDLE;
            if (this.isArmor(itemstack) || flag) {
                this.openInventory(player);
                return ActionResultType.sidedSuccess(this.level.isClientSide());
            }
        }

//    if (this.isChild()) {
        return super.mobInteract(player, hand);
//    } else {
//      this.mountTo(player);
//      return ActionResultType.sidedSuccess(this.world.isRemote());
//    }
    }

    @Override
    public int getMaxTemper() {
        return 160;
    }

    // Mate

    @Override
    public boolean canMate(final AnimalEntity otherAnimal) {
        if (otherAnimal == this) {
            return false;
        } else {
            return otherAnimal instanceof PegasusEntity && this.canParent() && ((PegasusEntity) otherAnimal).canParent();
        }
    }

    @Override
    public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity mate) {
        PegasusEntity child;
        PegasusEntity pegasusMate = (PegasusEntity) mate;
        child = GFRegistry.PEGASUS_ENTITY.create(world);
        int i = this.random.nextInt(9);
        CoatColors coatcolors;
        if (i < 4) {
            coatcolors = this.getCoatColor();
        } else if (i < 8) {
            coatcolors = pegasusMate.getCoatColor();
        } else {
            coatcolors = Util.getRandom(CoatColors.values(), this.random);
        }

        int j = this.random.nextInt(5);
        CoatTypes coattypes;
        if (j < 2) {
            coattypes = this.getCoatType();
        } else if (j < 4) {
            coattypes = pegasusMate.getCoatType();
        } else {
            coattypes = Util.getRandom(CoatTypes.values(), this.random);
        }

        child.setCoatColor(coatcolors, coattypes);

        this.setOffspringAttributes(mate, child);
        return child;
    }

    // Color

    public void setPackedCoatColor(int packedColorsTypes) {
        this.entityData.set(DATA_COLOR, packedColorsTypes);
    }

    public int getPackedCoatColor() {
        return this.entityData.get(DATA_COLOR);
    }

    public void setCoatColor(CoatColors color, CoatTypes type) {
        this.setPackedCoatColor(color.getId() & 255 | type.getId() << 8 & '\uff00');
    }

    public CoatColors getCoatColor() {
        return CoatColors.byId(this.getPackedCoatColor() & 255);
    }

    public CoatTypes getCoatType() {
        return CoatTypes.byId((this.getPackedCoatColor() & '\uff00') >> 8);
    }

    // NBT

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(KEY_COLOR, this.getPackedCoatColor());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setPackedCoatColor(compound.getInt(KEY_COLOR));
    }

    class AvoidPlayersGoal extends AvoidEntityGoal<PlayerEntity> {

        public AvoidPlayersGoal(final PegasusEntity pegasus) {
            super(pegasus, PlayerEntity.class, 16.0F, 1.1D, 0.95D, (entity) -> {
                return !entity.isDiscrete() && EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(entity) && !pegasus.isVehicle()
                        && (!pegasus.isTamed() || pegasus.getOwnerUUID() == null || !entity.getUUID().equals(pegasus.getOwnerUUID()));
            });
        }

        @Override
        public boolean canUse() {
            this.pathNav = this.mob.getNavigation();
            return super.canUse();
        }
    }
}
