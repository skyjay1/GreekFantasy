package greekfantasy.entity.misc;

import java.util.function.Predicate;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.CerberusEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class OrthusHeadItemEntity extends ItemEntity {
  
  private static final ResourceLocation FRAME_BLOCK_TAG = new ResourceLocation(GreekFantasy.MODID, "valid_cerberus_frame");
  
  private static final Predicate<BlockState> IS_FRAME = b -> b.is(BlockTags.getAllTags().getTag(FRAME_BLOCK_TAG));
  private static final Predicate<BlockState> IS_LAVA = b -> b.is(Blocks.LAVA);
  private static final Predicate<BlockState> IS_AIR = b -> b.getMaterial() == Material.AIR;

  public OrthusHeadItemEntity(EntityType<? extends ItemEntity> type, World world) {
    super(type, world);
  }
  
  private OrthusHeadItemEntity(World world, double posX, double posY, double posZ, ItemStack itemstack) {
    super(world, posX, posY, posZ, itemstack);
  }
  
  public static OrthusHeadItemEntity create(World world, double posX, double posY, double posZ, ItemStack itemstack) {
    return new OrthusHeadItemEntity(world, posX, posY, posZ, itemstack);
  }

  @Override
  public void remove() {
    if(!this.level.isClientSide() && this.isOnFire()) {
      if(!trySpawnCerberus(this.blockPosition())) {
        trySpawnCerberus(this.blockPosition().above());
      }
    }
    super.remove();
  }
  
  /**
   * Checks for valid spawn conditions for a Cerberus
   * and, if those conditions are met, spawns a Cerberus
   * at this item's location
   * @param lavaPos the BlockPos of a lava block
   * @return true if the Cerberus was spawned
   **/
  protected boolean trySpawnCerberus(final BlockPos lavaPos) {
    // check for lava nearby
    Direction dir = hasSquare(lavaPos);
    if(dir != null) {
      // a square of lava was found, check for the frame
      BlockPos framePos = lavaPos.relative(dir.getOpposite()).relative(dir.getCounterClockWise());
      if(hasFrame(IS_FRAME, framePos, dir) && hasSquare(IS_AIR, lavaPos.above(), dir) && hasFrame(IS_AIR, lavaPos.above(), dir)) {
        // a frame was found, fill the square with magma and spawn a cerberus
        fillSquare(Blocks.MAGMA_BLOCK.defaultBlockState(), lavaPos, dir);
        Vector3d center = this.position().add(0, 1.0D, 0);
        CerberusEntity.spawnCerberus(level, center);
        return true;
      }
    }
    return false;
  }
  
  /**
   * @param startPos a block pos
   * @return the direction in which this pos is at the lower left
   * of four lava blocks in a square, or null if none is found
   **/
  private Direction hasSquare(final BlockPos startPos) {
    Direction d;
    for(int i = 0; i < 4; i++) {
      d = Direction.from2DDataValue(i);
      if(hasSquare(IS_LAVA, startPos, d)) {
        return d;
      }
    }
    return null;
  }
  
  /**
   * 
   * @param test the predicate to test each block against
   * @param startPos the lower-left corner
   * @param d the direction to search in
   * @return whether a 2x2 of lava exists for the given block pos and direction
   **/
  private boolean hasSquare(final Predicate<BlockState> test, final BlockPos startPos, final Direction d) {
    final Direction c = d.getClockWise();
    return test.test(level.getBlockState(startPos))
        && test.test(level.getBlockState(startPos.relative(d, 1)))
        && test.test(level.getBlockState(startPos.relative(c, 1)))
        && test.test(level.getBlockState(startPos.relative(d, 1).relative(c, 1)));
  }
  
  /**
   * @param state the state to fill the area
   * @param startPos the lower-left position
   * @param d the direction to fill
   **/
  private void fillSquare(final BlockState state, final BlockPos startPos, final Direction d) {
    final Direction c = d.getClockWise();
    level.setBlock(startPos, state, 2);
    level.setBlock(startPos.relative(d, 1), state, 2);
    level.setBlock(startPos.relative(c, 1), state, 2);
    level.setBlock(startPos.relative(d, 1).relative(c, 1), state, 2);
  }
  
  /**
   * Checks if a frame exists here. Note: does not check the furthest corner blocks,
   * only the 8 blocks immediately surrounding the inner square.
   * @param test the predicate to test each block against
   * @param startPos the lower-left block of the frame
   * @param d the direction to search in
   * @return if a frame was found with the given position and direction
   **/
  private boolean hasFrame(final Predicate<BlockState> test, final BlockPos startPos, final Direction d) {
    final Direction c = d.getClockWise();
    return test.test(level.getBlockState(startPos.relative(d, 1)))
        && test.test(level.getBlockState(startPos.relative(d, 2)))
        && test.test(level.getBlockState(startPos.relative(d, 3).relative(c, 1)))
        && test.test(level.getBlockState(startPos.relative(d, 3).relative(c, 2)))
        && test.test(level.getBlockState(startPos.relative(d, 2).relative(c, 3)))
        && test.test(level.getBlockState(startPos.relative(d, 1).relative(c, 3)))
        && test.test(level.getBlockState(startPos.relative(c, 1)))
        && test.test(level.getBlockState(startPos.relative(c, 2)));
  }
}
