package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class GiantBoar extends Hoglin {

    public static final TagKey<Item> TRIGGER = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "giant_boar_trigger"));

    private static final EntityDataAccessor<Boolean> SPAWNING = SynchedEntityData.defineId(GiantBoar.class, EntityDataSerializers.BOOLEAN);
    private static final String KEY_SPAWNING = "Spawning";
    private static final String KEY_SPAWN_TIME = "SpawnTime";
    // bytes to use in World#setEntityState
    private static final byte SPAWN_CLIENT = 9;

    private static final int MAX_SPAWN_TIME = 90;

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    private int spawnTime0;
    private int spawnTime;

    public GiantBoar(final EntityType<? extends GiantBoar> type, final Level worldIn) {
        super(type, worldIn);
        this.xpReward = 30;

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.82D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6D);
    }

    public static GiantBoar spawnGiantBoar(final ServerLevel level, final Hoglin hoglin) {
        GiantBoar entity = GFRegistry.EntityReg.GIANT_BOAR.get().create(level);
        entity.copyPosition(hoglin);
        if (hoglin.hasCustomName()) {
            entity.setCustomName(hoglin.getCustomName());
            entity.setCustomNameVisible(hoglin.isCustomNameVisible());
        }
        entity.setPersistenceRequired();
        entity.yBodyRot = hoglin.yBodyRot;
        entity.setPortalCooldown();
        level.addFreshEntityWithPassengers(entity);
        entity.finalizeSpawn(level, level.getCurrentDifficultyAt(hoglin.blockPosition()), MobSpawnType.CONVERSION, null, null);
        entity.setSpawning(true);
        // remove the old hoglin
        hoglin.ejectPassengers();
        hoglin.discard();
        // trigger spawn for nearby players
        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(25.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
        }
        // play sound
        entity.playSound(SoundEvents.WITHER_SPAWN, 1.2F, 1.0F);
        return entity;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(SPAWNING, Boolean.FALSE);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new GiantBoar.SpawningGoal());
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());

        spawnTime0 = spawnTime;
        if (spawnTime > 0 || isSpawning()) {
            // reset attack target
            this.setTarget(null);
            // spawn particles
            final double width = this.getBbWidth();
            level.addParticle(ParticleTypes.ENTITY_EFFECT,
                    this.getX() + (random.nextDouble() - 0.5D) * width,
                    this.getY() + random.nextDouble(),
                    this.getZ() + (random.nextDouble() - 0.5D) * width, 0.01D, 0.01D, 0.01D);
            // reset spawning
            if (spawnTime++ >= MAX_SPAWN_TIME) {
                setSpawning(false);
            }
        }
    }

    // Hoglin overrides //

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canFallInLove() {
        return false;
    }

    @Override
    public boolean canBeHunted() { // canBeHunted
        return false;
    }

    @Override
    public boolean isConverting() { // canBeZombified
        return false;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        return isSpawning() || source == DamageSource.IN_WALL || source == DamageSource.WITHER || super.isInvulnerableTo(source);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType mobSpawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, mobSpawnType, spawnDataIn, dataTag);
        this.setImmuneToZombification(true);
        this.setBaby(false);
        this.setSpawning(true);
        return data;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    protected void ageBoundaryReached() {
    }

    @Override
    protected float getSoundVolume() {
        return 1.5F;
    }

    @Override
    public float getVoicePitch() {
        return 0.62F + random.nextFloat() * 0.24F;
    }

    // NBT methods //

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(KEY_SPAWNING, isSpawning());
        compound.putInt(KEY_SPAWN_TIME, spawnTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSpawning(compound.getBoolean(KEY_SPAWNING));
        spawnTime = compound.getInt(KEY_SPAWN_TIME);
        spawnTime0 = spawnTime;
    }

    // Prevent entity collisions //

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
    }

    // Boss //

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(final double disToPlayer) {
        return false;
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        if (this.hasCustomName()) {
            this.bossInfo.setName(this.getCustomName());
        }
        this.bossInfo.setVisible(GreekFantasy.CONFIG.showGiantBoarBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // Spawning //

    @Override
    public void handleEntityEvent(byte id) {
        if (id == SPAWN_CLIENT) {
            setSpawning(true);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public boolean isSpawning() {
        return this.getEntityData().get(SPAWNING);
    }

    public void setSpawning(final boolean spawning) {
        this.getEntityData().set(SPAWNING, spawning);
        if (spawning) {
            spawnTime = 1;
            // notify client
            if (!level.isClientSide()) {
                level.broadcastEntityEvent(this, SPAWN_CLIENT);
            }
        } else {
            spawnTime = 0;
        }
    }

    public float getSpawnPercent(final float partialTick) {
        if (spawnTime <= 0) {
            return 1.0F;
        }
        return Mth.lerp(partialTick, spawnTime0, spawnTime) / (float) MAX_SPAWN_TIME;
    }

    // Goals //

    class SpawningGoal extends Goal {

        public SpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return GiantBoar.this.isSpawning();
        }

        @Override
        public void tick() {
            GiantBoar.this.getNavigation().stop();
            GiantBoar.this.setTarget(null);
        }
    }

}
