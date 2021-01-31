package greekfantasy.loot;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.IFavor;
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
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class AutosmeltOrCobbleModifier extends LootModifier {
  
  private final Block stone;
  
  protected AutosmeltOrCobbleModifier(final ILootCondition[] conditionsIn, final Block stoneIn) {
    super(conditionsIn);
    stone = stoneIn;
  }

  @Override
  public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
    System.out.println("checking autosmelt modifier");
    Entity entity = context.get(LootParameters.THIS_ENTITY);
    final boolean canAutosmelt = GreekFantasy.PROXY.getFavorConfiguration().getSpecialFavorEffectMap().containsKey(SpecialFavorEffect.Type.MINING_AUTOSMELT);
    final boolean canCancel = GreekFantasy.PROXY.getFavorConfiguration().getSpecialFavorEffectMap().containsKey(SpecialFavorEffect.Type.MINING_CANCEL_ORES);
    // make sure this is a player and that autosmelt or unsmelt is enabled
    if(entity instanceof PlayerEntity && (canAutosmelt || canCancel)) {
      System.out.print("running modifier...");
      final PlayerEntity player = (PlayerEntity)entity;
      final long time = player.getEntityWorld().getGameTime() + player.getEntityId() * 3;
      ArrayList<ItemStack> replacement = new ArrayList<ItemStack>();
      LazyOptional<IFavor> lFavor = entity.getCapability(GreekFantasy.FAVOR);
      IFavor favor = lFavor.orElse(GreekFantasy.FAVOR.getDefaultInstance());
      if(lFavor.isPresent() && favor.hasNoTriggeredCooldown(time)) {
        SpecialFavorEffect autosmelt = GreekFantasy.PROXY.getFavorConfiguration().getSpecialFavorEffectMap().get(SpecialFavorEffect.Type.MINING_AUTOSMELT);
        SpecialFavorEffect unsmelt = GreekFantasy.PROXY.getFavorConfiguration().getSpecialFavorEffectMap().get(SpecialFavorEffect.Type.MINING_CANCEL_ORES);
        // if the player should autosmelt
        if(autosmelt.canApply(player)) {
          System.out.print("autosmelt");
          generatedLoot.forEach((stack) -> replacement.add(smelt(stack, context)));
        }
        // if the player should unsmelt
        if(unsmelt.canApply(player)) {
          System.out.print("unsmelt");
          replacement.add(new ItemStack(stone));
        }
        return replacement;
      }
    }
    System.out.print("...done!/n");
    return generatedLoot;
    
  }

  private static ItemStack smelt(ItemStack stack, LootContext context) {
    return context.getWorld().getRecipeManager().getRecipe(IRecipeType.SMELTING, new Inventory(stack), context.getWorld())
        .map(FurnaceRecipe::getRecipeOutput)
        .filter(itemStack -> !itemStack.isEmpty())
        .map(itemStack -> ItemHandlerHelper.copyStackWithSize(itemStack, stack.getCount() * itemStack.getCount()))
        .orElse(stack);
  }
  
  public static class Serializer extends GlobalLootModifierSerializer<AutosmeltOrCobbleModifier> {
    
    private static final String STONE = "stone";

    @Override
    public AutosmeltOrCobbleModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
      Block stone = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(JSONUtils.getString(object, STONE)));
      return new AutosmeltOrCobbleModifier(conditionsIn, stone);
    }

    @Override
    public JsonObject write(AutosmeltOrCobbleModifier instance) {
      JsonObject json = makeConditions(instance.conditions);
      json.addProperty(STONE, instance.stone.getRegistryName().toString());
      return json;
    }
  }
}
