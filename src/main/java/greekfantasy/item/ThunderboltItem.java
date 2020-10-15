package greekfantasy.item;

import greekfantasy.GreekFantasy;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ThunderboltItem extends Item {

  public ThunderboltItem(final Item.Properties properties) {
    super(properties);
  }
  
  @Override
  public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
    ItemStack stack = player.getHeldItem(hand);
    // prevent the item from being used up all the way
    if(stack.getMaxDamage() - stack.getDamage() <= 2) {
      return ActionResult.resultFail(stack);
    }
    
    if(!world.isRemote()) {
      // raytrace
      final Vector3d startVec = player.getEyePosition(1.0F);
      final float pitch = (float) Math.toRadians(-player.rotationPitch);
      final float yaw = (float) Math.toRadians(-player.rotationYaw);
      float cosYaw = MathHelper.cos(yaw - (float)Math.PI);
      float sinYaw = MathHelper.sin(yaw - (float)Math.PI);
      float cosPitch = -MathHelper.cos(pitch);
      float sinPitch = MathHelper.sin(pitch);
      final float dis = 64.0F;
      final Vector3d endVec = startVec.add(sinYaw * cosPitch * dis, sinPitch * dis, cosYaw * cosPitch * dis);
      RayTraceResult raytrace = player.world.rayTraceBlocks(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, player));
      // add a lightning bolt at the resulting position
      if(raytrace.getType() != RayTraceResult.Type.MISS) {
        LightningBoltEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
        bolt.setPosition(raytrace.getHitVec().getX(), raytrace.getHitVec().getY(), raytrace.getHitVec().getZ());
        world.addEntity(bolt);
        // cooldown and item damage
        player.getCooldownTracker().setCooldown(this, GreekFantasy.CONFIG.getThunderboltCooldown());
        if(!player.isCreative()) {
          stack.damageItem(1, player, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }
      }
    }
    
    return ActionResult.func_233538_a_(stack, world.isRemote());
  }
  
  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    return toRepair.getItem() == this && toRepair.getDamage() < toRepair.getMaxDamage() && isRepairItem(repair);
  }
  
  private boolean isRepairItem(final ItemStack repair) {
    // TODO what should be used to repair this?
    return repair.getItem() == Items.IRON_INGOT;
  }
}
