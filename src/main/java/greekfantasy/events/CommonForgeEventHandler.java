package greekfantasy.events;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import greekfantasy.GFRegistry;
import greekfantasy.GFWorldGen;
import greekfantasy.GreekFantasy;
import greekfantasy.entity.ArionEntity;
import greekfantasy.entity.CerastesEntity;
import greekfantasy.entity.DryadEntity;
import greekfantasy.entity.GeryonEntity;
import greekfantasy.entity.GiantBoarEntity;
import greekfantasy.entity.ShadeEntity;
import greekfantasy.entity.ai.NearestAttackableFavorablePlayerGoal;
import greekfantasy.entity.ai.NearestAttackableFavorablePlayerResetGoal;
import greekfantasy.favor.Favor;
import greekfantasy.favor.FavorManager;
import greekfantasy.favor.FavorRangeTarget;
import greekfantasy.favor.IFavor;
import greekfantasy.network.SDeityPacket;
import greekfantasy.network.SFavorRangeTargetPacket;
import greekfantasy.network.SPanfluteSongPacket;
import greekfantasy.network.SSwineEffectPacket;
import greekfantasy.util.PalladiumSavedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.piglin.AbstractPiglinEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class CommonForgeEventHandler {
  // items that can convert hoglins to giant boars
  protected static final IOptionalNamedTag<Item> GIANT_BOAR_TRIGGER = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "giant_boar_trigger"));
  protected static final IOptionalNamedTag<Item> ARION_TRIGGER = ItemTags.createOptional(new ResourceLocation(GreekFantasy.MODID, "arion_trigger"));

  /**
   * Used to spawn a shade with the player's XP when they die.
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
      // attempt to spawn a shade
      if(GreekFantasy.CONFIG.doesShadeSpawnOnDeath() && !player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) && !player.isSpectator() && player.experienceLevel > 3) {
        // save XP value
        int xp = player.experienceTotal;
        // remove XP from player
        player.addExperienceLevel(-(player.experienceLevel + 1));
        // give XP to shade and spawn into world
        final ShadeEntity shade = GFRegistry.SHADE_ENTITY.create(player.getEntityWorld());
        shade.setLocationAndAngles(player.getPosX(), player.getPosY(), player.getPosZ(), player.rotationYaw, player.rotationPitch);
        shade.setStoredXP((int)(xp * (0.4F + player.getRNG().nextFloat() * 0.2F)));
        shade.setOwnerUniqueId(PlayerEntity.getOfflineUUID(player.getDisplayName().getUnformattedComponentText()));
        shade.enablePersistence();
        player.getEntityWorld().addEntity(shade);
      }
    }
  }

  /**
   * Used to change a player's favor when they kill an entity.
   * Also used to summon a Geryon when a cow is killed and other spawn conditions are met
   * @param event the living death event
   */
  @SubscribeEvent
  public static void onLivingDeath(final LivingDeathEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && event.getSource().getTrueSource() instanceof PlayerEntity) {
      // update favor manager
      event.getSource().getTrueSource().getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onKillEntity(event.getEntityLiving(), (PlayerEntity)event.getSource().getTrueSource(), f));
      // check if the cow was killed by a player and if geryon can spawn here
      final BlockPos deathPos = event.getEntityLiving().getPosition();
      if(event.getEntityLiving() instanceof CowEntity && GeryonEntity.canGeryonSpawnOn(event.getEntityLiving().getEntityWorld(), deathPos)) {
        // check for Geryon Head blocks nearby
        final List<BlockPos> heads = new ArrayList<>();
        final int r = 3;
        BlockPos pos;
        countHeads:
        for(int x = -r; x <= r; x++) {
          for(int y = -2; y <= 2; y++) {
            for(int z = -r; z <= r; z++) {
              pos = deathPos.add(x, y, z);
              if(event.getEntityLiving().getEntityWorld().getBlockState(pos).isIn(GFRegistry.GIGANTE_HEAD)) {
                heads.add(pos);
              }
              if(heads.size() >= 3) break countHeads;
            }
          }
        }
        // if we found at least three heads, remove them and spawn a geryon
        if(heads.size() >= 3) {
          heads.subList(0, 3).forEach(p -> event.getEntityLiving().getEntityWorld().destroyBlock(p, false));
          final float yaw = MathHelper.wrapDegrees(event.getSource().getTrueSource().rotationYaw + 180.0F);
          GeryonEntity.spawnGeryon(event.getEntityLiving().getEntityWorld(), deathPos, yaw);
        }
      }
    }
  }
  
  /**
   * Used to set the player pose when the Swine effect is enabled.
   * Also ticks the FavorManager
   * @param event the PlayerTickEvent
   **/
  @SubscribeEvent
  public static void onLivingTick(final PlayerTickEvent event) {
    final boolean tick = (event.phase == TickEvent.Phase.START) && event.player.isAlive();
    if(tick && !event.player.getEntityWorld().isRemote() && event.player.isServerWorld()) {
      event.player.getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onPlayerTick(event.player, f));
    }
    if(tick && GreekFantasy.CONFIG.isSwineEnabled()) {
      final boolean isSwine = isSwine(event.player);
      final Pose forcedPose = event.player.getForcedPose();
      // drop armor
      if(isSwine && GreekFantasy.CONFIG.doesSwineDropArmor() && event.player.getRNG().nextInt(20) == 0) {
        final Iterable<ItemStack> armor = ImmutableList.copyOf(event.player.getArmorInventoryList());
        event.player.setItemStackToSlot(EquipmentSlotType.HEAD, ItemStack.EMPTY);
        event.player.setItemStackToSlot(EquipmentSlotType.CHEST, ItemStack.EMPTY);
        event.player.setItemStackToSlot(EquipmentSlotType.LEGS, ItemStack.EMPTY);
        event.player.setItemStackToSlot(EquipmentSlotType.FEET, ItemStack.EMPTY);
        for(final ItemStack i : armor) {
          final ItemEntity item = event.player.entityDropItem(i);
          if(item != null) {
            item.setNoPickupDelay();
          }
        }
      }
      // update the forced pose
      if(isSwine && forcedPose != Pose.FALL_FLYING) {
        // apply the forced pose
        event.player.setForcedPose(Pose.FALL_FLYING);
      } else if(!isSwine && Pose.FALL_FLYING == forcedPose) {
        // clear the forced pose
        event.player.setForcedPose(null);
      }
    }
  }
  
  /**
   * Used to notify the client when a server-side entity receives the Swine effect,
   * since this is not usually synced and the client needs it to affect rendering.
   * @param event the potion added event
   */
  @SubscribeEvent
  public static void onAddPotion(final PotionEvent.PotionAddedEvent event) {
    if(!event.getEntityLiving().getEntityWorld().isRemote()) {
      // notify favor manager
      if(event.getEntityLiving() instanceof PlayerEntity) {
        event.getEntityLiving().getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onAddPotion((PlayerEntity)event.getEntityLiving(), event.getPotionEffect().getPotion(), f));
      }
      // send swine effect packet
      if (GreekFantasy.CONFIG.isSwineEnabled() 
          && event.getPotionEffect().getPotion() == GFRegistry.SWINE_EFFECT
          && GreekFantasy.CONFIG.canSwineApply(event.getEntityLiving().getType().getRegistryName().toString())) {
        final int id = event.getEntityLiving().getEntityId();
        GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SSwineEffectPacket(id, event.getPotionEffect().getDuration()));
      }
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
   * Used to prevent players from using items while stunned.
   * @param event a PlayerInteractEvent or any of its children
   **/
  @SubscribeEvent
  public static void onPlayerInteract(final PlayerInteractEvent event) {
    if(GreekFantasy.CONFIG.doesStunPreventUse() && event.getPlayer().isAlive() && isStunned(event.getPlayer())) {
      // note: PlayerInteractEvent has several children but we receive and cancel all of the ones that can be cancelled
      if(event.isCancelable()) {
        event.setCanceled(true);
      }
    }
  }
  
  /**
   * Used to convert hoglin entities to giant boar entities
   * @param event the PlayerInteractEvent.EntityInteract event
   **/
  @SubscribeEvent
  public static void onPlayerInteract(final PlayerInteractEvent.EntityInteract event) {
    // when player uses poisonous potato on adult hoglin outside of nether
    if(!event.isCanceled() && (!GreekFantasy.CONFIG.getGiantBoarNonNether() || event.getWorld().getDimensionKey() != World.THE_NETHER) 
        && event.getTarget().getType() == EntityType.HOGLIN 
        && event.getTarget() instanceof HoglinEntity && event.getWorld() instanceof ServerWorld 
        && GIANT_BOAR_TRIGGER.contains(event.getItemStack().getItem())) {
      final HoglinEntity hoglin = (HoglinEntity)event.getTarget();
      if(!hoglin.isChild()) {
        // spawn giant boar and shrink the item stack
        GiantBoarEntity.spawnGiantBoar((ServerWorld)event.getWorld(), hoglin);
        if(!event.getPlayer().isCreative()) {
          event.getItemStack().shrink(1);
        }
      }
    } else if(!event.isCanceled() && event.getTarget().getType() == EntityType.HORSE && event.getTarget() instanceof HorseEntity
        && event.getWorld() instanceof ServerWorld && ARION_TRIGGER.contains(event.getItemStack().getItem())) {
      final HorseEntity horse = (HorseEntity)event.getTarget();
      if(!horse.isChild() && horse.isTame()) {
        // spawn Arion and shrink the item stack
        ArionEntity.spawnArion((ServerWorld)event.getWorld(), event.getPlayer(), horse);
        if(!event.getPlayer().isCreative()) {
          event.getItemStack().shrink(1);
        }
      }
      
    }
  }
  
  /**
   * Used to prevent players from using items while stunned.
   * Also used to change a player's favor when they attack an entity.
   * @param event the living attack event
   **/
  @SubscribeEvent
  public static void onPlayerAttack(final AttackEntityEvent event) {
    if(GreekFantasy.CONFIG.doesStunPreventUse() && event.getPlayer().isAlive() && isStunned(event.getPlayer())) {
      event.setCanceled(true);
    }
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && event.getPlayer().isAlive()) {
      event.getPlayer().getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onAttackEntity(event.getEntityLiving(), event.getPlayer(), f));
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
    if(GreekFantasy.CONFIG.doesStunPreventJump() && isStunned(event.getEntityLiving())) {
      event.getEntityLiving().setMotion(event.getEntityLiving().getMotion().add(0.0D, -0.42D, 0.0D));
    }
  }
  
  /**
   * Used to update FavorManager when a block is broken by the player.
   * Also used to anger nearby dryads when the player breaks a log block that may be a tree
   * @param event the block break event
   **/
  @SubscribeEvent
  public static void onBreakBlock(final BlockEvent.BreakEvent event) {
    if(event.getPlayer() != null && event.getPlayer().isServerWorld() && !event.getPlayer().isCreative()) {
      event.getPlayer().getCapability(GreekFantasy.FAVOR).ifPresent(f -> FavorManager.onBreakBlock(event.getPlayer(), event.getState().getBlock(), f));
    }
    if(GreekFantasy.CONFIG.isDryadAngryOnHarvest() && event.getPlayer() != null && !event.getPlayer().isCreative() && event.getState().isIn(BlockTags.LOGS)) {
      // make a list of nearby dryads
      final AxisAlignedBB aabb = new AxisAlignedBB(event.getPos()).grow(GreekFantasy.CONFIG.getDryadAngryRange());
      final List<DryadEntity> dryads = event.getWorld().getEntitiesWithinAABB(DryadEntity.class, aabb);
      for(final DryadEntity dryad : dryads) {
        // check if this is a tree according to the given dryad
        if(DryadEntity.isTreeAt(event.getWorld(), event.getPos().down(1), dryad.getVariant().getBlocks())
            || DryadEntity.isTreeAt(event.getWorld(), event.getPos().down(2), dryad.getVariant().getBlocks())) {
          // anger the dryad
          dryad.setAttackTarget(event.getPlayer());
          dryad.tryExitTree();
        }
      }
    }
  }
  
  /**
   * Used to add AI to Minecraft entities when they are spawned.
   * @param event the spawn event
   **/
  @SubscribeEvent
  public static void onLivingSpecialSpawn(final LivingSpawnEvent.SpecialSpawn event) {
    if(event.getEntityLiving().getType() == EntityType.RABBIT && !event.getEntityLiving().getEntityWorld().isRemote()) {
      final RabbitEntity rabbit = (RabbitEntity) event.getEntityLiving();
      if(rabbit.getRabbitType() != 99) {
        rabbit.goalSelector.addGoal(4, new AvoidEntityGoal<>(rabbit, CerastesEntity.class, e -> !((CerastesEntity)e).isHiding(), 6.0F, 2.2D, 2.2D, EntityPredicates.CAN_AI_TARGET::test));        
      }
    }
    if(!event.getEntityLiving().getEntityWorld().isRemote() && event.getEntityLiving() instanceof MobEntity
        && GreekFantasy.PROXY.getFavorRangeTarget().has(event.getEntityLiving().getType())) {
      final MobEntity mob = (MobEntity)event.getEntityLiving();
      // add favor-checking goals
      mob.targetSelector.addGoal(0, new NearestAttackableFavorablePlayerGoal(mob));
      mob.targetSelector.addGoal(1, new NearestAttackableFavorablePlayerResetGoal(mob));
    }
  }
  
  /**
   * Used to add prevent monsters from spawning near Palladium blocks
   * @param event the spawn event
   **/
  @SubscribeEvent
  public static void onLivingCheckSpawn(final LivingSpawnEvent.CheckSpawn event) {
    final int cRadius = GreekFantasy.CONFIG.getPalladiumChunkRange();
    final int cVertical = GreekFantasy.CONFIG.getPalladiumYRange() / 2; // divide by 2 to center on block
    if(GreekFantasy.CONFIG.isPalladiumEnabled() && !event.getEntityLiving().getEntityWorld().isRemote() 
        && event.getWorld() instanceof ServerWorld && event.getEntityLiving() instanceof IMob && event.getEntityLiving().isNonBoss()) {
      // check for nearby Statue Tile Entity
      final ServerWorld world = (ServerWorld)event.getWorld();
      final BlockPos blockPos = new BlockPos(event.getX(), event.getY(), event.getZ());
      final ChunkPos chunkPos = new ChunkPos(blockPos);
      final PalladiumSavedData data = PalladiumSavedData.getOrCreate(world);
      ChunkPos cPos;
      // search each chunk in a square radius centered on this chunk
      for(int cX = -cRadius; cX <= cRadius; cX++) {
        for(int cZ = -cRadius; cZ <= cRadius; cZ++) {
          cPos = new ChunkPos(chunkPos.x + cX, chunkPos.z + cZ);
          if(event.getWorld().chunkExists(cPos.x, cPos.z)) {
            // check each position to see if it's valid and within range
            for(final BlockPos p : data.getPalladium(world, cPos)) {
              if(!PalladiumSavedData.validate(world, p)) {
                data.removePalladium(cPos, p);
              } else if(Math.abs(p.getY() - blockPos.getY()) < cVertical) {
                // the position is preventing spawn, set result to DENY
                event.setResult(Result.DENY);
                return;
              }
            }
            return;
          }
        }
      }
    }
  }
  
  /**
   * Used to prevent certain mobs from attacking players who are under the Swine effect
   * @param event
   **/
  @SubscribeEvent
  public static void onLivingTarget(final LivingSetAttackTargetEvent event) {
    if(!event.getEntityLiving().getEntityWorld().isRemote() && event.getEntityLiving() instanceof MobEntity 
        && event.getTarget() instanceof PlayerEntity 
        && GreekFantasy.CONFIG.isSwineEnabled() && GreekFantasy.CONFIG.doesSwinePreventTarget() 
        && isSwine(event.getTarget()) && event.getTarget() != event.getEntityLiving().getAttackingEntity()
        && (event.getEntityLiving() instanceof HoglinEntity 
            || event.getEntityLiving() instanceof GiantBoarEntity
            || event.getEntityLiving() instanceof AbstractPiglinEntity)) {
      ((MobEntity)event.getEntityLiving()).setAttackTarget(null);
    }
    if(!event.getEntityLiving().getEntityWorld().isRemote() && event.getEntityLiving() instanceof MobEntity 
        && event.getTarget() instanceof PlayerEntity
        && GreekFantasy.PROXY.getFavorRangeTarget().has(event.getEntityLiving().getType())
        && !GreekFantasy.PROXY.getFavorRangeTarget().get(event.getEntityLiving().getType()).isInFavorRange((PlayerEntity)event.getTarget())
        && event.getTarget() != event.getEntityLiving().getRevengeTarget()) {
      ((MobEntity)event.getEntityLiving()).setAttackTarget(null);
    }
  }
  
  /**
   * Used to add features and mob spawns to each biome as it loads
   * @param event the biome load event
   **/
  @SubscribeEvent
  public static void onBiomeLoad(final BiomeLoadingEvent event) {
    GFWorldGen.addBiomeFeatures(event);
    GFWorldGen.addBiomeSpawns(event);
  }
  
  /**
   * Used to sync datapack data from the server to each client
   * @param event the player login event
   **/
  @SubscribeEvent
  public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
    PlayerEntity player = event.getPlayer();
    if (player instanceof ServerPlayerEntity) {
      // sync panflute songs
      GreekFantasy.PROXY.PANFLUTE_SONGS.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SPanfluteSongPacket(e.getKey(), e.getValue().get())));
      // sync deity
      GreekFantasy.PROXY.DEITY.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SDeityPacket(e.getKey(), e.getValue().get())));
      // sync favor range target
      GreekFantasy.PROXY.FAVOR_RANGE_TARGET.get(FavorRangeTarget.NAME).ifPresent(f -> GreekFantasy.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SFavorRangeTargetPacket(f)));
    }
  }
  
  /**
   * Used to sync panflute songs when resources are reloaded
   * @param event
   **/
  @SubscribeEvent
  public static void onAddReloadListeners(final AddReloadListenerEvent event) {
    GreekFantasy.LOGGER.debug("onAddReloadListeners");
    event.addListener(GreekFantasy.PROXY.PANFLUTE_SONGS);
    event.addListener(GreekFantasy.PROXY.DEITY);
    event.addListener(GreekFantasy.PROXY.FAVOR_RANGE_TARGET);
  }
  
  /** @return whether the entity should have the Stunned or Petrified effect applied **/
  private static boolean isStunned(final LivingEntity entity) {
    return entity.isPotionActive(GFRegistry.STUNNED_EFFECT) || entity.isPotionActive(GFRegistry.PETRIFIED_EFFECT);
  }
  
  /** @return whether the entity should have the Swine effect applied **/
  private static boolean isSwine(final LivingEntity livingEntity) {
    return livingEntity.isPotionActive(GFRegistry.SWINE_EFFECT);
  }
}
