package greekfantasy.entity.monster;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.util.GFMobType;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Random;

public class Drakaina extends Monster {

    private static final EntityDataAccessor<Byte> DATA_VARIANT = SynchedEntityData.defineId(Drakaina.class, EntityDataSerializers.BYTE);
    private static final String KEY_VARIANT = "Variant";
    private static final String KEY_BVARIANT = "BVariant";

    public Drakaina(final EntityType<? extends Drakaina> type, final Level worldIn) {
        super(type, worldIn);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.ATTACK_DAMAGE, 2.5D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RestrictSunGoal(this));
        this.goalSelector.addGoal(2, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_VARIANT, Variant.GREEN.getId());
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            boolean flag = this.shouldBurnInDay() && this.isSunBurnTick() && !this.fireImmune();
            if (flag) {
                ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
                if (!itemstack.isEmpty()) {
                    if (itemstack.isDamageableItem()) {
                        itemstack.setDamageValue(itemstack.getDamageValue() + this.random.nextInt(2));
                        if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
                            this.broadcastBreakEvent(EquipmentSlot.HEAD);
                            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    this.setSecondsOnFire(8);
                }
            }
        }

        super.aiStep();
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CAT_HISS;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.68F;
    }

    // MISC //

    @Override
    public MobType getMobType() {
        return GFMobType.SERPENT;
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return this.getVariant().getLootTable();
    }

    @Override
    public boolean fireImmune() {
        return getVariant() == Variant.RED || super.fireImmune();
    }

    @Override
    public boolean doHurtTarget(final Entity entity) {
        if (super.doHurtTarget(entity)) {
            // use special attack
            if (entity instanceof LivingEntity living) {
                final Variant variant = getVariant();
                switch (variant) {
                    case RED:
                        living.setSecondsOnFire(2 + random.nextInt(4));
                        break;
                    case BROWN:
                        living.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20 * (2 + random.nextInt(5))));
                        break;
                    case GREEN:
                        living.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * (2 + random.nextInt(5))));
                        break;
                }
            }
            // light target on fire if burning
            float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                entity.setSecondsOnFire(2 * (int) f);
            }
            return true;
        }
        return false;
    }

    protected boolean shouldBurnInDay() {
        return getVariant() != Variant.RED;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                           @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        final Variant variant;
        if (spawnType == MobSpawnType.COMMAND || spawnType == MobSpawnType.SPAWN_EGG || spawnType == MobSpawnType.SPAWNER || spawnType == MobSpawnType.DISPENSER) {
            variant = Variant.getRandom(level.getRandom());
        } else {
            variant = Variant.getForBiome(level.getBiome(this.blockPosition()));
        }
        this.setVariant(variant);
        return super.finalizeSpawn(level, difficulty, spawnType, spawnDataIn, dataTag);
    }

    // NBT and Variant //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_BVARIANT, getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KEY_VARIANT)) {
            setVariant(Variant.getByName(compound.getString(KEY_VARIANT)));
        } else {
            setVariant(Variant.getById(compound.getByte(KEY_BVARIANT)));
        }
    }

    public void setVariant(final Variant variant) {
        this.getEntityData().set(DATA_VARIANT, variant.getId());
    }

    public Variant getVariant() {
        return Variant.getById(this.getEntityData().get(DATA_VARIANT));
    }

    public enum Variant implements StringRepresentable {
        GREEN("green", BiomeTags.IS_FOREST),
        BROWN("brown", ForgeRegistries.BIOMES.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "has_spawn/brown_drakaina"))),
        RED("red", BiomeTags.IS_NETHER);

        private final String name;
        private final ResourceLocation lootTable;
        private final TagKey<Biome> biomeTag;

        Variant(final String name, final TagKey<Biome> biomeTag) {
            this.name = name;
            this.lootTable = new ResourceLocation(GreekFantasy.MODID, "entities/drakaina/" + this.name);
            this.biomeTag = biomeTag;
        }

        public static Variant getForBiome(final Holder<Biome> biome) {
            // iterate over values to find matching tag
            for(Variant v : values()) {
                if(biome.is(v.biomeTag)) {
                    return v;
                }
            }
            // defaults to GREEN
            return GREEN;
        }

        public static Variant getRandom(final RandomSource rand) {
            int len = values().length;
            return values()[rand.nextInt(len)];
        }

        public static Variant getByName(final String n) {
            // check the given name against all types
            for (final Variant t : values()) {
                if (t.getSerializedName().equals(n)) {
                    return t;
                }
            }
            // defaults to GREEN
            return GREEN;
        }

        public static Variant getById(final byte id) {
            return values()[Mth.clamp(id, 0, values().length - 1)];
        }

        public byte getId() {
            return (byte) this.ordinal();
        }

        public ResourceLocation getLootTable() {
            return lootTable;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
