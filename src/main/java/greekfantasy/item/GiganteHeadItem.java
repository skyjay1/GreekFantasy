package greekfantasy.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class GiganteHeadItem extends BlockItem implements Wearable {

    public GiganteHeadItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(itemstack);
        ItemStack itemstack1 = player.getItemBySlot(equipmentslot);
        if (itemstack1.isEmpty()) {
            player.setItemSlot(equipmentslot, itemstack.copy());
            if (!level.isClientSide()) {
                player.awardStat(Stats.ITEM_USED.get(this));
            }

            itemstack.setCount(0);
            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        } else {
            return super.use(level, player, hand);
        }
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
