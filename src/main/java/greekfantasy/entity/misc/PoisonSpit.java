package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class PoisonSpit extends MobEffectProjectile {

    public PoisonSpit(EntityType<? extends PoisonSpit> entityType, Level world) {
        super(entityType, world);
    }

    protected PoisonSpit(Level worldIn, LivingEntity thrower) {
        this(GFRegistry.EntityReg.POISON_SPIT.get(), worldIn);
        this.lifespan = 80;
        this.setOwner(thrower);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.78F, 0.4F);
        markHurt();
    }

    public static PoisonSpit create(Level worldIn, LivingEntity thrower) {
        return new PoisonSpit(worldIn, thrower);
    }

    @Override
    protected List<MobEffectInstance> getMobEffects(final LivingEntity entity) {
        return List.of(new MobEffectInstance(MobEffects.POISON, 90, 1));
    }

    @Override
    protected ParticleOptions getImpactParticle(final LivingEntity entity) {
        return ParticleTypes.DAMAGE_INDICATOR;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SNEEZE;
    }

    @Override
    protected float getImpactDamage(final LivingEntity entity) {
        return 0.5F;
    }
}
