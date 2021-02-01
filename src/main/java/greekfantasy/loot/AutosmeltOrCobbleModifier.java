package greekfantasy.loot;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.IFavor;
import greekfantasy.deity.favor_effect.FavorConfiguration;
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
    Entity entity = context.get(LootParameters.THIS_ENTITY);
    FavorConfiguration favorConfig = GreekFantasy.PROXY.getFavorConfiguration();
    // determine if either of the mining effects can activate
    final boolean canAutosmelt = favorConfig.hasSpecials(SpecialFavorEffect.Type.MINING_AUTOSMELT);
    final boolean canCancel = favorConfig.hasSpecials(SpecialFavorEffect.Type.MINING_CANCEL_ORES);
    // make sure this is an ore mined by a non-creative player
    if(context.has(LootParameters.BLOCK_STATE) && context.get(LootParameters.BLOCK_STATE).getBlock().isIn(ores) 
        && entity instanceof PlayerEntity && !entity.isSpectator() && !((PlayerEntity)entity).isCreative()
        && (canAutosmelt || canCancel)) {
      final PlayerEntity player = (PlayerEntity)entity;
      final long time = player.getEntityWorld().getGameTime() + player.getEntityId() * 3;
      ArrayList<ItemStack> replacement = new ArrayList<ItemStack>();
      LazyOptional<IFavor> lFavor = entity.getCapability(GreekFantasy.FAVOR);
      IFavor favor = lFavor.orElse(GreekFantasy.FAVOR.getDefaultInstance());
      // if the player's favor has no cooldown, activate the effect
      if(lFavor.isPresent() && favor.hasNoTriggeredCooldown(time)) {
        List<SpecialFavorEffect> autosmelts = favorConfig.getSpecials(SpecialFavorEffect.Type.MINING_AUTOSMELT);
        List<SpecialFavorEffect> unsmelts = favorConfig.getSpecials(SpecialFavorEffect.Type.MINING_CANCEL_ORES);
        long cooldown = 0;
        // autosmelt special favor effects
        if(canAutosmelt) {
          for(final SpecialFavorEffect autosmelt : autosmelts) {
            // if the item should autosmelt, get the items to add to the list
            if(autosmelt.canApply(player, favor)) {
              generatedLoot.forEach((stack) -> replacement.add(smelt(stack, context)));
              cooldown = autosmelt.getRandomCooldown(player.getRNG());
            }
          }
        }
        // unsmelt special favor effects
        if(canCancel) {
          for(final SpecialFavorEffect unsmelt : unsmelts) {
            // if the item should unsmelt, get the item to add to the list
            if(unsmelt.canApply(player, favor)) {
              replacement.add(new ItemStack(stone));
              cooldown = unsmelt.getRandomCooldown(player.getRNG());
            }
          }
        }
        // set the triggered cooldown
        if(cooldown > 0) {
          favor.setTriggeredTime(time, cooldown);
        }
        return replacement;
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
    return context.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.getWorld())
        .map(FurnaceRecipe::getRecipeOutput)
        .filter(itemStack -> !itemStack.isEmpty())
        .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
        .orElse(stack);
  }
  
  public static class Serializer extends GlobalLootModifierSerializer<AutosmeltOrCobbleModifier> {

    private static final String STONE = "stone";
    private static final String ORES = "ores";

    @Override
    public AutosmeltOrCobbleModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
      Block stone = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(JSONUtils.getString(object, STONE)));
      ResourceLocation oresTag = new ResourceLocation(JSONUtils.getString(object, ORES));
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
