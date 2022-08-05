package greekfantasy.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class InstrumentItem extends Item {

    protected final Supplier<SoundEvent> sound;

    public InstrumentItem(Properties properties, Supplier<SoundEvent> sound) {
        super(properties);
        this.sound = sound;
    }

    public float getVolume() {
        return 1.25F;
    }

    public SoundEvent getSound() {
        return sound.get();
    }

    @Override
    public int getUseDuration(final ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(final ItemStack stack) {
        return UseAnim.CROSSBOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        // check if player is sneaking and open GUI
        playerIn.startUsingItem(handIn);
        if (worldIn.isClientSide()) {
            greekfantasy.client.screen.ScreenLoader.openInstrumentScreen(playerIn, playerIn.getInventory().selected, itemstack);
        }
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.tooltip.right_click_instrument").withStyle(ChatFormatting.AQUA));
    }

}
