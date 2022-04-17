package greekfantasy.loot;

import java.util.List;

import com.google.gson.JsonObject;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.FavorConfiguration;
import greekfantasy.deity.favor.IFavor;
import greekfantasy.deity.favor_effect.ConfiguredSpecialFavorEffect;
import greekfantasy.deity.favor_effect.SpecialFavorEffect;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.util.LazyOptional;

public class CropMultiplierModifier extends LootModifier {
  
  private final ResourceLocation cropsTag;
  private final ITag<Block> crops;
  
  protected CropMultiplierModifier(final ILootCondition[] conditionsIn, final ResourceLocation cropsTagIn) {
    super(conditionsIn);
    cropsTag = cropsTagIn;
    crops = BlockTags.bind(cropsTagIn.toString());
  }

  @Override
  public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
    // get the entity and favor configuration ready
    Entity entity = context.hasParam(LootParameters.THIS_ENTITY) ? context.getParamOrNull(LootParameters.THIS_ENTITY) : null;
    FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
    // make sure this crop is harvested by a non-creative player
    if(entity instanceof PlayerEntity && context.hasParam(LootParameters.BLOCK_STATE) && context.getParamOrNull(LootParameters.BLOCK_STATE).getBlock().is(crops)
        && !entity.isSpectator() && !((PlayerEntity)entity).isCreative()) {
      // check favor levels and effects
      final PlayerEntity player = (PlayerEntity)entity;
      final long time = IFavor.calculateTime(player);
      LazyOptional<IFavor> lFavor = entity.getCapability(GreekFantasy.FAVOR);
      IFavor favor = lFavor.orElse(GreekFantasy.FAVOR.getDefaultInstance());
      // if the player's favor has no cooldown, activate the effect
      if(lFavor.isPresent() && favor.hasNoTriggeredCooldown(time)) {
        long cooldown = -1;
        for(final ConfiguredSpecialFavorEffect cropsMultiplier : favorConfig.getSpecials(SpecialFavorEffect.Type.CROP_HARVEST_MULTIPLIER)) {
          // if the item should be multiplied, change the size of each item stack
          if(cropsMultiplier.canApply(player, favor)) {
            generatedLoot.forEach(i -> i.grow((int) Math.round(i.getCount() * cropsMultiplier.getEffect().getMultiplier().orElse(0.0F))));
            cooldown = Math.max(cooldown, cropsMultiplier.getEffect().getRandomCooldown(player.getRandom()));
          }
        }
        // set the triggered cooldown
        if(cooldown > 0) {
          favor.setTriggeredTime(time, cooldown);
        }
      }
    }
    return generatedLoot;
  }
  
  public static class Serializer extends GlobalLootModifierSerializer<CropMultiplierModifier> {

    private static final String CROPS = "crops";

    @Override
    public CropMultiplierModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
      ResourceLocation cropsTag = new ResourceLocation(JSONUtils.getAsString(object, CROPS));
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
