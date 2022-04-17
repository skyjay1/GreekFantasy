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

import net.minecraft.block.AbstractBlock.Properties;

public class OrthusHeadBlock extends MobHeadBlock {
  
  private static final VoxelShape GROUND_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 7.5D, 12.0D);
    
  private static final Map<Direction, VoxelShape> WALL_SHAPES = Maps.newEnumMap(ImmutableMap.of(
      Direction.NORTH, Block.box(4.0D, 2.0D, 8.0D, 12.0D, 9.5D, 16.0D), 
      Direction.SOUTH, Block.box(4.0D, 2.0D, 0.0D, 12.0D, 9.5D, 8.0D), 
      Direction.EAST, Block.box(0.0D, 2.0D, 4.0D, 8.0D, 9.5D, 12.0D), 
      Direction.WEST, Block.box(8.0D, 2.0D, 4.0D, 16.0D, 9.5D, 12.0D)));
    
  public OrthusHeadBlock(final MobHeadTileEntity.HeadType head, Properties prop) {
    super(head, prop);
  }

  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    return state.getValue(WALL) ? WALL_SHAPES.get(state.getValue(FACING)) : GROUND_SHAPE; 
  }
}
