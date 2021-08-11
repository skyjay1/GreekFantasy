package greekfantasy.item;

import greekfantasy.GFRegistry;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class OliveOilItem extends BlockNamedItem {

  public OliveOilItem(final Item.Properties properties) {
    super(GFRegistry.OIL, properties);
  }
  
  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      items.add(new ItemStack(this));
    }
  }
}
