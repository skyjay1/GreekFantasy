package greekfantasy.item;


import greekfantasy.entity.misc.GreekFire;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GreekFireItem extends Item {

    public GreekFireItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, 10);
        // spawn a web ball entity
        if (!level.isClientSide()) {
            GreekFire greekFire = GreekFire.create(level, player);
            greekFire.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(greekFire);
        }

        // shrink the item stack
        if (!player.isCreative()) {
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
