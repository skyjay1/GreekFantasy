package greekfantasy.item;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MobHeadItem extends BlockItem {

  public MobHeadItem(final Block blockIn, final Item.Properties builderIn) {
    super(blockIn, builderIn);
  }
  
  @Nullable
  @Override
  public EquipmentSlotType getEquipmentSlot(final ItemStack stack) {
    return EquipmentSlotType.HEAD;
  }

  /**
   * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
   * {@link #onItemUse}.
   */
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
     ItemStack itemstack = playerIn.getHeldItem(handIn);
     EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
     ItemStack itemstack1 = playerIn.getItemStackFromSlot(equipmentslottype);
     if (itemstack1.isEmpty()) {
        playerIn.setItemStackToSlot(equipmentslottype, itemstack.copy());
        itemstack.setCount(0);
        return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
     } else {
        return ActionResult.resultFail(itemstack);
     }
  }

}
