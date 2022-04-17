package greekfantasy.loot;

import java.util.ArrayList;
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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class AutosmeltOrCobbleModifier extends LootModifier {
  
  private final Block stone;
  private final ResourceLocation oresTag;
  private final ITag<Block> ores;
  
  protected AutosmeltOrCobbleModifier(final ILootCondition[] conditionsIn, final Block stoneIn, final ResourceLocation oresTagIn) {
    super(conditionsIn);
    stone = stoneIn;
    oresTag = oresTagIn;
    ores = BlockTags.createOptional(oresTagIn);
  }

  @Override
  public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
    // get the entity and favor configuration ready
    Entity entity = context.hasParam(LootParameters.THIS_ENTITY) ? context.getParamOrNull(LootParameters.THIS_ENTITY) : null;
    FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
    // determine if either of the mining effects can activate
    final boolean canAutosmelt = true; // favorConfig.hasSpecials(SpecialFavorEffect.Type.MINING_AUTOSMELT);
    final boolean canCancel = true; // favorConfig.hasSpecials(SpecialFavorEffect.Type.MINING_CANCEL_ORES);
    // make sure this is an ore mined by a non-creative player
    if(entity instanceof PlayerEntity && context.hasParam(LootParameters.BLOCK_STATE) 
        && context.getParamOrNull(LootParameters.BLOCK_STATE).getBlock().is(ores) 
        && !entity.isSpectator() && !((PlayerEntity)entity).isCreative()
        && (canAutosmelt || canCancel)) {
      final PlayerEntity player = (PlayerEntity)entity;
      final long time = IFavor.calculateTime(player);
      LazyOptional<IFavor> lFavor = entity.getCapability(GreekFantasy.FAVOR);
      IFavor favor = lFavor.orElse(GreekFantasy.FAVOR.getDefaultInstance());
      // if the player's favor has no cooldown, activate the effect
      if(lFavor.isPresent() && favor.hasNoTriggeredCooldown(time)) {
        ArrayList<ItemStack> replacement = new ArrayList<ItemStack>();
        if(canAutosmelt) {
          // autosmelt special favor effects
          for(final ConfiguredSpecialFavorEffect autosmelt : favorConfig.getSpecials(SpecialFavorEffect.Type.MINING_AUTOSMELT)) {
            // if the item should autosmelt, get the items to add to the list
            if(autosmelt.canApply(player, favor)) {
              generatedLoot.forEach((stack) -> replacement.add(smelt(stack, context)));
              favor.setTriggeredTime(time, autosmelt.getEffect().getRandomCooldown(player.getRandom()));
              return replacement;
            }
          }
        }
        if(canCancel) {
          // unsmelt special favor effects
          for(final ConfiguredSpecialFavorEffect unsmelt : favorConfig.getSpecials(SpecialFavorEffect.Type.MINING_CANCEL_ORES)) {
            // if the item should unsmelt, get the item to add to the list
            if(unsmelt.canApply(player, favor)) {
              replacement.add(new ItemStack(stone));
              favor.setTriggeredTime(time, unsmelt.getEffect().getRandomCooldown(player.getRandom()));
              return replacement;
            }
          }
        }
      }
    }
    return generatedLoot;
  }
  
  
  

  /**
   * @param stack the item to smelt
   * @param context the loot context
   * @return the item that would normally result from smelting the given item
   */
  private static ItemStack smelt(ItemStack stack, LootContext context) {
    return context.getLevel().getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(stack), context.getLevel())
        .map(FurnaceRecipe::getResultItem)
        .filter(itemStack -> !itemStack.isEmpty())
        .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
        .orElse(stack);
  }
  
  public static class Serializer extends GlobalLootModifierSerializer<AutosmeltOrCobbleModifier> {

    private static final String STONE = "stone";
    private static final String ORES = "ores";

    @Override
    public AutosmeltOrCobbleModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
      Block stone = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(JSONUtils.getAsString(object, STONE)));
      ResourceLocation oresTag = new ResourceLocation(JSONUtils.getAsString(object, ORES));
      return new AutosmeltOrCobbleModifier(conditionsIn, stone, oresTag);
    }

    @Override
    public JsonObject write(AutosmeltOrCobbleModifier instance) {
      JsonObject json = makeConditions(instance.conditions);
      json.addProperty(STONE, instance.stone.getRegistryName().toString());
      json.addProperty(ORES, instance.oresTag.toString());
      return json;
    }
  }
}
