package greekfantasy.block;

import greekfantasy.GFRegistry;
import greekfantasy.tileentity.VaseTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class VaseBlock extends Block {
  
  protected static final VoxelShape AABB = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 15.0D, 12.0D);

  public VaseBlock(final Block.Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    final TileEntity te = worldIn.getTileEntity(pos);
    // item / inventory interaction
    if(playerIn.isServerWorld() && te instanceof VaseTileEntity) {
      final VaseTileEntity teVase = (VaseTileEntity)te;
      final ItemStack teStack = teVase.getStackInSlot(0);
      final ItemStack heldItem = playerIn.getHeldItem(handIn);
      if(teStack.isEmpty()) {
        // attempt to add item to inventory
        teVase.setInventorySlotContents(0, heldItem.copy());
        if(!playerIn.isCreative()) {
          // remove from player
          playerIn.setHeldItem(handIn, ItemStack.EMPTY);
        }
      } else if(playerIn.isSneaking() || heldItem.isEmpty()) {
        // attempt to drop item from inventory
        ItemEntity dropItem = new ItemEntity(worldIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), teVase.removeStackFromSlot(0));
        dropItem.setPickupDelay(0);
        worldIn.addEntity(dropItem);
      }
      return ActionResultType.CONSUME;
    }
    return ActionResultType.SUCCESS;
  }

  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    return AABB;
  }
  
  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    final VaseTileEntity te = GFRegistry.VASE_TE.create();
    return te;
  }
}
