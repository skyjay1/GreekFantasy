package greekfantasy.block;

import java.util.function.Consumer;
import java.util.function.Supplier;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.favor.Deity;
import greekfantasy.favor.FavorLevel;
import greekfantasy.favor.FavorManager;
import greekfantasy.favor.IDeity;
import greekfantasy.gui.StatueContainer;
import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.util.StatuePose;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class StatueBlock extends HorizontalBlock implements IWaterLoggable {  
  
  public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  
  protected static final VoxelShape AABB_STATUE_BOTTOM = VoxelShapes.combine(
      Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D),
      Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
      IBooleanFunction.OR);
  protected static final VoxelShape AABB_STATUE_TOP = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 24.0D, 14.0D);
  
  protected final StatueMaterial statueMaterial; 
  private final ResourceLocation deity;
  private final Consumer<StatueTileEntity> tileEntityInit;
  
  public StatueBlock(final StatueMaterial material) {
    this(material, te -> te.setStatueFemale(Math.random() < 0.5D));
  }
  
  public StatueBlock(final StatueMaterial material, final Consumer<StatueTileEntity> teInit) {
    this(material, teInit, Block.Properties.create(Material.ROCK, MaterialColor.LIGHT_GRAY).hardnessAndResistance(1.5F, 6.0F).sound(SoundType.STONE).notSolid().setLightLevel(b -> material.getLightLevel()));
  }
  
  public StatueBlock(final StatueMaterial material, final Consumer<StatueTileEntity> teInit, final AbstractBlock.Properties properties) {
    this(material, teInit, properties, Deity.EMPTY.getName());
  }
  
  public StatueBlock(final StatueMaterial material, final Consumer<StatueTileEntity> teInit, final AbstractBlock.Properties properties, final ResourceLocation deityName) {
    super(properties);
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(WATERLOGGED, false).with(HALF, DoubleBlockHalf.LOWER).with(HORIZONTAL_FACING, Direction.NORTH));
    this.statueMaterial = material;
    this.tileEntityInit = teInit;
    this.deity = deityName;
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(HALF, HORIZONTAL_FACING, WATERLOGGED);
  }
  
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    FluidState fluid = context.getWorld().getFluidState(context.getPos());
    return this.getDefaultState().with(WATERLOGGED, fluid.isTagged(FluidTags.WATER)).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    // place upper block
    final Direction facing = state.get(HORIZONTAL_FACING);
    FluidState fluid = worldIn.getFluidState(pos.up());
    worldIn.setBlockState(pos.up(), state.with(WATERLOGGED, fluid.isTagged(FluidTags.WATER)).with(HALF, DoubleBlockHalf.UPPER).with(HORIZONTAL_FACING, facing), 3);
  }
  
  @Override
  public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
    final DoubleBlockHalf half = state.get(HALF);
    final boolean isUpper = half == DoubleBlockHalf.UPPER;
    final BlockPos tePos = isUpper ? pos.down() : pos;
    // drop items from inventory
    TileEntity tileentity = worldIn.getTileEntity(tePos);
    if (canDropItems(state, worldIn) && !worldIn.isRemote() && tileentity instanceof StatueTileEntity) {
      InventoryHelper.dropItems(worldIn, pos, ((StatueTileEntity) tileentity).getInventory());
    }
    // replace other block with air
    final BlockPos otherHalf = isUpper ? pos.down() : pos.up();
    worldIn.destroyBlock(otherHalf, true, player);
    worldIn.playEvent(player, 2001, pos, Block.getStateId(state));
    super.onBlockHarvested(worldIn, pos, state, player);
  }
  
  @Override
  public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.isIn(newState.getBlock())) {
      final DoubleBlockHalf half = state.get(HALF);
      final boolean isUpper = half == DoubleBlockHalf.UPPER;
      final BlockPos tePos = isUpper ? pos.down() : pos;
      // drop items from inventory
      TileEntity tileentity = worldIn.getTileEntity(tePos);
      if (canDropItems(state, worldIn) && !worldIn.isRemote() && tileentity instanceof StatueTileEntity) {
        InventoryHelper.dropItems(worldIn, pos, ((StatueTileEntity) tileentity).getInventory());
      }
      // replace other block with air
      final BlockPos otherHalf = isUpper ? pos.down() : pos.up();
      worldIn.destroyBlock(otherHalf, true);
      super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
  }

  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
    return worldIn.isAirBlock(pos.up()) || worldIn.getBlockState(pos.up()).getMaterial().isReplaceable();
  }
  
  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.get(WATERLOGGED)) {
      worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
    }
    return stateIn;
  }

  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    // prepare to interact with this block
    final BlockPos tePos = state.get(HALF) == DoubleBlockHalf.UPPER ? pos.down() : pos;
    final TileEntity te = worldIn.getTileEntity(tePos);
    final ItemStack stack = playerIn.getHeldItem(handIn);
    // player interaction is server side only  
    if (playerIn instanceof ServerPlayerEntity && te instanceof StatueTileEntity) {
      final StatueTileEntity teStatue = (StatueTileEntity)te;
      // handle deity statue interaction
      final IDeity teDeity = teStatue.getDeity();
      if(teDeity != Deity.EMPTY && teDeity.getName().equals(deity)) {
        playerIn.getCapability(GreekFantasy.FAVOR).ifPresent(f -> {
          FavorLevel i = f.getFavor(teDeity);
          if(FavorManager.onGiveItem(teStatue, teDeity, playerIn, i, stack)) {
            //f.setFavor(teDeity, i);
            // spawn particles
//            for(int j = 0; j < 6 + playerIn.getRNG().nextInt(4); j++) {
//              playerIn.world.addOptionalParticle(ParticleTypes.HAPPY_VILLAGER, teStatue.getPos().getX() + playerIn.getRNG().nextDouble(), teStatue.getPos().up().getY() + playerIn.getRNG().nextDouble(), teStatue.getPos().getZ() + playerIn.getRNG().nextDouble(), 0, 0, 0);
//            }
          }
          // print current favor level
          i.sendStatusMessage(playerIn, teDeity);
        });
        return ActionResultType.SUCCESS;
      } 
      // handle nametag interaction
      if(!stack.isEmpty() && stack.getItem() == Items.NAME_TAG && stack.hasDisplayName()) {        
        teStatue.setTextureName(stack.getDisplayName().getUnformattedComponentText(), true);
    		if(!playerIn.isCreative()) {
    		  stack.shrink(1);
    		}
        return ActionResultType.CONSUME;
      }
      // prepare to open the statue GUI
      final StatuePose currentPose = teStatue.getStatuePose();
      final boolean isFemale = teStatue.isStatueFemale();
      final String name = teStatue.getTextureName();
      final Direction facing = state.get(StatueBlock.HORIZONTAL_FACING);
      // open the container GUI
      NetworkHooks.openGui((ServerPlayerEntity)playerIn, 
        new SimpleNamedContainerProvider((id, inventory, player) -> 
            new StatueContainer(id, inventory, teStatue, currentPose, isFemale, name, tePos, facing), 
            StringTextComponent.EMPTY), 
            buf -> {
              buf.writeBoolean(isFemale);
              buf.writeBlockPos(tePos);
              buf.writeCompoundTag(currentPose.serializeNBT());
              buf.writeString(name);
              buf.writeByte(facing.getHorizontalIndex());
            }
        );
    }
    return ActionResultType.SUCCESS;
  }
  
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
  }
  
  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {    
    return state.get(HALF) == DoubleBlockHalf.UPPER ? AABB_STATUE_TOP : AABB_STATUE_BOTTOM;
  }

  @Override
  public PushReaction getPushReaction(BlockState state) {
    return PushReaction.DESTROY;
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    final StatueTileEntity te = GFRegistry.STATUE_TE.create();
    te.setUpper(state.get(HALF) == DoubleBlockHalf.UPPER);
    if(hasDeity()) {
      te.setDeityName(deity.toString());
      te.setItem(te.getDeity().getRightHandItem(), HandSide.RIGHT);
      te.setItem(te.getDeity().getLeftHandItem(), HandSide.LEFT);
    }
    tileEntityInit.accept(te);
    return te;
  }
  
  public StatueMaterial getStatueMaterial() {
    return this.statueMaterial;
  }
  
  public boolean canDropItems(final BlockState state, final IBlockReader world) {
    return this.statueMaterial.dropsItems() && !hasDeity();
  }
  
  public boolean hasDeity() {
    return !deity.equals(Deity.EMPTY.getName());
  }
  
  public static enum StatueMaterial implements IStringSerializable {
    LIMESTONE("limestone", true, true, 0, () -> GFRegistry.POLISHED_LIMESTONE_SLAB.getDefaultState()),
    MARBLE("marble", true, true, 0, () -> GFRegistry.POLISHED_MARBLE_SLAB.getDefaultState()),
    WOOD("wood", false, false, 11, () -> Blocks.SPRUCE_SLAB.getDefaultState());
    
    private final ResourceLocation stoneTexture;
    private final String name;
    private final boolean hasGui;
    private final boolean dropsItems;
    private final int light;
    private final Supplier<BlockState> base;
    
    private StatueMaterial(final String nameIn, final boolean hasGuiIn, final boolean dropsItemsIn, final int lightIn, final Supplier<BlockState> baseIn) {
      this.name = nameIn;
      this.hasGui = hasGuiIn;
      this.dropsItems = dropsItemsIn;
      this.light = lightIn;
      this.base = baseIn;
      this.stoneTexture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/statue/" + nameIn + ".png");
    }
    
    public boolean hasGui() { return hasGui; }
    
    public boolean dropsItems() { return dropsItems; }
    
    public boolean hasSkin() { return this != WOOD; }
    
    public BlockState getBase() { return base.get(); }
    
    public ResourceLocation getStoneTexture() { return this.stoneTexture; }
    
    public int getLightLevel() { return light; }
    
    public byte getId() { return (byte) this.ordinal(); }
    
    public static StatueMaterial getById(final byte id) { return values()[id]; }

    @Override
    public String getString() { return name; }
  }
}
