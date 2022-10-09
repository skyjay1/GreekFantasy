package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.boss.Charybdis;
import greekfantasy.integration.RGCompat;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class Whirl extends WaterAnimal {

    protected static final TagKey<Item> TRIGGER = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation(GreekFantasy.MODID, "charybdis_trigger"));

    protected static final EntityDataAccessor<Boolean> ATTRACT_MOBS = SynchedEntityData.defineId(Whirl.class, EntityDataSerializers.BOOLEAN);
    protected static final String KEY_AFFECTS_MOBS = "AttractMobs";
    protected static final String KEY_LIFE_TICKS = "LifeTicks";

    /**
     * The number of ticks until the entity starts taking damage
     **/
    protected boolean limitedLifespan;
    protected int limitedLifeTicks;

    public Whirl(final EntityType<? extends Whirl> type, final Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.05D)
                .add(Attributes.ATTACK_DAMAGE, 0.25D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(ATTRACT_MOBS, Boolean.FALSE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new Whirl.SwirlGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // remove if colliding with another whirl or a charybdis
        final List<WaterAnimal> waterMobList = this.level.getEntitiesOfClass(WaterAnimal.class, this.getBoundingBox().inflate(1.0D),
                e -> e != this && e.isAlive() && (e.getType() == GFRegistry.EntityReg.CHARYBDIS.get() || e.getType() == GFRegistry.EntityReg.WHIRL.get()));
        if (!waterMobList.isEmpty() && this.isAlive()) {
            this.hurt(DamageSource.STARVE, this.getMaxHealth() * 2.0F);
            return;
        }

        // lifespan
        if (this.limitedLifespan && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.setHealth(this.getHealth() - 1.0F);
        }

        // remove nearby items
        final List<ItemEntity> itemEntityList = this.level.getEntities(EntityType.ITEM, this.getBoundingBox().inflate(1.0D, 0.0F, 1.0D), e -> e.isInWaterOrBubble() && e.getY() < (this.getY() + this.getBbHeight()));
        for (final ItemEntity e : itemEntityList) {
            // check for trigger items
            if (this.level instanceof ServerLevel && !e.getItem().isEmpty() && e.getItem().is(TRIGGER)) {
                Charybdis.spawnCharybdis((ServerLevel) this.level, this);
                e.discard();
            }
            // start to remove items
            if (!e.hasPickUpDelay()) {
                e.discard();
            }
            // play sound when item is removed
            if (!e.isAlive()) {
                this.playSound(SoundEvents.GENERIC_DRINK, 0.6F, 0.8F + this.getRandom().nextFloat() * 0.4F);
            }
        }


    }

    @Override
    public void tick() {
        super.tick();

        // spawn particles
        if (this.level.isClientSide() && tickCount % 3 == 0 && this.isInWaterOrBubble()) {
            // spawn particles in spiral
            float maxY = this.getBbHeight() * 1.65F;
            float y = 0;
            float nY = 90;
            float dY = maxY / nY;
            double posX = this.getX();
            double posY = this.getY();
            double posZ = this.getZ();
            // for each y-position, increase the angle and spawn particle here
            for (float a = 0, nA = 28 + random.nextInt(4), dA = (2 * (float) Math.PI) / nA; y < maxY; a += dA) {
                float radius = y * 0.35F;
                float cosA = Mth.cos(a) * radius;
                float sinA = Mth.sin(a) * radius;
                //bubbles(posX + cosA, posY + y, posZ + sinA, 0.125D, 1);
                level.addParticle(ParticleTypes.BUBBLE, posX + cosA, posY + y - (maxY * 0.4), posZ + sinA, 0.0D, 0.085D, 0.0D);
                y += dY;
            }
        }
    }

    @Override
    protected void doPush(final Entity entityIn) {
        super.doPush(entityIn);
    }

    @Override
    public double getFluidJumpThreshold() {
        return getBbHeight() - 0.1D;
    }

    // Misc //

    @Override
    public MobCategory getClassification(boolean forSpawnCount) {
        return MobCategory.WATER_CREATURE;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        return source.isProjectile() || super.isInvulnerableTo(source);
    }

    @Override
    protected void actuallyHurt(final DamageSource source, final float amountIn) {
        float amount = amountIn;
        if (!source.isBypassMagic() && getAttractMobs()) {
            amount *= 0.25F;
        }
        super.actuallyHurt(source, amount);
    }

    // Prevent entity collisions //

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.9F;
    }

    // Lifespan and Attract Mobs //

    public void setAttractMobs(final boolean attractsMobs) {
        this.getEntityData().set(ATTRACT_MOBS, attractsMobs);
    }

    public boolean getAttractMobs() {
        return this.getEntityData().get(ATTRACT_MOBS);
    }

    public void setLimitedLife(int life) {
        this.limitedLifespan = true;
        this.limitedLifeTicks = life;
    }

    @Override
    public ResourceLocation getDefaultLootTable() {
        return limitedLifespan ? BuiltInLootTables.EMPTY : super.getDefaultLootTable();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean(KEY_AFFECTS_MOBS, getAttractMobs());
        if (this.limitedLifespan) {
            compound.putInt(KEY_LIFE_TICKS, this.limitedLifeTicks);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setAttractMobs(compound.getBoolean(KEY_AFFECTS_MOBS));
        if (compound.contains(KEY_LIFE_TICKS)) {
            setLimitedLife(compound.getInt(KEY_LIFE_TICKS));
        }
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    @Override
    public float getVoicePitch() {
        return 0.8F + random.nextFloat() * 0.2F;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    // Goals //

    private static class SwirlGoal extends greekfantasy.entity.ai.SwirlGoal {
        private final Whirl whirl;

        public SwirlGoal(final Whirl entity) {
            super(entity, 10000, 0, 9.0D, 0.12F, false, e ->
                    !(e.getType() == GFRegistry.EntityReg.WHIRL.get() || e.getType() == GFRegistry.EntityReg.CHARYBDIS.get())
                            && ((e instanceof LivingEntity && entity.getAttractMobs()) || e instanceof ItemEntity)
                            && (!(e instanceof Player) || !(GreekFantasy.isRGLoaded() && RGCompat.getInstance().canUseLordOfTheSea((Player) e))));
            this.whirl = entity;
        }

        @Override
        protected void onCollideWith(Entity e) {
            // attack living entities, if enabled
            if (whirl.getAttractMobs() && e instanceof LivingEntity) {
                e.hurt(DamageSource.mobAttack(entity), 1.0F);
            }
        }
    }
}
