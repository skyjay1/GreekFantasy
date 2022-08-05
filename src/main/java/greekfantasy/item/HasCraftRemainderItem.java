package greekfantasy.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class HasCraftRemainderItem extends Item {

    protected Supplier<Item> craftRemainderSupplier;

    public HasCraftRemainderItem(final Supplier<Item> craftRemainderSupplier, Properties properties) {
        super(properties);
        this.craftRemainderSupplier = craftRemainderSupplier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        if (result.getObject().isEmpty()) {
            return new InteractionResultHolder<>(result.getResult(), getCraftingRemainingItem(result.getObject()));
        }
        return result;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack item, Level level, LivingEntity entity) {
        ItemStack container = item.getCraftingRemainingItem();
        ItemStack result = super.finishUsingItem(item, level, entity);
        // replace with container item
        if (result.isEmpty()) {
            return container;
        }
        if (entity instanceof Player player && !player.isCreative()) {
            player.getInventory().add(container);
        }
        return result;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return new ItemStack(craftRemainderSupplier.get());
    }
}
