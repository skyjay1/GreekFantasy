package greekfantasy.events;

import greekfantasy.GreekFantasy;
import greekfantasy.entity.ShadeEntity;
import greekfantasy.proxy.Proxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonEventHandler {
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onPlayerDeath(final LivingDeathEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof PlayerEntity) {
      final PlayerEntity player = (PlayerEntity) event.getEntityLiving();
      // check pre-conditions
      if(!player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !player.isSpectator() && player.experienceLevel > 4) {
        // save XP value
        int xp = player.experienceTotal;
        // remove XP from player
        player.addExperienceLevel(-player.experienceLevel);
        // give XP to shade and spawn into world
        GreekFantasy.LOGGER.info("Spawning Shade at " + player.getPositionVec() + " for player " + player.getDisplayName());
        final ShadeEntity shade = Proxy.SHADE_ENTITY.create(player.getEntityWorld());
        shade.setLocationAndAngles(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw, player.rotationPitch);
        shade.setStoredXP(xp);
        player.getEntityWorld().addEntity(shade);
      }
    }
  }
}
