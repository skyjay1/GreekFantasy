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
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        BlockRayTraceResult result = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.SOURCE_ONLY);
        BlockRayTraceResult result2 = result.withPosition(result.getBlockPos().above());
        ActionResultType actionresult = super.useOn(new ItemUseContext(player, hand, result2));
        return new ActionResult<>(actionresult, player.getItemInHand(hand));
    }

    @Override
    public ActionResultType place(BlockItemUseContext context) {
        ActionResultType result = super.place(context);
        if ((result == ActionResultType.SUCCESS || result == ActionResultType.CONSUME) && !context.getPlayer().isCreative()) {
            context.getPlayer().addItem(new ItemStack(Items.GLASS_BOTTLE));
        }
        return result;
    }
}
