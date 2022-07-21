package greekfantasy.entity.misc;

import greekfantasy.GFRegistry;
import greekfantasy.block.OliveOilBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.NetworkHooks;

public class GreekFire extends ThrowableItemProjectile {

    public GreekFire(EntityType<? extends GreekFire> entityType, Level level) {
        super(entityType, level);
    }

    private GreekFire(Level level, LivingEntity thrower) {
        super(GFRegistry.EntityReg.GREEK_FIRE.get(), thrower, level);
    }

    private GreekFire(Level level, double x, double y, double z) {
        super(GFRegistry.EntityReg.GREEK_FIRE.get(), x, y, z, level);
    }

    public static GreekFire create(Level level, double x, double y, double z) {
        return new GreekFire(level, x, y, z);
    }

    public static GreekFire create(Level level, LivingEntity thrower) {
        return new GreekFire(level, thrower);
    }

    @Override
    protected Item getDefaultItem() {
        return GFRegistry.ItemReg.GREEK_FIRE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult raytrace) {
        super.onHitEntity(raytrace);
        causeExplosion(raytrace.getLocation());
    }

    @Override
    protected void onHit(HitResult raytrace) {
        super.onHit(raytrace);
        if (!this.level.isClientSide() && level instanceof ServerLevel && this.isAlive()) {
            causeExplosion(raytrace.getLocation());
            discard();
        }
    }

    protected Explosion causeExplosion(final Vec3 vec) {
        // cause explosion at this location
        final float size = 1.25F;
        final float size2 = size * 1.5F;
        Explosion exp = this.level.explode(this.getOwner(), DamageSource.ON_FIRE, null, vec.x, vec.y, vec.z, size, false, Explosion.BlockInteraction.NONE);
        final BlockState oilBlock = GFRegistry.BlockReg.OLIVE_OIL.get().defaultBlockState().setValue(OliveOilBlock.LIT, true);
        final BlockState waterloggedOilBlock = oilBlock.setValue(OliveOilBlock.WATERLOGGED, true);
        // place oil fire around the area
        BlockPos origin = new BlockPos(vec.x, vec.y, vec.z);
        BlockPos.MutableBlockPos pos = origin.mutable();
        BlockState state;
        for(float x = -size2; x < size2; x++) {
            for(float y = -size2; y < size2; y++) {
                for(float z = -size2; z < size2; z++) {
                    // update position
                    pos.setWithOffset(origin, Mth.floor(x), Mth.floor(y), Mth.floor(z));
                    // determine if block should be destroyed
                    if (level.random.nextInt(3) > 0) {
                        state = level.getBlockState(pos);
                        if ((state.getMaterial().isReplaceable() && level.getBlockState(pos.below()).isSolidRender(level, pos.below()))) {
                            // attempt to place lit oil
                            this.level.setBlockAndUpdate(pos, oilBlock);
                        } else if (level.getBlockState(pos).getFluidState().is(FluidTags.WATER) && level.isEmptyBlock(pos.above())) {
                            // attempt to place waterlogged lit oil and soul fire
                            this.level.setBlockAndUpdate(pos, waterloggedOilBlock);
                            this.level.setBlock(pos.above(), Blocks.SOUL_FIRE.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }
        // create explosion
        return exp;
    }

    @Override
    public void tick() {
        // attempt to raytrace with fluids
        HitResult raytraceresult = level.clip(new ClipContext(
                this.position().add(-0.1D, -0.1D, -0.1D), this.position().add(0.1D, 0.1D, 0.1D),
                ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, this));
        if (raytraceresult.getType() == HitResult.Type.BLOCK) {
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
    public Entity changeDimension(ServerLevel serverWorld, ITeleporter iTeleporter) {
        Entity entity = this.getOwner();
        if (entity != null && entity.level.dimension() != serverWorld.dimension()) {
            setOwner(null);
        }
        return super.changeDimension(serverWorld, iTeleporter);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
