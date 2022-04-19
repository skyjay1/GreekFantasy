package greekfantasy.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class MadCowEntity extends CowEntity implements IMob {

    // used to coordinate custom attack goal and run around goal
    private boolean isAttacking;

    public MadCowEntity(final EntityType<? extends MadCowEntity> type, final World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return CowEntity.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.25D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MadCowEntity.AttackGoal(this, 1.75D, 130));
        this.goalSelector.addGoal(3, new MadCowEntity.RunAroundGoal(this, 1.75D));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // slowly lose health
        if (!this.isPersistenceRequired() && this.random.nextInt(400) == 0) {
            this.hurt(DamageSource.STARVE, 1.0F);
        }
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    class AttackGoal extends MeleeAttackGoal {

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
            MadCowEntity.this.isAttacking = true;
        }

        @Override
        public void stop() {
            super.stop();
            cooldown = maxCooldown;
            MadCowEntity.this.isAttacking = false;
        }

    }

    class RunAroundGoal extends net.minecraft.entity.ai.goal.PanicGoal {

        public RunAroundGoal(final CreatureEntity creatureIn, final double speedIn) {
            super(creatureIn, speedIn);
        }

        @Override
        public boolean canUse() {
            if (MadCowEntity.this.isAttacking) {
                return false;
            }
            return findRandomPosition();
        }

        @Override
        public boolean canContinueToUse() {
            if (MadCowEntity.this.isAttacking) {
                return false;
            }
            return super.canContinueToUse();
        }
    }
}
