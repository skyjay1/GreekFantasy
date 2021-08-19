package greekfantasy.crafting;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class SalveRecipe extends ShapelessRecipe {
  
  public static final String CATEGORY = "salve";
  private static final String KEY_RESULT_TAG = "result_tag";

  private final CompoundNBT resultTag;

  public SalveRecipe(ResourceLocation idIn, ItemStack resultIn, CompoundNBT resultTagIn, NonNullList<Ingredient> recipeItemsIn) {
    super(idIn, CATEGORY, resultIn, recipeItemsIn);
    resultTag = resultTagIn;
  }

  /**
   * Returns an Item that is the result of this recipe
   */
  @Override
  public ItemStack getCraftingResult(CraftingInventory inv) {    
    final ItemStack result = super.getCraftingResult(inv);
    // add the result NBT tag
    if(resultTag != null) {
      result.setTag(resultTag);
    }
    return result;
  }
  
  @Override
  public IRecipeSerializer<?> getSerializer() {
    return GFRegistry.SALVE_RECIPE_SERIALIZER;
  }
  
  public CompoundNBT getResultTag() {
    return resultTag;
  }
  
  public static class Factory extends ShapelessRecipe.Serializer {
    
    @Override
    public ShapelessRecipe read(ResourceLocation recipeId, JsonObject json) {
      // read the recipe from shapeless recipe serializer
      final ShapelessRecipe recipe = super.read(recipeId, json);
      final String tagString = JSONUtils.getString(json, KEY_RESULT_TAG);
      // attempt to read compound tag
      CompoundNBT tag;
      try {
        tag = JsonToNBT.getTagFromJson(tagString);
      } catch (CommandSyntaxException e) {
        GreekFantasy.LOGGER.error("Failed to parse compound tag \"" + tagString + "\" in recipe with id " + recipeId);
        tag = new CompoundNBT();
      }
      
      return new SalveRecipe(recipeId, recipe.getRecipeOutput(), tag, recipe.getIngredients());     
    }

    @Override
    public ShapelessRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
      final ShapelessRecipe recipe = super.read(recipeId, buffer);
      CompoundNBT tag = buffer.readCompoundTag();
      return new SalveRecipe(recipeId, recipe.getRecipeOutput(), tag, recipe.getIngredients());
    }

    @Override
    public void write(PacketBuffer buffer, ShapelessRecipe recipeIn) {
      super.write(buffer, recipeIn);
      final SalveRecipe recipe = (SalveRecipe) recipeIn;
      buffer.writeCompoundTag(recipe.getResultTag());
    } 
  }
}