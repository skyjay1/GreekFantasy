package greekfantasy.entity.misc;

import com.google.common.collect.ImmutableList;
import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class PigSpellEntity extends EffectProjectileEntity {

    public PigSpellEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    protected PigSpellEntity(World worldIn, LivingEntity thrower) {
        this(GFRegistry.PIG_SPELL_ENTITY, worldIn);
        super.setOwner(thrower);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
        // this unmapped method from ProjectileEntity does some math, then calls #shoot
        // params: thrower, rotationPitch, rotationYaw, ???, speed, inaccuracy
        shootFromRotation(thrower, thrower.xRot, thrower.yRot, 0.0F, 0.75F, 0.5F);
        markHurt();
    }

    public static PigSpellEntity create(World worldIn, LivingEntity thrower) {
        return new PigSpellEntity(worldIn, thrower);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    protected List<EffectInstance> getPotionEffects(final LivingEntity entity) {
        Effect effect = Effects.MOVEMENT_SLOWDOWN;
        int amp = 1;
        if (GreekFantasy.CONFIG.isPigEnabled() && GreekFantasy.CONFIG.canPigApply(entity.getType().getRegistryName().toString())) {
            effect = GFRegistry.PIG_EFFECT;
            amp = 0;
        }
        final int duration = GreekFantasy.CONFIG.getPigWandDuration();
        final int slowness = entity instanceof PlayerEntity ? 1 : duration;
        return ImmutableList.of(
                new EffectInstance(effect, duration, amp, false, true),
                new EffectInstance(Effects.MOVEMENT_SLOWDOWN, slowness, amp + 1, false, false, false),
                new EffectInstance(Effects.DIG_SLOWDOWN, duration - 1, amp + 1, false, false, false),
                new EffectInstance(Effects.WEAKNESS, duration - 1, amp + 1, false, false, false));
    }

    protected IParticleData getImpactParticle(final LivingEntity entity) {
        return ParticleTypes.ENCHANT;
    }

    protected IParticleData getTrailParticle() {
        return ParticleTypes.ENCHANTED_HIT;
    }

    protected float getImpactDamage(final LivingEntity entity) {
        return 0.0F;
    }
}
