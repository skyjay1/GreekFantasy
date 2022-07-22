package greekfantasy.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class GiganteHeadItem extends BlockItem {

    public GiganteHeadItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
        consumer.accept(new net.minecraftforge.client.IItemRenderProperties() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return greekfantasy.client.blockentity.BlockEntityRendererProvider.getGiganteHead();
            }
        });
    }
}
