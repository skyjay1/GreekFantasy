package greekfantasy.entity;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumSet;

public class EmpusaEntity extends MonsterEntity {

    private static final byte DRAINING_START = 4;
    private static final byte DRAINING_END = 5;

    private boolean isDraining;

    public EmpusaEntity(final EntityType<? extends EmpusaEntity> type, final World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 24.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.21D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        if (GreekFantasy.CONFIG.EMPUSA_ATTACK.get()) {
            this.goalSelector.addGoal(2, new DrainAttackGoal(this));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // hurt in daytime, then remove when at half health
        if (this.isEffectiveAi() && this.getCommandSenderWorld().getDayTime() % 24000L < 11000L && this.hurtTime == 0 && random.nextInt(20) == 0) {
            this.hurt(DamageSource.STARVE, 1.0F);
            if (this.getHealth() < this.getMaxHealth() / 2.0F && this.getTarget() == null) {
                // remove the entity without dropping loot
                this.remove();
                return;
            }
        }

        // spawn particles
        if (level.isClientSide() && this.isDraining()) {
            particleRay();
        }
    }


    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        switch (id) {
            case DRAINING_START:
                this.isDraining = true;
                this.playSound(SoundEvents.ENDERMAN_SCREAM, this.getSoundVolume(), this.getVoicePitch());
                break;
            case DRAINING_END:
                this.isDraining = false;
                break;
            default:
                super.handleEntityEvent(id);
                break;
        }
    }

    public void setDraining(final boolean draining) {
        this.isDraining = draining;
        if (this.isEffectiveAi()) {
            this.level.broadcastEntityEvent(this, draining ? DRAINING_START : DRAINING_END);
        }
    }

    public boolean isDraining() {
        return this.isDraining;
    }

    public void particleRay() {
        Vector3d pos = this.getEyePosition(1.0F).add(0, -this.getBbHeight() * 0.25D, 0);
        Vector3d lookVec = this.getLookAngle();
        Vector3d scaled;
        for (double i = 0, l = lookVec.scale(12.0D).length(), stepSize = 0.25F; i < l; i += stepSize) {
            scaled = lookVec.scale(i);
            final double x = pos.x + scaled.x;
            final double y = pos.y + scaled.y;
            final double z = pos.z + scaled.z;
            final AxisAlignedBB aabb = new AxisAlignedBB(x - 0.1D, y - 0.1D, z - 0.1D, x + 0.1D, y + 0.1D, z + 0.1D);
            if (!this.getCommandSenderWorld().getEntities(this, aabb).isEmpty()) {
                return;
            }
            this.getCommandSenderWorld().addParticle(ParticleTypes.CRIT, x, y, z, 0, 0, 0);
        }
    }

    static class DrainAttackGoal extends Goal {

        private final EmpusaEntity entity;
        private final int MAX_DRAIN_TIME = 80;
        private final int MAX_COOLDOWN = 160;
        private int drainingTime;
        private int cooldown;

        protected DrainAttackGoal(final EmpusaEntity entityIn) {
            this.setFlags(EnumSet.allOf(Goal.Flag.class));
            this.entity = entityIn;
            this.cooldown = MAX_COOLDOWN / 2;
        }

        @Override
        public boolean canUse() {
            if (this.cooldown > 0) {
                cooldown--;
            } else return this.entity.getTarget() != null
                    && entity.distanceToSqr(this.entity.getTarget()) < 36.0D
                    && this.entity.canSee(this.entity.getTarget());
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return this.entity.isDraining() && this.entity.getTarget() != null
                    && this.entity.canSee(this.entity.getTarget());
        }

        @Override
        public void start() {
            this.drainingTime = 1;
            this.entity.setDraining(true);
        }

        @Override
        public void tick() {
            if (drainingTime > 0 && drainingTime < MAX_DRAIN_TIME) {
                drainingTime++;
                // stop the entity from moving, and adjust look vecs
                this.entity.getNavigation().stop();
                this.entity.lookAt(this.entity.getTarget(), 100.0F, 100.0F);
                // drain health from targetPos
                if (drainingTime > (MAX_DRAIN_TIME / 3) && this.entity.getTarget().hurtTime == 0) {
                    final DamageSource src = DamageSource.indirectMagic(this.entity, this.entity);
                    float amount = (float) this.entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 0.5F;
                    if (this.entity.getTarget().hurt(src, amount)) {
                        this.entity.heal(amount * 1.5F);
                    }
                }
            } else {
                stop();
            }
        }

        @Override
        public void stop() {
            this.entity.setDraining(false);
            this.drainingTime = 0;
            this.cooldown = MAX_COOLDOWN;
        }
    }

}
