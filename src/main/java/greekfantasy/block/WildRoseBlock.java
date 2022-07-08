package greekfantasy.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class WildRoseBlock extends FlowerBlock {

    private final MobEffect mobEffect;
    private final int duration;

    public WildRoseBlock(MobEffect mobEffect, int duration, Properties properties) {
        super(mobEffect, duration, properties);
        this.mobEffect = mobEffect;
        this.duration = duration;
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState blockstate, Level level, BlockPos blockpos, Random rand) {
        VoxelShape shape = getShape(blockstate, level, blockpos, CollisionContext.empty());
        Vec3 center = shape.bounds().getCenter();
        double posX = blockpos.getX() + center.x;
        double posZ = blockpos.getZ() + center.z;
        if (rand.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.ENTITY_EFFECT, posX + rand.nextDouble() / 5.0D, blockpos.getY() + 0.5D - rand.nextDouble(), posZ + rand.nextDouble() / 5.0D, 1.0D, 0.6D, 0.92D);
        }
    }

    @Override
    public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide() && entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(this.mobEffect, this.duration * 8));
        }
    }

}
