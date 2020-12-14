package greekfantasy.events;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.render.PlayerPigRenderer;
import greekfantasy.client.render.PlayerSkyjayRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class ClientForgeEventHandler {
  
  private static PlayerPigRenderer<PlayerEntity> playerPigRenderer;
  private static PlayerSkyjayRenderer<PlayerEntity> skyjayRenderer;
    
  /**
   * Used to hide the player and their armor / held items
   * while using the Helm of Darkness.
   * Also used to render players as pigs when under the Swine effect
   * @param event the RenderPlayerEvent (Pre)
   **/
  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void renderPlayer(final RenderPlayerEvent.Pre event) {
    if(GreekFantasy.CONFIG.doesHelmHideArmor() && hasHelmOfDarkness(event.getPlayer())) {
      event.setCanceled(true);
    } else if(isSwine(event.getPlayer())) {
      event.setCanceled(true);
      // render pig instead
      if(null == playerPigRenderer) {
        Minecraft mc = Minecraft.getInstance();
        playerPigRenderer = new PlayerPigRenderer<PlayerEntity>(mc.getRenderManager());
      }
      playerPigRenderer.render(event.getPlayer(), event.getPlayer().rotationYaw, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), 
          playerPigRenderer.getPackedLight(event.getPlayer(), event.getPartialRenderTick()));//15728640);
    } else if(!event.isCanceled() && "skyjay1".equals(event.getPlayer().getName().getUnformattedComponentText())) {
      if(null == skyjayRenderer) {
        Minecraft mc = Minecraft.getInstance();
        skyjayRenderer = new PlayerSkyjayRenderer<PlayerEntity>(mc.getRenderManager());
      }
      skyjayRenderer.render(event.getPlayer(), event.getPlayer().rotationYaw, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), 15728880);
    }
  }
  
  /**
   * Used to hide the first-person view of held items
   * while a player is using the Helm of Darkness.
   * @param event the RenderHandEvent
   **/
  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void renderPlayerHand(final RenderHandEvent event) {
    final Minecraft mc = Minecraft.getInstance();
    if((GreekFantasy.CONFIG.doesHelmHideArmor() && hasHelmOfDarkness(mc.player)) 
        || (isSwine(mc.player) && mc.player.getHeldItemMainhand().isEmpty())) {
      event.setCanceled(true);
    }
  }
    
  /**
   * This method handles when the player is wearing the winged sandals item.
   * It's a little buggy because the stepHeight applied here doesn't seem to persist
   * more than a tick, but it works and hopefully doesn't break things too much.
   * @param event the player tick event (only handles TickEvent.Phase.START)
   **/
  @SubscribeEvent(priority = EventPriority.HIGH)
  public static void onPlayerTick(final PlayerTickEvent event) {
    if(event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT && GreekFantasy.CONFIG.isOverstepEnabled() 
        && event.player instanceof ClientPlayerEntity) {
      final ClientPlayerEntity player = (ClientPlayerEntity)event.player;
      final Minecraft mc = Minecraft.getInstance();
      final boolean hasOverstep = hasOverstep(player);
      // apply step height changes      
      if(hasOverstep && !player.isSneaking() && (player.stepHeight < 1.0F || player.isAutoJumpEnabled())) {
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

  /** @return whether the player is wearing the Helm of Darkness **/
  private static boolean hasHelmOfDarkness(final PlayerEntity player) {
    return player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == GFRegistry.HELM_OF_DARKNESS;
  }
  
  /** @return whether the player should have the client-side overstep step-height logic applied **/
  private static boolean hasOverstep(final PlayerEntity player) {
    return EnchantmentHelper.getEnchantmentLevel(GFRegistry.OVERSTEP_ENCHANTMENT, player.getItemStackFromSlot(EquipmentSlotType.FEET)) > 0;
  }
  
  /** @return whether the entity should have the client-side stun/petrify FOV or particle effects **/
  private static boolean isStunned(final LivingEntity livingEntity) {
    return (livingEntity.getActivePotionEffect(GFRegistry.PETRIFIED_EFFECT) != null || livingEntity.getActivePotionEffect(GFRegistry.STUNNED_EFFECT) != null);
  }
  
  /** @return whether the entity should have the Swine effect applied **/
  private static boolean isSwine(final LivingEntity livingEntity) {
    return (livingEntity.getActivePotionEffect(GFRegistry.SWINE_EFFECT) != null);
  }
}
