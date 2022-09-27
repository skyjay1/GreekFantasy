package greekfantasy.item;

import greekfantasy.GreekFantasy;
import greekfantasy.util.Quest;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class QuestItem extends Item {

    private static final String QUEST_NAME = "quest.name";
    public static final String KEY_QUEST = "QuestId";

    public QuestItem(Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> list) {
        if (this.allowdedIn(group)) {
            List<ResourceLocation> questIds = new ArrayList<>();
            // add each non-disabled quest to the list
            for(Map.Entry<ResourceLocation, Quest> entry : GreekFantasy.QUEST_MAP.entrySet()) {
                if(!entry.getValue().isDisabled()) {
                    questIds.add(entry.getKey());
                }
            }
            // sort by namespace and path
            questIds.sort(ResourceLocation::compareNamespaced);
            // add itemstack for each quest
            for(ResourceLocation questId : questIds) {
                ItemStack itemStack = new ItemStack(this);
                itemStack.getOrCreateTag().putString(KEY_QUEST, questId.toString());
                list.add(itemStack);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        // open GUI
        playerIn.startUsingItem(handIn);
        if (worldIn.isClientSide()) {
            greekfantasy.client.screen.ScreenLoader.openQuestScreen(playerIn, playerIn.getInventory().selected, itemstack);
        }
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        if(itemStack.hasTag() && itemStack.getTag().contains(KEY_QUEST)) {
            ResourceLocation questId = ResourceLocation.tryParse(itemStack.getTag().getString(KEY_QUEST));
            if(questId != null) {
                return Quest.getDescriptionFromKey(questId);
            }
        }
        return super.getDescriptionId(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        list.add(new TranslatableComponent("item.greekfantasy.quest.tooltip").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType) {
        return 67;
    }
}
