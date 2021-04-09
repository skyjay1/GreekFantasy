package greekfantasy.event;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import greekfantasy.GFRegistry;
import greekfantasy.GFWorldGen;
import greekfantasy.GFWorldSavedData;
import greekfantasy.GreekFantasy;
import greekfantasy.deity.favor.FavorCommand;
import greekfantasy.deity.favor_effect.FavorConfiguration;
import greekfantasy.entity.ArionEntity;
import greekfantasy.entity.CerastesEntity;
import greekfantasy.entity.CirceEntity;
import greekfantasy.entity.DryadEntity;
import greekfantasy.entity.GeryonEntity;
import greekfantasy.entity.GiantBoarEntity;
import greekfantasy.entity.GoldenRamEntity;
import greekfantasy.entity.NemeanLionEntity;
import greekfantasy.entity.ShadeEntity;
import greekfantasy.item.AchillesArmorItem;
import greekfantasy.item.NemeanLionHideItem;
import greekfantasy.network.SDeityPacket;
import greekfantasy.network.SFavorConfigurationPacket;
import greekfantasy.network.SPanfluteSongPacket;
import greekfantasy.network.SSwineEffectPacket;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
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
   * Used to summon a Geryon when a cow is killed and other spawn conditions are met
   * @param event the living death event
   */
  @SubscribeEvent
  public static void onLivingDeath(final LivingDeathEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && event.getSource().getTrueSource() instanceof PlayerEntity) {
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
   * Used to set the player pose when the Swine effect is enabled;
   * Used to place Golden String at the player's position;
   * Used to set the player pose when they are riding a Nemean Lion;
   * @param event the PlayerTickEvent
   **/
  @SubscribeEvent
  public static void onLivingTick(final PlayerTickEvent event) {
    if((event.phase == TickEvent.Phase.END) && event.player.isAlive()) {
      // Update Nemean Lion riding pose
      final boolean isRidingLion = event.player.getRidingEntity() instanceof NemeanLionEntity;
      final Pose currentPose = event.player.getForcedPose();
      // update the forced pose
      if(isRidingLion && currentPose != Pose.FALL_FLYING) {
        // apply the forced pose
        event.player.setForcedPose(Pose.FALL_FLYING);
        event.player.setPose(Pose.FALL_FLYING);
      } else if(!isRidingLion && Pose.FALL_FLYING == currentPose) {
        // clear the forced pose
        event.player.setForcedPose(null);
      }
      
      // update Swine pose and armor
      if(GreekFantasy.CONFIG.isSwineEnabled()) {
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
          event.player.setPose(Pose.FALL_FLYING);
        } else if(!isSwine && Pose.FALL_FLYING == forcedPose) {
          // clear the forced pose
          event.player.setForcedPose(null);
        }
      }
      
      // place golden string if player is holding golden string
      // TODO also check if player is in a maze structure
      if(event.player.ticksExisted % 4 == 0 && (event.player.getHeldItemMainhand().getItem() == GFRegistry.GOLDEN_BALL || event.player.getHeldItemOffhand().getItem() == GFRegistry.GOLDEN_BALL)) {
        BlockPos pos = event.player.getPosition();
        BlockState current = event.player.getEntityWorld().getBlockState(pos);
        BlockState string = GFRegistry.GOLDEN_STRING_BLOCK.getDefaultState().with(BlockStateProperties.WATERLOGGED, current.getFluidState().getFluid().isIn(FluidTags.WATER));
        boolean replaceable = current.getMaterial() == Material.AIR || current.getMaterial() == Material.WATER;
        if(replaceable && current.getBlock() != GFRegistry.GOLDEN_STRING_BLOCK 
            && string.isValidPosition(event.player.getEntityWorld(), pos)) {
          event.player.getEntityWorld().setBlockState(pos, string, 3);
        }
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
    // send swine effect packet
    if (!event.getEntityLiving().getEntityWorld().isRemote() && GreekFantasy.CONFIG.isSwineEnabled() 
        && event.getPotionEffect().getPotion() == GFRegistry.SWINE_EFFECT
        && GreekFantasy.CONFIG.canSwineApply(event.getEntityLiving().getType().getRegistryName().toString())) {
      final int id = event.getEntityLiving().getEntityId();
      GreekFantasy.CHANNEL.send(PacketDistributor.ALL.noArg(), new SSwineEffectPacket(id, event.getPotionEffect().getDuration()));
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
   * Used to prevent projectile damage when wearing certain armors
   * @param event the living attack event
   **/
  @SubscribeEvent
  public static void onProjectileImpact(final ProjectileImpactEvent.Arrow event) {
    AbstractArrowEntity arrow = event.getArrow();
    if(!event.isCanceled() && !arrow.getEntityWorld().isRemote() && 
        event.getRayTraceResult().getType() == RayTraceResult.Type.ENTITY) {
      Entity entity = ((EntityRayTraceResult)event.getRayTraceResult()).getEntity();
      if(entity instanceof LivingEntity) {
        // if the projectile hit the legs/feet while wearing any achilles armor, multiply the damage
        int achillesArmor = countAchillesArmor((LivingEntity)entity);
        float critChance = -1;
        float immuneChance = -1;
        // if wearing nemean lion hide, determine arrow immunity chance
        if(isWearingNemeanHide((LivingEntity)entity)) {
          immuneChance = NemeanLionHideItem.getProjectileImmunityChance();
        }
        // if wearing achilles armmor, determine max arrow immunity chance
        if(achillesArmor > 0) {
          immuneChance = Math.max(immuneChance, AchillesArmorItem.getProjectileImmunityChance(achillesArmor));
          // determine crit chance
          if((arrow.getPosY() - arrow.getHeight() * 0.5D) < (entity.getPosY() + entity.getHeight() * 0.24D)) {
            critChance = AchillesArmorItem.getProjectileCritChance(achillesArmor);
          }
        }
        // if wearing achilles armor, check crit chance
        if(critChance > 0 && (Math.random() < critChance)) {
          arrow.setDamage(arrow.getDamage() * (2.0D + 0.5D * achillesArmor));
        } else if(immuneChance > 0 && Math.random() < immuneChance) {
          // otherwise, cancel the event
          event.setCanceled(true);
          // "bounce" the projectile
          arrow.setDamage(0.0D);
          arrow.setMotion(arrow.getMotion().mul(-0.45D, 0.65D, -0.45D));
        }
      }
    }
  }
  
  /**
   * Used to prevent players from using items while stunned.
   * @param event the living attack event
   **/
  @SubscribeEvent
  public static void onPlayerAttack(final AttackEntityEvent event) {
    if(GreekFantasy.CONFIG.doesStunPreventUse() && event.getPlayer().isAlive() && isStunned(event.getPlayer())) {
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
  public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
    // attempt to add rabbit-flees-cerastes goal
    if(event.getEntity() != null && event.getEntity().getType() == EntityType.RABBIT && !event.getEntity().getEntityWorld().isRemote()) {
      final RabbitEntity rabbit = (RabbitEntity) event.getEntity();
      if(rabbit.getRabbitType() != 99) {
        rabbit.goalSelector.addGoal(4, new AvoidEntityGoal<>(rabbit, CerastesEntity.class, e -> !((CerastesEntity)e).isHiding(), 6.0F, 2.2D, 2.2D, EntityPredicates.CAN_AI_TARGET::test));        
      }
    }
  }
  
  /**
   * Used to sometimes replace Witch with Circe when a witch is spawned.
   * Used to sometimes replace Sheep with Golden Ram when a yellow sheep is spawned.
   * @param event
   */
  @SubscribeEvent
  public static void onEntitySpawn(final LivingSpawnEvent.SpecialSpawn event) {
    // check if the entity is a witch
    if(event.getEntity() != null && event.getEntity().getType() == EntityType.WITCH && (event.getWorld().getRandom().nextDouble() * 100.0D) < GreekFantasy.CONFIG.getCirceChance()
        && event.getWorld() instanceof World) {
      event.setCanceled(true);
      // spawn Circe instead of witch
      final CirceEntity circe = GFRegistry.CIRCE_ENTITY.create((World)event.getWorld());
      circe.setLocationAndAngles(event.getX(), event.getY(), event.getZ(), 0, 0);
      event.getWorld().addEntity(circe);
    }
    // check if the entity is a sheep
    if(event.getEntity() != null && event.getEntity().getType() == EntityType.SHEEP && event.getWorld() instanceof World) {
      // check if the sheep has yellow wool
      SheepEntity sheep = (SheepEntity)event.getEntity();
      if(sheep.getFleeceColor() == DyeColor.YELLOW && (event.getWorld().getRandom().nextDouble() * 100.0D) < GreekFantasy.CONFIG.getGoldenRamChance()) {
        // spawn Golden Ram instead of sheep
        event.setCanceled(true);
        final GoldenRamEntity ram = GFRegistry.GOLDEN_RAM_ENTITY.create((World)event.getWorld());
        ram.setLocationAndAngles(event.getX(), event.getY(), event.getZ(), 0, 0);
        event.getWorld().addEntity(ram);
      }
    }
  }
  
  /**
   * Used to replace ocelot with Nemean Lion when the former is struck by lightning
   * (while under the Strength potion effect)
   * @param event the EntityStruckByLightning event
   */
  @SubscribeEvent
  public static void onEntityStruckByLightning(final EntityStruckByLightningEvent event) {
    if(event.getEntity() instanceof LivingEntity && event.getEntity().getType() == EntityType.OCELOT 
        && ((LivingEntity)event.getEntity()).getActivePotionEffect(Effects.STRENGTH) != null
        && event.getEntity().world.getDifficulty() != Difficulty.PEACEFUL 
        && event.getEntity().world.rand.nextInt(100) < GreekFantasy.CONFIG.getLightningLionChance()) {
      // remove the entity and spawn a nemean lion
      NemeanLionEntity lion = GFRegistry.NEMEAN_LION_ENTITY.create(event.getEntity().world);
      lion.copyLocationAndAnglesFrom(event.getEntity());
      if(event.getEntity().hasCustomName()) {
        lion.setCustomName(event.getEntity().getCustomName());
        lion.setCustomNameVisible(event.getEntity().isCustomNameVisible());
      }
      lion.enablePersistence();
      event.getEntity().getEntityWorld().addEntity(lion);
      event.getEntity().remove();
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
      final GFWorldSavedData data = GFWorldSavedData.getOrCreate(world);
      ChunkPos cPos;
      // search each chunk in a square radius centered on this chunk
      for(int cX = -cRadius; cX <= cRadius; cX++) {
        for(int cZ = -cRadius; cZ <= cRadius; cZ++) {
          cPos = new ChunkPos(chunkPos.x + cX, chunkPos.z + cZ);
          if(event.getWorld().chunkExists(cPos.x, cPos.z)) {
            // check each position to see if it's valid and within range
            for(final BlockPos p : data.getPalladium(world, cPos)) {
              if(!GFWorldSavedData.validatePalladium(world, p)) {
                data.removePalladium(cPos, p);
              } else if(Math.abs(p.getY() - blockPos.getY()) < cVertical) {
                // the position is preventing spawn, set result to DENY
                event.setResult(Result.DENY);
                return;
              }
            }
            // return;
          }
        }
      }
    }
  }
  
  /**
   * Used to prevent certain mobs from attacking players when either the player
   * or the mob are under the swine effect
   * @param event the living target event
   **/
  @SubscribeEvent
  public static void onLivingTarget(final LivingSetAttackTargetEvent event) {
    if(!event.getEntityLiving().getEntityWorld().isRemote() && event.getEntityLiving() instanceof MobEntity
        && event.getTarget() instanceof LivingEntity
        && GreekFantasy.CONFIG.isSwineEnabled() && GreekFantasy.CONFIG.doesSwinePreventTarget() 
        && (isSwine(event.getEntityLiving()) || isSwine(event.getTarget()))) {
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
      GreekFantasy.PROXY.FAVOR_CONFIGURATION.get(FavorConfiguration.NAME).ifPresent(f -> GreekFantasy.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SFavorConfigurationPacket(f)));
    }
  }
  
  /**
   * Used to sync datapack info when resources are reloaded
   * @param event the reload listener event
   **/
  @SubscribeEvent
  public static void onAddReloadListeners(final AddReloadListenerEvent event) {
    GreekFantasy.LOGGER.debug("onAddReloadListeners");
    event.addListener(GreekFantasy.PROXY.PANFLUTE_SONGS);
    event.addListener(GreekFantasy.PROXY.DEITY);
    event.addListener(GreekFantasy.PROXY.FAVOR_CONFIGURATION);
  }
  
  /**
   * Used to add custom commands
   * @param event the command registry event
   */
  @SubscribeEvent
  public static void onAddCommands(final RegisterCommandsEvent event) {
    FavorCommand.register(event.getDispatcher());
  }
  
  /** @return whether the living entity is wearing the Nemean Hide **/
  private static boolean isWearingNemeanHide(final LivingEntity livingEntity) {
    return livingEntity.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == GFRegistry.NEMEAN_LION_HIDE;
  }
  
  /** @return whether the living entity is wearing Achilles Armor **/
  private static int countAchillesArmor(final LivingEntity livingEntity) {
    int count = 0;
    for(final ItemStack stack : livingEntity.getArmorInventoryList()) {
      if(stack.getItem() instanceof AchillesArmorItem) {
        count++;
      }
    }
    return count;
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
