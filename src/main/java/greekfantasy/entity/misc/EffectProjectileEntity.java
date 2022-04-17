package greekfantasy.entity.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public abstract class EffectProjectileEntity extends ProjectileEntity {

    protected int lifespan = 300;

    public EffectProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult raytrace) {
        super.onHitEntity(raytrace);
        Entity thrower = getOwner();
        if (raytrace.getEntity() != thrower && raytrace.getEntity() instanceof LivingEntity) {
            final LivingEntity entity = (LivingEntity) raytrace.getEntity();
            // add potion effects
            for (final EffectInstance effect : getPotionEffects(entity)) {
                entity.addEffect(effect);
            }
            // impact may inflict damage
            float damage = getImpactDamage(entity);
            if (damage > 0 && thrower instanceof LivingEntity) {
                entity.hurt(DamageSource.indirectMobAttack(this, (LivingEntity) thrower), damage);
            }
            // add particle effect
            addParticles(getImpactParticle(entity), 6 + random.nextInt(6));
        }
    }

    @Override
    protected void onHit(RayTraceResult raytrace) {
        super.onHit(raytrace);
        if (this.isAlive()) {
            remove();
        }
    }

    @Override
    public void tick() {
        Entity thrower = getOwner();
        if (thrower instanceof net.minecraft.entity.player.PlayerEntity && !thrower.isAlive()) {
            remove();
            return;
        }
        // remove if too old
        if (this.tickCount > lifespan) {
            remove();
            return;
        }
        // check for impact
        if (!this.getCommandSenderWorld().isClientSide()) {
            RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
            if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS
                    && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                this.onHit(raytraceresult);
            }
        }
        // particle trail
        if (this.tickCount > 2) {
            addParticles(getTrailParticle(), 2);
        }
        // movement
        Vector3d motion = this.getDeltaMovement();
        double d0 = this.getX() + motion.x;
        double d1 = this.getY() + motion.y;
        double d2 = this.getZ() + motion.z;
        // lerp rotation and pitch
        this.updateRotation();
        // actually move the entity
        this.setPos(d0, d1, d2);
        // super method
        super.tick();
    }

    @Override
    public Entity changeDimension(ServerWorld serverWorld, ITeleporter iTeleporter) {
        Entity entity = getOwner();
        if (entity != null && entity.level.dimension() != serverWorld.dimension()) {
            setOwner(null);
        }
        return super.changeDimension(serverWorld, iTeleporter);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void defineSynchedData() {
    }

    abstract List<EffectInstance> getPotionEffects(final LivingEntity entity);

    abstract IParticleData getImpactParticle(final LivingEntity entity);

    abstract IParticleData getTrailParticle();

    abstract float getImpactDamage(final LivingEntity entity);

    protected void addParticles(final IParticleData type, final int count) {
        if (this.getCommandSenderWorld().isClientSide()) {
            final double x = getX();
            final double y = getY() + 0.1D;
            final double z = getZ();
            final double motion = 0.08D;
            final double width = getBbWidth() / 2;
            final double height = getBbHeight() / 2;
            for (int i = 0; i < count; i++) {
                level.addParticle(type,
                        x + (level.random.nextDouble() - 0.5D) * width,
                        y + height,
                        z + (level.random.nextDouble() - 0.5D) * width,
                        (level.random.nextDouble() - 0.5D) * motion,
                        (level.random.nextDouble() - 0.5D) * motion,
                        (level.random.nextDouble() - 0.5D) * motion);
            }
        }
    }

}
