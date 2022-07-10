package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;

import java.util.List;

public class HealingSpell extends MobEffectProjectile {

    public HealingSpell(EntityType<? extends HealingSpell> entityType, Level world) {
        super(entityType, world);
    }

    protected HealingSpell(Level worldIn, LivingEntity thrower) {
        this(GFRegistry.EntityReg.HEALING_SPELL.get(), worldIn);
        this.setOwner(thrower);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.78F, 0.4F);
        markHurt();
    }

    public static HealingSpell create(Level worldIn, LivingEntity thrower) {
        return new HealingSpell(worldIn, thrower);
    }

    @Override
    protected List<MobEffectInstance> getMobEffects(final LivingEntity entity) {
        return List.of(new MobEffectInstance(MobEffects.HEAL, 1, 1));
    }

    @Override
    protected ParticleOptions getImpactParticle(final LivingEntity entity) {
        return entity.getMobType() == MobType.UNDEAD ? ParticleTypes.DAMAGE_INDICATOR : ParticleTypes.HEART;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.HAPPY_VILLAGER;
    }

    @Override
    protected float getImpactDamage(final LivingEntity entity) {
        return 0.0F;
    }
}
