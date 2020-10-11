package greekfantasy.item;

import greekfantasy.GFRegistry;
import greekfantasy.entity.SpartiEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DragonToothItem extends Item {

  public DragonToothItem(final Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    
    // summon a Sparti (TODO move this to a projectile entity)
    final SpartiEntity sparti = GFRegistry.SPARTI_ENTITY.create(world);
    sparti.setLocationAndAngles(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw + 180.0F, 0);
    sparti.setOwner(player);
    sparti.setSpawning();
    sparti.setEquipmentOnSpawn();
    world.addEntity(sparti);
    
    // shrink the item stack
    if(!player.isCreative()) {
      stack.shrink(1);
    }
    
    return ActionResult.func_233538_a_(stack, world.isRemote());
  }
}
