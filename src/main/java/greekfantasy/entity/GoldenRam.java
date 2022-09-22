package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GoldenRam extends Sheep implements NeutralMob {

    public static final TagKey<Item> TRIGGER = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "golden_ram_trigger"));

    private static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private int angerTime;
    private UUID angerTarget;

    public GoldenRam(final EntityType<? extends GoldenRam> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Sheep.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D);
    }

    public static GoldenRam spawnGoldenRam(final ServerLevel level, final Player player, final Sheep sheep) {
        GoldenRam entity = GFRegistry.EntityReg.GOLDEN_RAM.get().create(level);
        entity.copyPosition(sheep);
        entity.yBodyRot = sheep.yBodyRot;
        if (sheep.hasCustomName()) {
            entity.setCustomName(sheep.getCustomName());
            entity.setCustomNameVisible(sheep.isCustomNameVisible());
        }
        entity.setPersistenceRequired();
        entity.setPortalCooldown();
        entity.setAge(sheep.getAge());
        level.addFreshEntityWithPassengers(entity);
        entity.finalizeSpawn(level, level.getCurrentDifficultyAt(sheep.blockPosition()), MobSpawnType.CONVERSION, null, null);
        // remove the old entity
        sheep.discard();
        // give potion effects
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60));
        // play sound
        entity.playSound(SoundEvents.PLAYER_LEVELUP, 1.2F, 1.0F);
        return entity;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.54D, true));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // anger timer
        if (!this.level.isClientSide()) {
            this.updatePersistentAnger((ServerLevel) this.level, true);
        }
        // when sheared, despawn and replace with regular sheep
        if (this.isSheared()) {
            Sheep entity = EntityType.SHEEP.create(level);
            entity.copyPosition(this);
            entity.yBodyRot = this.yBodyRot;
            entity.setPortalCooldown();
            entity.setColor(this.getColor());
            entity.setSheared(true);
            entity.setAge(this.getAge());
            level.addFreshEntity(entity);
            // remove self
            this.discard();
        }
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level level, BlockPos pos, int fortune) {
        level.playSound(null, this, SoundEvents.SHEEP_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 0.8F);
        if (!level.isClientSide()) {
            this.setSheared(true);
            // create a list of items to return
            List<ItemStack> items = new ArrayList<>();
            items.add(new ItemStack(GFRegistry.ItemReg.GOLDEN_FLEECE.get()));
            if (random.nextBoolean() || fortune > 0) {
                items.add(new ItemStack(GFRegistry.ItemReg.HORN.get()));
            }
            return items;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return this.getType().getDefaultLootTable();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canMate(Animal otherAnimal) {
        return false;
    }

    @Nullable
    public Sheep getBreedOffspring(ServerLevel world, AgeableMob parentB) {
        return null;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType mobSpawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, mobSpawnType, spawnDataIn, dataTag);
        this.setColor(DyeColor.YELLOW);
        this.setBaby(false);
        return data;
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
}
