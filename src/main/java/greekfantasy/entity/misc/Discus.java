package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.NetworkHooks;

public class Discus extends ThrowableItemProjectile {

    public Discus(EntityType<? extends Discus> entityType, Level world) {
        super(entityType, world);
    }

    private Discus(Level level, LivingEntity thrower) {
        super(GFRegistry.EntityReg.DISCUS.get(), thrower, level);
    }

    private Discus(Level level, double x, double y, double z) {
        super(GFRegistry.EntityReg.DISCUS.get(), x, y, z, level);
    }

    public static Discus create(Level level, double x, double y, double z) {
        return new Discus(level, x, y, z);
    }

    public static Discus create(Level level, LivingEntity thrower) {
        return new Discus(level, thrower);
    }

    @Override
    protected Item getDefaultItem() {
        return GFRegistry.ItemReg.DISCUS.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult raytrace) {
        super.onHitEntity(raytrace);
        final float damage = (float) (this.getDeltaMovement().horizontalDistanceSqr() * 2.2D);
        raytrace.getEntity().hurt(DamageSource.thrown(this, getOwner()), damage);
    }

    @Override
    protected void onHit(HitResult raytrace) {
        super.onHit(raytrace);
        if (random.nextFloat() < 0.028F && !(getOwner() instanceof Player player && player.isCreative())) {
            final Vec3 vec = raytrace.getLocation();
            final ItemEntity item = new ItemEntity(this.level, vec.x, vec.y + 0.25D, vec.z, new ItemStack(getDefaultItem()));
            this.level.addFreshEntity(item);
        } else {
            this.playSound(SoundEvents.ITEM_BREAK, 1.0F, 1.0F + random.nextFloat() * 0.2F);
        }
        this.discard();
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

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravity() {
        return 0.08F;
    }

    @Override
    public Entity changeDimension(ServerLevel serverLevel, ITeleporter iTeleporter) {
        Entity entity = this.getOwner();
        if (entity != null && entity.level.dimension() != serverLevel.dimension()) {
            setOwner(null);
        }
        return super.changeDimension(serverLevel, iTeleporter);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
