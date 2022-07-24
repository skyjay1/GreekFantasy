package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;

public class CurseOfCirce extends MobEffectProjectile {

    public CurseOfCirce(EntityType<? extends CurseOfCirce> entityType, Level world) {
        super(entityType, world);
    }

    protected CurseOfCirce(Level worldIn, LivingEntity thrower) {
        this(GFRegistry.EntityReg.CURSE_OF_CIRCE.get(), worldIn);
        this.lifespan = 90;
        this.setOwner(thrower);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.78F, 0.4F);
        markHurt();
    }

    public static CurseOfCirce create(Level worldIn, LivingEntity thrower) {
        return new CurseOfCirce(worldIn, thrower);
    }

    @Override
    protected List<MobEffectInstance> getMobEffects(final LivingEntity entity) {
        MobEffect effect = MobEffects.MOVEMENT_SLOWDOWN;
        int amp = 1;
        if (GreekFantasy.CONFIG.isCurseOfCirceEnabled() && GreekFantasy.CONFIG.isCurseOfCirceApplicable(entity)) {
            effect = GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get();
            amp = 0;
        }
        final int mobEffectDuration = GreekFantasy.CONFIG.CURSE_OF_CIRCE_DURATION.get();
        final int slownessDuration = entity instanceof Player ? 1 : mobEffectDuration;
        return List.of(
                new MobEffectInstance(effect, mobEffectDuration, amp, false, true),
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slownessDuration, amp + 1, false, false, false),
                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, mobEffectDuration - 1, amp + 1, false, false, false));
    }

    @Override
    protected ParticleOptions getImpactParticle(final LivingEntity entity) {
        return ParticleTypes.ENCHANT;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.ENCHANTED_HIT;
    }

    @Override
    protected float getImpactDamage(final LivingEntity entity) {
        return 0.0F;
    }
}
