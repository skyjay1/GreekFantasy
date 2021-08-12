package greekfantasy.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class SalveItem extends Item {
  
  private static final String KEY_EFFECTS = "Effects";
  
  private final int useDuration = 50;

  public SalveItem(final Item.Properties properties) {
    super(properties);
  }
  
  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      ItemStack stack = new ItemStack(this);
      ListNBT list = new ListNBT();
      CompoundNBT effectTag = new CompoundNBT();
      effectTag.putString("Potion", Effects.REGENERATION.getRegistryName().toString());
      list.add(effectTag);
      stack.getOrCreateTag().put(KEY_EFFECTS, list);
      items.add(stack);
    }
  }

  @Override
  public ItemStack onItemUseFinish(ItemStack item, World world, LivingEntity entity) {
    ItemStack stack = super.onItemUseFinish(item, world, entity);
    CompoundNBT tag = item.getTag();
    if (tag != null && tag.contains(KEY_EFFECTS, 9)) {
      ListNBT list = tag.getList(KEY_EFFECTS, 10);
      // add each effect in the tag
      for (int i = 0; i < list.size(); i++) {
        CompoundNBT effectTag = list.getCompound(i);
        // add Id from potion tag
        effectTag.putByte("Id", (byte) Effect.getId(ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectTag.getString("Potion")))));
        // add Amplifier if not present
        if(!effectTag.contains("Amplifier")) {
          effectTag.putByte("Amplifier", (byte) 0);
        }
        // add Duration if not present
        if(!effectTag.contains("Duration")) {
          effectTag.putByte("Duration", (byte) 115);
        }
        // read the effect instance from NBT
        EffectInstance effect = EffectInstance.read(effectTag);
        if(effect != null) {
          entity.addPotionEffect(effect);
        }
      }
    }
    // if creative player, return the original item stack instead of consuming
    if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) {
      return stack;
    }
    return item;
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
    ItemStack itemstack = player.getHeldItem(hand);
    player.setActiveHand(hand);
    return ActionResult.resultConsume(itemstack);
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getUseDuration(ItemStack stack) {
    return useDuration;
  }
}
