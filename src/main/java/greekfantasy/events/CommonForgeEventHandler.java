package greekfantasy.events;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.CerastesEntity;
import greekfantasy.entity.ShadeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonForgeEventHandler {
  
  /**
   * Used to spawn a shade with the player's XP when they die.
   * @param event the death event
   **/
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onPlayerDeath(final LivingDeathEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && GreekFantasy.CONFIG.SHADE_SPAWN.get() && event.getEntityLiving() instanceof PlayerEntity) {
      final PlayerEntity player = (PlayerEntity) event.getEntityLiving();
      // check pre-conditions
      if(!player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !player.isSpectator() && player.experienceLevel > 3) {
        // save XP value
        int xp = player.experienceTotal;
        // remove XP from player
        player.addExperienceLevel(-xp);
        // give XP to shade and spawn into world
        final ShadeEntity shade = GFRegistry.SHADE_ENTITY.create(player.getEntityWorld());
        shade.setLocationAndAngles(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw, player.rotationPitch);
        shade.setStoredXP(xp);
        shade.setOwnerUniqueId(player.getUniqueID());
        player.getEntityWorld().addEntity(shade);
      }
    }
  }
  
  /**
   * Used to prevent players from using items while stunned.
   * @param event a PlayerInteractEvent or any of its children
   **/
  @SubscribeEvent
  public static void onPlayerInteract(final PlayerInteractEvent event) {
    if(GreekFantasy.CONFIG.STUN_PREVENTS_USE.get() && event.getPlayer().isAlive() && isStunned(event.getPlayer())) {
      // note: PlayerInteractEvent has several children but we receive and cancel all of the ones that can be cancelled
      if(event.isCancelable()) {
        event.setCanceled(true);
      }
    }
  }
  
  /**
   * Used to prevent players from using items while stunned.
   * @param event a PlayerInteractEvent or any of its children
   **/
  @SubscribeEvent
  public static void onPlayerAttack(final AttackEntityEvent event) {
    if(GreekFantasy.CONFIG.STUN_PREVENTS_USE.get() && event.getPlayer().isAlive() && isStunned(event.getPlayer())) {
      event.setCanceled(true);
    }
  }
  
  /**
   * Used to prevent players (or potentially, other living entities)
   * from jumping while stunned. Accomplishes this by applying a negative velocity
   * equal to what the positive velocity would have been.
   * @param event the LivingJumpEvent
   **/
  @SubscribeEvent
  public static void onLivingJump(final LivingJumpEvent event) {
    if(GreekFantasy.CONFIG.STUN_PREVENTS_JUMP.get() && isStunned(event.getEntityLiving())) {
      event.getEntityLiving().setMotion(event.getEntityLiving().getMotion().add(0.0D, -0.42D, 0.0D));
    }
  }
  
  /**
   * Used to add AI to Minecraft entities when they are spawned.
   * @param event the spawn event
   **/
  @SubscribeEvent
  public static void onLivingSpawn(final LivingSpawnEvent event) {
    if(event.getEntityLiving().getType() == EntityType.RABBIT) {
      final RabbitEntity rabbit = (RabbitEntity) event.getEntityLiving();
      if(rabbit.getRabbitType() != 99) {
        rabbit.goalSelector.addGoal(4, new AvoidEntityGoal<>(rabbit, CerastesEntity.class, 10.0F, 2.2D, 2.2D));
      }
    }
  }
  
  /**
   * Used to add features and mob spawns to each biome as it loads
   * @param event the biome load event
   **/
  @SubscribeEvent
  public static void onBiomeLoad(final BiomeLoadingEvent event) {
    GFRegistry.addBiomeFeatures(event);
    GFRegistry.addBiomeSpawns(event);
  }
  
  private static boolean isStunned(final LivingEntity entity) {
    return (entity.getActivePotionEffect(GFRegistry.PETRIFIED_EFFECT) != null || entity.getActivePotionEffect(GFRegistry.STUNNED_EFFECT) != null);
  }
}
