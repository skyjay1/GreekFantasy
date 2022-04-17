package greekfantasy.entity;

import greekfantasy.GFRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class GiantBoarEntity extends HoglinEntity {

    private static final DataParameter<Boolean> SPAWNING = EntityDataManager.defineId(GiantBoarEntity.class, DataSerializers.BOOLEAN);
    private static final String KEY_SPAWNING = "Spawning";
    private static final String KEY_SPAWN_TIME = "SpawnTime";
    // bytes to use in World#setEntityState
    private static final byte SPAWN_CLIENT = 9;

    private static final int MAX_SPAWN_TIME = 90;

    private final ServerBossInfo bossInfo = new ServerBossInfo(this.getDisplayName(), BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);

    private int spawnTime;

    public GiantBoarEntity(final EntityType<? extends GiantBoarEntity> type, final World worldIn) {
        super(type, worldIn);
        this.maxUpStep = 1.0F;
        this.xpReward = 30;

    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.82D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.25D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 6.0D);
    }

    public static GiantBoarEntity spawnGiantBoar(final ServerWorld world, final HoglinEntity hoglin) {
        GiantBoarEntity entity = GFRegistry.GIANT_BOAR_ENTITY.create(world);
        entity.copyPosition(hoglin);
        entity.finalizeSpawn(world, world.getCurrentDifficultyAt(hoglin.blockPosition()), SpawnReason.CONVERSION, null, null);
        if (hoglin.hasCustomName()) {
            entity.setCustomName(hoglin.getCustomName());
            entity.setCustomNameVisible(hoglin.isCustomNameVisible());
        }
        entity.setPersistenceRequired();
        entity.yBodyRot = hoglin.yBodyRot;
        entity.setPortalCooldown();
        world.addFreshEntity(entity);
        entity.setSpawning(true);
        // remove the old hoglin
        hoglin.remove();
        // trigger spawn for nearby players
        for (ServerPlayerEntity player : world.getEntitiesOfClass(ServerPlayerEntity.class, entity.getBoundingBox().inflate(25.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
        }
        // play sound
        world.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WITHER_SPAWN, entity.getSoundSource(), 1.2F, 1.0F, false);
        return entity;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(SPAWNING, Boolean.valueOf(false));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new GiantBoarEntity.SpawningGoal());
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());

        if (spawnTime > 0) {
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
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setImmuneToZombification(true); // set IsImmuneToZombification
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
    protected float getVoicePitch() {
        return 0.62F + random.nextFloat() * 0.24F;
    }

    // NBT methods //

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(KEY_SPAWNING, isSpawning());
        compound.putInt(KEY_SPAWN_TIME, spawnTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        setSpawning(compound.getBoolean(KEY_SPAWNING));
        spawnTime = compound.getInt(KEY_SPAWN_TIME);
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
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // Spawning //

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == SPAWN_CLIENT) {
            setSpawning(true);
        } else {
            super.handleEntityEvent(id);
        }
    }

    public boolean isSpawning() {
        return spawnTime > 0 || this.getEntityData().get(SPAWNING).booleanValue();
    }

    public void setSpawning(final boolean spawning) {
        spawnTime = spawning ? 1 : 0;
        this.getEntityData().set(SPAWNING, spawning);
        if (spawning && !level.isClientSide()) {
            level.broadcastEntityEvent(this, SPAWN_CLIENT);
        }
    }

    public float getSpawnPercent(final float partialTick) {
        if (spawnTime <= 0) {
            return 1.0F;
        }
        final float prevSpawnPercent = Math.max((float) spawnTime - partialTick, 0.0F) / (float) MAX_SPAWN_TIME;
        final float spawnPercent = (float) spawnTime / (float) MAX_SPAWN_TIME;
        return MathHelper.lerp(partialTick / 8, prevSpawnPercent, spawnPercent);
    }

    // Goals //

    class SpawningGoal extends Goal {

        public SpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return GiantBoarEntity.this.isSpawning();
        }

        @Override
        public void tick() {
            GiantBoarEntity.this.getNavigation().stop();
        }
    }

}
