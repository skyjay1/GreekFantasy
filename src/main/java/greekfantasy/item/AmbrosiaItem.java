package greekfantasy.item;

import greekfantasy.GFRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.world.World;

public class AmbrosiaItem extends Item {

  public AmbrosiaItem(final Item.Properties properties) {
    super(properties);
  }
  
  @Override
  public ItemStack getContainerItem(ItemStack itemStack) { return new ItemStack(GFRegistry.HORN); }
  
  @Override
  public boolean hasContainerItem(ItemStack stack) { return true; }
  
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
  
}
