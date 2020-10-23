package greekfantasy.block;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.entity.CerberusEntity;
import greekfantasy.entity.GeryonEntity;
import greekfantasy.tileentity.MobHeadTileEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class MobHeadBlock extends HorizontalBlock {
  
  public static final BooleanProperty WALL = BooleanProperty.create("wall");
  private static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.9D, 16.0D);
  protected final MobHeadTileEntity.HeadType headType;
  
  private static final Predicate<BlockState> CERBERUS_BODY = b -> b.isIn(BlockTags.SOUL_SPEED_BLOCKS);
  private BlockPattern CERBERUS_PATTERN;
  
//  private static final Predicate<BlockState> GERYON_BODY = b -> b.isIn(BlockTags.SOUL_SPEED_BLOCKS);
//  private BlockPattern GERYON_PATTERN;
  
  public MobHeadBlock(final MobHeadTileEntity.HeadType head, Properties prop) {
    super(prop);
    headType = head;
    this.setDefaultState(this.getStateContainer().getBaseState().with(WALL, Boolean.valueOf(false)).with(HORIZONTAL_FACING, Direction.NORTH));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(WALL).add(HORIZONTAL_FACING);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    final Direction facing = context.getPlacementHorizontalFacing().getOpposite();
    final boolean wall = context.getFace() != Direction.UP && context.getFace() != Direction.DOWN;
    return this.getDefaultState().with(WALL, wall).with(HORIZONTAL_FACING, facing);
  }
  
  @Override
  public VoxelShape getShape(final BlockState state, final IBlockReader worldIn, final BlockPos pos, final ISelectionContext cxt) {
    return SHAPE;
  }
  
  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    // attempt to spawn boss
//    if(headType == MobHeadTileEntity.HeadType.GIGANTE) {
//      // attempt to spawn a geryon
//      final BlockPattern pattern = getGeryonPattern();
//      final BlockPattern.PatternHelper patternHelper = pattern.match(worldIn, pos);
//      if(patternHelper != null) {
//        // the entity was found, remove blocks
//        removeMatchingBlocks(worldIn, pattern, patternHelper);
//        // prepare an entity to spawn
//        final BlockPos p = patternHelper.translateOffset(1, 4, 0).getPos();
//        GeryonEntity geryon = GFRegistry.GERYON_ENTITY.create(worldIn);
//        placeEntity(worldIn, geryon, p, patternHelper.getForwards().getAxis());
//      }
//    } else 
    if(headType == MobHeadTileEntity.HeadType.ORTHUS) {
      // attempt to spawn a cerberus
      final BlockPattern pattern = getCerberusPattern();
      final BlockPattern.PatternHelper patternHelper = pattern.match(worldIn, pos);
      if(patternHelper != null) {
        // the entity was found, remove blocks
        removeMatchingBlocks(worldIn, pattern, patternHelper);
        // prepare an entity to spawn
        final BlockPos p = patternHelper.translateOffset(1, 1, 2).getPos();
        CerberusEntity.spawnCerberus(worldIn, p, (patternHelper.getForwards().getAxis() == Direction.Axis.X) ? 0.0F : 90.0F);
      }
    }
  }
  
  @Override
  public boolean hasTileEntity(final BlockState state) {
    return true;
  }
  
  @Override
  public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
    final MobHeadTileEntity te = GFRegistry.BOSS_HEAD_TE.create();
    te.setHeadType(headType);
    te.setWall(state.get(WALL));
    return te;
  }
  
  /**
   * Sets the location and rotation of the given entity
   * @param worldIn the world
   * @param entity the entity
   * @param pos the position to place the entity
   * @param axis the axis along which the pattern matched
   **/
  protected void placeEntity(final World worldIn, final MobEntity entity, final BlockPos pos, final Direction.Axis axis) {
    final float yaw = (axis == Direction.Axis.X) ? 0.0F : 90.0F;
    entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, yaw, 0.0F);
    entity.renderYawOffset = yaw;
    worldIn.addEntity(entity);
    triggerCriteria(worldIn, entity);
  }
  
  /**
   * Destroys the blocks that were used in the given pattern,
   * assuming it has been identified in the world
   * @param worldIn the world
   * @param pattern the pattern
   * @param helper the pattern helper
   **/
  protected void removeMatchingBlocks(final World worldIn, final BlockPattern pattern, final BlockPattern.PatternHelper helper) {
    for (int palm = 0, pLen = pattern.getPalmLength(); palm < pLen; palm++) {
      for (int thumb = 0, tLen = pattern.getThumbLength(); thumb < tLen; thumb++) {
        for(int finger = 0, fLen = pattern.getFingerLength(); finger < fLen; finger++) {
          CachedBlockInfo info = helper.translateOffset(palm, thumb, finger);
          worldIn.destroyBlock(info.getPos(), false);
        }
      }
    }
  }
  
  /**
   * Checks for nearby players and triggers the
   * entity spawn criteria for each one
   * @param worldIn the world
   * @param entity the entity that was summoned
   **/
  protected void triggerCriteria(final World worldIn, final MobEntity entity) {
    // trigger spawn for nearby players
    for (ServerPlayerEntity player : worldIn.getEntitiesWithinAABB(ServerPlayerEntity.class, entity.getBoundingBox().grow(25.0D))) {
      CriteriaTriggers.SUMMONED_ENTITY.trigger(player, entity);
    }
  }
  
//  private BlockPattern getGeryonPattern() {
//    if(GERYON_PATTERN == null) {
//      GERYON_PATTERN = BlockPatternBuilder.start().aisle("^^^", "###", "###", "###", "###")
//        .where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(GFRegistry.GIGANTE_HEAD)))
//        .where('#', CachedBlockInfo.hasState(GERYON_BODY))
//        .where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
//    }
//    return GERYON_PATTERN;
//  }
  
  private BlockPattern getCerberusPattern() {
    if(CERBERUS_PATTERN == null) {
      CERBERUS_PATTERN = BlockPatternBuilder.start()
          .aisle("###", "#~#")
          .aisle("###", "~~~")
          .aisle("###", "#~#")
          .aisle("^^^", "~~~")
        .where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(GFRegistry.ORTHUS_HEAD)))
        .where('#', CachedBlockInfo.hasState(CERBERUS_BODY))
        .where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
    }
    return CERBERUS_PATTERN;
  }
}
