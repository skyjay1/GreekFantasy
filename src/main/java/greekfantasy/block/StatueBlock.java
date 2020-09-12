package greekfantasy.block;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class StatueBlock extends HorizontalBlock {

  public StatueBlock(final Block.Properties builder) {
    super(builder);
  }
  
  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    return GFRegistry.STATUE_TE.create();
  }
  
}
