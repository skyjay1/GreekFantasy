package greekfantasy.block;

import java.util.EnumMap;

import com.google.common.collect.Maps;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class CappedPillarBlock extends Block implements IWaterLoggable {
  
   public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
   public static final BooleanProperty HIDDEN = BooleanProperty.create("hidden");
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   
   protected static final VoxelShape PILLAR_X = Block.makeCuboidShape(0, 3, 3, 16, 13, 13);
   protected static final VoxelShape PILLAR_Y = Block.makeCuboidShape(3, 0, 3, 13, 16, 13);
   protected static final VoxelShape PILLAR_Z = Block.makeCuboidShape(3, 3, 0, 13, 13, 16);
   
   protected static final EnumMap<Direction, VoxelShape> SLAB_SHAPES = Maps.newEnumMap(Direction.class);
   static {
     SLAB_SHAPES.put(Direction.UP, VoxelShapes.combine(PILLAR_Y, Block.makeCuboidShape(0, 8, 0, 16, 16, 16), IBooleanFunction.OR));
     SLAB_SHAPES.put(Direction.DOWN, VoxelShapes.combine(PILLAR_Y, Block.makeCuboidShape(0, 0, 0, 0, 8, 0), IBooleanFunction.OR));
     SLAB_SHAPES.put(Direction.NORTH, VoxelShapes.combine(PILLAR_Z, Block.makeCuboidShape(0, 0, 0, 16, 16, 8), IBooleanFunction.OR));
     SLAB_SHAPES.put(Direction.SOUTH, VoxelShapes.combine(PILLAR_Z, Block.makeCuboidShape(0, 0, 8, 16, 16, 16), IBooleanFunction.OR));
     SLAB_SHAPES.put(Direction.EAST, VoxelShapes.combine(PILLAR_X, Block.makeCuboidShape(8, 0, 0, 16, 16, 16), IBooleanFunction.OR));
     SLAB_SHAPES.put(Direction.WEST, VoxelShapes.combine(PILLAR_X, Block.makeCuboidShape(0, 0, 0, 8, 16, 16), IBooleanFunction.OR));
   }
   
   public CappedPillarBlock(final Properties properties) {
     super(properties);
     this.setDefaultState(this.getStateContainer().getBaseState()
         .with(WATERLOGGED, false).with(FACING, Direction.UP).with(HIDDEN, Boolean.valueOf(false)));
   }
   
   @Override
   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
     builder.add(FACING, HIDDEN, WATERLOGGED);
   }
   
   @Override
   public BlockState getStateForPlacement(BlockItemUseContext context) {
     Direction side = context.getFace().getOpposite();
     if(context.getPlayer().isSneaking() || isPillarBlock(context.getWorld().getBlockState(context.getPos().offset(side)))) {
       side = side.getOpposite();
     }
     FluidState fluid = context.getWorld().getFluidState(context.getPos());
     return this.getDefaultState().with(FACING, side).with(WATERLOGGED, fluid.isTagged(FluidTags.WATER));
   }
   
   @Override
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
       BlockPos currentPos, BlockPos facingPos) {
     if (stateIn.get(WATERLOGGED)) {
       worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
     }
     final Direction capDir = stateIn.get(FACING);
     final BlockState adjacent = worldIn.getBlockState(currentPos.offset(capDir));
     final boolean hidden = isPillarBlock(adjacent) && adjacent.get(FACING) == capDir;
     return stateIn.with(HIDDEN, Boolean.valueOf(hidden));
   }
   
   @Override
   public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
     final boolean hidden = state.get(HIDDEN);
     final Direction facing = state.get(FACING);
     // if slab is not hidden, return prebuilt slab+pillar shape
     if(!hidden) {
       return SLAB_SHAPES.get(facing);
     }
     // if slab is hidden, only return pillar shape
     if(facing.getAxis() == Direction.Axis.Y) {
       return PILLAR_Y;
     } else if(facing.getAxis() == Direction.Axis.X) {
       return PILLAR_X;
     } else {
       return PILLAR_Z;
     }
   }
   
   @Override
   public FluidState getFluidState(BlockState state) {
     return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
   }
   
   @Override
   public BlockState rotate(final BlockState state, final Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   @Override
   public BlockState mirror(final BlockState state, final Mirror mirrorIn) {
      return rotate(state, mirrorIn.toRotation(state.get(FACING)));
   }
   
   private boolean isPillarBlock(final BlockState state) {
     return state.getBlock() == GFRegistry.MARBLE_PILLAR
         || state.getBlock() == GFRegistry.LIMESTONE_PILLAR
         || state.getBlock() instanceof CappedPillarBlock ;
   }
}
