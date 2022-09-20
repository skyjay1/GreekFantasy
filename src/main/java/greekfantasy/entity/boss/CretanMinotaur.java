package greekfantasy.entity.boss;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.IntervalRangedAttackGoal;
import greekfantasy.entity.ai.SummonMobGoal;
import greekfantasy.entity.misc.ThrowingAxe;
import greekfantasy.entity.monster.Minotaur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.List;

public class CretanMinotaur extends Minotaur implements RangedAttackMob {

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.PROGRESS);

    public CretanMinotaur(final EntityType<? extends CretanMinotaur> type, final Level worldIn) {
        super(type, worldIn);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 114.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.31D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.ARMOR, 10.0D);
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
    protected void registerChargeGoal() {
        this.goalSelector.addGoal(2, new MinotaurRangedAttackGoal(this, 5, 1, 95));
        this.goalSelector.addGoal(3, new MinotaurSummonMobGoal(this, 10, 130, 1));
        //this.goalSelector.addGoal(4, new ChargeAttackGoal(2.94D));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        if(!this.level.isClientSide() && this.tickCount % 60 == 1) {
            // clear player list
            this.bossInfo.removeAllPlayers();
            // locate nearby players and add them to the boss event when in range
            List<ServerPlayer> serverPlayers = level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox().inflate(24.0D));
            for(ServerPlayer player : serverPlayers) {
                if(player != null && this.getSensing().hasLineOfSight(player)) {
                    this.bossInfo.addPlayer(player);
                }
            }
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        ItemStack axe = new ItemStack(GFRegistry.ItemReg.THROWING_AXE.get());
        this.setItemInHand(InteractionHand.MAIN_HAND, axe);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.8F);
    }

    protected void populateDefaultEquipmentEnchantments(DifficultyInstance difficulty) {
        this.enchantSpawnedWeapon(3.25F);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficultyIn, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficultyIn, reason, spawnDataIn, dataTag);
        populateDefaultEquipmentSlots(difficultyIn);
        populateDefaultEquipmentEnchantments(difficultyIn);
        return data;
    }

        // Boss //

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.setVisible(GreekFantasy.CONFIG.showCretanBossBar());
        if(this.hasCustomName()) {
            this.bossInfo.setName(this.getCustomName());
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // Sound methods

    @Override
    protected float getSoundVolume() {
        return 1.2F;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() - 0.2F;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 240;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (!level.isClientSide()) {
            ItemStack axe = this.getMainHandItem().copy();
            ThrowingAxe throwingAxe = new ThrowingAxe(level, this, axe, false);
            throwingAxe.setBaseDamage(throwingAxe.getBaseDamage() + this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5D);
            double dx = target.getX() - throwingAxe.getX();
            double dy = target.getY(0.67D) - throwingAxe.getY();
            double dz = target.getZ() - throwingAxe.getZ();
            double dis = Math.sqrt(dx * dx + dz * dz);
            throwingAxe.shoot(dx, dy + dis * (double) 0.08F, dz, 1.12F, (float) (8 - this.level.getDifficulty().getId()));
            this.level.addFreshEntity(throwingAxe);
        }
        this.swing(InteractionHand.MAIN_HAND);
        this.playSound(SoundEvents.TRIDENT_THROW, 1.2F, 1.2F + this.random.nextFloat() * 0.2F);
    }

    static class MinotaurRangedAttackGoal extends IntervalRangedAttackGoal<CretanMinotaur> {
        protected final CretanMinotaur entity;

        public MinotaurRangedAttackGoal(CretanMinotaur mob, int attackInterval, int count, int maxCooldown) {
            super(mob, attackInterval, count, maxCooldown, 12.0F);
            this.entity = mob;
        }

        @Override
        public boolean canUse() {
            return this.entity.isNoneState() && super.canUse();
        }
    }

    static class MinotaurSummonMobGoal extends SummonMobGoal<Minotaur> {
        protected final CretanMinotaur entity;

        public MinotaurSummonMobGoal(CretanMinotaur entity, int duration, int cooldown, int mobCount) {
            super(entity, duration, cooldown, GFRegistry.EntityReg.MINOTAUR.get(), mobCount);
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return this.entity.isNoneState() && super.canUse() && (this.entity.getHealth() / this.entity.getMaxHealth() < 0.5F);
        }
    }
}
