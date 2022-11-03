package greekfantasy.entity;

import com.google.common.collect.ImmutableMap;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.GoToWaterGoal;
import greekfantasy.entity.ai.TridentRangedAttackGoal;
import greekfantasy.entity.ai.WaterAnimalMoveControl;
import greekfantasy.entity.boss.Charybdis;
import greekfantasy.entity.boss.Scylla;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;

public class Naiad extends PathfinderMob implements RangedAttackMob, NeutralMob {

    protected static final EntityDataAccessor<String> DATA_VARIANT = SynchedEntityData.defineId(Naiad.class, EntityDataSerializers.STRING);
    protected static final String KEY_VARIANT = "Variant";

    protected Variant variant = Variant.RIVER;

    protected final WaterBoundPathNavigation waterNavigation;
    protected final GroundPathNavigation groundNavigation;

    protected static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(4, 10);
    protected int angerTime;
    protected UUID angerTarget;

    protected EntityDimensions swimmingDimensions;

    public Naiad(final EntityType<? extends Naiad> type, final Level level) {
        super(type, level);
        this.moveControl = new WaterAnimalMoveControl(this);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.waterNavigation = new WaterBoundPathNavigation(this, level);
        this.groundNavigation = new GroundPathNavigation(this, level);
        this.swimmingDimensions = EntityDimensions.scalable(0.48F, 0.48F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ARMOR, 1.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_VARIANT, Variant.OCEAN.getSerializedName());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new GoToWaterGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new TridentRangedAttackGoal(this, 1.0D, 40, 10.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Satyr.class, 10.0F, 1.2D, 1.1D));
        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Charybdis.class, 12.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(6, new AvoidEntityGoal<>(this, Scylla.class, 12.0F, 1.0D, 1.0D));
        this.goalSelector.addGoal(7, new RandomSwimmingGoal(this, 0.8D, 120) {
            @Override
            public boolean canUse() {
                return Naiad.this.isInWater() && super.canUse();
            }
        });
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.9D) {
            @Override
            public boolean canUse() {
                return !Naiad.this.isInWater() && super.canUse();
            }
        });
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Drowned.class, false));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public void tick() {
        super.tick();
        // update pose
        if (this.isInWater() && this.getDeltaMovement().horizontalDistanceSqr() > 0.0012D) {
            this.setPose(Pose.SWIMMING);
        } else if (this.getPose() == Pose.SWIMMING) {
            this.setPose(Pose.STANDING);
        }
        // check potion effects
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel
                && this.getVariant() == Variant.OCEAN
                && this.getEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()) != null) {
            // spawn scylla
            Scylla.spawnScylla(serverLevel, this);
            // add particles
            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                    this.getX(), this.getY() + this.getBbHeight() / 2.0D, this.getZ(),
                    40, getBbWidth(), getBbHeight() / 2.0D, getBbWidth(), 0.0D);
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return (pose == Pose.SWIMMING) ? swimmingDimensions : super.getDimensions(pose);
    }

    // NeutralMob methods

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

    @Override
    public boolean doHurtTarget(final Entity entity) {
        if (super.doHurtTarget(entity)) {
            // reset anger after successful attack
            if (entity.getUUID().equals(this.getPersistentAngerTarget())) {
                this.setPersistentAngerTarget(null);
            }
            return true;
        }
        return false;
    }

    // Variant methods

    public void setVariant(final Variant variantIn) {
        this.variant = variantIn;
        this.getEntityData().set(DATA_VARIANT, variantIn.getSerializedName());
    }

    public Variant getVariant() {
        return variant;
    }

    public Variant getVariantByName(final String name) {
        return Variant.getByName(name);
    }

    public Variant getRandomVariant(final RandomSource rand) {
        return Variant.getRandom(rand);
    }

    public Variant getVariantForBiome(final Holder<Biome> biome) {
        return Variant.getForBiome(biome);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(DATA_VARIANT)) {
            this.variant = getVariantByName(this.getEntityData().get(DATA_VARIANT));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString(KEY_VARIANT, this.getEntityData().get(DATA_VARIANT));
        this.addPersistentAngerSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(getVariantByName(compound.getString(KEY_VARIANT)));
        this.readPersistentAngerSaveData(this.level, compound);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType mobType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, mobType, spawnDataIn, dataTag);
        final Variant variant;
        if (mobType == MobSpawnType.COMMAND || mobType == MobSpawnType.SPAWN_EGG || mobType == MobSpawnType.SPAWNER || mobType == MobSpawnType.DISPENSER) {
            variant = getRandomVariant(worldIn.getRandom());
        } else {
            variant = getVariantForBiome(worldIn.getBiome(this.blockPosition()));
        }
        this.setVariant(variant);
        populateDefaultEquipmentSlots(worldIn.getRandom(), difficultyIn);
        return data;
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return this.getVariant().getDeathLootTable();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return false;
    }

    @Override
    protected int decreaseAirSupply(int airSupply) {
        return airSupply;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        // immune to damage from other centaurs
        if (source.getDirectEntity() instanceof ThrownTrident && source.getEntity() != null && source.getEntity().getType() == this.getType()) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    /*protected SoundEvent getAmbientSound() {
        return this.isInWater() ? SoundEvents.DROWNED_AMBIENT_WATER : SoundEvents.DROWNED_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_32386_) {
        return this.isInWater() ? SoundEvents.DROWNED_HURT_WATER : SoundEvents.DROWNED_HURT;
    }

    protected SoundEvent getDeathSound() {
        return this.isInWater() ? SoundEvents.DROWNED_DEATH_WATER : SoundEvents.DROWNED_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.DROWNED_STEP;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.DROWNED_SWIM;
    }
*/

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        float tridentChance = (this.getVariant() == Variant.OCEAN) ? 0.55F : 0.31F;
        if (random.nextFloat() < tridentChance) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
        }

    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level) {
        return level.isUnobstructed(this);
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    @Override
    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        ThrownTrident throwntrident = new ThrownTrident(this.level, this, new ItemStack(Items.TRIDENT));
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.33D) - throwntrident.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        throwntrident.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.TRIDENT_THROW, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(throwntrident);
    }

    public static class Variant implements StringRepresentable {
        public static final Variant RIVER = new Naiad.Variant("river", new ResourceLocation("minecraft", "is_river"));
        public static final Variant OCEAN = new Naiad.Variant("ocean", new ResourceLocation("minecraft", "is_ocean"));

        protected final String name;
        protected final TagKey<Biome> biomeTag;
        protected final ResourceLocation deathLootTable;

        public static ImmutableMap<String, Variant> WATER = ImmutableMap.<String, Variant>builder()
                .put(RIVER.getSerializedName(), RIVER)
                .put(OCEAN.getSerializedName(), OCEAN)
                .build();

        protected Variant(final String nameIn, final ResourceLocation biomeTag) {
            this.name = nameIn;
            this.biomeTag = ForgeRegistries.BIOMES.tags().createTagKey(biomeTag);
            this.deathLootTable = new ResourceLocation(GreekFantasy.MODID, "entities/naiad/" + name);
        }

        public static Variant getForBiome(final Holder<Biome> biome) {
            for (Variant variant : WATER.values()) {
                if (biome.is(variant.biomeTag)) {
                    return variant;
                }
            }
            return Variant.RIVER;
        }

        public static Variant getRandom(final RandomSource rand) {
            int len = WATER.size();
            return len > 0 ? WATER.entrySet().asList().get(rand.nextInt(len)).getValue() : RIVER;
        }

        public static Variant getByName(final String n) {
            return WATER.getOrDefault(n, RIVER);
        }

        public TagKey<Biome> getBiome() {
            return biomeTag;
        }

        public ResourceLocation getDeathLootTable() {
            return deathLootTable;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
