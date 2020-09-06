package greekfantasy.item;

import com.google.common.collect.Sets;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;

public class ClubItem extends ToolItem {
  
  // TODO make club deal immense knockback attack
 
  public ClubItem(final IItemTier tier, final Item.Properties properties) {
    super(tier.getAttackDamage() + 6.0F, -3.5F, tier, Sets.newHashSet(), properties);
  }
}
