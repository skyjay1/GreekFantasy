package greekfantasy;

import greekfantasy.entity.Arion;
import greekfantasy.entity.Cerastes;
import greekfantasy.entity.GoldenRam;
import greekfantasy.entity.Orthus;
import greekfantasy.entity.Palladium;
import greekfantasy.entity.Whirl;
import greekfantasy.entity.boss.Geryon;
import greekfantasy.entity.boss.GiantBoar;
import greekfantasy.entity.boss.NemeanLion;
import greekfantasy.entity.monster.Circe;
import greekfantasy.entity.monster.Shade;
import greekfantasy.integration.RGCompat;
import greekfantasy.item.HellenicArmorItem;
import greekfantasy.item.NemeanLionHideItem;
import greekfantasy.item.ThunderboltItem;
import greekfantasy.mob_effect.CurseOfCirceEffect;
import greekfantasy.network.SCurseOfCircePacket;
import greekfantasy.network.SQuestPacket;
import greekfantasy.network.SSongPacket;
import greekfantasy.util.SummonBossUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class GFEvents {

    public static final class ForgeHandler {

        public static final UUID STEP_HEIGHT_MODIFIER = UUID.fromString("3b9d697f-8823-4e0e-b704-be09f54712d7");
        /** Used in the client tick event to ensure items with Overstep provide a step height attribute bonus **/
        private static final AttributeModifier stepHeightModifier = new AttributeModifier(STEP_HEIGHT_MODIFIER, "Armor step height modifier", 0.62D, AttributeModifier.Operation.ADDITION);;

        /**
         * Used to spawn a shade with the player's XP when they die.
         *
         * @param event the death event
         **/
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onPlayerDeath(final LivingDeathEvent event) {
            if (!event.isCanceled() && !event.getEntity().level.isClientSide() && event.getEntity() instanceof Player player) {
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
            if (!event.isCanceled() && event.getEntity().isEffectiveAi() && event.getSource().getEntity() instanceof Player) {
                // check if the cow was killed by a player and if geryon can spawn here
                final BlockPos deathPos = event.getEntity().blockPosition();
                if (event.getEntity() instanceof Cow && Geryon.canGeryonSpawnOn(event.getEntity().level, deathPos)) {
                    // check for Geryon Head blocks nearby
                    final List<BlockPos> heads = new ArrayList<>();
                    final int r = 3;
                    BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                    for (int x = -r; x <= r; x++) {
                        for (int y = -2; y <= 2; y++) {
                            for (int z = -r; z <= r; z++) {
                                pos.setWithOffset(deathPos, x, y, z);
                                if (event.getEntity().level.getBlockState(pos).is(GFRegistry.BlockReg.GIGANTE_HEAD.get())) {
                                    heads.add(pos.immutable());
                                }
                                // if we found at least three heads, remove them and spawn a geryon
                                if (heads.size() >= 3) {
                                    heads.subList(0, 3).forEach(p -> event.getEntity().level.destroyBlock(p, false));
                                    final float yaw = Mth.wrapDegrees(event.getSource().getEntity().getYRot() + 180.0F);
                                    Geryon.spawnGeryon(event.getEntity().level, deathPos, yaw);
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
        public static void onLivingTick(final LivingEvent.LivingTickEvent event) {
            // only handle event on server
            if(event.getEntity().level.isClientSide()) {
                return;
            }

            // handle Prisoner of Hades mob effect
            final MobEffect prisonerOfHades = GFRegistry.MobEffectReg.PRISONER_OF_HADES.get();
            if(event.getEntity().hasEffect(prisonerOfHades)) {
                // remove when not in nether
                if (event.getEntity().level.dimension() != Level.NETHER
                        || (GreekFantasy.isRGLoaded() && event.getEntity() instanceof Player player
                            && RGCompat.getInstance().canRemovePrisonerEffect(player))) {
                    event.getEntity().removeEffect(prisonerOfHades);
                } else {
                    // set portal cooldown
                    event.getEntity().setPortalCooldown();
                }
            }

            // update silkstep enchantment
            if (GreekFantasy.CONFIG.isSilkstepEnabled()
                    && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.SILKSTEP.get(),
                        event.getEntity().getItemBySlot(EquipmentSlot.FEET)) > 0
                    && (!(event.getEntity() instanceof Player player && player.getAbilities().flying))
                    && event.getEntity().stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {
                // this variable will become true if the player is collided with a cobweb
                boolean cobweb = false;
                // check all blocks within player's bounding box
                AABB axisalignedbb = event.getEntity().getBoundingBox();
                BlockPos blockpos = new BlockPos(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
                BlockPos blockpos1 = new BlockPos(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
                BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
                entryloop:
                for (int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
                    for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                        for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                            blockpos$mutable.set(i, j, k);
                            // if the block is a cobweb, exit the loops and change the motion multiplier
                            if (event.getEntity().level.getBlockState(blockpos$mutable).is(Blocks.COBWEB)) {
                                cobweb = true;
                                break entryloop;
                            }
                        }
                    }
                }
                // actually reset the speed multiplier
                if (cobweb) {
                   event.getEntity().stuckSpeedMultiplier = Vec3.ZERO;
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

            // update pose when player is riding nemean lion
            final boolean isRidingLion = event.player.getVehicle() instanceof NemeanLion;
            final Pose currentPose = event.player.getForcedPose();
            // update the forced pose
            if (isRidingLion && currentPose != Pose.FALL_FLYING) {
                // apply the forced pose
                event.player.setForcedPose(Pose.FALL_FLYING);
                event.player.setPose(Pose.FALL_FLYING);
            } else if (!isRidingLion && Pose.FALL_FLYING == currentPose) {
                // clear the forced pose
                event.player.setForcedPose(null);
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
            if (GreekFantasy.CONFIG.isFlyingEnabled() && event.getEntity() instanceof Player player && player.level instanceof ServerLevel
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
            if (event.isCancelable() && event.getEntity().isAlive()
                    && (event.getEntity().hasEffect(GFRegistry.MobEffectReg.STUNNED.get()))
                        || event.getEntity().hasEffect(GFRegistry.MobEffectReg.PETRIFIED.get())) {
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
            if (event.isCancelable() && event.getEntity().isAlive()
                    && (event.getEntity().hasEffect(GFRegistry.MobEffectReg.STUNNED.get()))
                    || event.getEntity().hasEffect(GFRegistry.MobEffectReg.PETRIFIED.get())) {
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
            if (!event.getEntity().level.isClientSide()
                    && GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && event.getEntity() instanceof Mob mob
                    && event.getTarget() != mob.getLastHurtByMob()
                    && (mob.hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get())
                        || event.getTarget().hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()))) {
                // remove attack target
                mob.setTarget(null);
            }
            // check for stunned or petrified
            if (!event.getEntity().level.isClientSide()
                    && event.getEntity() instanceof Mob mob
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
        public static void onMobEffectStart(final MobEffectEvent.Added event) {
            if(!event.getEntity().level.isClientSide()
                    && GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && event.getEffectInstance() != null
                    && event.getEffectInstance().getEffect() == GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()
                    && event.getOldEffectInstance() == null) {
                // update health
                if(event.getOldEffectInstance() == null) {
                    float health = Mth.clamp(event.getEntity().getHealth(), 1.0F, event.getEntity().getMaxHealth() + (float) CurseOfCirceEffect.HEALTH_MODIFIER);
                    event.getEntity().setHealth(health);
                }
                // send packet
                GreekFantasy.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> event.getEntity()),
                        SCurseOfCircePacket.addEffect(event.getEntity().getId(), event.getEffectInstance().getDuration()));
            }
        }

        /**
         * Used to update client when Curse of Circe is removed
         * @param event the potion added event
         */
        @SubscribeEvent
        public static void onMobEffectRemove(final MobEffectEvent.Remove event) {
            if(!event.getEntity().level.isClientSide() && GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && event.getEffectInstance() != null
                    && event.getEffectInstance().getEffect() == GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()) {
                // send packet
                GreekFantasy.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> event.getEntity()),
                        SCurseOfCircePacket.removeEffect(event.getEntity().getId()));
            }
        }

        /**
         * Used to update client when Curse of Circe is expired
         * @param event the potion added event
         */
        @SubscribeEvent
        public static void onMobEffectExpire(final MobEffectEvent.Expired event) {
            if(!event.getEntity().level.isClientSide() && GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && event.getEffectInstance() != null
                    && event.getEffectInstance().getEffect() == GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get()) {
                // send packet
                GreekFantasy.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> event.getEntity()),
                        SCurseOfCircePacket.removeEffect(event.getEntity().getId()));
            }
        }

        /**
         * Checks if a Bronze block was placed and notifies SpawnBossUtil
         **/
        @SubscribeEvent
        public static void onEntityPlaceBlock(final BlockEvent.EntityPlaceEvent event) {
            // ensure the block matches
            if (!event.isCanceled() && event.getPlacedBlock().is(SummonBossUtil.BRONZE_BLOCK)
                    && event.getLevel() instanceof Level) {
                // delegate to SummonBossUtil
                SummonBossUtil.onPlaceBronzeBlock((Level) event.getLevel(), event.getPos(), event.getPlacedBlock(), event.getEntity());
            }
        }

        /**
         * Used to add a step height modifier to items with Overstep enchantment
         * @param event the attribute modifier event
         */
        @SubscribeEvent
        public static void onItemAttributeModifiers(final ItemAttributeModifierEvent event) {
            // determine if step height modifer should apply
            if(event.getSlotType() == EquipmentSlot.FEET && event.getItemStack().getEnchantmentLevel(GFRegistry.EnchantmentReg.OVERSTEP.get()) > 0) {
                event.addModifier(ForgeMod.STEP_HEIGHT_ADDITION.get(), stepHeightModifier);
            }
        }

        /**
         * Used to add AI to Minecraft entities when they are spawned.
         *
         * @param event the spawn event
         **/
        @SubscribeEvent
        public static void onEntityJoinWorld(final EntityJoinLevelEvent event) {
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
         * Used to add prevent monsters from spawning near Palladium blocks
         *
         * @param event the spawn event
         **/
        @SubscribeEvent
        public static void onLivingCheckSpawn(final LivingSpawnEvent.CheckSpawn event) {
            final int cRadius = GreekFantasy.CONFIG.getPalladiumChunkRange();
            final int cVertical = GreekFantasy.CONFIG.getPalladiumYRange() / 2; // divide by 2 to center on block
            if (GreekFantasy.CONFIG.isPalladiumEnabled() && !event.getEntity().level.isClientSide()
                    && (event.getSpawnReason() == MobSpawnType.NATURAL
                        || event.getSpawnReason() == MobSpawnType.REINFORCEMENT
                        || event.getSpawnReason() == MobSpawnType.PATROL
                        || event.getSpawnReason() == MobSpawnType.SPAWNER)
                    && event.getLevel() instanceof ServerLevel level
                    && event.getEntity() instanceof Enemy && event.getEntity().canChangeDimensions()) {
                // determine spawn area
                final BlockPos eventPos = new BlockPos(event.getX(), event.getY(), event.getZ());
                final ChunkPos eventChunkPos = new ChunkPos(eventPos);
                final ChunkPos minChunkPos = new ChunkPos(eventChunkPos.x - cRadius, eventChunkPos.z - cRadius);
                final ChunkPos maxChunkPos = new ChunkPos(eventChunkPos.x + cRadius, eventChunkPos.z + cRadius);
                final BlockPos minBlock = minChunkPos.getBlockAt(0, eventPos.getY() - cVertical, 0);
                final BlockPos maxBlock = maxChunkPos.getBlockAt(15, eventPos.getY() + cVertical, 15);
                final AABB aabb = new AABB(minBlock, maxBlock);

                // search each chunk in range for a palladium
                LevelEntityGetter<Entity> entityGetter = level.getEntities();
                entityGetter.get(EntityTypeTest.forClass(Palladium.class), aabb, e -> {
                    if(event.getResult() != Event.Result.DENY) {
                        event.setResult(Event.Result.DENY);
                    }
                });
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
            if (event.getEntity() != null && event.getEntity().getType() == EntityType.WITCH
                    && event.getLevel() instanceof Level
                    && (event.getLevel().getRandom().nextDouble() * 100.0D) < GreekFantasy.CONFIG.CIRCE_SPAWN_CHANCE.get()) {
                event.setCanceled(true);
                // spawn Circe instead of witch
                final Circe circe = GFRegistry.EntityReg.CIRCE.get().create((Level) event.getLevel());
                circe.moveTo(event.getX(), event.getY(), event.getZ(), 0, 0);
                event.getLevel().addFreshEntity(circe);
            }
        }

        /**
         * Used to replace ocelot with Nemean Lion when the former is struck by lightning
         * (while under the Strength potion effect)
         *
         * @param event the EntityStruckByLightning event
         */
        @SubscribeEvent
        public static void onEntityStruckByLightning(final EntityStruckByLightningEvent event) {
            if (event.getEntity() instanceof LivingEntity livingEntity && livingEntity.getType() == EntityType.OCELOT
                    && livingEntity.getEffect(MobEffects.DAMAGE_BOOST) != null
                    && livingEntity.level.getDifficulty() != Difficulty.PEACEFUL
                    && livingEntity.level.random.nextFloat() * 100.0F < GreekFantasy.CONFIG.NEMEAN_LION_LIGHTNING_CHANCE.get()) {
                // remove the entity and spawn a nemean lion
                NemeanLion lion = GFRegistry.EntityReg.NEMEAN_LION.get().create(event.getEntity().level);
                lion.copyPosition(event.getEntity());
                if (event.getEntity().hasCustomName()) {
                    lion.setCustomName(event.getEntity().getCustomName());
                    lion.setCustomNameVisible(event.getEntity().isCustomNameVisible());
                }
                lion.setPersistenceRequired();
                event.getEntity().level.addFreshEntity(lion);
                event.getEntity().discard();
            }
        }

        /**
         * Used to convert hoglins to giant boars.
         * Used to convert horses to arions.
         * Used to convert yellow sheep to golden rams.
         *
         * @param event the PlayerInteractEvent.EntityInteract event
         **/
        @SubscribeEvent
        public static void onPlayerInteract(final PlayerInteractEvent.EntityInteract event) {
            if(event.isCanceled() || !(event.getLevel() instanceof ServerLevel)) {
                return;
            }
            ServerLevel level = (ServerLevel) event.getLevel();
            // when player uses poisonous potato on adult hoglin while outside the nether
            if ((!GreekFantasy.CONFIG.GIANT_BOAR_NON_NETHER.get() || level.dimension() != Level.NETHER)
                    && event.getTarget().getType() == EntityType.HOGLIN
                    && event.getTarget() instanceof Hoglin hoglin
                    && event.getItemStack().is(GiantBoar.TRIGGER)) {
                if (!hoglin.isBaby()) {
                    // spawn giant boar and shrink the item stack
                    GiantBoar.spawnGiantBoar(level, hoglin);
                    if (!event.getEntity().isCreative()) {
                        event.getItemStack().shrink(1);
                    }
                }
            }
            // when player uses enchanted golden apple on adult horse
            if (event.getTarget().getType() == EntityType.HORSE
                    && event.getTarget() instanceof Horse horse
                    && event.getItemStack().is(Arion.TRIGGER)) {
                if (!horse.isBaby() && horse.isTamed()) {
                    // spawn Arion and shrink the item stack
                    Arion.spawnArion(level, event.getEntity(), horse);
                    if (!event.getEntity().isCreative()) {
                        event.getItemStack().shrink(1);
                    }
                }
            }
            // when player uses dragon breath on shearable yellow sheep
            if (event.getTarget().getType() == EntityType.SHEEP
                    && event.getTarget() instanceof Sheep sheep
                    && event.getItemStack().is(GoldenRam.TRIGGER)) {
                if (sheep.readyForShearing() && sheep.getColor() == DyeColor.YELLOW) {
                    // spawn Arion and shrink the item stack
                    GoldenRam.spawnGoldenRam(level, event.getEntity(), sheep);
                    if (!event.getEntity().isCreative()) {
                        event.getItemStack().shrink(1);
                    }
                }
            }
        }


        @SubscribeEvent
        public static void onPlayerStartUsingItem(final LivingEntityUseItemEvent.Start event) {
            if(!event.getEntity().level.isClientSide() && event.getEntity() instanceof ServerPlayer player
                    && !event.isCanceled() && event.getItem().is(Items.TRIDENT)
                    && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.LORD_OF_THE_SEA.get(), event.getItem()) > 0
                    && !player.getCooldowns().isOnCooldown(Items.TRIDENT)
                    && (!GreekFantasy.isRGLoaded() || RGCompat.getInstance().canUseLordOfTheSea(player))) {
                // cancel the event
                event.setCanceled(true);
                useLordOfTheSea(player, event.getItem());
            }
        }

        @SubscribeEvent
        public static void onPlayerRightClickItem(final PlayerInteractEvent.RightClickItem event) {
            if(!event.getEntity().level.isClientSide() && event.getEntity() instanceof ServerPlayer player
                    && !event.isCanceled() && event.getItemStack().is(Items.CLOCK)
                    && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.DAYBREAK.get(), event.getItemStack()) > 0
                    && player.level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
                    && player.level.getDayTime() % 24000L > 13000L
                    && (!GreekFantasy.isRGLoaded() || RGCompat.getInstance().canUseDaybreak(player))) {
                // cancel the event
                event.setCanceled(true);
                useDaybreak(player, event.getItemStack());
            }
        }

        /**
         * Used to sync datapack data from the server to each client
         *
         * @param event the player login event
         **/
        @SubscribeEvent
        public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            if (player instanceof ServerPlayer) {
                // sync songs
                GreekFantasy.SONGS.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SSongPacket(e.getKey(), e.getValue().get())));
                // sync quests
                GreekFantasy.QUESTS.getEntries().forEach(e -> GreekFantasy.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new SQuestPacket(e.getKey(), e.getValue().get())));
            }
            // remove this when not in beta
            // event.getEntity().displayClientMessage(Component.literal("You are using a beta version of Greek Fantasy - do not distribute.").withStyle(ChatFormatting.AQUA), false);
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

        private static void useLordOfTheSea(final ServerPlayer player, final ItemStack item) {
            final BlockHitResult raytrace = ThunderboltItem.raytraceFromEntity(player, 48.0F);
            // add a lightning bolt at the resulting position
            if (raytrace.getType() != HitResult.Type.MISS) {
                final Whirl whirl = GFRegistry.EntityReg.WHIRL.get().create(player.level);
                final BlockPos pos = new BlockPos(raytrace.getLocation());
                // make sure there is enough water here
                if (player.level.getFluidState(pos).is(FluidTags.WATER)
                        && player.level.getFluidState(pos.below((int) Math.ceil(whirl.getBbHeight()))).is(FluidTags.WATER)) {
                    // summon a powerful whirl with limited life and mob attracting turned on
                    whirl.moveTo(raytrace.getLocation().x(), raytrace.getLocation().y() - whirl.getBbHeight(), raytrace.getLocation().z(), 0, 0);
                    player.level.addFreshEntity(whirl);
                    whirl.setLimitedLife(GreekFantasy.CONFIG.LORD_OF_THE_SEA_WHIRL_LIFESPAN.get() * 20);
                    whirl.setAttractMobs(true);
                    whirl.playSound(SoundEvents.TRIDENT_THUNDER, 1.5F, 0.6F + whirl.getRandom().nextFloat() * 0.32F);
                    // summon a lightning bolt
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(player.level);
                    bolt.setSilent(true);
                    bolt.setPos(raytrace.getLocation().x(), raytrace.getLocation().y(), raytrace.getLocation().z());
                    bolt.setVisualOnly(true);
                    player.level.addFreshEntity(bolt);
                    // cooldown and item damage
                    player.getCooldowns().addCooldown(item.getItem(), 100);
                    if (!player.isCreative()) {
                        item.hurtAndBreak(25, player, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                    }
                }
            }
        }

        private static void useDaybreak(final ServerPlayer player, final ItemStack item) {
            final ServerLevel world = player.getLevel();
            long nextDay = world.getLevelData().getDayTime() + 24000L;
            world.setDayTime(nextDay - nextDay % 24000L);
            // break the item
            player.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            if (!player.isCreative()) {
                item.shrink(1);
            }
        }
    }

    public static final class ModHandler {

        @SubscribeEvent
        public static void onAddPackFinders(final AddPackFindersEvent event) {
            if(event.getPackType() == PackType.SERVER_DATA) {
                // register RPG Gods data pack
                if(GreekFantasy.isRGLoaded()) {
                    GreekFantasy.LOGGER.info("Greek Fantasy detected RPG Gods, registering data pack now");
                    registerAddon(event, "data_rpggods");
                }
            }
        }

        private static void registerAddon(final AddPackFindersEvent event, final String packName) {
            event.addRepositorySource((packConsumer, constructor) -> {
                Pack pack = Pack.create(GreekFantasy.MODID + ":" + packName, true, () -> {
                    Path path = ModList.get().getModFileById(GreekFantasy.MODID).getFile().findResource("/" + packName);
                    return new PathPackResources(packName, path);
                }, constructor, Pack.Position.TOP, PackSource.DEFAULT);

                if (pack != null) {
                    packConsumer.accept(pack);
                } else {
                    GreekFantasy.LOGGER.error(GreekFantasy.MODID + ": Failed to register data pack \"" + packName + "\"");
                }
            });
        }

    }
}
