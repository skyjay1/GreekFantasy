package greekfantasy.block;

import java.util.Optional;
import java.util.Random;

import greekfantasy.GFRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.PortalSize;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OilBlock extends Block implements IWaterLoggable {
  
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final BooleanProperty LIT = BlockStateProperties.LIT;
  
  protected final float fireDamage;
  protected static final VoxelShape shape = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
  protected static final VoxelShape shapeWaterlogged = Block.makeCuboidShape(0.0D, 14.0D, 0.0D, 16.0D, 15.0D, 16.0D);
  
  public OilBlock(Properties properties) {
    super(properties);
    this.setDefaultState(this.getStateContainer().getBaseState()
        .with(LIT, false).with(WATERLOGGED, false));
    this.fireDamage = 2.5F;
  }
  
  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(LIT, WATERLOGGED);
  }
  
  @Override
  public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
      final PlayerEntity playerIn, final Hand handIn, final BlockRayTraceResult hit) {
    ItemStack heldItem = playerIn.getHeldItem(handIn);
    if(!worldIn.isRemote() && !state.get(LIT) && !heldItem.isEmpty()) {
      // use the item to interact with this block
      if(heldItem.getItem() == Items.GLASS_BOTTLE) {
        // remove this block
        worldIn.destroyBlock(pos, false);
        // shrink held item stack
        if(!playerIn.isCreative()) {
          heldItem.shrink(1);
        }
        // spawn oil bottle
        playerIn.addItemStackToInventory(new ItemStack(GFRegistry.OLIVE_OIL));
        return ActionResultType.CONSUME;
      } else if(heldItem.getItem() == Items.FLINT_AND_STEEL) {
        // replace this block with fire
        setFire(worldIn, state, pos);
        // play sound effect and damage item
        playerIn.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, 0.4F, 1.0F);
        heldItem.damageItem(1, playerIn, i -> i.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return ActionResultType.CONSUME;
      }
    }
    return ActionResultType.PASS;
  }

  @Override
  public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
    // if invalid position, remove this block
    if(!this.isValidPosition(state, worldIn, pos)) {
      worldIn.destroyBlock(pos, false);
    }
    // if waterlogged, attempt to "float" up
    if(state.get(WATERLOGGED)) {
      if(worldIn.getBlockState(pos.up()).getBlock() == Blocks.WATER) {
        // remove this block and set above block to this state
        worldIn.setBlockState(pos.up(), state);
        worldIn.setBlockState(pos, state.getFluidState().getBlockState(), 2);
        // schedule another tick to continue moving upward
        worldIn.getPendingBlockTicks().scheduleTick(pos.up(), this, 40);
        return;
      } else if(worldIn.getBlockState(pos.up()).getBlock() == this) {
        // remove this block and "merge" with the one above it
        worldIn.setBlockState(pos, state.getFluidState().getBlockState(), 2);
        worldIn.getPendingBlockTicks().scheduleTick(pos.up(), this, 40);
        return;
      }
    }
    
    // determine if this block is adjacent to fire
    boolean nextToFire = nextToFire(worldIn, pos);
    // if the block is next to fire, either ignite or schedule another update
    if (nextToFire) {
      if (worldIn.getRandom().nextInt(10) == 0) {
        // replace this block with fire
        setFire(worldIn, state, pos);
      } else {
        // schedule another tick to replace self with fire
        worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2 + rand.nextInt(5));
      }
    }
  }

  @Override
  public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
      BlockPos currentPos, BlockPos facingPos) {
    // if invalid position, remove this block
    if(!this.isValidPosition(stateIn, worldIn, currentPos)) {
      return stateIn.getFluidState().getBlockState();
    }
    // if a fire block is placed nearby, begin catching fire
    if (isFire(worldIn, facingState, facingPos)) {
      worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 5);
    }
    // if waterlogged, schedule water ticks
    if (stateIn.get(WATERLOGGED)) {
      // if waterlogged AND lit, remove self if soul fire is not above this block
      if(stateIn.get(LIT) && worldIn.getBlockState(currentPos.up()).getBlock() != Blocks.SOUL_FIRE) {
        worldIn.destroyBlock(currentPos, false);
      } else {
        worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 2);
        worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }
    }
    return stateIn;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    // determine if block is waterlogged
    FluidState fluid = context.getWorld().getFluidState(context.getPos());
    boolean waterlogged = fluid.isTagged(FluidTags.WATER);
    // if waterlogged OR next to fire, schedule an update tick
    if(waterlogged || nextToFire(context.getWorld(), context.getPos())) {
      context.getWorld().getPendingBlockTicks().scheduleTick(context.getPos(), this, 5);
    }
    return super.getDefaultState().with(WATERLOGGED, waterlogged);
  }
  
  @Override
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return !state.get(WATERLOGGED) ? shape : shapeWaterlogged;
  }
  
  // Waterlogged methods
  
  @Override
  public FluidState getFluidState(BlockState state) {
    return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
  }
  
  // Comparator methods

  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @Override
  public int getComparatorInputOverride(BlockState state, World worldIn, BlockPos pos) {
    return state.get(LIT) ? 15 : 0;
  }
  
  // Fire methods
  
  @Override
  public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) { 
    return worldIn.getBlockState(pos).getFluidState().getFluid() == Fluids.WATER || worldIn.getBlockState(pos.down()).isOpaqueCube(worldIn, pos.down()); 
  }
  
  @Override
  public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
    if (state.get(LIT) && !entityIn.isImmuneToFire()) {
      entityIn.forceFireTicks(entityIn.getFireTimer() + 1);
      if (entityIn.getFireTimer() == 0) {
        entityIn.setFire(9);
      }

      entityIn.attackEntityFrom(DamageSource.IN_FIRE, this.fireDamage);
    }

    super.onEntityCollision(state, worldIn, pos, entityIn);
  }

  @Override
  public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
    if (!oldState.matchesBlock(state.getBlock())) {
      if (state.get(LIT) && canLightPortal(worldIn)) {
        Optional<PortalSize> optional = PortalSize.func_242964_a(worldIn, pos, Direction.Axis.X);
        optional = net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(worldIn, pos, optional);
        if (optional.isPresent()) {
          optional.get().placePortalBlocks();
          return;
        }
      }

      if (!state.isValidPosition(worldIn, pos)) {
        worldIn.removeBlock(pos, false);
      }

    }
  }

  /**
   * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect this block
   */
  @Override
  public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
    if (state.get(LIT)) {
      if (!worldIn.isRemote()) {
        worldIn.playEvent((PlayerEntity) null, 1009, pos, 0);
      }
    } else {
      super.onBlockHarvested(worldIn, pos, state, player);
    }
  }

  @OnlyIn(Dist.CLIENT)
  public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    if (stateIn.get(LIT)) {
      // play fire sound
      if (rand.nextInt(24) == 0) {
        worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
            SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
      }
      // add smoke particles
      for (int i = 0; i < 2; ++i) {
        double d0 = (double) pos.getX() + rand.nextDouble();
        double d1 = (double) pos.getY() + rand.nextDouble() * 0.5D + 0.5D;
        double d2 = (double) pos.getZ() + rand.nextDouble();
        worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
    }
  }

  private static boolean canLightPortal(World world) {
    return world.getDimensionKey() == World.OVERWORLD || world.getDimensionKey() == World.THE_NETHER;
  }
  
  /**
   * @param world the world
   * @param pos the block position
   * @return true if the block is next to fire
   */
  private static boolean nextToFire(IWorld world, BlockPos pos) {
    BlockPos p;
    BlockState s;
    for(final Direction d : Direction.values()) {
      p = pos.offset(d);
      s = world.getBlockState(p);
      if(isFire(world, s, p)) {
        return true;
      }
    }
    return false;
  }
  
  private static boolean isFire(IWorld world, BlockState state, BlockPos pos) {
    return state.isIn(BlockTags.FIRE) || (state.getBlock() == GFRegistry.OIL && state.get(LIT));
  }
  
  public static void setFire(IWorld worldIn, BlockState state, BlockPos pos) {
    worldIn.setBlockState(pos, state.with(LIT, true), 3); 
    if(state.get(WATERLOGGED)) {
      // when waterlogged, place soul fire
      if(worldIn.isAirBlock(pos.up())) {
        worldIn.setBlockState(pos.up(), Blocks.SOUL_FIRE.getDefaultState(), 3);
      }
    }
  }
}
