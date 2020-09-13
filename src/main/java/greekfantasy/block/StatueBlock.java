package greekfantasy.block;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.tileentity.StatueTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class StatueBlock extends HorizontalBlock {  
  
  public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
  
  protected static final VoxelShape AABB_SLAB_BOTTOM = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
  protected static final VoxelShape AABB_STATUE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
  protected static final VoxelShape AABB_STATUE_TOP = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 24.0D, 14.0D);
  
  private final StatueMaterial statueMaterial;
    
  public StatueBlock(final StatueMaterial material) {
    super(Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_GRAY).notSolid());
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(HORIZONTAL_FACING, Direction.NORTH)
        .with(HALF, DoubleBlockHalf.LOWER));
    this.statueMaterial = material;
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(HORIZONTAL_FACING).add(HALF);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    worldIn.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 3);
  }
  
  @Override
  public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
    final DoubleBlockHalf half = state.get(HALF);
    final boolean isUpper = half == DoubleBlockHalf.UPPER;
    BlockPos blockpos = isUpper ? pos.down() : pos.up();
    BlockState blockstate = worldIn.getBlockState(blockpos);
    if (blockstate.getBlock() == state.getBlock() && blockstate.get(HALF) != half) {
      worldIn.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
      TileEntity tileentity = worldIn.getTileEntity(blockpos);
      if (!worldIn.isRemote() && tileentity instanceof StatueTileEntity) {
        InventoryHelper.dropItems(worldIn, blockpos, ((StatueTileEntity) tileentity).getInventory());
      }
      worldIn.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
    }

    super.onBlockHarvested(worldIn, pos, state, player);
  }
  
  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.isIn(newState.getBlock())) {
      final DoubleBlockHalf half = state.get(HALF);
      final boolean isUpper = half == DoubleBlockHalf.UPPER;
      BlockPos blockpos = isUpper ? pos.down() : pos.up();
      TileEntity tileentity = worldIn.getTileEntity(blockpos);
      if (!worldIn.isRemote() && tileentity instanceof StatueTileEntity) {
        InventoryHelper.dropItems(worldIn, blockpos, ((StatueTileEntity) tileentity).getInventory());
      }

      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
 }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    return worldIn.isAirBlock(pos.up());
  }

  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, 
      final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
    final BlockPos tePos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos;
    final TileEntity te = worldIn.getTileEntity(tePos);
    if(te instanceof StatueTileEntity) {
      return ((StatueTileEntity)te).onBlockActivated(state, worldIn, tePos, player, handIn, hit);
    }
    return ActionResultType.PASS;
  }
  
  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    VoxelShape shape = VoxelShapes.empty();
    if(state.get(HALF) == DoubleBlockHalf.UPPER) {
      shape = AABB_STATUE_TOP;
    } else {
      shape = VoxelShapes.combine(AABB_SLAB_BOTTOM, AABB_STATUE, IBooleanFunction.OR).simplify();
    }
    
    return shape;
  }
  
  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    final StatueTileEntity te = GFRegistry.STATUE_TE.create();
    te.setUpper(state.get(HALF) == DoubleBlockHalf.UPPER);
    te.setStatueFemale(this.RANDOM.nextBoolean());
    return te;
  }
  
  public StatueMaterial getStatueMaterial() {
    return this.statueMaterial;
  }
  
  public static enum StatueMaterial implements IStringSerializable {
    LIMESTONE("limestone"),
    MARBLE("marble");
    
    private final String name;
    private final ResourceLocation textureMale;
    private final ResourceLocation textureFemale;
    
    private StatueMaterial(final String nameIn) {
      this.name = nameIn;
      this.textureMale = new ResourceLocation(GreekFantasy.MODID, "textures/entity/statue/" + nameIn + ".png");
      this.textureFemale = new ResourceLocation(GreekFantasy.MODID, "textures/entity/statue/" + nameIn + "_slim.png");
    }
    
    public ResourceLocation getTexture(final boolean isFemaleModel) {
      return isFemaleModel ? textureFemale : textureMale;
    }
    
    public byte getId() {
      return (byte) this.ordinal();
    }
    
    public static StatueMaterial getById(final byte id) {
      return values()[id];
    }

    @Override
    public String getString() {
      return name;
    }
  }
}
