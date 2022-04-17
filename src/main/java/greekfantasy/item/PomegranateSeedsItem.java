package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.IFavor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;

public class PomegranateSeedsItem extends BlockNamedItem {
  
  public static final Food POMEGRANATE_SEEDS = new Food.Builder().fast().alwaysEat().nutrition(2).saturationMod(0.1F).build();
  
  public PomegranateSeedsItem(final Item.Properties properties) {
    super(GFRegistry.POMEGRANATE_SAPLING, properties);
  }
  
  @Override
  public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.allowdedIn(group)) {
      items.add(new ItemStack(this));
    }
  }

  @Override
  public ItemStack finishUsingItem(ItemStack item, World world, LivingEntity entity) {
    ResourceLocation hades = new ResourceLocation(GreekFantasy.MODID, "hades");
    IFavor favor = entity.getCapability(GreekFantasy.FAVOR).orElse(null);
    // determine how to eat the item
    if(Dimension.NETHER.equals(world.dimension()) 
        || (entity instanceof PlayerEntity && favor != null && favor.getFavor(hades).getLevel() >= 4)) {
      // normal eating when in nether or high favor with Hades (level 4+)
      item = super.finishUsingItem(item, world, entity);
      // give prisoner potion effect
      if(GreekFantasy.CONFIG.isPrisonerEnabled()) {
        entity.addEffect(new EffectInstance(GFRegistry.PRISONER_EFFECT, GreekFantasy.CONFIG.getPrisonerDuration()));
      }
    } else {
      // give naseau effect and shrink the itemstack
      entity.addEffect(new EffectInstance(Effects.CONFUSION, 220));
      if (!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).abilities.instabuild) {
        item.shrink(1);
      }
    }
    return item;
  }
}
