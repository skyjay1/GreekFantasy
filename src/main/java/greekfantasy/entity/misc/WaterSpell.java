package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class WaterSpell extends MobEffectProjectile {

    public WaterSpell(EntityType<? extends WaterSpell> entityType, Level world) {
        super(entityType, world);
    }

    protected WaterSpell(Level worldIn, LivingEntity thrower) {
        this(GFRegistry.EntityReg.WATER_SPELL.get(), worldIn);
        this.lifespan = 400;
        this.setOwner(thrower);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.78F, 0.4F);
        markHurt();
    }

    public static WaterSpell create(Level worldIn, LivingEntity thrower) {
        return new WaterSpell(worldIn, thrower);
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -getGravity(), 0.0D));
        }
        super.tick();
    }

    @Override
    protected List<MobEffectInstance> getMobEffects(final LivingEntity entity) {
        List<MobEffectInstance> list = new ArrayList<>();
        // add slow swim to the list
        list.add(new MobEffectInstance(GFRegistry.MobEffectReg.SLOW_SWIM.get(), 180, 2));
        // add poison to the list depending on random chance and difficulty
        if(random.nextInt(15) < 1 + level.getDifficulty().getId() * 4) {
            list.add(new MobEffectInstance(MobEffects.POISON, 50, 0));
        }
        return list;
    }

    @Override
    protected ParticleOptions getImpactParticle(final LivingEntity entity) {
        return ParticleTypes.DAMAGE_INDICATOR;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SPLASH;
    }

    @Override
    protected float getImpactDamage(final LivingEntity entity) {
        return 4.0F;
    }

    public float getGravity() {
        return 0.08F;
    }
}
