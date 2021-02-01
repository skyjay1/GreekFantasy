package greekfantasy.event;

import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.Favor;
import greekfantasy.deity.favor.FavorManager;
import greekfantasy.deity.favor.IFavor;
import greekfantasy.entity.ai.FleeFromFavorablePlayerGoal;
import greekfantasy.entity.ai.NearestAttackableFavorablePlayerGoal;
import greekfantasy.entity.ai.NearestAttackableFavorablePlayerResetGoal;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FavorEventHandler {
  

  /**
   * Used to trigger FavorManager when the player is killed
   * @param event the death event
   **/
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onPlayerDeath(final LivingDeathEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof PlayerEntity) {
      final PlayerEntity player = (PlayerEntity) event.getEntityLiving();
      final Entity source = event.getSource().getTrueSource();
      // trigger FavorManager
      if(source != null && !player.isSpectator() && !player.isCreative()) {
        player.getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onPlayerKilled(player, (LivingEntity)source, f));
      }
    }
  }
  
  /**
   * Used to trigger FavorManager when the player kills an entity
   * @param event the living death event
   */
  @SubscribeEvent
  public static void onLivingDeath(final LivingDeathEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && event.getSource().getTrueSource() instanceof PlayerEntity) {
      // update favor manager
      event.getSource().getTrueSource().getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onKillEntity(event.getEntityLiving(), (PlayerEntity)event.getSource().getTrueSource(), f));
    }
  }

  /**
   * Used to tick the FavorManager
   * @param event the PlayerTickEvent
   **/
  @SubscribeEvent
  public static void onLivingTick(final PlayerTickEvent event) {
    final boolean tick = (event.phase == TickEvent.Phase.START) && event.player.isAlive();
    if(tick && !event.player.getEntityWorld().isRemote() && event.player.isServerWorld()) {
      event.player.getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onPlayerTick(event.player, f));
    }
  }
  
  /**
   * Used to trigger the FavorManager
   * @param event the potion added event
   */
  @SubscribeEvent
  public static void onAddPotion(final PotionEvent.PotionAddedEvent event) {
    if(!event.getEntityLiving().getEntityWorld().isRemote() && event.getEntityLiving() instanceof PlayerEntity) {
      // notify favor manager
      event.getEntityLiving().getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onAddPotion((PlayerEntity)event.getEntityLiving(), event.getPotionEffect().getPotion(), f));
    }
  }
  
  /**
   * Used to attach Favor to players
   * @param event the capability attach event (Entity)
   */
  @SubscribeEvent
  public static void onAttachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
    if(event.getObject() instanceof PlayerEntity) {
      event.addCapability(IFavor.REGISTRY_NAME, new Favor.Provider());
    }
  }
  
  /**
   * Used to ensure that favor persists across deaths
   * @param event the player clone event
   */
  @SubscribeEvent
  public static void onPlayerClone(final PlayerEvent.Clone event) {
    if(event.isWasDeath()) {
      LazyOptional<IFavor> original = event.getOriginal().getCapability(GreekFantasy.FAVOR);
      LazyOptional<IFavor> copy = event.getPlayer().getCapability(GreekFantasy.FAVOR);
      if(original.isPresent() && copy.isPresent()) {
        copy.ifPresent(f -> f.deserializeNBT(original.orElseGet(() -> GreekFantasy.FAVOR.getDefaultInstance()).serializeNBT()));
      }
    }
  }
  
  /**
   * Used to change a player's favor when they attack an entity.
   * @param event the player attack event
   **/
  @SubscribeEvent
  public static void onPlayerAttack(final AttackEntityEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && event.getPlayer().isAlive()) {
      event.getPlayer().getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onAttackEntity(event.getEntityLiving(), event.getPlayer(), f));
    }
  }
  
  
  /**
   * Used to trigger Favor Manager when the player is attacked by an entity.
   * @param event the living attack event
   **/
  @SubscribeEvent
  public static void onPlayerAttack(final LivingAttackEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() 
        && event.getEntityLiving() instanceof PlayerEntity
        && event.getSource().getImmediateSource() != null) {
      ((PlayerEntity)event.getEntityLiving()).getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onPlayerHurt((PlayerEntity)event.getEntityLiving(), event.getSource().getImmediateSource(), f));
    }
  }  
  
  /**
   * Used to update FavorManager when a block is broken by the player.
   * @param event the block break event
   **/
  @SubscribeEvent
  public static void onBreakBlock(final BlockEvent.BreakEvent event) {
    if(event.getPlayer() != null && event.getPlayer().isServerWorld() && !event.getPlayer().isCreative()) {
      event.getPlayer().getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onBreakBlock(event.getPlayer(), event.getState().getBlock(), f));
    }
  }
  
  /**
   * Used to add Favor-based AI to mobs when they are spawned.
   * @param event the spawn event
   **/
  @SubscribeEvent
  public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
    // attempt to add player target goals
    if(!event.getEntity().getEntityWorld().isRemote() && event.getEntity() instanceof MobEntity
        && GreekFantasy.PROXY.getFavorConfiguration().hasEntity(event.getEntity().getType())
        && GreekFantasy.PROXY.getFavorConfiguration().getEntity(event.getEntity().getType()).hasHostileRange()) {
      final MobEntity mob = (MobEntity)event.getEntity();
      // add favor-checking goals
      mob.targetSelector.addGoal(0, new NearestAttackableFavorablePlayerGoal(mob));
      mob.targetSelector.addGoal(1, new NearestAttackableFavorablePlayerResetGoal(mob));
    }
    // attempt to add flee goals
    if(!event.getEntity().getEntityWorld().isRemote() && event.getEntity() instanceof CreatureEntity
        && GreekFantasy.PROXY.getFavorConfiguration().hasEntity(event.getEntity().getType())
        && GreekFantasy.PROXY.getFavorConfiguration().getEntity(event.getEntity().getType()).hasFleeRange()) {
      final CreatureEntity creature = (CreatureEntity)event.getEntity();
      // add favor-checking goals
      creature.goalSelector.addGoal(1, new FleeFromFavorablePlayerGoal(creature));
    }
  }
  
  /**
   * Used to prevent certain mobs from attacking players based on Favor
   * @param event
   **/
  @SubscribeEvent
  public static void onLivingTarget(final LivingSetAttackTargetEvent event) {
    if(!event.getEntityLiving().getEntityWorld().isRemote() && event.getEntityLiving() instanceof MobEntity 
        && event.getTarget() instanceof PlayerEntity
        && GreekFantasy.PROXY.getFavorConfiguration().hasEntity(event.getEntityLiving().getType())
        && !GreekFantasy.PROXY.getFavorConfiguration().getEntity(event.getEntityLiving().getType()).getHostileRange().isInFavorRange((PlayerEntity)event.getTarget())
        && event.getTarget() != event.getEntityLiving().getRevengeTarget()) {
      ((MobEntity)event.getEntityLiving()).setAttackTarget(null);
    }
  }
}
