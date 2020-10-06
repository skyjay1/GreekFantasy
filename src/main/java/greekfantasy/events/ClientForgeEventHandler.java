package greekfantasy.events;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ClientForgeEventHandler {
    
  /**
   * This method handles when the player is wearing the winged sandals item.
   * It's a little buggy because the stepHeight applied here doesn't seem to persist
   * more than a tick, but it works and hopefully doesn't break things too much.
   * @param event
   **/
  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void onPlayerTick(final PlayerTickEvent event) {
    if(event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT && GreekFantasy.CONFIG.isOverstepEnabled()) {
      final ClientPlayerEntity player = (ClientPlayerEntity)event.player;
      final Minecraft mc = Minecraft.getInstance();
      final boolean hasOverstep = hasOverstep(player);
      // apply step height changes      
      if(hasOverstep && (player.stepHeight < 1.0F || player.isAutoJumpEnabled())) {
        player.stepHeight = 1.25F;
        // use Access Transformers to use/modify this field directly
        player.autoJumpEnabled = false;
      } else if(player.stepHeight > 1.2F) {
        // restore defaults
        player.stepHeight = 0.6F;
        player.autoJumpEnabled = mc.gameSettings.autoJump;
      }
    }
  }
  
  /**
   * Prevents the screen from "zooming in" when negative move-speed modifiers
   * are applied, but only if the player has the Stunned or Petrified effect.
   * @param event the FOVModifier event
   **/
  @SubscribeEvent
  public static void modifyFOV(final FOVModifier event) {
    final Minecraft mc = Minecraft.getInstance();
    if(mc != null) {
      final PlayerEntity player = mc.player;
      if(player.isAlive() && isStunned(player)) {
        event.setFOV(mc.gameSettings.fov);
      }
    }
  }
  
  /** @return whether the player should have the client-side overstep step-height logic applied **/
  private static boolean hasOverstep(final PlayerEntity player) {
    // return player.getItemStackFromSlot(EquipmentSlotType.FEET).getItem() == GFRegistry.WINGED_SANDALS;
    return EnchantmentHelper.getEnchantmentLevel(GFRegistry.OVERSTEP_ENCHANTMENT, player.getItemStackFromSlot(EquipmentSlotType.FEET)) > 0;
  }
  
  /** @return whether the player should have the client-side stun/petrify FOV logic applied **/
  private static boolean isStunned(final PlayerEntity player) {
    return (player.getActivePotionEffect(GFRegistry.PETRIFIED_EFFECT) != null || player.getActivePotionEffect(GFRegistry.STUNNED_EFFECT) != null);
  }
  
}
