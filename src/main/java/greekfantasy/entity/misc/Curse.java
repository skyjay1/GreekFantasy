package greekfantasy.entity.misc;

import com.google.common.collect.ImmutableList;
import greekfantasy.GFRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class Curse extends MobEffectProjectile {

    private static final MobEffect[] CURSES = {
            MobEffects.BLINDNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.POISON, MobEffects.HARM, MobEffects.WEAKNESS,
            MobEffects.WITHER, MobEffects.LEVITATION, MobEffects.GLOWING, MobEffects.DIG_SLOWDOWN, MobEffects.HUNGER,
            MobEffects.CONFUSION, MobEffects.UNLUCK};

    public Curse(EntityType<? extends Curse> entityType, Level world) {
        super(entityType, world);
    }

    protected Curse(Level worldIn, LivingEntity thrower) {
        this(GFRegistry.EntityReg.CURSE.get(), worldIn);
        this.lifespan = 90;
        this.setOwner(thrower);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        // this unmapped method from ProjectileEntity does some math, then calls #shoot
        // params: thrower, rotationPitch, rotationYaw, ???, speed, inaccuracy
        shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.78F, 0.4F);
        markHurt();
    }

    public static Curse create(Level worldIn, LivingEntity thrower) {
        return new Curse(worldIn, thrower);
    }

    @Override
    protected List<MobEffectInstance> getMobEffects(final LivingEntity entity) {
        final MobEffect mobEffect = CURSES[entity.getRandom().nextInt(CURSES.length)];
        return ImmutableList.of(new MobEffectInstance(mobEffect, 200, entity.getRandom().nextInt(2)));
    }

    @Override
    protected ParticleOptions getImpactParticle(final LivingEntity entity) {
        return ParticleTypes.DAMAGE_INDICATOR;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    @Override
    protected float getImpactDamage(final LivingEntity entity) {
        return 0.5F;
    }
}
