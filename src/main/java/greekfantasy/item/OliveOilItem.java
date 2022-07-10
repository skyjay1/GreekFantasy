package greekfantasy.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class OliveOilItem extends BlockItem {

    public OliveOilItem(final Block block, final Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext blockPlaceContext) {
        // determine container item
        ItemStack container = blockPlaceContext.getItemInHand().getContainerItem();
        InteractionResult result = super.place(blockPlaceContext);
        // if block was placed, give player container item
        if(result == InteractionResult.SUCCESS && blockPlaceContext.getPlayer() != null && !blockPlaceContext.getPlayer().isCreative()) {
            blockPlaceContext.getPlayer().addItem(container);
        }
        return result;
    }
}
