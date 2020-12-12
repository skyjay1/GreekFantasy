package greekfantasy.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;

import greekfantasy.GFRegistry;
import greekfantasy.GFWorldGen;
import greekfantasy.GreekFantasy;
import greekfantasy.block.StatueBlock.StatueMaterial;
import greekfantasy.entity.CerastesEntity;
import greekfantasy.entity.DryadEntity;
import greekfantasy.entity.GeryonEntity;
import greekfantasy.entity.ShadeEntity;
import greekfantasy.network.SPanfluteSongPacket;
import greekfantasy.tileentity.StatueTileEntity;
import greekfantasy.util.PanfluteSong;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
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
  
  // This map tracks Palladium locations per chunk, per world
  private static Map<RegistryKey<World>, Map<ChunkPos, TimestampList<BlockPos>>> palladiumMap = new TreeMap<>();
  private static final TimestampList<BlockPos> EMPTY_TIMESTAMP_LIST = new TimestampList<>(0, new ArrayList<>());
  
  /**
   * Used to spawn a shade with the player's XP when they die.
   * @param event the death event
   **/
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onPlayerDeath(final LivingDeathEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && GreekFantasy.CONFIG.doesShadeSpawnOnDeath() && event.getEntityLiving() instanceof PlayerEntity) {
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
        shade.setOwnerUniqueId(PlayerEntity.getOfflineUUID(player.getDisplayName().getUnformattedComponentText()));
        shade.enablePersistence();
        player.getEntityWorld().addEntity(shade);
      }
    }
  }
  
  @SubscribeEvent
  public static void onCowDeath(final LivingDeathEvent event) {
    if(!event.isCanceled() && event.getEntityLiving().isServerWorld() && event.getEntityLiving() instanceof CowEntity) {
      // check if the cow was killed by a player and if geryon can spawn here
      final BlockPos deathPos = event.getEntityLiving().getPosition();
      if(event.getSource().getTrueSource() instanceof PlayerEntity && GeryonEntity.canGeryonSpawnOn(event.getEntityLiving().getEntityWorld(), deathPos)) {
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
   * Used to prevent players from using items while stunned.
   * @param event a PlayerInteractEvent or any of its children
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
   * Used to anger nearby dryads when the player breaks a log block that may be a tree
   * @param event the block break event
   **/
  @SubscribeEvent
  public static void onBreakLog(final BlockEvent.BreakEvent event) {
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
  }
  
  
  /**
   * Used to add prevent monsters from spawning near Palladium blocks
   * @param event the spawn event
   **/
  @SubscribeEvent
  public static void onLivingCheckSpawn(final LivingSpawnEvent.CheckSpawn event) {
    final int cRadius = GreekFantasy.CONFIG.getPalladiumChunkRange();
    final int cVertical = GreekFantasy.CONFIG.getPalladiumYRange() / 2; // divide by 2 to center on block
    if(cRadius > 0 && !event.getEntityLiving().getEntityWorld().isRemote() 
        && event.getWorld() instanceof World && event.getEntityLiving() instanceof IMob) {
      // check for nearby Statue Tile Entity
      final World world = (World)event.getWorld();
      final BlockPos blockPos = new BlockPos(event.getX(), event.getY(), event.getZ());
      final ChunkPos chunkPos = new ChunkPos(blockPos);
      ChunkPos cPos;
      for(int cX = -cRadius; cX <= cRadius; cX++) {
        for(int cZ = -cRadius; cZ <= cRadius; cZ++) {
          cPos = new ChunkPos(chunkPos.x + cX, chunkPos.z + cZ);
          if(event.getWorld().chunkExists(cPos.x, cPos.z) && !getPalladiumList(world, blockPos, cPos, cVertical).isEmpty()) {
            event.setResult(Result.DENY);
            return;
          }
        }
      }
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
   * Used to sync panflute songs from the server to each client
   * @param event the player login event
   **/
  @SubscribeEvent
  public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
    PlayerEntity player = event.getPlayer();
    if (player instanceof ServerPlayerEntity) {
      for(final Entry<ResourceLocation, Optional<PanfluteSong>> e : GreekFantasy.PROXY.PANFLUTE_SONGS.getEntries()) {
        GreekFantasy.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new SPanfluteSongPacket(e.getKey(), e.getValue().get()));
      }
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
  }
  
  private static boolean isStunned(final LivingEntity entity) {
    return (entity.getActivePotionEffect(GFRegistry.STUNNED_EFFECT) != null || entity.getActivePotionEffect(GFRegistry.PETRIFIED_EFFECT) != null);
  }
  
  private static List<BlockPos> getPalladiumList(final World world, final BlockPos spawnPos, final ChunkPos chunkPos, final int verticalRange) {
    final RegistryKey<World> dimension = world.getDimensionKey();
    palladiumMap.putIfAbsent(dimension, new HashMap<>());
    final TimestampList<BlockPos> timestampList = palladiumMap.get(dimension).getOrDefault(chunkPos, EMPTY_TIMESTAMP_LIST);
    // if the timestamp is too old or the map has not yet been filled, recalculate palladium positions
    if(timestampList.shouldUpdate(world.getServer().getServerTime()) || !palladiumMap.get(dimension).containsKey(chunkPos)) {
//      GreekFantasy.LOGGER.debug("Registering Palladium list for " + dimension.getLocation().toString() + " at " + chunkPos.toString());
      world.getServer().runAsync(() -> {
//        GreekFantasy.LOGGER.debug("Filling Palladium list for " + dimension.getLocation().toString() + " at " + chunkPos.toString());
        palladiumMap.get(dimension).put(chunkPos, timestampList.update(world.getServer().getServerTime(), fillPalladiumList(world, spawnPos, chunkPos, verticalRange)));
      });
    }
    return (palladiumMap.get(dimension).getOrDefault(chunkPos, EMPTY_TIMESTAMP_LIST)).list;
  }
  
  private static List<BlockPos> fillPalladiumList(final World world, final BlockPos spawnPos, final ChunkPos chunkPos, final int verticalRange) {
    // iterate through all tile entities in this chunk and fill a list with Palladium entries
    List<BlockPos> palladiumList = new ArrayList<>();
    Map<BlockPos, TileEntity> chunkTEMap = world.getChunk(chunkPos.x, chunkPos.z).getTileEntityMap();
    for(final Entry<BlockPos, TileEntity> e : chunkTEMap.entrySet()) {
      if(e.getValue() instanceof StatueTileEntity && ((StatueTileEntity)e.getValue()).getStatueMaterial() == StatueMaterial.WOOD && Math.abs(e.getKey().getY() - spawnPos.getY()) < verticalRange) {
        palladiumList.add(e.getKey());
      }
    }
    return palladiumList;
  }
  
  protected static class TimestampList<T> {
    protected long timestamp;
    protected final List<T> list = new ArrayList<>();
    
    protected TimestampList(final long serverTime, final List<T> listIn) {
      timestamp = serverTime;
      list.addAll(listIn);
    }
    
    protected boolean shouldUpdate(final long serverTime) {
      return serverTime - timestamp > GreekFantasy.CONFIG.getPalladiumRefreshInterval();
    }
    
    protected TimestampList<T> update(final long serverTime, final List<T> listIn) {
      if(shouldUpdate(serverTime)) {
        timestamp = serverTime;
        list.clear();
        list.addAll(listIn);
      }
      return this;
    }
  }
}
