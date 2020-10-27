package greekfantasy.block;

import java.util.Random;

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

public class WildRoseBlock extends FlowerBlock {

  public WildRoseBlock(Effect effect, int duration, Properties properties) {
    super(effect, duration, properties);
  }
  
  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState blockstate, World world, BlockPos blockpos, Random rand) {
    VoxelShape shape = getShape(blockstate, world, blockpos, ISelectionContext.dummy());
    Vector3d center = shape.getBoundingBox().getCenter();
    double posX = blockpos.getX() + center.x;
    double posZ = blockpos.getZ() + center.z;
    for (int i = rand.nextInt(3); i > 0; i--) {
      world.addParticle(ParticleTypes.AMBIENT_ENTITY_EFFECT, posX + rand.nextDouble() / 5.0D, blockpos.getY() + 0.5D - rand.nextDouble(), posZ + rand.nextDouble() / 5.0D, 1.0D, 0.6D, 0.92D);
    } 
  }
  
  @Override
  public void onEntityCollision(BlockState blockstate, World world, BlockPos pos, Entity entity) {
    if (!world.isRemote() && entity instanceof LivingEntity) {
      LivingEntity livingentity = (LivingEntity)entity;
      livingentity.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 80));
    }
  }

}
