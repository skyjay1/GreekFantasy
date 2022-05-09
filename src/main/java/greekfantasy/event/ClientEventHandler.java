package greekfantasy.event;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.particle.GorgonParticle;
import greekfantasy.client.render.AraRenderer;
import greekfantasy.client.render.ArachneRenderer;
import greekfantasy.client.render.ArionRenderer;
import greekfantasy.client.render.BabySpiderRenderer;
import greekfantasy.client.render.BronzeBullRenderer;
import greekfantasy.client.render.CentaurRenderer;
import greekfantasy.client.render.CerastesRenderer;
import greekfantasy.client.render.CerberusRenderer;
import greekfantasy.client.render.CharybdisRenderer;
import greekfantasy.client.render.CirceRenderer;
import greekfantasy.client.render.CretanMinotaurRenderer;
import greekfantasy.client.render.CurseRenderer;
import greekfantasy.client.render.CyclopesRenderer;
import greekfantasy.client.render.CyprianRenderer;
import greekfantasy.client.render.DrakainaRenderer;
import greekfantasy.client.render.DryadRenderer;
import greekfantasy.client.render.ElpisRenderer;
import greekfantasy.client.render.EmpusaRenderer;
import greekfantasy.client.render.EntityAsPigRenderer;
import greekfantasy.client.render.FuryRenderer;
import greekfantasy.client.render.GeryonRenderer;
import greekfantasy.client.render.GiantBoarRenderer;
import greekfantasy.client.render.GiganteRenderer;
import greekfantasy.client.render.GoldenRamRenderer;
import greekfantasy.client.render.GorgonRenderer;
import greekfantasy.client.render.HarpyRenderer;
import greekfantasy.client.render.HealingSpellRenderer;
import greekfantasy.client.render.HydraHeadRenderer;
import greekfantasy.client.render.HydraRenderer;
import greekfantasy.client.render.LampadRenderer;
import greekfantasy.client.render.MadCowRenderer;
import greekfantasy.client.render.MakhaiRenderer;
import greekfantasy.client.render.MinotaurRenderer;
import greekfantasy.client.render.NaiadRenderer;
import greekfantasy.client.render.NemeanLionRenderer;
import greekfantasy.client.render.OrthusRenderer;
import greekfantasy.client.render.PalladiumRenderer;
import greekfantasy.client.render.PegasusRenderer;
import greekfantasy.client.render.PigSpellRenderer;
import greekfantasy.client.render.PoisonSpitRenderer;
import greekfantasy.client.render.PythonRenderer;
import greekfantasy.client.render.SatyrRenderer;
import greekfantasy.client.render.ShadeRenderer;
import greekfantasy.client.render.SirenRenderer;
import greekfantasy.client.render.SpartiRenderer;
import greekfantasy.client.render.SpearRenderer;
import greekfantasy.client.render.TalosRenderer;
import greekfantasy.client.render.UnicornRenderer;
import greekfantasy.client.render.WhirlRenderer;
import greekfantasy.client.render.layer.NemeanLionHideLayer;
import greekfantasy.client.render.layer.PlayerSkyjayLayer;
import greekfantasy.client.render.tileentity.MobHeadTileEntityRenderer;
import greekfantasy.client.render.tileentity.VaseTileEntityRenderer;
import greekfantasy.entity.PegasusEntity;
import greekfantasy.entity.misc.DiscusEntity;
import greekfantasy.entity.misc.DragonToothEntity;
import greekfantasy.entity.misc.GreekFireEntity;
import greekfantasy.entity.misc.WebBallEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

public class ClientEventHandler {

    public static final class ModEvents {
        /**
         * Used to trigger several client-side registrations,
         * such as entity / tile-entity renders, container renders,
         * layers, etc.
         *
         * @param event the client setup event
         **/
        @SubscribeEvent
        public static void setupClient(final FMLClientSetupEvent event) {
            registerEntityRenders();
            registerTileEntityRenders();
            registerRenderLayers();
            registerModelProperties();
            registerPlayerLayers();
        }

        /**
         * Used to register custom particle renders.
         * Currently handles the Gorgon particle
         *
         * @param event the particle factory registry event
         **/
        @SubscribeEvent
        public static void registerParticleRenders(final ParticleFactoryRegisterEvent event) {
            GreekFantasy.LOGGER.debug("registerParticleRenders");
            final Minecraft mc = Minecraft.getInstance();
            mc.particleEngine.register(GFRegistry.ParticleReg.GORGON_PARTICLE, new GorgonParticle.Factory());
        }

        /**
         * Used to register block color handlers.
         * Currently used to color leaves.
         *
         * @param event the ColorHandlerEvent (Block)
         **/
        @SubscribeEvent
        public static void onBlockColors(final ColorHandlerEvent.Block event) {
            GreekFantasy.LOGGER.debug("registerBlockColors");
            event.getBlockColors().register(
                    (BlockState stateIn, IBlockDisplayReader world, BlockPos pos, int color) -> 0xD8E3D0, GFRegistry.BlockReg.OLIVE_LEAVES);
            event.getBlockColors().register(
                    (BlockState stateIn, IBlockDisplayReader world, BlockPos pos, int color) -> 0x80f66b, GFRegistry.BlockReg.GOLDEN_APPLE_LEAVES);
        }

        /**
         * Used to register item color handlers.
         * Currently used to color leaves.
         *
         * @param event the ColorHandlerEvent (Item)
         **/
        @SubscribeEvent
        public static void onItemColors(final ColorHandlerEvent.Item event) {
            GreekFantasy.LOGGER.debug("registerItemColors");
            event.getItemColors().register((ItemStack item, int i) -> 0xD8E3D0, GFRegistry.BlockReg.OLIVE_LEAVES);
            event.getItemColors().register((ItemStack item, int i) -> 0x80f66b, GFRegistry.BlockReg.GOLDEN_APPLE_LEAVES);
        }

        private static void registerEntityRenders() {
            GreekFantasy.LOGGER.debug("registerEntityRenders");
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.ARA_ENTITY, AraRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.ARACHNE_ENTITY, ArachneRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.ARION_ENTITY, ArionRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.BABY_SPIDER_ENTITY, BabySpiderRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.BRONZE_BULL_ENTITY, BronzeBullRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.TALOS_ENTITY, TalosRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.CENTAUR_ENTITY, CentaurRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.CERASTES_ENTITY, CerastesRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.CERBERUS_ENTITY, CerberusRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.CHARYBDIS_ENTITY, CharybdisRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.CIRCE_ENTITY, CirceRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.CRETAN_MINOTAUR_ENTITY, CretanMinotaurRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.CURSE_ENTITY, CurseRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.CYCLOPES_ENTITY, CyclopesRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.CYPRIAN_ENTITY, CyprianRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.DISCUS_ENTITY, m -> new SpriteRenderer<DiscusEntity>(m, Minecraft.getInstance().getItemRenderer()));
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.DRAGON_TOOTH_ENTITY, m -> new SpriteRenderer<DragonToothEntity>(m, Minecraft.getInstance().getItemRenderer()));
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.DRAKAINA_ENTITY, DrakainaRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.DRYAD_ENTITY, DryadRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.ELPIS_ENTITY, ElpisRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.EMPUSA_ENTITY, EmpusaRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.FURY_ENTITY, FuryRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.GERYON_ENTITY, GeryonRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.GIANT_BOAR_ENTITY, GiantBoarRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.GIGANTE_ENTITY, GiganteRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.GOLDEN_RAM_ENTITY, GoldenRamRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.GORGON_ENTITY, GorgonRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.GREEK_FIRE_ENTITY, m -> new SpriteRenderer<GreekFireEntity>(m, Minecraft.getInstance().getItemRenderer()));
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.HARPY_ENTITY, HarpyRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.HEALING_SPELL_ENTITY, HealingSpellRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.HYDRA_ENTITY, HydraRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.HYDRA_HEAD_ENTITY, HydraHeadRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.LAMPAD_ENTITY, LampadRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.MAD_COW_ENTITY, MadCowRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.MAKHAI_ENTITY, MakhaiRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.MINOTAUR_ENTITY, MinotaurRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.NEMEAN_LION_ENTITY, NemeanLionRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.NAIAD_ENTITY, NaiadRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.ORTHUS_ENTITY, OrthusRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.ORTHUS_HEAD_ITEM_ENTITY, m -> new ItemRenderer(m, Minecraft.getInstance().getItemRenderer()));
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.PALLADIUM_ENTITY, PalladiumRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.PEGASUS_ENTITY, PegasusRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.POISON_SPIT_ENTITY, PoisonSpitRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.PYTHON_ENTITY, PythonRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.SATYR_ENTITY, SatyrRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.SHADE_ENTITY, ShadeRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.SIREN_ENTITY, SirenRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.SPARTI_ENTITY, SpartiRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.SPEAR_ENTITY, SpearRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.PIG_SPELL_ENTITY, PigSpellRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.UNICORN_ENTITY, UnicornRenderer::new);
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.WEB_BALL_ENTITY, m -> new SpriteRenderer<WebBallEntity>(m, Minecraft.getInstance().getItemRenderer()));
            RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EntityReg.WHIRL_ENTITY, WhirlRenderer::new);
        }

        private static void registerTileEntityRenders() {
            GreekFantasy.LOGGER.debug("registerTileEntityRenders");
            ClientRegistry.bindTileEntityRenderer(GFRegistry.BlockEntityReg.VASE_TE, VaseTileEntityRenderer::new);
            ClientRegistry.bindTileEntityRenderer(GFRegistry.BlockEntityReg.BOSS_HEAD_TE, MobHeadTileEntityRenderer::new);
        }

        private static void registerRenderLayers() {
            GreekFantasy.LOGGER.debug("registerRenderLayers");
            // cutout mipped
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.OLIVE_LEAVES, RenderType.cutoutMipped());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.POMEGRANATE_LEAVES, RenderType.cutoutMipped());
            // cutout
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.OLIVE_DOOR, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.OLIVE_TRAPDOOR, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.OLIVE_SAPLING, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.POMEGRANATE_DOOR, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.POMEGRANATE_TRAPDOOR, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.POMEGRANATE_SAPLING, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.GOLDEN_APPLE_SAPLING, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.REEDS, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.WILD_ROSE, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.GOLDEN_STRING_BLOCK, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(GFRegistry.BlockReg.OIL, RenderType.cutout());
        }

        private static void registerPlayerLayers() {
            GreekFantasy.LOGGER.debug("registerPlayerLayers");
            final Minecraft mc = Minecraft.getInstance();
            final Map<String, PlayerRenderer> skinMap = mc.getEntityRenderDispatcher().getSkinMap();
            // add default renderer layers
            final PlayerRenderer defaultRenderer = skinMap.get("default");
            defaultRenderer.addLayer(new PlayerSkyjayLayer<>(defaultRenderer));
            defaultRenderer.addLayer(new NemeanLionHideLayer<>(defaultRenderer, new BipedModel<>(0.501F), new BipedModel<>(1.001F)));
            // add slim renderer layers
            final PlayerRenderer slimRenderer = skinMap.get("slim");
            slimRenderer.addLayer(new PlayerSkyjayLayer<>(slimRenderer));
            slimRenderer.addLayer(new NemeanLionHideLayer<>(slimRenderer, new BipedModel<>(0.501F), new BipedModel<>(1.001F)));
        }

        private static void registerModelProperties() {
            GreekFantasy.LOGGER.debug("registerModelProperties");
            // Register instrument properties
            registerInstrumentProperties(GFRegistry.ItemReg.PANFLUTE);
            registerInstrumentProperties(GFRegistry.ItemReg.WOODEN_LYRE);
            registerInstrumentProperties(GFRegistry.ItemReg.GOLD_LYRE);
            // Register bow properties
            registerBowProperties(GFRegistry.ItemReg.APOLLO_BOW);
            registerBowProperties(GFRegistry.ItemReg.ARTEMIS_BOW);
            registerBowProperties(GFRegistry.ItemReg.CURSED_BOW);
            // Register spear properties
            registerSpearProperties(GFRegistry.ItemReg.BIDENT);
            registerSpearProperties(GFRegistry.ItemReg.WOODEN_SPEAR);
            registerSpearProperties(GFRegistry.ItemReg.STONE_SPEAR);
            registerSpearProperties(GFRegistry.ItemReg.IRON_SPEAR);
        }

        private static void registerInstrumentProperties(final Item instrument) {
            ItemModelsProperties.register(instrument, new ResourceLocation("playing"),
                    (item, world, entity) -> (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }

        private static void registerBowProperties(final Item bow) {
            ItemModelsProperties.register(bow, new ResourceLocation("pull"),
                    (item, world, entity) -> {
                        if (entity == null) return 0.0F;
                        if (entity.getUseItem() != item) return 0.0F;
                        return (item.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                    });
            ItemModelsProperties.register(bow, new ResourceLocation("pulling"),
                    (item, world, entity) -> (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }

        private static void registerSpearProperties(final Item spear) {
            ItemModelsProperties.register(spear, new ResourceLocation("throwing"), (item, world, entity) ->
                    (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }
    }

    public static final class ForgeEvents {

        private static EntityAsPigRenderer<LivingEntity> pigRenderer;

        private static boolean wasJumping;

        /**
         * Used to render players as pigs when under the Swine effect
         *
         * @param event the RenderLivingEvent (Pre)
         **/
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void renderLiving(final RenderLivingEvent.Pre<LivingEntity, ?> event) {
            // swine
            if (CommonEventHandler.isSwine(event.getEntity())) {
                event.setCanceled(true);
                // render pig instead
                if (null == pigRenderer) {
                    Minecraft mc = Minecraft.getInstance();
                    pigRenderer = new EntityAsPigRenderer<LivingEntity>(mc.getEntityRenderDispatcher());
                }
                pigRenderer.render(event.getEntity(), event.getEntity().yRot, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(),
                        pigRenderer.getPackedLightCoords(event.getEntity(), event.getPartialRenderTick()));
            }
        }

        /**
         * Used to hide the player and their armor / held items
         * while using the Helm of Darkness.
         *
         * @param event the RenderPlayerEvent (Pre)
         **/
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void renderPlayer(final RenderPlayerEvent.Pre event) {
            if (GreekFantasy.CONFIG.doesHelmHideArmor() && CommonEventHandler.hasHelmOfDarkness(event.getPlayer())) {
                event.setCanceled(true);
            }
        }

        /**
         * Used to hide the first-person view of held items
         * while a player is using the Helm of Darkness.
         *
         * @param event the RenderHandEvent
         **/
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void renderPlayerHand(final RenderHandEvent event) {
            final Minecraft mc = Minecraft.getInstance();
            if ((GreekFantasy.CONFIG.doesHelmHideArmor() && CommonEventHandler.hasHelmOfDarkness(mc.player))
                    || (CommonEventHandler.isSwine(mc.player) && mc.player.getMainHandItem().isEmpty())) {
                event.setCanceled(true);
            }
        }

        /**
         * This method handles when the player is wearing the winged sandals item.
         * It's a little buggy because the stepHeight applied here doesn't seem to persist
         * more than a tick, but it works and hopefully doesn't break things too much.
         *
         * @param event the player tick event (only handles TickEvent.Phase.START)
         **/
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            // winged sandals logic
            if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT && GreekFantasy.CONFIG.isOverstepEnabled()
                    && event.player instanceof ClientPlayerEntity) {
                final ClientPlayerEntity player = (ClientPlayerEntity) event.player;
                final Minecraft mc = Minecraft.getInstance();
                final boolean hasOverstep = CommonEventHandler.hasOverstep(player);
                // apply step height changes
                if (hasOverstep && !player.isShiftKeyDown() && (player.maxUpStep < 1.0F || player.isAutoJumpEnabled())) {
                    player.maxUpStep = 1.25F;
                    // use Access Transformers to use/modify this field directly
                    player.autoJumpEnabled = false;
                } else if (player.maxUpStep > 1.2F) {
                    // restore defaults
                    player.maxUpStep = 0.6F;
                    player.autoJumpEnabled = mc.options.autoJump;
                }
            }
        }

        /**
         * Used to handle jumping when the player is riding a pegasus
         *
         * @param event the client tick event
         */
        @SubscribeEvent
        public static void onClientTick(final TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                final Minecraft mc = Minecraft.getInstance();
                if (mc.player != null && mc.player.isRidingJumpable() && mc.player.getVehicle() instanceof PegasusEntity) {
                    mc.player.jumpRidingTicks = -10;
                    if (mc.player.input.jumping && !wasJumping) {
                        // if starting to jump, set flag
                        wasJumping = true;
                    } else if (!mc.player.input.jumping && wasJumping) {
                        // if not jumping but was previously, send jump packet
                        wasJumping = false;
                        ((PegasusEntity) mc.player.getVehicle()).flyingJump();
                    }
                }
            }
        }

        /**
         * Prevents the screen from "zooming in" when negative move-speed modifiers
         * are applied, but only if the player has the Stunned or Petrified effect.
         *
         * @param event the FOVModifier event
         **/
        @SubscribeEvent
        public static void modifyFOV(final EntityViewRenderEvent.FOVModifier event) {
            final Minecraft mc = Minecraft.getInstance();
            if (mc != null && GreekFantasy.CONFIG.isForceFOVReset()) {
                final PlayerEntity player = mc.player;
                if (player.isAlive() && (CommonEventHandler.isStunned(player) || player.getItemBySlot(EquipmentSlotType.FEET).getItem() == GFRegistry.ItemReg.WINGED_SANDALS)) {
                    event.setFOV(mc.options.fov);
                }
            }
        }
    }
}
