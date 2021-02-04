package greekfantasy.event;

import greekfantasy.GFRegistry;
import greekfantasy.GFWorldSavedData;
import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.Favor;
import greekfantasy.deity.favor.FavorManager;
import greekfantasy.deity.favor.IFavor;
import greekfantasy.deity.favor_effect.SpecialFavorEffect;
import greekfantasy.entity.WhirlEntity;
import greekfantasy.entity.ai.FleeFromFavorablePlayerGoal;
import greekfantasy.entity.ai.NearestAttackableFavorablePlayerGoal;
import greekfantasy.entity.ai.NearestAttackableFavorablePlayerResetGoal;
import greekfantasy.item.ThunderboltItem;
import greekfantasy.network.SSimpleParticlesPacket;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.network.PacketDistributor;

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
  public static void onPlayerTick(final PlayerTickEvent event) {
    final boolean tick = (event.phase == TickEvent.Phase.START) && event.player.isAlive();
    if(tick && !event.player.getEntityWorld().isRemote() && event.player.isServerWorld()) {
      event.player.getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onPlayerTick(event.player, f));
    }
  }
  
  /**
   * Used to change xp amount based on Favor
   * @param event the PickupXP event
   */
  @SubscribeEvent
  public static void onPlayerXP(final PlayerXpEvent.PickupXp event) {
    if(!event.getPlayer().getEntityWorld().isRemote() && event.getPlayer().isServerWorld()
        && GreekFantasy.PROXY.getFavorConfiguration().hasSpecials(SpecialFavorEffect.Type.XP_MULTIPLIER)) {
      event.getPlayer().getCapability(GreekFantasy.FAVOR).ifPresent(f -> event.getOrb().xpValue = FavorManager.onPlayerXP(event.getPlayer(), f, event.getOrb().xpValue));
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
      event.getEntityLiving().getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onAddPotion((PlayerEntity)event.getEntityLiving(), event.getPotionEffect(), f));
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
   * Used to enable flying players when they equip enchanted winged sandals.
   * @param event the equipment change event
   */
  @SubscribeEvent
  public static void onChangeEquipment(final LivingEquipmentChangeEvent event) {
    // Check which equipment was changed and if it is a player
    if(GreekFantasy.CONFIG.isFlyingEnabled() && event.getEntityLiving() instanceof PlayerEntity && event.getEntityLiving().getEntityWorld() instanceof ServerWorld 
        && event.getSlot() == EquipmentSlotType.FEET && event.getTo().getItem() == GFRegistry.WINGED_SANDALS 
        && EnchantmentHelper.getEnchantmentLevel(GFRegistry.FLYING_ENCHANTMENT, event.getTo()) > 0) {
      final PlayerEntity player = (PlayerEntity)event.getEntityLiving();
      GFWorldSavedData data = GFWorldSavedData.getOrCreate((ServerWorld)event.getEntityLiving().getEntityWorld());
      // Check the player's favor level before enabling flight
      if(GreekFantasy.PROXY.getFavorConfiguration().getFlyingDeityRange().isInFavorRange(player)) {
        data.addFlyingPlayer(player);
      }
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
   * Used to summon a Whirl when an enchanted trident is stopped being used before it is thrown
   * @param event the item use event (stop)
   */
  @SubscribeEvent
  public static void onStopUseItem(final LivingEntityUseItemEvent.Stop event) {
    final ItemStack item = event.getEntityLiving().getHeldItemMainhand();
    // Determine if the event can run (server-side player using enchanted trident with no cooldown and in favor range)
    if(!event.isCanceled() && event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().getEntityWorld().isRemote()
        && GreekFantasy.CONFIG.isLordOfTheSeaEnabled() && item.getItem() == Items.TRIDENT
        && EnchantmentHelper.getEnchantmentLevel(GFRegistry.LORD_OF_THE_SEA_ENCHANTMENT, item) > 0
        && !((PlayerEntity)event.getEntityLiving()).getCooldownTracker().hasCooldown(Items.TRIDENT)
        && GreekFantasy.PROXY.getFavorConfiguration().getLordOfTheSeaDeityRange().isInFavorRange((PlayerEntity)event.getEntityLiving())) {
      // The player has used an enchanted item and has the correct favor range, so the effect should be applied
      final PlayerEntity player = (PlayerEntity)event.getEntityLiving();
      final RayTraceResult raytrace = ThunderboltItem.raytraceFromEntity(event.getEntityLiving().getEntityWorld(), player, 48.0F);
      // add a lightning bolt at the resulting position
      if(raytrace.getType() != RayTraceResult.Type.MISS) {
        final WhirlEntity whirl = GFRegistry.WHIRL_ENTITY.create(player.getEntityWorld());
        final BlockPos pos = new BlockPos(raytrace.getHitVec());
        // make sure there is enough water here
        if(player.getEntityWorld().getFluidState(pos).isTagged(FluidTags.WATER)
            && player.getEntityWorld().getFluidState(pos.down((int)Math.ceil(whirl.getHeight()))).isTagged(FluidTags.WATER)) {
          // summon a powerful whirl with limited life and mob attracting turned on
          whirl.setLocationAndAngles(raytrace.getHitVec().getX(), raytrace.getHitVec().getY() - whirl.getHeight(), raytrace.getHitVec().getZ(), 0, 0);
          event.getEntityLiving().getEntityWorld().addEntity(whirl);
          whirl.setLimitedLife(GreekFantasy.CONFIG.getWhirlLifespan() * 20);
          whirl.setAttractMobs(true);
          whirl.playSound(SoundEvents.ITEM_TRIDENT_THUNDER, 1.5F, 0.6F + whirl.getRNG().nextFloat() * 0.32F);
          // cooldown and item damage
          player.getCooldownTracker().setCooldown(item.getItem(), GreekFantasy.CONFIG.getWhirlLifespan() * 10);
          if(!player.isCreative()) {
            item.damageItem(25, player, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
          }        
        }
      }
    }
  }
  
  /**
   * Used to prevent players from trading with villagers when at low favor
   * @param event the entity interact event
   */
  @SubscribeEvent
  public static void onEntityInteract(final PlayerInteractEvent.EntityInteract event) {
    if(!event.getPlayer().getEntityWorld().isRemote() && event.getTarget() instanceof AbstractVillagerEntity
        && GreekFantasy.PROXY.getFavorConfiguration().hasSpecials(SpecialFavorEffect.Type.TRADING_CANCEL)) {
      AbstractVillagerEntity villager = (AbstractVillagerEntity)event.getTarget();
      event.getPlayer().getCapability(GreekFantasy.FAVOR).ifPresent(f -> {
        // note: this special favor effect does not check or change cooldown time
        // determine which special favor effect to use
        for(final SpecialFavorEffect effect : GreekFantasy.PROXY.getFavorConfiguration().getSpecials(SpecialFavorEffect.Type.TRADING_CANCEL)) {
          if(effect.canApply(event.getPlayer(), f)) {
            // close the container
            event.setCanceled(true);
            // cause the villager to shake its head and spawn particles
            villager.setShakeHeadTicks(40);
            villager.playSound(SoundEvents.ENTITY_VILLAGER_NO, 0.5F, 1.0F);
            GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SSimpleParticlesPacket(false, event.getTarget().getPosition().up(1), 4));
            return;
          }
        }        
      });
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
   * Used to modify arrows after they are fired by a player, based on favor
   * @param event the entity join world event
   */
  @SubscribeEvent
  public static void onArrowJoinWorld(final EntityJoinWorldEvent event) {
    // attempt to add player target goals
    if(!event.getEntity().getEntityWorld().isRemote() && event.getEntity() instanceof AbstractArrowEntity
        && GreekFantasy.PROXY.getFavorConfiguration().hasSpecials(SpecialFavorEffect.Type.ARROW_DAMAGE_MULTIPLIER)) {
      final AbstractArrowEntity arrow = (AbstractArrowEntity) event.getEntity();
      final Entity thrower = arrow.func_234616_v_();
      if(thrower instanceof PlayerEntity) {
        thrower.getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onShootArrow((PlayerEntity)thrower, f, arrow));
      }
    }
  }
  
  /**
   * Used to change the number of baby entities that spawn based on favor
   * @param event the baby entity spawn event
   */
  @SubscribeEvent
  public static void onBabySpawn(final BabyEntitySpawnEvent event) {
    final World world = event.getParentA().getEntityWorld();
    if(!event.isCanceled() && world instanceof ServerWorld && event.getCausedByPlayer() != null 
        && !event.getCausedByPlayer().isCreative() && !event.getCausedByPlayer().isSpectator()
        && event.getParentA() instanceof AnimalEntity && event.getParentB() instanceof AnimalEntity
        && GreekFantasy.PROXY.getFavorConfiguration().hasSpecials(SpecialFavorEffect.Type.BREEDING_OFFSPRING_MULTIPLIER)) {
      event.getCausedByPlayer().getCapability(GreekFantasy.FAVOR).ifPresent(f -> {
        int numBabies = FavorManager.onBabySpawn(event.getCausedByPlayer(), f);
        if(numBabies < 1) {
          // number of babies is zero, so cancel the event
          event.setCanceled(true);
        } else if(numBabies > 1) {
          // number of babies is more than one, so spawn additional mobs
          for(int i = 1; i < numBabies; i++) {
            AgeableEntity bonusChild = ((AnimalEntity)event.getParentA()).func_241840_a((ServerWorld)world, (AnimalEntity)event.getParentB());
            if(bonusChild != null) {
              bonusChild.copyLocationAndAnglesFrom(event.getChild());
              world.addEntity(bonusChild);
            }
          }
        }
      });
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
