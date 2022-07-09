package greekfantasy.item;


import greekfantasy.entity.misc.WebBall;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class WebBallItem extends Item {

    public WebBallItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(this, 10);
        // spawn a web ball entity
        if (!level.isClientSide()) {
            WebBall webBall = WebBall.create(level, player);
            webBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(webBall);
            // set the web type with hardcoded chances for web, spider, and item (config?)
            webBall.setWebType(level.getRandom().nextFloat() < 0.35F,
                    level.getRandom().nextFloat() < 0.4F,
                    level.getRandom().nextFloat() < 0.6F);
        }

        // shrink the item stack
        if (!player.isCreative()) {
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
