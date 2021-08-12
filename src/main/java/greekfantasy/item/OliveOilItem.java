package greekfantasy.item;

import greekfantasy.GFRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.BlockNamedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;

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
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    BlockRayTraceResult result = rayTrace(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
    BlockRayTraceResult result2 = result.withPosition(result.getPos().up());
    ActionResultType actionresult = super.onItemUse(new ItemUseContext(player, hand, result2));
    return new ActionResult<>(actionresult, player.getHeldItem(hand));
  }

  @Override
  public ActionResultType tryPlace(BlockItemUseContext context) {
    ActionResultType result = super.tryPlace(context);
    if((result == ActionResultType.SUCCESS || result == ActionResultType.CONSUME) && !context.getPlayer().isCreative()) {
      context.getPlayer().addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
    }
    return result;
  }
}
