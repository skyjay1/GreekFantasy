package greekfantasy.integration;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.item.QuestItem;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEICompat implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(GreekFantasy.MODID, "jei_provider");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, GFRegistry.ItemReg.QUEST.get(), (ItemStack ingredient, UidContext context) -> {
            if(ingredient.hasTag() && ingredient.getTag().contains(QuestItem.KEY_QUEST)) {
                return ingredient.getTag().getString(QuestItem.KEY_QUEST);
            }
            return "empty";
        });
    }
}
