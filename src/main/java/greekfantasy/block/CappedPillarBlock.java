package greekfantasy.block;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CappedPillarBlock extends Block {
  
   public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());
   public static final BooleanProperty HIDDEN = BooleanProperty.create("hidden");
   
   public CappedPillarBlock(final Properties properties) {
     super(properties);
     this.setDefaultState(this.getStateContainer().getBaseState()
         .with(FACING, Direction.UP).with(HIDDEN, Boolean.valueOf(false)));
   }
   
   @Override
   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
     builder.add(FACING, HIDDEN);
   }
   
   @Override
   public BlockState getStateForPlacement(BlockItemUseContext context) {
     Direction side = context.getFace().getOpposite();
     if(!context.getPlayer().isSneaking() && isPillarBlock(context.getWorld().getBlockState(context.getPos().offset(side)))) {
       side = side.getOpposite();
     }
     return this.getDefaultState().with(FACING, side);
   }
   
   @Override
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
       BlockPos currentPos, BlockPos facingPos) {
     final Direction capDir = stateIn.get(FACING);
     final BlockState adjacent = worldIn.getBlockState(currentPos.offset(capDir));
     final boolean hidden = isPillarBlock(adjacent) && adjacent.get(FACING) == capDir;
     return stateIn.with(HIDDEN, Boolean.valueOf(hidden));
   }
   
   private boolean isPillarBlock(final BlockState state) {
     return state.getBlock() == GFRegistry.MARBLE_PILLAR
         || state.getBlock() == GFRegistry.LIMESTONE_PILLAR
         || state.getBlock() instanceof CappedPillarBlock ;
   }
   

   @Override
   public BlockState rotate(final BlockState state, final Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   @Override
   public BlockState mirror(final BlockState state, final Mirror mirrorIn) {
      return rotate(state, mirrorIn.toRotation(state.get(FACING)));
   }

}
