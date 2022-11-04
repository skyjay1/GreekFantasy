package greekfantasy.entity.monster;


import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class MadCow extends Cow implements Enemy {

    // used to coordinate custom attack goal and run around goal
    private boolean isAttacking;

    public MadCow(final EntityType<? extends MadCow> type, final Level worldIn) {
        super(type, worldIn);
        // remove pathfinding malus to encourage dangerous movement
        this.setPathfindingMalus(BlockPathTypes.DANGER_CACTUS, 0);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_CACTUS, 0);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0);
        this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Cow.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 0.25D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MadCowAttackGoal(this, 1.75D, 130));
        this.goalSelector.addGoal(3, new MadCowPanicGoal(this, 1.75D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
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
    public MobCategory getClassification(boolean forSpawnCount) {
        return MobCategory.MONSTER;
    }

    @Override
    public boolean canCutCorner(BlockPathTypes pathType) {
        return pathType != BlockPathTypes.WALKABLE_DOOR;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    class MadCowAttackGoal extends MeleeAttackGoal {

        private final int maxCooldown;
        private int cooldown;

        public MadCowAttackGoal(PathfinderMob creature, double speedIn, int maxCooldownIn) {
            super(creature, speedIn, false);
            maxCooldown = maxCooldownIn;
            cooldown = 10;
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
            MadCow.this.isAttacking = true;
        }

        @Override
        public void stop() {
            super.stop();
            cooldown = maxCooldown;
            MadCow.this.isAttacking = false;
        }

    }

    class MadCowPanicGoal extends PanicGoal {

        public MadCowPanicGoal(final PathfinderMob creatureIn, final double speedIn) {
            super(creatureIn, speedIn);
        }

        @Override
        public boolean canUse() {
            if (MadCow.this.isAttacking) {
                return false;
            }
            return findRandomPosition();
        }

        @Override
        public boolean canContinueToUse() {
            if (MadCow.this.isAttacking) {
                return false;
            }
            return super.canContinueToUse();
        }
    }
}
