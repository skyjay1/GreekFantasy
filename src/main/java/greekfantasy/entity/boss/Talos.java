package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.entity.Automaton;
import greekfantasy.entity.ai.CooldownMeleeAttackGoal;
import greekfantasy.item.ClubItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class Talos extends Automaton implements Enemy {

    private final ServerBossEvent bossInfo = (ServerBossEvent) new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(true);

    public Talos(final EntityType<? extends Talos> type, final Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 260.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.20D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ATTACK_KNOCKBACK, ClubItem.ATTACK_KNOCKBACK_AMOUNT * 0.75D)
                .add(Attributes.ARMOR, 20.0D)
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 0.6F);
    }

    public static Talos spawnTalos(final Level level, final BlockPos pos, final float yaw) {
        Talos entity = GFRegistry.EntityReg.TALOS.get().create(level);
        entity.moveTo(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
        entity.yBodyRot = yaw;
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.addFreshEntityWithPassengers(entity);
            entity.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null, null);
        }
        entity.setSpawning(true);
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
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    @Override
    protected void registerAutomatonGoals() {
        this.goalSelector.addGoal(4, new CooldownMeleeAttackGoal<>(this, 1.0D, false, getMeleeAttackCooldown()));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    protected int getRangedAttackCooldown() {
        return 188;
    }

    @Override
    protected int getMeleeAttackCooldown() {
        return 40;
    }

    @Override
    protected double getBonusAttackKnockback() {
        return 0.92D;
    }

    @Override
    protected int getMaxSpawnTime() {
        return 94;
    }

    @Override
    protected int getMaxShootTime() {
        return 80;
    }

    public float getMaxShootingAngle() {
        return -0.98F;
    }

    @Override
    protected boolean isHealItem(final ItemStack itemStack) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    protected float getJumpPower() {
        return 1.2F * super.getJumpPower();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void pushEntities() {
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(final double disToPlayer) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(final DamageSource source) {
        return source.isMagic() || source.getDirectEntity() instanceof AbstractArrow || super.isInvulnerableTo(source);
    }

    @Override
    protected float getSoundVolume() {
        return 1.8F;
    }

    @Override
    public float getVoicePitch() {
        return 0.6F + random.nextFloat() * 0.25F;
    }

    // Boss Logic

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }
}
