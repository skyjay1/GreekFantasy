package greekfantasy.entity.monster;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ai.IntervalRangedAttackGoal;
import greekfantasy.entity.misc.CurseOfCirce;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class Circe extends Monster implements RangedAttackMob {

    protected static final Predicate<LivingEntity> IS_CURSED = e -> (e != null && e.getEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()) != null);
    protected static final Predicate<LivingEntity> NOT_CURSED = e -> (e != null && null == e.getEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()));

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    public Circe(final EntityType<? extends Circe> type, final Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.ARMOR, 1.5D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new IntervalRangedAttackGoal<>(this, 90, 1, GreekFantasy.CONFIG.WAND_OF_CIRCE_COOLDOWN.get() * 4));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, LivingEntity.class, 6.0F, 1.4D, 1.2D, e -> NOT_CURSED.test(e) && e == Circe.this.getTarget()));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D) {
            @Override
            public boolean canUse() {
                return null == Circe.this.getTarget() && Circe.this.random.nextInt(90) == 0 && super.canUse();
            }
        });
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, NOT_CURSED.and(e -> GreekFantasy.CONFIG.isCurseOfCirceApplicable(e))));
        this.targetSelector.addGoal(4, new Circe.ResetTargetGoal());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
        // apply potion effect to self
        if (this.random.nextFloat() < 0.15F && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().isFire()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            this.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 900));
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(difficulty);
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(GFRegistry.ItemReg.WAND_OF_CIRCE.get()));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnDataIn, dataTag);
        populateDefaultEquipmentSlots(difficulty);
        return data;
    }

    // Attack //

    @Override
    public void performRangedAttack(LivingEntity arg0, float arg1) {
        if (!level.isClientSide()) {
            CurseOfCirce spell = CurseOfCirce.create(level, this);
            level.addFreshEntity(spell);
        }
        this.playSound(SoundEvents.ILLUSIONER_CAST_SPELL, 1.2F, 1.0F);
        // swing arm
        this.swing(InteractionHand.MAIN_HAND);
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.WITCH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.8F;
    }

    //Boss //

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossInfo.addPlayer(player);
        this.bossInfo.setName(this.hasCustomName() ? this.getCustomName() : this.getDisplayName());
        this.bossInfo.setVisible(GreekFantasy.CONFIG.showCirceBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    // Goals //

    class ResetTargetGoal extends Goal {

        protected int interval;

        public ResetTargetGoal() {
            this(10);
        }

        public ResetTargetGoal(int intervalIn) {
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
            interval = Math.max(1, intervalIn);
        }

        @Override
        public boolean canUse() {
            return Circe.this.tickCount % interval == 0 && Circe.this.isAlive()
                    && Circe.IS_CURSED.test(Circe.this.getTarget());
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Circe.this.setTarget(null);
        }
    }

}
