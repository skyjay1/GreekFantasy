package greekfantasy.item;

import greekfantasy.entity.misc.OrthusHead;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class OrthusHeadItem extends BlockItem {

    public OrthusHeadItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return armorType == EquipmentSlot.HEAD;
    }

    @Override
    public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.IItemRenderProperties> consumer) {
        consumer.accept(new net.minecraftforge.client.IItemRenderProperties() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return greekfantasy.client.blockentity.BlockEntityRendererProvider.getOrthusHead();
            }
        });
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return stack.getItem() == this;
    }

    @Override
    public Entity createEntity(Level level, Entity original, ItemStack itemstack) {
        final OrthusHead e = OrthusHead.create(level, original.getX(), original.getY(), original.getZ(), itemstack);
        e.setDeltaMovement(original.getDeltaMovement());
        e.setPickUpDelay(40);
        return e;
    }
}
