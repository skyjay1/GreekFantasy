package greekfantasy.loot;

import java.util.List;

import com.google.gson.JsonObject;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.IFavor;
import greekfantasy.deity.favor_effect.FavorConfiguration;
import greekfantasy.deity.favor_effect.SpecialFavorEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.util.LazyOptional;

public class CropMultiplierModifier extends LootModifier {
  
  private final ResourceLocation cropsTag;
  private final ITag<Item> crops;
  
  protected CropMultiplierModifier(final ILootCondition[] conditionsIn, final ResourceLocation cropsTagIn) {
    super(conditionsIn);
    cropsTag = cropsTagIn;
    crops = ItemTags.createOptional(cropsTagIn);
  }

  @Override
  public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
    // get the entity and favor configuration ready
    Entity entity = context.get(LootParameters.THIS_ENTITY);
    FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
    // make sure this crop is harvested by a non-creative player
    if(favorConfig.hasSpecials(SpecialFavorEffect.Type.CROP_HARVEST_MULTIPLIER)
        && !entity.isSpectator() && entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative()) {
      // check if any of the generated loot is a crop before doing the rest of the heavy work
      boolean hasCropDrop = false;
      for(final ItemStack i : generatedLoot) {
        if(i.getItem().isIn(crops)) {
          hasCropDrop = true;
          break;
        }
      }
      if(hasCropDrop) {
        // if at least one drop is a crop, check favor levels and effects
        final PlayerEntity player = (PlayerEntity)entity;
        final long time = player.getEntityWorld().getGameTime() + player.getEntityId() * 3;
        LazyOptional<IFavor> lFavor = entity.getCapability(GreekFantasy.FAVOR);
        IFavor favor = lFavor.orElse(GreekFantasy.FAVOR.getDefaultInstance());
        // if the player's favor has no cooldown, activate the effect
        if(lFavor.isPresent() && favor.hasNoTriggeredCooldown(time)) {
          long cooldown = -1;
          for(final SpecialFavorEffect cropsMultiplier : favorConfig.getSpecials(SpecialFavorEffect.Type.CROP_HARVEST_MULTIPLIER)) {
            // if the item should be multiplied, change the size of each item stack
            if(cropsMultiplier.canApply(player, favor)) {
              generatedLoot.forEach(i -> {
                if(i.getItem().isIn(crops)) {
                  i.grow((int) (i.getCount() * cropsMultiplier.getMultiplier().orElse(0.0F)));
                }
              });
              cooldown = Math.max(cooldown, cropsMultiplier.getRandomCooldown(player.getRNG()));
            }
          }
          // set the triggered cooldown
          if(cooldown > 0) {
            favor.setTriggeredTime(time, cooldown);
          }
        }
      }
    }
    return generatedLoot;
  }
  
  public static class Serializer extends GlobalLootModifierSerializer<CropMultiplierModifier> {

    private static final String CROPS = "crops";

    @Override
    public CropMultiplierModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
      ResourceLocation cropsTag = new ResourceLocation(JSONUtils.getString(object, CROPS));
      return new CropMultiplierModifier(conditionsIn, cropsTag);
    }

    @Override
    public JsonObject write(CropMultiplierModifier instance) {
      JsonObject json = makeConditions(instance.conditions);
      json.addProperty(CROPS, instance.cropsTag.toString());
      return json;
    }
  }
}
