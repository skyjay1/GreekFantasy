package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.block.OilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.network.NetworkHooks;

public class GreekFireEntity extends ProjectileItemEntity {

    public GreekFireEntity(EntityType<? extends GreekFireEntity> entityType, World world) {
        super(entityType, world);
    }

    private GreekFireEntity(World worldIn, LivingEntity thrower) {
        super(GFRegistry.GREEK_FIRE_ENTITY, thrower, worldIn);
    }

    private GreekFireEntity(World worldIn, double x, double y, double z) {
        super(GFRegistry.GREEK_FIRE_ENTITY, x, y, z, worldIn);
    }

    public static GreekFireEntity create(World worldIn, double x, double y, double z) {
        return new GreekFireEntity(worldIn, x, y, z);
    }

    public static GreekFireEntity create(World worldIn, LivingEntity thrower) {
        return new GreekFireEntity(worldIn, thrower);
    }

    @Override
    protected Item getDefaultItem() {
        return GFRegistry.GREEK_FIRE;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult raytrace) {
        super.onHitEntity(raytrace);
        causeExplosion(raytrace.getLocation());
    }

    @Override
    protected void onHit(RayTraceResult raytrace) {
        super.onHit(raytrace);
        if (!this.level.isClientSide() && level instanceof IServerWorld && this.isAlive()) {
            causeExplosion(raytrace.getLocation());
            remove();
        }
    }

    protected Explosion causeExplosion(final Vector3d vec) {
        // cause explosion at this location
        final float size = 1.25F;
        final float size2 = size * 1.5F;
        Explosion exp = this.level.explode(this.getOwner(), DamageSource.ON_FIRE, null, vec.x, vec.y, vec.z, size, false, Explosion.Mode.NONE);
        // place oil fire around the area
        BlockPos.Mutable pos = new BlockPos.Mutable(vec.x, vec.y, vec.z);
        BlockState state;
        for (double x = vec.x - size2; x < vec.x + size2; x++) {
            for (double y = vec.y - size2; y < vec.y + size2; y++) {
                for (double z = vec.z - size2; z < vec.z + size2; z++) {
                    pos.set(x, y, z);
                    state = level.getBlockState(pos);
                    if (level.random.nextInt(3) > 0) {
                        if ((state.getMaterial().isReplaceable() && level.getBlockState(pos.below()).isSolidRender(level, pos.below()))) {
                            // attempt to place lit oil
                            this.level.setBlockAndUpdate(pos, GFRegistry.OIL.defaultBlockState().setValue(OilBlock.LIT, true));
                        } else if (level.getBlockState(pos).getBlock() == Blocks.WATER && level.isEmptyBlock(pos.above())) {
                            // attempt to place waterlogged lit oil and soul fire
                            this.level.setBlockAndUpdate(pos, GFRegistry.OIL.defaultBlockState().setValue(OilBlock.WATERLOGGED, true).setValue(OilBlock.LIT, true));
                            this.level.setBlock(pos.above(), Blocks.SOUL_FIRE.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
        return exp;
    }

    @Override
    public void tick() {
        // attempt to raytrace with fluids
        RayTraceResult raytraceresult = level.clip(new RayTraceContext(
                this.position().add(-0.1D, -0.1D, -0.1D), this.position().add(0.1D, 0.1D, 0.1D),
                RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, this));
        if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
            onHit(raytraceresult);
        } else {
            super.tick();
        }
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravity() {
        return 0.09F;
    }

    @Override
    public Entity changeDimension(ServerWorld serverWorld, ITeleporter iTeleporter) {
        Entity entity = this.getOwner();
        if (entity != null && entity.level.dimension() != serverWorld.dimension()) {
            setOwner(null);
        }
        return super.changeDimension(serverWorld, iTeleporter);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
