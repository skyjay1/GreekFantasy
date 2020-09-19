package greekfantasy.events;

import greekfantasy.GFRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.EntityViewRenderEvent.FOVModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientForgeEventHandler {
  
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
  
  private static boolean isStunned(final PlayerEntity player) {
    return (player.getActivePotionEffect(GFRegistry.PETRIFIED_EFFECT) != null || player.getActivePotionEffect(GFRegistry.STUNNED_EFFECT) != null);
  }
  
}
