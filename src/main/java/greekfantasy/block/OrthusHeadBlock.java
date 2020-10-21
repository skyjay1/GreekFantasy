package greekfantasy.block;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import greekfantasy.tileentity.MobHeadTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class OrthusHeadBlock extends MobHeadBlock {
  
  private static final VoxelShape GROUND_SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 7.5D, 12.0D);
  
  private static final Map<Direction, VoxelShape> WALL_SHAPES = Maps.newEnumMap(ImmutableMap.of(
      Direction.NORTH, Block.makeCuboidShape(4.0D, 2.0D, 8.0D, 12.0D, 9.5D, 16.0D), 
      Direction.SOUTH, Block.makeCuboidShape(4.0D, 2.0D, 0.0D, 12.0D, 9.5D, 8.0D), 
      Direction.EAST, Block.makeCuboidShape(0.0D, 2.0D, 4.0D, 8.0D, 9.5D, 12.0D), 
      Direction.WEST, Block.makeCuboidShape(8.0D, 2.0D, 4.0D, 16.0D, 9.5D, 12.0D)));
    
  public OrthusHeadBlock(final MobHeadTileEntity.HeadType head, Properties prop) {
    super(head, prop);
  }

  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    return state.get(WALL) ? WALL_SHAPES.get(state.get(HORIZONTAL_FACING)) : GROUND_SHAPE; 
  }
}
