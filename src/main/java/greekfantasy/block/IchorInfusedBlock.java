package greekfantasy.block;

import greekfantasy.GFRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class IchorInfusedBlock extends Block {

    @Nullable
    private BlockPattern talosPattern;
    @Nullable
    private BlockPattern bronzeBullPattern;
    private static final TagKey<Block> BRONZE_BLOCK = ForgeRegistries.BLOCKS.tags().createTagKey(new ResourceLocation("forge", "storage_blocks/bronze"));
    private static final Predicate<BlockState> IS_BODY_BLOCK = (state) -> state != null && (state.is(BRONZE_BLOCK));

    public IchorInfusedBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        // check if a talos was built
        BlockPattern pattern = this.getTalosPattern();
        BlockPattern.BlockPatternMatch helper = pattern.find(worldIn, pos);
        if (helper != null) {
            // remove the blocks that were used
            for (int i = 0; i < pattern.getWidth(); ++i) {
                for (int j = 0; j < pattern.getHeight(); ++j) {
                    for (int k = 0; k < pattern.getDepth(); ++k) {
                        BlockInWorld cachedblockinfo1 = helper.getBlock(i, j, k);
                        worldIn.destroyBlock(cachedblockinfo1.getPos(), false);
                    }
                }
            }
            // spawn the talos
            // TODO TalosEntity.spawnTalos(worldIn, helper.getBlock(1, 2, 0).getPos(), 0);
        }
        // check if a bronze bull was built
        pattern = this.getBronzeBullPattern();
        helper = pattern.find(worldIn, pos);
        if (helper != null) {
            // remove the blocks that were used
            for (int i = 0; i < pattern.getWidth(); ++i) {
                for (int j = 0; j < pattern.getHeight(); ++j) {
                    for (int k = 0; k < pattern.getDepth(); ++k) {
                        BlockInWorld cachedblockinfo1 = helper.getBlock(i, j, k);
                        worldIn.destroyBlock(cachedblockinfo1.getPos(), false);
                    }
                }
            }
            // spawn the bronze bull
            // TODO BronzeBullEntity.spawnBronzeBull(worldIn, helper.getBlock(1, 1, 0).getPos(), 0);
        }
    }

    private BlockPattern getTalosPattern() {
        if (this.talosPattern == null) {
            this.talosPattern = BlockPatternBuilder.start()
                    .aisle("~^~", "###", "###")
                    .where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(this)))
                    .where('#', BlockInWorld.hasState(IS_BODY_BLOCK))
                    .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return this.talosPattern;
    }

    private BlockPattern getBronzeBullPattern() {
        if (this.bronzeBullPattern == null) {
            this.bronzeBullPattern = BlockPatternBuilder.start()
                    .aisle("^##^", "~##~")
                    .aisle("~##~", "~##~")
                    .where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(this)))
                    .where('#', BlockInWorld.hasState(IS_BODY_BLOCK))
                    .where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return this.bronzeBullPattern;
    }
}
