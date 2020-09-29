package greekfantasy.block;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CappedPillarBlock extends Block {
  
   public static final DirectionProperty CAP = DirectionProperty.create("cap", Direction.values());
   public static final BooleanProperty HIDDEN = BooleanProperty.create("hidden");
   
   public CappedPillarBlock(final Properties properties) {
     super(properties);
     this.setDefaultState(this.getStateContainer().getBaseState()
         .with(CAP, Direction.UP).with(HIDDEN, Boolean.valueOf(false)));
   }
   
   @Override
   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
     builder.add(CAP, HIDDEN);
   }
   
   @Override
   public BlockState getStateForPlacement(BlockItemUseContext context) {
     Direction side = context.getFace().getOpposite();
     if(isPillarBlock(context.getWorld().getBlockState(context.getPos().offset(side)))) {
       side = side.getOpposite();
     }
     return this.getDefaultState().with(CAP, side);
   }
   
   @Override
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
       BlockPos currentPos, BlockPos facingPos) {
     final BlockState adjacent = worldIn.getBlockState(currentPos.offset(stateIn.get(CAP)));
     return stateIn.with(HIDDEN, Boolean.valueOf(isPillarBlock(adjacent)));
   }
   
   private boolean isPillarBlock(final BlockState state) {
     return state.getBlock() == GFRegistry.MARBLE_PILLAR
         || state.getBlock() == GFRegistry.LIMESTONE_PILLAR
         || state.getBlock() instanceof CappedPillarBlock ;
   }

}
