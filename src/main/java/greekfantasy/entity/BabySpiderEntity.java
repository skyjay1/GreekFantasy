package greekfantasy.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class BabySpiderEntity extends SpiderEntity {

    // used to coordinate custom attack goal and run around goal
    private boolean isAttacking;

    public BabySpiderEntity(EntityType<? extends BabySpiderEntity> type, World worldIn) {
        super(type, worldIn);
        this.xpReward = 1;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.MAX_HEALTH, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.36F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.24F));
        this.goalSelector.addGoal(4, new BabySpiderEntity.AttackGoal(this, 1.0D, 100));
        this.goalSelector.addGoal(5, new BabySpiderEntity.RunAroundGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new BabySpiderEntity.TargetGoal<>(this, PlayerEntity.class));
        this.targetSelector.addGoal(3, new BabySpiderEntity.TargetGoal<>(this, IronGolemEntity.class));
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return 0.15F;
    }

    class AttackGoal extends net.minecraft.entity.ai.goal.MeleeAttackGoal {

        private final int maxCooldown;
        private int cooldown;

        public AttackGoal(CreatureEntity creature, double speedIn, int maxCooldownIn) {
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
            BabySpiderEntity.this.isAttacking = true;
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
            BabySpiderEntity.this.isAttacking = false;
        }

        @Override
        protected double getAttackReachSqr(LivingEntity attackTarget) {
            return 6.0F + attackTarget.getBbWidth();
        }
    }

    class RunAroundGoal extends net.minecraft.entity.ai.goal.PanicGoal {

        public RunAroundGoal(final CreatureEntity creatureIn, final double speedIn) {
            super(creatureIn, speedIn);
        }

        @Override
        public boolean canUse() {
            if (BabySpiderEntity.this.isAttacking) {
                return false;
            }
            return findRandomPosition();
        }

        @Override
        public boolean canContinueToUse() {
            if (BabySpiderEntity.this.isAttacking) {
                return false;
            }
            return super.canContinueToUse();
        }
    }

    class TargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

        public TargetGoal(BabySpiderEntity spider, Class<T> classTarget) {
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
