package greekfantasy.entity.monster;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BabySpider extends Spider {

    // used to coordinate custom attack goal and run around goal
    private boolean isAttacking;

    public BabySpider(EntityType<? extends BabySpider> type, Level level) {
        super(type, level);
        this.xpReward = 1;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36F);
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() + 0.5F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.24F));
        this.goalSelector.addGoal(4, new AttackGoal(this, 1.0D, 100));
        this.goalSelector.addGoal(5, new RunAroundGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new TargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(3, new TargetGoal<>(this, IronGolem.class));
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn) {
        return 0.15F;
    }

    class AttackGoal extends MeleeAttackGoal {

        private final int maxCooldown;
        private int cooldown;

        public AttackGoal(PathfinderMob creature, double speedIn, int maxCooldownIn) {
            super(creature, speedIn, false);
            maxCooldown = maxCooldownIn;
            cooldown = 20;
        }

        @Override
        public boolean canUse() {
            if (cooldown > 0) {
                cooldown--;
            } else {
                return super.canUse();
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            BabySpider.this.isAttacking = true;
        }


        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            float f = this.mob.getBrightness();
            if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
                this.mob.setTarget(null);
                return false;
            } else {
                return super.canContinueToUse();
            }
        }


        @Override
        public void stop() {
            super.stop();
            cooldown = maxCooldown;
            BabySpider.this.isAttacking = false;
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return 6.0F + attackTarget.getBbWidth();
        }
    }

    class RunAroundGoal extends PanicGoal {

        public RunAroundGoal(final PathfinderMob creatureIn, final double speedIn) {
            super(creatureIn, speedIn);
        }

        @Override
        public boolean canUse() {
            if (BabySpider.this.isAttacking) {
                return false;
            }
            return findRandomPosition();
        }

        @Override
        public boolean canContinueToUse() {
            if (BabySpider.this.isAttacking) {
                return false;
            }
            return super.canContinueToUse();
        }
    }

    class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        public TargetGoal(BabySpider spider, Class<T> classTarget) {
            super(spider, classTarget, true);
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean canUse() {
            float f = this.mob.getBrightness();
            return !(f >= 0.5F) && super.canUse();
        }
    }
}
