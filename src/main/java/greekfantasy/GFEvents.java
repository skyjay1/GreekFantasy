package greekfantasy;

import greekfantasy.entity.Arion;
import greekfantasy.entity.Cerastes;
import greekfantasy.entity.Orthus;
import greekfantasy.entity.boss.Geryon;
import greekfantasy.entity.boss.GiantBoar;
import greekfantasy.entity.monster.Circe;
import greekfantasy.entity.monster.Shade;
import greekfantasy.integration.RGCompat;
import greekfantasy.item.HellenicArmorItem;
import greekfantasy.item.NemeanLionHideItem;
import greekfantasy.mob_effect.CurseOfCirceEffect;
import greekfantasy.network.SCurseOfCircePacket;
import greekfantasy.network.SQuestPacket;
import greekfantasy.network.SSongPacket;
import greekfantasy.util.SummonBossUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class GFEvents {

    public static final class ForgeHandler {

        /**
         * Used to spawn a shade with the player's XP when they die.
         *
         * @param event the death event
         **/
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onPlayerDeath(final LivingDeathEvent event) {
            if (!event.isCanceled() && !event.getEntityLiving().level.isClientSide() && event.getEntityLiving() instanceof Player player) {
                // attempt to spawn a shade
                if (!player.isSpectator() && player.experienceLevel > 3
                        && !player.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)
                        && player.getRandom().nextFloat() * 100.0F < GreekFantasy.CONFIG.SHADE_SPAWN_CHANCE.get()) {
                    // save XP value
                    int xp = player.totalExperience;
                    // remove XP from player
                    player.giveExperienceLevels(-(player.experienceLevel + 1));
                    // give XP to shade and spawn into world
                    final Shade shade = GFRegistry.EntityReg.SHADE.get().create(player.level);
                    shade.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
                    shade.setStoredXP(Mth.floor(xp * 0.9F));
                    shade.setOwnerUUID(player.getUUID());
                    shade.setPersistenceRequired();
                    player.level.addFreshEntity(shade);
                }
            }
        }

        /**
         * Used to summon a Geryon when a cow is killed and other spawn conditions are met
         *
         * @param event the living death event
         */
        @SubscribeEvent
        public static void onLivingDeath(final LivingDeathEvent event) {
            if (!event.isCanceled() && event.getEntityLiving().isEffectiveAi() && event.getSource().getEntity() instanceof Player) {
                // check if the cow was killed by a player and if geryon can spawn here
                final BlockPos deathPos = event.getEntityLiving().blockPosition();
                if (event.getEntityLiving() instanceof Cow && Geryon.canGeryonSpawnOn(event.getEntityLiving().level, deathPos)) {
                    // check for Geryon Head blocks nearby
                    final List<BlockPos> heads = new ArrayList<>();
                    final int r = 3;
                    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                    for (int x = -r; x <= r; x++) {
                        for (int y = -2; y <= 2; y++) {
                            for (int z = -r; z <= r; z++) {
                                pos.setWithOffset(deathPos, x, y, z);
                                if (event.getEntityLiving().level.getBlockState(pos).is(GFRegistry.BlockReg.GIGANTE_HEAD.get())) {
                                    heads.add(pos.immutable());
                                }
                                // if we found at least three heads, remove them and spawn a geryon
                                if (heads.size() >= 3) {
                                    heads.subList(0, 3).forEach(p -> event.getEntityLiving().level.destroyBlock(p, false));
                                    final float yaw = Mth.wrapDegrees(event.getSource().getEntity().getYRot() + 180.0F);
                                    Geryon.spawnGeryon(event.getEntityLiving().level, deathPos, yaw);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Used to prevent or increase projectile damage when wearing certain armor
         * @param event the projectile impact event
         */
        @SubscribeEvent
        public static void onProjectileImpact(final ProjectileImpactEvent event) {
            if(event.getRayTraceResult().getType() == HitResult.Type.ENTITY
                    && event.getRayTraceResult() instanceof EntityHitResult entityHitResult
                    && entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
                // determine dot product between entity and projectile
                final double dot = getDotProduct(livingEntity, event.getProjectile(), true);
                final int achillesCount = HellenicArmorItem.getAchillesCount(livingEntity);
                // determine if the entity is wearing armor and immune to projectiles
                if(achillesCount > 0 && HellenicArmorItem.isImmune(livingEntity, event.getProjectile(), dot, achillesCount)) {
                    // reflect the projectile motion
                    event.getProjectile().setDeltaMovement(event.getProjectile().getDeltaMovement().multiply(-1.0D, 1.0D, -1.0D));
                    // cancel the event
                    event.setCanceled(true);
                    return;
                }
                // determine if entity is wearing armor and weak to arrow projectiles
                if(achillesCount > 0 && event.getProjectile() instanceof AbstractArrow arrow
                        && HellenicArmorItem.isCritical(livingEntity, arrow, dot, achillesCount)) {
                    // double the damage of the projectile
                    arrow.setBaseDamage(arrow.getBaseDamage() * (2.0D + 0.5D * achillesCount));
                    event.setCanceled(false);
                    return;
                }
                // determine if the entity is wearing nemean lion hide and immune to projectile
                if(achillesCount == 0 && livingEntity.getItemBySlot(EquipmentSlot.HEAD).is(GFRegistry.ItemReg.NEMEAN_LION_HIDE.get())
                        && NemeanLionHideItem.isImmune(livingEntity, event.getProjectile(), dot)) {
                    // reflect the projectile motion
                    event.getProjectile().setDeltaMovement(event.getProjectile().getDeltaMovement().multiply(-1.0D, 1.0D, -1.0D));
                    // cancel the event
                    event.setCanceled(true);
                    return;
                }
            }
        }

        /**
         * @param first the first entity
         * @param second the second entity
         * @param horizontalOnly true if the dot product should only account for horizontal facing
         * @return the dot product  between the facing directions of two entities
         */
        private static double getDotProduct(final Entity first, final Entity second, final boolean horizontalOnly) {
            Vec3 firstFacing = Vec3.directionFromRotation(first.getXRot(), first.getYRot());
            Vec3 secondFacing = Vec3.directionFromRotation(second.getXRot(), horizontalOnly ? first.getYRot() : second.getYRot());
            return secondFacing.dot(firstFacing);
        }

        /**
         * Used to handle Prisoner of Hades effect
         * (updating portal cooldown and removing when out of the nether)
         * @param event
         */
        @SubscribeEvent
        public static void onLivingTick(final LivingEvent.LivingUpdateEvent event) {
            // only handle event on server
            if(event.getEntityLiving().level.isClientSide()) {
                return;
            }

            // handle Prisoner of Hades mob effect
            final MobEffect prisonerOfHades = GFRegistry.MobEffectReg.PRISONER_OF_HADES.get();
            if(event.getEntityLiving().hasEffect(prisonerOfHades)) {
                // remove when not in nether
                if (event.getEntityLiving().level.dimension() != Level.NETHER
                        || (GreekFantasy.isRGLoaded() && event.getEntityLiving() instanceof Player player
                            && RGCompat.getInstance().canRemovePrisonerEffect(player))) {
                    event.getEntityLiving().removeEffect(prisonerOfHades);
                } else {
                    // set portal cooldown
                    event.getEntityLiving().setPortalCooldown();
                }
            }

            // update silkstep enchantment
            if (GreekFantasy.CONFIG.isSilkstepEnabled()
                    && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.SILKSTEP.get(),
                        event.getEntityLiving().getItemBySlot(EquipmentSlot.FEET)) > 0
                    && (!(event.getEntityLiving() instanceof Player player && player.getAbilities().flying))
                    && event.getEntityLiving().stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {
                // this variable will become true if the player is collided with a cobweb
                boolean cobweb = false;
                // check all blocks within player's bounding box
                AABB axisalignedbb = event.getEntityLiving().getBoundingBox();
                BlockPos blockpos = new BlockPos(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
                BlockPos blockpos1 = new BlockPos(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
                BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
                entryloop:
                for (int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
                    for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                        for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                            blockpos$mutable.set(i, j, k);
                            // if the block is a cobweb, exit the loops and change the motion multiplier
                            if (event.getEntityLiving().level.getBlockState(blockpos$mutable).is(Blocks.COBWEB)) {
                                cobweb = true;
                                break entryloop;
                            }
                        }
                    }
                }
                // actually reset the speed multiplier
                if (cobweb) {
                   event.getEntityLiving().stuckSpeedMultiplier = Vec3.ZERO;
                }
            }
        }

        /**
         * Used to change player pose when under Curse of Circe.
         * @param event
         */
        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            if(event.phase != TickEvent.Phase.START || !event.player.isAlive()) {
                return;
            }

            // update pose when player is under curse of circe
            if(GreekFantasy.CONFIG.isCurseOfCirceEnabled()) {
                final boolean curseOfCirce = event.player.hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get());
                final Pose forcedPose = event.player.getForcedPose();
                // update the forced pose
                if (curseOfCirce && forcedPose != Pose.FALL_FLYING) {
                    // apply the forced pose
                    event.player.setForcedPose(Pose.FALL_FLYING);
                    event.player.setPose(Pose.FALL_FLYING);
                } else if (!curseOfCirce && Pose.FALL_FLYING == forcedPose) {
                    // clear the forced pose
                    event.player.setForcedPose(null);
                }
            }

            // every few ticks, ensure that flying players can still fly
            if (GreekFantasy.CONFIG.isFlyingEnabled() && event.player.level instanceof ServerLevel &&
                    !event.player.isCreative() && !event.player.isSpectator() && event.player.tickCount > 10
                    && event.player.level.getGameTime() % 11 == 0) {
                // load saved data
                final GFSavedData data = GFSavedData.getOrCreate((ServerLevel) event.player.level);
                // remove flying players who do not meet the conditions
                if (data.hasFlyingPlayer(event.player) && !GFSavedData.validatePlayer(event.player)) {
                    data.removeFlyingPlayer(event.player);
                }
            }
        }

        /**
         * Used to enable flying players when they equip enchanted winged sandals.
         *
         * @param event the equipment change event
         */
        @SubscribeEvent
        public static void onChangeEquipment(final LivingEquipmentChangeEvent event) {
            // Check which equipment was changed and if it is a player
            if (GreekFantasy.CONFIG.isFlyingEnabled() && event.getEntityLiving() instanceof Player player && player.level instanceof ServerLevel
                    && event.getSlot() == EquipmentSlot.FEET && event.getTo().is(GFRegistry.ItemReg.WINGED_SANDALS.get())) {
                GFSavedData data = GFSavedData.getOrCreate((ServerLevel) player.level);
                // ensure player meets conditions before enabling flight
                if (GFSavedData.validatePlayer(player)) {
                    data.addFlyingPlayer(player);
                }
            }
        }

        /**
         * Used to prevent players from using items while stunned.
         * Note: PlayerInteractEvent has several children but
         * we receive and cancel all of the ones that can be cancelled
         *
         * @param event a PlayerInteractEvent or any of its children
         **/
        @SubscribeEvent
        public static void onPlayerInteract(final PlayerInteractEvent event) {
            if (event.isCancelable() && event.getPlayer().isAlive()
                    && (event.getPlayer().hasEffect(GFRegistry.MobEffectReg.STUNNED.get()))
                        || event.getPlayer().hasEffect(GFRegistry.MobEffectReg.PETRIFIED.get())) {
                // cancel the event
                event.setCanceled(true);
            }
        }

        /**
         * Used to prevent players from using items while stunned.
         *
         * @param event the living attack event
         **/
        @SubscribeEvent
        public static void onPlayerAttack(final AttackEntityEvent event) {
            if (event.isCancelable() && event.getPlayer().isAlive()
                    && (event.getPlayer().hasEffect(GFRegistry.MobEffectReg.STUNNED.get()))
                    || event.getPlayer().hasEffect(GFRegistry.MobEffectReg.PETRIFIED.get())) {
                // cancel the event
                event.setCanceled(true);
            }
        }

        /**
         * Used to prevent mobs from attacking players when either the player
         * or the mob are under Curse of Circe, Stunned, or Petrified
         *
         * @param event the living target event
         **/
        @SubscribeEvent
        public static void onLivingTarget(final LivingSetAttackTargetEvent event) {
            if(null == event.getTarget()) {
                return;
            }
            // check for curse of circe
            if (!event.getEntityLiving().level.isClientSide()
                    && GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && event.getEntityLiving() instanceof Mob mob
                    && event.getTarget() != mob.getLastHurtByMob()
                    && (mob.hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get())
                        || event.getTarget().hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()))) {
                // remove attack target
                mob.setTarget(null);
            }
            // check for stunned or petrified
            if (!event.getEntityLiving().level.isClientSide()
                    && event.getEntityLiving() instanceof Mob mob
                    && (mob.hasEffect(GFRegistry.MobEffectReg.STUNNED.get())
                    || mob.hasEffect(GFRegistry.MobEffectReg.PETRIFIED.get()))) {
                // remove attack target
                mob.setTarget(null);
            }
        }

        /**
         * Used to update client when Curse of Circe is applied
         * @param event the potion added event
         */
        @SubscribeEvent
        public static void onMobEffectStart(final PotionEvent.PotionAddedEvent event) {
            if(!event.getEntityLiving().level.isClientSide()
                    && GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && event.getPotionEffect().getEffect() == GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()
                    && event.getOldPotionEffect() == null) {
                // update health
                if(event.getOldPotionEffect() == null) {
                    float health = Mth.clamp(event.getEntityLiving().getHealth(), 1.0F, event.getEntityLiving().getMaxHealth() + (float) CurseOfCirceEffect.HEALTH_MODIFIER);
                    event.getEntityLiving().setHealth(health);
                }
                // send packet
                GreekFantasy.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> event.getEntityLiving()),
                        SCurseOfCircePacket.addEffect(event.getEntityLiving().getId(), event.getPotionEffect().getDuration()));
            }
        }

        /**
         * Used to update client when Curse of Circe is removed
         * @param event the potion added event
         */
        @SubscribeEvent
        public static void onMobEffectRemove(final PotionEvent.PotionRemoveEvent event) {
            if(!event.getEntityLiving().level.isClientSide() && GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && event.getPotionEffect().getEffect() == GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()) {
                // send packet
                GreekFantasy.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> event.getEntityLiving()),
                        SCurseOfCircePacket.removeEffect(event.getEntityLiving().getId()));
            }
        }

        /**
         * Used to update client when Curse of Circe is expired
         * @param event the potion added event
         */
        @SubscribeEvent
        public static void onMobEffectExpire(final PotionEvent.PotionExpiryEvent event) {
            if(!event.getEntityLiving().level.isClientSide() && GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && event.getPotionEffect().getEffect() == GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()) {
                // send packet
                GreekFantasy.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> event.getEntityLiving()),
                        SCurseOfCircePacket.removeEffect(event.getEntityLiving().getId()));
            }
        }

        /**
         * Checks if a Bronze block was placed and notifies SpawnBossUtil
         **/
        @SubscribeEvent
        public static void onEntityPlaceBlock(final BlockEvent.EntityPlaceEvent event) {
            // ensure the block matches
            if (!event.isCanceled() && event.getPlacedBlock().is(SummonBossUtil.BRONZE_BLOCK)
                    && event.getWorld() instanceof Level) {
                // delegate to SummonBossUtil
                SummonBossUtil.onPlaceBronzeBlock((Level) event.getWorld(), event.getPos(), event.getPlacedBlock(), event.getEntity());
            }
        }

        /**
         * Used to add AI to Minecraft entities when they are spawned.
         *
         * @param event the spawn event
         **/
        @SubscribeEvent
        public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
            if(event.getEntity() instanceof PathfinderMob mob && !event.getEntity().level.isClientSide()) {
                // add avoid orthus goal to wither skeleton
                if(mob.getType() == EntityType.WITHER_SKELETON) {
                    mob.goalSelector.addGoal(3, new AvoidEntityGoal<>(mob, Orthus.class, 6.0F, 1.0D, 1.2D));
                }
                // add avoid cerastes goal to rabbits
                if(mob.getType() == EntityType.RABBIT && ((Rabbit)event.getEntity()).getRabbitType() != 99) {
                    mob.goalSelector.addGoal(3, new AvoidEntityGoal<>(mob, Cerastes.class, 6.0F, 1.0D, 1.2D,
                            e -> e instanceof Cerastes cerastes && !cerastes.isHiding()));
                }
            }
        }

        /**
         * Used to sometimes replace Witch with Circe when a witch is spawned.
         * Used to sometimes replace Sheep with Golden Ram when a yellow sheep is spawned.
         *
         * @param event the LivingSpawnEvent.SpecialSpawn
         */
        @SubscribeEvent
        public static void onEntitySpawn(final LivingSpawnEvent.SpecialSpawn event) {
            // check if the entity is a witch
            if (event.getEntity() != null && event.getEntity().getType() == EntityType.WITCH &&
                    (event.getWorld().getRandom().nextDouble() * 100.0D) < GreekFantasy.CONFIG.CIRCE_SPAWN_CHANCE.get()
                    && event.getWorld() instanceof Level) {
                event.setCanceled(true);
                // spawn Circe instead of witch
                final Circe circe = GFRegistry.EntityReg.CIRCE.get().create((Level) event.getWorld());
                circe.moveTo(event.getX(), event.getY(), event.getZ(), 0, 0);
                event.getWorld().addFreshEntity(circe);
            }
            // check if the entity is a sheep
           /* if (event.getEntity() != null && event.getEntity().getType() == EntityType.SHEEP && event.getWorld() instanceof World) {
                // check if the sheep has yellow wool
                SheepEntity sheep = (SheepEntity) event.getEntity();
                if (sheep.getColor() == DyeColor.YELLOW && (event.getWorld().getRandom().nextDouble() * 100.0D) < GreekFantasy.CONFIG.getGoldenRamChance()) {
                    // spawn Golden Ram instead of sheep
                    event.setCanceled(true);
                    final GoldenRamEntity ram = GFRegistry.EntityReg.GOLDEN_RAM_ENTITY.create((World) event.getWorld());
                    ram.moveTo(event.getX(), event.getY(), event.getZ(), 0, 0);
                    event.getWorld().addFreshEntity(ram);
                }
            }*/
        }

        /**
         * Used to replace ocelot with Nemean Lion when the former is struck by lightning
         * (while under the Strength potion effect)
         *
         * @param event the EntityStruckByLightning event
         */
        @SubscribeEvent
        public static void onEntityStruckByLightning(final EntityStruckByLightningEvent event) {
            /*if (event.getEntity() instanceof LivingEntity && event.getEntity().getType() == EntityType.OCELOT
                    && ((LivingEntity) event.getEntity()).getEffect(MobEffects.DAMAGE_BOOST) != null
                    && event.getEntity().level.getDifficulty() != Difficulty.PEACEFUL
                    && event.getEntity().level.random.nextFloat() * 100.0F < GreekFantasy.CONFIG.getLightningLionChance()) {
                // remove the entity and spawn a nemean lion
                NemeanLionEntity lion = GFRegistry.EntityReg.NEMEAN_LION_ENTITY.create(event.getEntity().level);
                lion.copyPosition(event.getEntity());
                if (event.getEntity().hasCustomName()) {
                    lion.setCustomName(event.getEntity().getCustomName());
                    lion.setCustomNameVisible(event.getEntity().isCustomNameVisible());
                }
                lion.setPersistenceRequired();
                event.getEntity().getCommandSenderWorld().addFreshEntity(lion);
                event.getEntity().remove();
            }*/
        }

        /**
         * Used to convert hoglins to giant boars.
         * Used to convert horses to arions.
         *
         * @param event the PlayerInteractEvent.EntityInteract event
         **/
        @SubscribeEvent
        public static void onPlayerInteract(final PlayerInteractEvent.EntityInteract event) {
            // when player uses poisonous potato on adult hoglin while outside the nether
            if (!event.isCanceled() && (!GreekFantasy.CONFIG.GIANT_BOAR_NON_NETHER.get() || event.getWorld().dimension() != Level.NETHER)
                    && event.getTarget().getType() == EntityType.HOGLIN
                    && event.getTarget() instanceof Hoglin hoglin && event.getWorld() instanceof ServerLevel
                    && event.getItemStack().is(GiantBoar.TRIGGER)) {
                if (!hoglin.isBaby()) {
                    // spawn giant boar and shrink the item stack
                    GiantBoar.spawnGiantBoar((ServerLevel) event.getWorld(), hoglin);
                    if (!event.getPlayer().isCreative()) {
                        event.getItemStack().shrink(1);
                    }
                }
            } else if (!event.isCanceled() && event.getTarget().getType() == EntityType.HORSE && event.getTarget() instanceof Horse horse
                    && event.getWorld() instanceof ServerLevel level && event.getItemStack().is(Arion.TRIGGER)) {
                // when player uses enchanted golden apple on adult horse
                if (!horse.isBaby() && horse.isTamed()) {
                    // spawn Arion and shrink the item stack
                    Arion.spawnArion(level, event.getPlayer(), horse);
                    if (!event.getPlayer().isCreative()) {
                        event.getItemStack().shrink(1);
                    }
                }
            }
        }

        /**
         * Used to sync datapack data from the server to each client
         *
         * @param event the player login event
         **/
        @SubscribeEvent
        public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getPlayer();
            if (player instanceof ServerPlayer) {
                // sync songs
                GreekFantasy.SONGS.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SSongPacket(e.getKey(), e.getValue().get())));
                // sync quests
                GreekFantasy.QUESTS.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SQuestPacket(e.getKey(), e.getValue().get())));
            }
        }

        /**
         * Used to sync datapack info when resources are reloaded
         *
         * @param event the reload listener event
         **/
        @SubscribeEvent
        public static void onAddReloadListeners(final AddReloadListenerEvent event) {
            event.addListener(GreekFantasy.SONGS);
            event.addListener(GreekFantasy.QUESTS);
        }

    }

    public static final class ModHandler {

    }
}
