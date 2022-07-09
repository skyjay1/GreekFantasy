package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.NetworkHooks;

public class DragonTooth extends ThrowableItemProjectile {

    public DragonTooth(EntityType<? extends DragonTooth> entityType, Level level) {
        super(entityType, level);
    }

    private DragonTooth(Level level, LivingEntity thrower) {
        super(GFRegistry.EntityReg.DRAGON_TOOTH.get(), thrower, level);
    }

    private DragonTooth(Level level, double x, double y, double z) {
        super(GFRegistry.EntityReg.DRAGON_TOOTH.get(), x, y, z, level);
    }

    public static DragonTooth create(Level level, double x, double y, double z) {
        return new DragonTooth(level, x, y, z);
    }

    public static DragonTooth create(Level level, LivingEntity thrower) {
        return new DragonTooth(level, thrower);
    }

    @Override
    protected Item getDefaultItem() {
        return GFRegistry.ItemReg.DRAGON_TOOTH.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult raytrace) {
        super.onHitEntity(raytrace);
        final float damage = GreekFantasy.CONFIG.DRAGON_TOOTH_SPARTI_COUNT.get() > 0 ? 0.0F : 1.5F;
        raytrace.getEntity().hurt(DamageSource.thrown(this, getOwner()), damage);
    }

    @Override
    protected void onHit(HitResult raytrace) {
        super.onHit(raytrace);
        if (!this.level.isClientSide() && this.isAlive()) {
            Entity thrower = getOwner();
            // spawn a configurable number of sparti
            for (int i = 0, n = GreekFantasy.CONFIG.DRAGON_TOOTH_SPARTI_COUNT.get(), life = 20 * GreekFantasy.CONFIG.DRAGON_TOOTH_SPARTI_LIFESPAN.get(); i < n; i++) {
                // TODO
                /*final SpartiEntity sparti = GFRegistry.EntityReg.SPARTI_ENTITY.create(level);
                sparti.moveTo(raytrace.getLocation().x, raytrace.getLocation().y, raytrace.getLocation().z, 0, 0);
                level.addFreshEntity(sparti);
                if (thrower instanceof PlayerEntity) {
                    sparti.xRot = MathHelper.wrapDegrees(thrower.yRot + 180.0F);
                    sparti.setTame(true);
                    sparti.setOwnerUUID(thrower.getUUID());
                }
                sparti.setLimitedLife(life);
                sparti.finalizeSpawn((IServerWorld) level, level.getCurrentDifficultyAt(new BlockPos(raytrace.getLocation())), SpawnReason.MOB_SUMMONED, null, null);*/
            }
            discard();
        }
    }

    @Override
    public void tick() {
        Entity entity = getOwner();
        if (entity instanceof Player && !entity.isAlive()) {
            discard();
        } else {
            super.tick();
        }
    }

    @Override
    public Entity changeDimension(ServerLevel serverWorld, ITeleporter iTeleporter) {
        Entity entity = getOwner();
        if (entity != null && entity.level.dimension() != serverWorld.dimension()) {
            setOwner(null);
        }
        return super.changeDimension(serverWorld, iTeleporter);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getGravity() {
        return 0.11F;
    }
}
