package greekfantasy.entity;

import greekfantasy.GFRegistry;
import greekfantasy.entity.ai.CooldownMeleeAttackGoal;
import greekfantasy.entity.ai.CooldownRangedAttackGoal;
import greekfantasy.entity.boss.BronzeBull;
import greekfantasy.entity.util.HasCustomCooldown;
import greekfantasy.item.ClubItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.EnumSet;


public class Automaton extends AbstractGolem implements RangedAttackMob, HasCustomCooldown {
    protected static final TagKey<Item> BRONZE_INGOT = ForgeRegistries.ITEMS.tags().createTagKey(new ResourceLocation("forge", "ingots/bronze"));
    protected static final TagKey<EntityType<?>> BOSSES = ForgeRegistries.ENTITY_TYPES.tags().createTagKey(new ResourceLocation("forge", "bosses"));

    protected static final EntityDataAccessor<Byte> STATE = SynchedEntityData.defineId(Automaton.class, EntityDataSerializers.BYTE);
    protected static final String KEY_STATE = "AutomatonState";
    protected static final String KEY_SPAWN = "SpawnTime";
    protected static final String KEY_SHOOT = "ShootTime";
    // bytes to use in STATE
    protected static final byte NONE = (byte) 0;
    protected static final byte SPAWNING = (byte) 1;
    protected static final byte SHOOT = (byte) 2;
    // bytes to use in Level#broadcastEntityEvent
    protected static final byte SPAWN_EVENT = 8;
    protected static final byte SHOOT_EVENT = 9;
    protected static final byte ATTACK_EVENT = 10;

    protected int spawnTime0;
    protected int spawnTime;
    protected int shootTime;
    protected float shootAngle0;
    protected float shootAngle;
    protected int attackCooldown;
    protected static final int MAX_ATTACK_TIMER = 10;
    protected int attackTimer;
    protected int crackiness;

    public Automaton(final EntityType<? extends Automaton> type, final Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 160.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 9.5D)
                .add(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.35D)
                .add(Attributes.ARMOR, 10.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.2F);
    }

    public static Automaton spawnAutomaton(final Level level, final BlockPos pos, final float yaw) {
        Automaton entity = GFRegistry.EntityReg.AUTOMATON.get().create(level);
        entity.moveTo(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
        entity.yBodyRot = yaw;
        level.addFreshEntity(entity);
        entity.setSpawning(true);
        if(level instanceof ServerLevel serverLevel) {
            entity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
            // trigger spawn for nearby players
            for (ServerPlayer player : serverLevel.getEntitiesOfClass(ServerPlayer.class, entity.getBoundingBox().inflate(15.0D))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
            }
        }
        return entity;
    }

    @Override
    public void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(STATE, NONE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new Automaton.SpawningGoal());
        this.goalSelector.addGoal(3, new Automaton.AutomatonRangedAttackGoal(4, 25.0F, getRangedAttackCooldown()));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        registerAutomatonGoals();
    }

    protected void registerAutomatonGoals() {
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1D, Ingredient.of(BRONZE_INGOT), false));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                e -> e instanceof Enemy && e.canChangeDimensions() && !e.getType().is(BOSSES)));
    }

    protected int getRangedAttackCooldown() {
        return 188;
    }

    protected int getMeleeAttackCooldown() {
        return 0;
    }

    protected double getBonusAttackKnockback() {
        return 0.45D;
    }

    protected int getMaxSpawnTime() {
        return 70;
    }

    protected int getMaxShootTime() {
        return 16;
    }

    public float getMaxShootingAngle() {
        return -1.4708F;
    }

    protected boolean isHealItem(final ItemStack itemStack) {
        return itemStack.is(BRONZE_INGOT);
    }

    @Override
    public void travel(final Vec3 vec) {
        Vec3 travelVec = vec;
        if(this.isInWaterRainOrBubble()) {
            travelVec = vec.multiply(0.6D, 1.0D, 0.6D);
        }
        super.travel(travelVec);
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        if (this.getType() == GFRegistry.EntityReg.AUTOMATON.get() && entity instanceof Player) {
            return false;
        }
        return super.canAttack(entity);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // crackiness
        this.crackiness = Mth.floor(this.getHealth() / this.getMaxHealth() * 4.0D);

        // attack cooldown
        tickCustomCooldown();

        // update spawn time
        spawnTime0 = spawnTime;
        if (isSpawning() || spawnTime > 0) {
            // update timer
            if (--spawnTime <= 0) {
                setSpawning(false);
            }
        }

        // update attack timer
        if(attackTimer > 0 && ++attackTimer >= MAX_ATTACK_TIMER) {
            attackTimer = 0;
        }

        // update shoot time
        shootAngle0 = shootAngle;
        if (isShooting() || shootTime > 0) {
            // update angle
            shootAngle = Math.min(1.0F, shootAngle + 0.08F);
            // update timer
            if (shootTime++ >= getMaxShootTime()) {
                setShooting(false);
                shootTime = 0;
            }
        } else {
            shootAngle = Math.max(0.0F, shootAngle - 0.08F);
        }
    }

    @Override
    public void tick() {
        super.tick();

        // spawn particles
        if (this.level.isClientSide() && this.getDeltaMovement().horizontalDistanceSqr() > (double) 2.5000003E-7F && this.random.nextInt(3) == 0) {
            int i = Mth.floor(this.getX());
            int j = Mth.floor(this.getY() - (double) 0.2F);
            int k = Mth.floor(this.getZ());
            BlockPos pos = new BlockPos(i, j, k);
            BlockState blockstate = this.level.getBlockState(pos);
            if (!this.level.isEmptyBlock(pos)) {
                final BlockParticleOption data = new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos);
                final double radius = this.getBbWidth() * 0.8F;
                final double motion = 4.0D;
                this.level.addParticle(data,
                        this.getX() + (this.random.nextDouble() - 0.5D) * radius * 2,
                        this.getY() + 0.1D,
                        this.getZ() + (this.random.nextDouble() - 0.5D) * radius * 2,
                        motion * (this.random.nextDouble() - 0.5D), 0.45D, (this.random.nextDouble() - 0.5D) * motion);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if(this.getHealth() < this.getMaxHealth() && isHealItem(itemstack)) {
            if(!this.level.isClientSide()) {
                this.heal(this.getMaxHealth() * 0.5F);
                if(!player.isCreative()) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
                ((ServerLevel)this.level).sendParticles(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getEyeY(), this.getZ(), 8, 0.5D, 0.5D, 0.5D, 0.5D);
            }
            return InteractionResult.sidedSuccess(this.level.isClientSide());
        }
        return super.mobInteract(player,hand);
    }

    @Override
    public boolean doHurtTarget(final Entity entityIn) {
        if (super.doHurtTarget(entityIn)) {
            // apply extra knockback velocity when attacking (ignores knockback resistance)
            final double knockbackFactor = getBonusAttackKnockback();
            final Vec3 myPos = this.position();
            final Vec3 ePos = entityIn.position();
            final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
            final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
            entityIn.push(dX, knockbackFactor / 2.0D, dZ);
            entityIn.hurtMarked = true;
            attackTimer = 1;
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.6F + random.nextFloat() * 0.2F);
            this.level.broadcastEntityEvent(this, ATTACK_EVENT);
            return true;
        }
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        final SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnDataIn, dataTag);
        this.setSpawning(true);
        return data;
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
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
    public boolean isInvulnerableTo(final DamageSource source) {
        return isSpawning() || source == DamageSource.DROWN || source == DamageSource.IN_WALL
                || source == DamageSource.WITHER || super.isInvulnerableTo(source);
    }

    @Override
    public boolean hurt(final DamageSource source, final float amount) {
        // manually adjust damage amount for arrows
        float hurtAmount = amount;
        if(source.getDirectEntity() instanceof AbstractArrow) {
            hurtAmount = amount * 0.1F;
        }
        return super.hurt(source, hurtAmount);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 280;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BLAZE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 0.6F + random.nextFloat() * 0.25F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte(KEY_STATE, this.getState());
        compound.putInt(KEY_SPAWN, spawnTime);
        compound.putInt(KEY_SHOOT, shootTime);
        saveCustomCooldown(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setState(compound.getByte(KEY_STATE));
        spawnTime = compound.getInt(KEY_SPAWN);
        shootTime = compound.getInt(KEY_SHOOT);
        readCustomCooldown(compound);
    }

    public byte getState() {
        return this.getEntityData().get(STATE);
    }

    public void setState(final byte state) {
        this.getEntityData().set(STATE, state);
    }

    public boolean isNoneState() {
        return getState() == NONE;
    }

    public boolean isSpawning() {
        return spawnTime > 0 || getState() == SPAWNING;
    }

    public void setShooting(final boolean shoot) {
        setState(shoot ? SHOOT : NONE);
    }

    public boolean isShooting() {
        return getState() == SHOOT;
    }

    public void setSpawning(final boolean spawning) {
        spawnTime = spawning ? getMaxSpawnTime() : 0;
        setState(spawning ? SPAWNING : NONE);
        if (spawning && !this.level.isClientSide()) {
            this.level.broadcastEntityEvent(this, SPAWN_EVENT);
        }
    }

    public int getCrackiness() {
        return this.crackiness;
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case SPAWN_EVENT:
                setSpawning(true);
                break;
            case SHOOT_EVENT:
                shootTime = 1;
                this.playSound(SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, 1.1F, 1.0F);
                break;
            case ATTACK_EVENT:
                attackTimer = 1;
                this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 0.6F + random.nextFloat() * 0.2F);
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    public float getSpawnPercent(final float partialTick) {
        return 1.0F - Mth.lerp(partialTick, spawnTime0, spawnTime) / (float) getMaxSpawnTime();
    }

    public float getShootAnglePercent(final float partialTick) {
        return Mth.lerp(partialTick, shootAngle0, shootAngle);
    }

    public int getAttackTimer() {
        return attackTimer;
    }

    public float getAttackPercent(final float partialTick) {
        if(attackTimer <= 0) {
            return 0.0F;
        }
        return Mth.lerp(partialTick, attackTimer - 1, attackTimer) / (float) MAX_ATTACK_TIMER;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (this.level.isClientSide() || !this.isShooting() || shootTime < (getMaxShootTime() / 4)) {
            return;
        }
        ItemStack itemstack = new ItemStack(Items.ARROW);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, itemstack, distanceFactor);
        // this is adapted from LlamaSpit code, it moves the arrow nearer to right-front of the body
        // arrow.setPosition(this.getPosX() - (this.getWidth() + 1.0F) * 0.5D * Mth.sin(this.renderYawOffset * 0.017453292F), this.getPosYEye() - 0.10000000149011612D, this.getPosZ() + (this.getWidth() + 1.0F) * 0.5D * Mth.cos(this.renderYawOffset * 0.017453292F));
        arrow.setPos(this.getX() - (this.getBbWidth()) * 0.85D * Mth.sin(this.yBodyRot * 0.017453292F + 1.0F), this.getEyeY() - 0.74D, this.getZ() + (this.getBbWidth()) * 0.85D * Mth.cos(this.yBodyRot * 0.017453292F + 1.0F));
        double dx = target.getX() - arrow.getX();
        double dy = target.getY(0.67D) - arrow.getY();
        double dz = target.getZ() - arrow.getZ();
        double dis = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dis * (double) 0.2F, dz, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        arrow.setBaseDamage(1.0D + this.level.getDifficulty().getId() * 0.25D);
        arrow.setOwner(this);
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(arrow);
    }

    @Override
    public void setCustomCooldown(int cooldown) {
        this.attackCooldown = cooldown;
    }

    @Override
    public int getCustomCooldown() {
        return this.attackCooldown;
    }

    // Custom goals

    class SpawningGoal extends Goal {

        public SpawningGoal() {
            setFlags(EnumSet.allOf(Goal.Flag.class));
        }

        @Override
        public boolean canUse() {
            return Automaton.this.isSpawning();
        }

        @Override
        public void tick() {
            Automaton.this.getNavigation().stop();
            Automaton.this.getLookControl().setLookAt(Automaton.this.getX(), Automaton.this.getY(), Automaton.this.getZ());
            Automaton.this.setRot(0, 0);
            Automaton.this.setTarget(null);
        }
    }

    class AutomatonRangedAttackGoal extends CooldownRangedAttackGoal {

        public AutomatonRangedAttackGoal(int interval, float attackDistance, int cooldown) {
            super(Automaton.this, interval, attackDistance, cooldown);
        }

        @Override
        public boolean canUse() {
            return Automaton.this.isNoneState() && super.canUse();
        }

        @Override
        public void start() {
            super.start();
            Automaton.this.setShooting(true);
            Automaton.this.level.broadcastEntityEvent(Automaton.this, SHOOT_EVENT);
            Automaton.this.shootTime = 1;
        }

        @Override
        public boolean canContinueToUse() {
            return Automaton.this.shootTime > 0 && Automaton.this.isShooting();
        }

        @Override
        public void tick() {
            super.tick();
        }

        @Override
        public void stop() {
            Automaton.this.setState(NONE);
            Automaton.this.shootTime = 0;
            super.stop();
        }
    }
}
