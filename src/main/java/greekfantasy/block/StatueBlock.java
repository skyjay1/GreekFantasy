package greekfantasy.block;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import greekfantasy.GFRegistry;
import greekfantasy.GFWorldSavedData;
import greekfantasy.GreekFantasy;
import greekfantasy.deity.Deity;
import greekfantasy.deity.IDeity;
import greekfantasy.deity.favor.FavorLevel;
import greekfantasy.deity.favor.FavorManager;
import greekfantasy.gui.DeityContainer;
import greekfantasy.gui.StatueContainer;
import greekfantasy.network.SSimpleParticlesPacket;
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
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
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
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
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
import net.minecraftforge.fml.network.PacketDistributor;

public class StatueBlock extends HorizontalBlock implements IWaterLoggable {  
  
  public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  
  protected static final VoxelShape AABB_STATUE_BOTTOM = VoxelShapes.joinUnoptimized(
      Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D),
      Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
      IBooleanFunction.OR);
  protected static final VoxelShape AABB_STATUE_TOP = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 24.0D, 14.0D);
  
  protected final StatueMaterial statueMaterial; 
  private final ResourceLocation deity;
  private final Consumer<StatueTileEntity> tileEntityInit;
  
  public StatueBlock(final StatueMaterial material) {
    this(material, te -> te.setStatueFemale(Math.random() < 0.5D));
  }
  
  public StatueBlock(final StatueMaterial material, final Consumer<StatueTileEntity> teInit) {
    this(material, teInit, Block.Properties.of(Material.STONE, MaterialColor.COLOR_LIGHT_GRAY).strength(1.5F, 6.0F).sound(SoundType.STONE).noOcclusion().lightLevel(b -> material.getLightLevel()));
  }
  
  public StatueBlock(final StatueMaterial material, final Consumer<StatueTileEntity> teInit, final AbstractBlock.Properties properties) {
    this(material, teInit, properties, Deity.EMPTY.getName());
  }
  
  public StatueBlock(final StatueMaterial material, final Consumer<StatueTileEntity> teInit, final AbstractBlock.Properties properties, final ResourceLocation deityName) {
    super(properties);
    this.registerDefaultState(this.getStateDefinition().any()
        .setValue(WATERLOGGED, false).setValue(HALF, DoubleBlockHalf.LOWER).setValue(FACING, Direction.NORTH));
    this.statueMaterial = material;
    this.tileEntityInit = teInit;
    this.deity = deityName;
  }
  
  @Override
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(HALF, FACING, WATERLOGGED);
  }
  
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
    return this.defaultBlockState().setValue(WATERLOGGED, fluid.is(FluidTags.WATER)).setValue(FACING, context.getHorizontalDirection().getOpposite());
  }
  
  @SuppressWarnings("deprecation")
  @Override
  public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
    super.onPlace(state, worldIn, pos, oldState, isMoving);
    // update palladium data
    if(statueMaterial == StatueBlock.StatueMaterial.WOOD && worldIn instanceof net.minecraft.world.server.ServerWorld) {
      GFWorldSavedData data = GFWorldSavedData.getOrCreate((net.minecraft.world.server.ServerWorld)worldIn);
      data.addPalladium(new ChunkPos(pos), pos);
    }
  }

  @Override
  public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    // place upper block
    final Direction facing = state.getValue(FACING);
    FluidState fluid = worldIn.getFluidState(pos.above());
    worldIn.setBlock(pos.above(), state.setValue(WATERLOGGED, fluid.is(FluidTags.WATER)).setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, facing), 3);
  }
  
  @Override
  public void playerWillDestroy(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
    final DoubleBlockHalf half = state.getValue(HALF);
    final boolean isUpper = half == DoubleBlockHalf.UPPER;
    final BlockPos tePos = isUpper ? pos.below() : pos;
    // drop items from inventory
    TileEntity tileentity = worldIn.getBlockEntity(tePos);
    if (canDropItems(state, worldIn) && !worldIn.isClientSide() && tileentity instanceof StatueTileEntity) {
      InventoryHelper.dropContents(worldIn, pos, ((StatueTileEntity) tileentity).getInventory());
    }
    // replace other block with air
    final BlockPos otherHalf = isUpper ? pos.below() : pos.above();
    worldIn.destroyBlock(otherHalf, true, player);
    worldIn.levelEvent(player, 2001, pos, Block.getId(state));
    super.playerWillDestroy(worldIn, pos, state, player);
  }
  
  @Override
  public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      final DoubleBlockHalf half = state.getValue(HALF);
      final boolean isUpper = half == DoubleBlockHalf.UPPER;
      final BlockPos tePos = isUpper ? pos.below() : pos;
      // drop items from inventory
      TileEntity tileentity = worldIn.getBlockEntity(tePos);
      if (canDropItems(state, worldIn) && !worldIn.isClientSide() && tileentity instanceof StatueTileEntity) {
        InventoryHelper.dropContents(worldIn, pos, ((StatueTileEntity) tileentity).getInventory());
      }
      // replace other block with air
      final BlockPos otherHalf = isUpper ? pos.below() : pos.above();
      worldIn.destroyBlock(otherHalf, true);
      // update palladium data
      if(statueMaterial == StatueBlock.StatueMaterial.WOOD && worldIn instanceof net.minecraft.world.server.ServerWorld) {
        GFWorldSavedData data = GFWorldSavedData.getOrCreate((net.minecraft.world.server.ServerWorld)worldIn);
        data.removePalladium(new ChunkPos(pos), pos);
      }
      super.onRemove(state, worldIn, pos, newState, isMoving);
    }
  }

  @Override
  public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos) {
    return worldIn.isEmptyBlock(pos.above()) || worldIn.getBlockState(pos.above()).getMaterial().isReplaceable();
  }
  
  @Override
  public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
    if (stateIn.getValue(WATERLOGGED)) {
      worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
    }
    return stateIn;
  }

  @Override
  public ActionResultType use(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    // do not open gui for wooden statues
    if(!this.statueMaterial.hasGui()) {
      return ActionResultType.PASS;
    }
    // prepare to interact with this block
    final BlockPos tePos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    final TileEntity te = worldIn.getBlockEntity(tePos);
    final ItemStack stack = playerIn.getItemInHand(handIn);
    // player interaction is server side only  
    if (playerIn instanceof ServerPlayerEntity && te instanceof StatueTileEntity) {
      final StatueTileEntity teStatue = (StatueTileEntity)te;
      // handle deity statue interaction
      if(teStatue.hasDeity() && teStatue.getDeityName().equals(deity.toString()) && teStatue.getDeity().isEnabled()) {
        final IDeity ideity = teStatue.getDeity();
        playerIn.getCapability(GreekFantasy.FAVOR).ifPresent(f -> {
          if(f.isEnabled()) {
            FavorLevel level = f.getFavor(ideity);
            ItemStack copy = stack.copy();
            if(FavorManager.onGiveItem(teStatue, ideity, playerIn, f, level, stack)) {
              final boolean happy = ideity.getItemFavorModifier(copy.getItem()) > 0;
              GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SSimpleParticlesPacket(happy, pos, 10));
            } else if(!stack.isEmpty()) {
              playerIn.setItemInHand(handIn, teStatue.handleItemInteraction(playerIn, ideity, f, stack));
            } else {
              // open deity screen
              NetworkHooks.openGui((ServerPlayerEntity) playerIn,
                  new SimpleNamedContainerProvider((id, inventory, player) -> 
                    new DeityContainer(id, inventory, f, ideity.getName()),
                      StringTextComponent.EMPTY),
                  buf -> {
                    buf.writeNbt(f.serializeNBT());
                    buf.writeResourceLocation(ideity.getName());
                  });
            }
            // print current favor level
            // level.sendStatusMessage(playerIn, ideity);
          }
        });
        return ActionResultType.SUCCESS;
      } 
      // handle nametag interaction
      if(!stack.isEmpty() && stack.getItem() == Items.NAME_TAG && stack.hasCustomHoverName()) {        
        teStatue.setTextureName(stack.getHoverName().getContents(), true);
    		if(!playerIn.isCreative()) {
    		  stack.shrink(1);
    		}
        return ActionResultType.CONSUME;
      }
      // prepare to open the statue GUI
      final StatuePose currentPose = teStatue.getStatuePose();
      final boolean isFemale = teStatue.isStatueFemale();
      final String name = teStatue.getTextureName();
      final Direction facing = state.getValue(StatueBlock.FACING);
      // open the container GUI
      NetworkHooks.openGui((ServerPlayerEntity)playerIn, 
        new SimpleNamedContainerProvider((id, inventory, player) -> 
            new StatueContainer(id, inventory, teStatue, currentPose, isFemale, name, tePos, facing), 
            StringTextComponent.EMPTY), 
            buf -> {
              buf.writeBoolean(isFemale);
              buf.writeBlockPos(tePos);
              buf.writeNbt(currentPose.serializeNBT());
              buf.writeUtf(name);
              buf.writeByte(facing.get2DDataValue());
            }
        );
    }
    return ActionResultType.SUCCESS;
  }
  
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
  }
  
  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {    
    return state.getValue(HALF) == DoubleBlockHalf.UPPER ? AABB_STATUE_TOP : AABB_STATUE_BOTTOM;
  }

  @Override
  public PushReaction getPistonPushReaction(BlockState state) {
    return PushReaction.DESTROY;
  }

  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    final StatueTileEntity te = GFRegistry.STATUE_TE.create();
    te.setUpper(state.getValue(HALF) == DoubleBlockHalf.UPPER);
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
  
  // Comparator methods

  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  public int getAnalogOutputSignal(BlockState state, World worldIn, BlockPos pos) {
    final BlockPos tePos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    final TileEntity te = worldIn.getBlockEntity(tePos);
    if(te instanceof IInventory) {
      return Container.getRedstoneSignalFromContainer((IInventory)te);
    }
    return 0;
  }
  
  // Material
  
  public static enum StatueMaterial implements IStringSerializable {
    LIMESTONE("limestone", true, 0, () -> GFRegistry.POLISHED_LIMESTONE_SLAB.defaultBlockState()),
    MARBLE("marble", true, 0, () -> GFRegistry.POLISHED_MARBLE_SLAB.defaultBlockState()),
    WOOD("wood", false, 11, () -> Blocks.SPRUCE_SLAB.defaultBlockState());
    
    private final ResourceLocation stoneTexture;
    private final String name;
    private final boolean hasGui;
    private final boolean dropsItems;
    private final int light;
    private final Supplier<BlockState> base;
    
    private StatueMaterial(final String nameIn, final boolean hasGuiIn, final int lightIn, final Supplier<BlockState> baseIn) {
      this(nameIn, nameIn, hasGuiIn, hasGuiIn, lightIn, baseIn);
    }
    
    private StatueMaterial(final String nameIn, final String textureNameIn, final boolean hasGuiIn, final boolean dropsItemsIn, final int lightIn, final Supplier<BlockState> baseIn) {
      this.name = nameIn;
      this.hasGui = hasGuiIn;
      this.dropsItems = dropsItemsIn;
      this.light = lightIn;
      this.base = baseIn;
      this.stoneTexture = new ResourceLocation(GreekFantasy.MODID, "textures/entity/statue/" + textureNameIn + ".png");
    }
    
    public boolean hasGui() { return hasGui; }
    
    public boolean dropsItems() { return dropsItems; }
    
    public boolean hasSkin() { return this != WOOD; }
    
    public BlockState getBase() { return base.get(); }
    
    public ResourceLocation getStoneTexture() { return this.stoneTexture; }
    
    public int getLightLevel() { return light; }
    
    public byte getId() { return (byte) this.ordinal(); }
    
    public static StatueMaterial getById(final byte id) { return values()[id]; }
    
    public static StatueMaterial getByName(final String id) {
      for(final StatueMaterial s : values()) {
        if(s.getSerializedName().equals(id)) {
          return s;
        }
      }
      // defaults to limestone texture
      return LIMESTONE;
    }

    @Override
    public String getSerializedName() { return name; }
  }
}
