package greekfantasy.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class WildRoseBlock extends FlowerBlock {

    public WildRoseBlock(Effect effect, int duration, Properties properties) {
        super(effect, duration, properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState blockstate, World world, BlockPos blockpos, Random rand) {
        VoxelShape shape = getShape(blockstate, world, blockpos, ISelectionContext.empty());
        Vector3d center = shape.bounds().getCenter();
        double posX = blockpos.getX() + center.x;
        double posZ = blockpos.getZ() + center.z;
        if (rand.nextInt(3) == 0) {
            world.addParticle(ParticleTypes.ENTITY_EFFECT, posX + rand.nextDouble() / 5.0D, blockpos.getY() + 0.5D - rand.nextDouble(), posZ + rand.nextDouble() / 5.0D, 1.0D, 0.6D, 0.92D);
        }
    }

    @Override
    public void entityInside(BlockState blockstate, World world, BlockPos pos, Entity entity) {
        if (!world.isClientSide() && entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity) entity;
            livingentity.addEffect(new EffectInstance(Effects.ABSORPTION, 78));
        }
    }

}
