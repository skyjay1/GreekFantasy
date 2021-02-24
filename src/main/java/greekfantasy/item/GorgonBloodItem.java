package greekfantasy.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DrinkHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GorgonBloodItem extends Item {
  
  public static final Food GORGON_BLOOD_GOOD = (new Food.Builder().hunger(1).saturation(0.1F).setAlwaysEdible()
      .effect(() -> new EffectInstance(Effects.REGENERATION, 180), 1.0F)).build();
  
  public static final Food GORGON_BLOOD_BAD = (new Food.Builder().hunger(1).saturation(0.1F).setAlwaysEdible()
      .effect(() -> new EffectInstance(Effects.POISON, 180), 1.0F)).build();

  public GorgonBloodItem(final Item.Properties properties) {
    super(properties);
  }
  
  @Override
  public boolean isFood() { return true; }
  
  @Override
  public Food getFood() { return Math.random() < 0.5D ? GORGON_BLOOD_BAD : GORGON_BLOOD_GOOD; }

  @Override
  public UseAction getUseAction(ItemStack stack) { return UseAction.DRINK; }

  @Override
  public SoundEvent getEatSound() { return getDrinkSound(); }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) { return DrinkHelper.startDrinking(world, player, hand); }

  @Override
  public ItemStack onItemUseFinish(ItemStack item, World world, LivingEntity entity) {
    super.onItemUseFinish(item, world, entity);
    if (entity instanceof ServerPlayerEntity) {
      ServerPlayerEntity serverPlayer = (ServerPlayerEntity)entity;
      CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, item);
      serverPlayer.addStat(Stats.ITEM_USED.get(this));
    }
    
    if (item.isEmpty())
      return this.getContainerItem(item); 
    if (entity instanceof PlayerEntity && !((PlayerEntity)entity).abilities.isCreativeMode) {
      ItemStack containerStack = this.getContainerItem(item);
      PlayerEntity player = (PlayerEntity)entity;
      if (!player.inventory.addItemStackToInventory(containerStack)) {
        player.dropItem(containerStack, false);
      }
    } 
    
    return item;
  }
  
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    tooltip.add(new TranslationTextComponent("item.greekfantasy.gorgon_blood.tooltip_heal").mergeStyle(TextFormatting.GREEN));
    tooltip.add(new TranslationTextComponent("item.greekfantasy.gorgon_blood.tooltip_poison").mergeStyle(TextFormatting.GREEN));
  }
}
