package greekfantasy.item;

import javax.annotation.Nullable;

import greekfantasy.GreekFantasy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ConchItem extends Item {

  public ConchItem(final Item.Properties properties) {
    super(properties);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand handIn) {
    final ItemStack item = player.getHeldItem(handIn);
    if(!world.isRemote() && GreekFantasy.CONFIG.isConchEnabled()) {
      // raytrace
      final RayTraceResult raytrace = ThunderboltItem.raytraceFromEntity(world, player, 32.0F);
      final BlockPos hitPos = new BlockPos(raytrace.getHitVec());
      // attempt to place water at the hit position
      if(raytrace.getType() == RayTraceResult.Type.BLOCK && world.isBlockModifiable(player, hitPos) && player.canPlayerEdit(hitPos, null, item)) {
        // take/place water and set cooldown
        if(player.isSneaking()) {
          attemptTakeWater(player, world, hitPos);
        } else {
          attemptPlaceWater(player, world, hitPos, player.getHorizontalFacing().getOpposite());
        }
        player.getCooldownTracker().setCooldown(this, GreekFantasy.CONFIG.getConchCooldown());
        return ActionResult.resultSuccess(item);
      }
    }
    return ActionResult.resultPass(item);
  }
  
  public static boolean attemptTakeWater(final PlayerEntity player, final World worldIn, final BlockPos pos) {
    final BlockState blockstate = worldIn.getBlockState(pos);
    final FluidState fluidstate = worldIn.getFluidState(pos);
    final Block block = blockstate.getBlock();
    if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn, pos, blockstate, Fluids.EMPTY)) {
      // if this block can receive liquid, attempt to do that first
      ((ILiquidContainer)block).receiveFluid(worldIn, pos, blockstate, Fluids.EMPTY.getDefaultState());
      worldIn.playSound(player, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
      return true;
    } else {
      // destroy the block that's already here if it's replaceable
      if (!worldIn.isRemote() && fluidstate.isTagged(FluidTags.WATER)) {
        worldIn.destroyBlock(pos, true);
      }
      // finally, attempt to place water directly
      if (blockstate.getFluidState().isSource() && !worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 8 | 2 | 1)) {
        return false;
      } else {
        worldIn.playSound(player, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return true;
      }
    }
  }
  
  public static boolean attemptPlaceWater(final PlayerEntity player, final World worldIn, final BlockPos pos, 
      @Nullable final Direction facing) {
    final BlockState selected = worldIn.getBlockState(pos);
    final Block block = selected.getBlock();
    boolean replaceable = selected.isReplaceable(Fluids.WATER);
    boolean canReceiveWater = worldIn.isAirBlock(pos) || replaceable || (block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn, pos, selected, Fluids.WATER));
    // if this block cannot receive water, offset and try again
    if(!canReceiveWater && facing != null) {
      // passing null as the facing direction indicates that this was a recursive call
      return attemptPlaceWater(player, worldIn, pos.offset(facing), null);
    }
    // if water cannot be placed in this dimension, spawn particles and play sound instead
    if (worldIn.getDimensionType().isUltrawarm()) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      worldIn.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
          2.6F + (player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.8F);
      // spawn "extinguish" particles
      for (int l = 0; l < 8; ++l) {
        worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(),
            (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
      }
      return false;
    } else if (block instanceof ILiquidContainer && ((ILiquidContainer)block).canContainFluid(worldIn, pos, selected, Fluids.WATER)) {
      // if this block can receive liquid, attempt to do that first
      ((ILiquidContainer)block).receiveFluid(worldIn, pos, selected, Fluids.WATER.getStillFluidState(false));
      worldIn.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
      return true;
    } else {
      // destroy the block that's already here if it's replaceable
      if (!worldIn.isRemote() && replaceable && !selected.getMaterial().isLiquid()) {
        worldIn.destroyBlock(pos, true);
      }
      // finally, attempt to place water directly
      if (!worldIn.setBlockState(pos, Fluids.WATER.getDefaultState().getBlockState(), 11)
          && !selected.getFluidState().isSource()) {
        return false;
      } else {
        worldIn.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return true;
      }
    }
  }

}
