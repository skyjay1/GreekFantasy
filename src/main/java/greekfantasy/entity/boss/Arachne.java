package greekfantasy.entity.boss;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.misc.WebBall;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Arachne extends Monster implements RangedAttackMob {

    private final ServerBossEvent bossInfo = new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS);

    public Arachne(EntityType<? extends Arachne> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.285D)
                .add(Attributes.ATTACK_DAMAGE, 4.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ArachneRangedAttackGoal(1.0D, 75, 15.0F));
        this.goalSelector.addGoal(4, new ArachneAttackGoal(1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // boss info
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    // Sounds //

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 motionMultiplierIn) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, motionMultiplierIn);
        }
    }

    // Ranged Attack //

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        WebBall web = WebBall.create(level, this);
        // set the web type with sometimes web, always spider, and no item
        web.setWebType(getRandom().nextBoolean(), true, false);
        // this is copied from LlamaSpit code, it moves the arrow nearer to the centaur's human-body
        web.setPos(this.getX() - (this.getBbWidth() + 1.0F) * 0.5D * Mth.sin(this.yBodyRot * 0.017453292F),
                this.getEyeY() - 0.1D,
                this.getZ() + (this.getBbWidth() + 1.0F) * 0.5D * Mth.cos(this.yBodyRot * 0.017453292F));
        double dx = target.getX() - web.getX();
        double dy = target.getY(0.67D) - web.getY();
        double dz = target.getZ() - web.getZ();
        double dis = Math.sqrt(dx * dx + dz * dz);
        web.shoot(dx, dy + dis * (double) 0.2F, dz, 1.14F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.LLAMA_SPIT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(web);
    }

    // Misc //

    @Override
    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return potioneffectIn.getEffect() != MobEffects.POISON && super.canBeAffected(potioneffectIn);
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
        this.bossInfo.setVisible(GreekFantasy.CONFIG.showArachneBossBar());
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    class ArachneRangedAttackGoal extends RangedAttackGoal {
        public ArachneRangedAttackGoal(double moveSpeed, int attackInterval, float attackDistance) {
            super(Arachne.this, moveSpeed, attackInterval, attackDistance);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = Arachne.this.getTarget();
            return target != null && Arachne.this.distanceToSqr(target) > 9.0D && super.canUse();
        }
    }

    class ArachneAttackGoal extends net.minecraft.world.entity.ai.goal.MeleeAttackGoal {

        public ArachneAttackGoal(double moveSpeed, boolean useLongMemory) {
            super(Arachne.this, moveSpeed, useLongMemory);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = Arachne.this.getTarget();
            return target != null && Arachne.this.distanceToSqr(target) < 9.0D && super.canUse();
        }
    }

}
