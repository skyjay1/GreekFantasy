package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class HornOfPlentyItem extends HasCraftRemainderItem {

    private static final ResourceLocation LOOT_TABLE_LOCATION = new ResourceLocation(GreekFantasy.MODID, "gameplay/horn_of_plenty");

    public HornOfPlentyItem(final Supplier<Item> craftRemainderSupplier, Properties properties) {
        super(craftRemainderSupplier, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if(level instanceof ServerLevel) {
            // create list of food items using loot table
            sampleLoot(player, itemStack).forEach(item -> {
                // spawn each food item at player location
                ItemEntity itemEntity = player.spawnAtLocation(item);
                if (itemEntity != null) {
                    itemEntity.setNoPickUpDelay();
                }
            });
        }
        ItemStack container = getContainerItem(itemStack);
        if (!player.isCreative()) {
            itemStack.hurtAndBreak(1, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        player.getCooldowns().addCooldown(this, 5);
        return InteractionResultHolder.sidedSuccess(itemStack.isEmpty() ? container : itemStack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(new TranslatableComponent(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }

    protected List<ItemStack> sampleLoot(final Player player, final ItemStack itemStack) {
        LootTable loottable = player.level.getServer().getLootTables().get(LOOT_TABLE_LOCATION);
        return loottable.getRandomItems(new LootContext.Builder((ServerLevel) player.level)
                .withRandom(player.level.random)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.TOOL, itemStack)
                .create(LootContextParamSets.GIFT));
    }
}
