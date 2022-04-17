package greekfantasy.block;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import greekfantasy.GFRegistry;
import greekfantasy.entity.BronzeBullEntity;
import greekfantasy.entity.TalosEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class IchorInfusedBlock extends Block {
  
  @Nullable
  private BlockPattern talosPattern;
  @Nullable
  private BlockPattern bronzeBullPattern;
  // TODO: change to copper for 1.17
  private static final Predicate<BlockState> IS_BODY_BLOCK = (state) -> state != null && (state.is(Blocks.GOLD_BLOCK));
  
  public IchorInfusedBlock(final Properties properties) {
    super(properties);
  }
  
  @Override
  public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    // check if a talos was built
    BlockPattern pattern = this.getTalosPattern();
    BlockPattern.PatternHelper helper = pattern.find(worldIn, pos);
    if(helper != null) {
      // remove the blocks that were used
      for (int i = 0; i < pattern.getWidth(); ++i) {
        for (int j = 0; j < pattern.getHeight(); ++j) {
          for (int k = 0; k < pattern.getDepth(); ++k) {
            CachedBlockInfo cachedblockinfo1 = helper.getBlock(i, j, k);
            worldIn.destroyBlock(cachedblockinfo1.getPos(), false);
          }
        }
      }
      // spawn the talos
      TalosEntity.spawnTalos(worldIn, helper.getBlock(1, 2, 0).getPos(), 0);
    }
    // check if a bronze bull was built
    pattern = this.getBronzeBullPattern();
    helper = pattern.find(worldIn, pos);
    if(helper != null) {
      // remove the blocks that were used
      for (int i = 0; i < pattern.getWidth(); ++i) {
        for (int j = 0; j < pattern.getHeight(); ++j) {
          for (int k = 0; k < pattern.getDepth(); ++k) {
            CachedBlockInfo cachedblockinfo1 = helper.getBlock(i, j, k);
            worldIn.destroyBlock(cachedblockinfo1.getPos(), false);
          }
        }
      }
      // spawn the bronze bull
      BronzeBullEntity.spawnBronzeBull(worldIn, helper.getBlock(1, 1, 0).getPos(), 0);
    }
  }

  private BlockPattern getTalosPattern() {
    if (this.talosPattern == null) {
       this.talosPattern = BlockPatternBuilder.start()
           .aisle("~^~", "###", "###")
           .where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(GFRegistry.ICHOR_INFUSED_BLOCK)))
           .where('#', CachedBlockInfo.hasState(IS_BODY_BLOCK))
           .where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
    }
    return this.talosPattern;
  }
  
  private BlockPattern getBronzeBullPattern() {
    if (this.bronzeBullPattern == null) {
      this.bronzeBullPattern = BlockPatternBuilder.start()
          .aisle("^##^", "~##~")
          .aisle("~##~", "~##~")
          .where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(GFRegistry.ICHOR_INFUSED_BLOCK)))
          .where('#', CachedBlockInfo.hasState(IS_BODY_BLOCK))
          .where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
   }
   return this.bronzeBullPattern;
  }
}
