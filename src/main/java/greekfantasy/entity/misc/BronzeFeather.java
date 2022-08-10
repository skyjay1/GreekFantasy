package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.NetworkHooks;

public class BronzeFeather extends ThrowableItemProjectile {

    public BronzeFeather(EntityType<? extends BronzeFeather> entityType, Level level) {
        super(entityType, level);
    }

    private BronzeFeather(Level level, LivingEntity thrower) {
        super(GFRegistry.EntityReg.BRONZE_FEATHER.get(), thrower, level);
    }

    private BronzeFeather(Level level, double x, double y, double z) {
        super(GFRegistry.EntityReg.BRONZE_FEATHER.get(), x, y, z, level);
    }

    public static BronzeFeather create(Level level, double x, double y, double z) {
        return new BronzeFeather(level, x, y, z);
    }

    public static BronzeFeather create(Level level, LivingEntity thrower) {
        return new BronzeFeather(level, thrower);
    }

    @Override
    protected Item getDefaultItem() {
        return GFRegistry.ItemReg.BRONZE_FEATHER.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult raytrace) {
        super.onHitEntity(raytrace);
        final float damage = 1.0F;
        raytrace.getEntity().hurt(DamageSource.thrown(this, getOwner()).bypassArmor(), damage);
        discard();
    }

    @Override
    protected void onHit(HitResult raytrace) {
        super.onHit(raytrace);
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
        return 0.02F;
    }
}
