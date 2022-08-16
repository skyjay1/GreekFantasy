package greekfantasy.client;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.armor.HellenicArmorModel;
import greekfantasy.client.armor.NemeanArmorModel;
import greekfantasy.client.armor.WingedSandalsModel;
import greekfantasy.client.blockentity.CerberusHeadBlockEntityRenderer;
import greekfantasy.client.blockentity.GiganteHeadBlockEntityRenderer;
import greekfantasy.client.blockentity.OrthusHeadBlockEntityRenderer;
import greekfantasy.client.blockentity.VaseBlockEntityRenderer;
import greekfantasy.client.blockentity.model.CerberusHeadModel;
import greekfantasy.client.blockentity.model.GiganteHeadModel;
import greekfantasy.client.blockentity.model.OrthusHeadModel;
import greekfantasy.client.entity.AraRenderer;
import greekfantasy.client.entity.ArachneRenderer;
import greekfantasy.client.entity.ArionRenderer;
import greekfantasy.client.entity.AutomatonRenderer;
import greekfantasy.client.entity.BabySpiderRenderer;
import greekfantasy.client.entity.BronzeBullRenderer;
import greekfantasy.client.entity.CentaurRenderer;
import greekfantasy.client.entity.CerastesRenderer;
import greekfantasy.client.entity.CerberusRenderer;
import greekfantasy.client.entity.CharybdisRenderer;
import greekfantasy.client.entity.CirceRenderer;
import greekfantasy.client.entity.CretanMinotaurRenderer;
import greekfantasy.client.entity.CyclopsRenderer;
import greekfantasy.client.entity.CyprianRenderer;
import greekfantasy.client.entity.DrakainaRenderer;
import greekfantasy.client.entity.DryadRenderer;
import greekfantasy.client.entity.ElpisRenderer;
import greekfantasy.client.entity.EmpusaRenderer;
import greekfantasy.client.entity.FakePigRenderer;
import greekfantasy.client.entity.FuryRenderer;
import greekfantasy.client.entity.GeryonRenderer;
import greekfantasy.client.entity.GiantBoarRenderer;
import greekfantasy.client.entity.GiganteRenderer;
import greekfantasy.client.entity.GoldenRamRenderer;
import greekfantasy.client.entity.GorgonRenderer;
import greekfantasy.client.entity.HarpyRenderer;
import greekfantasy.client.entity.HydraHeadRenderer;
import greekfantasy.client.entity.HydraRenderer;
import greekfantasy.client.entity.LampadRenderer;
import greekfantasy.client.entity.MadCowRenderer;
import greekfantasy.client.entity.MakhaiRenderer;
import greekfantasy.client.entity.MinotaurRenderer;
import greekfantasy.client.entity.NaiadRenderer;
import greekfantasy.client.entity.NemeanLionRenderer;
import greekfantasy.client.entity.OrthusRenderer;
import greekfantasy.client.entity.PalladiumRenderer;
import greekfantasy.client.entity.PegasusRenderer;
import greekfantasy.client.entity.PythonRenderer;
import greekfantasy.client.entity.SatyrRenderer;
import greekfantasy.client.entity.ShadeRenderer;
import greekfantasy.client.entity.SirenRenderer;
import greekfantasy.client.entity.SpartiRenderer;
import greekfantasy.client.entity.SpearRenderer;
import greekfantasy.client.entity.SpellRenderer;
import greekfantasy.client.entity.StymphalianRenderer;
import greekfantasy.client.entity.TalosRenderer;
import greekfantasy.client.entity.UnicornRenderer;
import greekfantasy.client.entity.WhirlRenderer;
import greekfantasy.client.entity.layer.NemeanLionHideLayer;
import greekfantasy.client.entity.layer.PlayerSoulFireLayer;
import greekfantasy.client.entity.model.AraModel;
import greekfantasy.client.entity.model.ArachneModel;
import greekfantasy.client.entity.model.BabySpiderModel;
import greekfantasy.client.entity.model.BronzeBullModel;
import greekfantasy.client.entity.model.CentaurModel;
import greekfantasy.client.entity.model.CerastesModel;
import greekfantasy.client.entity.model.CerberusModel;
import greekfantasy.client.entity.model.CharybdisModel;
import greekfantasy.client.entity.model.CirceModel;
import greekfantasy.client.entity.model.CyclopsModel;
import greekfantasy.client.entity.model.CyprianModel;
import greekfantasy.client.entity.model.DrakainaModel;
import greekfantasy.client.entity.model.ElpisModel;
import greekfantasy.client.entity.model.EmpusaModel;
import greekfantasy.client.entity.model.FuryModel;
import greekfantasy.client.entity.model.GeryonModel;
import greekfantasy.client.entity.model.GiganteModel;
import greekfantasy.client.entity.model.GoldenRamModel;
import greekfantasy.client.entity.model.GorgonModel;
import greekfantasy.client.entity.model.HalfHorseModel;
import greekfantasy.client.entity.model.HarpyModel;
import greekfantasy.client.entity.model.HydraBodyModel;
import greekfantasy.client.entity.model.HydraHeadModel;
import greekfantasy.client.entity.model.MakhaiModel;
import greekfantasy.client.entity.model.MinotaurModel;
import greekfantasy.client.entity.model.NemeanLionModel;
import greekfantasy.client.entity.model.NymphModel;
import greekfantasy.client.entity.model.OrthusModel;
import greekfantasy.client.entity.model.PalladiumModel;
import greekfantasy.client.entity.model.PegasusModel;
import greekfantasy.client.entity.model.PythonModel;
import greekfantasy.client.entity.model.SatyrModel;
import greekfantasy.client.entity.model.ShadeModel;
import greekfantasy.client.entity.model.SirenModel;
import greekfantasy.client.entity.model.SpearModel;
import greekfantasy.client.entity.model.SpellModel;
import greekfantasy.client.entity.model.AutomatonModel;
import greekfantasy.client.entity.model.StymphalianModel;
import greekfantasy.client.entity.model.UnicornModel;
import greekfantasy.client.particle.GorgonParticle;
import greekfantasy.entity.Pegasus;
import greekfantasy.item.WingedSandalsItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

public final class GFClientEvents {

    public static final class ForgeHandler {

        /** Used in render living event as a replacement for entities that should be rendered as pigs **/
        private static FakePigRenderer<LivingEntity> pigRenderer;
        /** Used in the client tick event to handle movement when riding a pegasus **/
        private static boolean wasJumping;

        /**
         * Used to hide the third-person view of an entity that is
         * wearing the Helm of Darkness. Next, attempts to render
         * any entity that is under the Curse of Circe as a pig.
         *
         * @param event the Pre RenderLivingEvent
         */
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onRenderLiving(final RenderLivingEvent.Pre<?, ?> event) {
            // cancel rendering when helm of darkness is equipped
            if (GreekFantasy.CONFIG.helmHidesArmor() && event.getEntity().getItemBySlot(EquipmentSlot.HEAD).is(GFRegistry.ItemReg.HELM_OF_DARKNESS.get())) {
                event.setCanceled(true);
                return;
            }
            // render pig when curse of circe is applied
            if (GreekFantasy.CONFIG.isCurseOfCirceEnabled() && event.getEntity().hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get())) {
                // lazy-load pig renderer
                if (null == pigRenderer) {
                    Minecraft mc = Minecraft.getInstance();
                    EntityRendererProvider.Context context = new EntityRendererProvider.Context(mc.getEntityRenderDispatcher(),
                            mc.getItemRenderer(), mc.getResourceManager(), mc.getEntityModels(), mc.font);
                    pigRenderer = new FakePigRenderer<>(context);
                }
                // render pig
                pigRenderer.render(event.getEntity(), event.getEntity().getXRot(), event.getPartialTick(),
                        event.getPoseStack(), event.getMultiBufferSource(),
                        pigRenderer.getPackedLightCoords(event.getEntity(), event.getPartialTick()));
                // cancel event
                event.setCanceled(true);
            }
        }

        /**
         * Used to hide the first-person view of held items
         * while a player is using the Helm of Darkness or under
         * the Curse of Circe.
         *
         * @param event the RenderHandEvent
         **/
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onRenderHand(final RenderHandEvent event) {
            final Minecraft mc = Minecraft.getInstance();
            if(null == mc.player) {
                return;
            }
            if (GreekFantasy.CONFIG.helmHidesArmor() && mc.player.getItemBySlot(EquipmentSlot.HEAD).is(GFRegistry.ItemReg.HELM_OF_DARKNESS.get())) {
                event.setCanceled(true);
                return;
            }
            if (GreekFantasy.CONFIG.isCurseOfCirceEnabled()
                    && mc.player.hasEffect(GFRegistry.MobEffectReg.CURSE_OF_CIRCE.get())) {
                event.setCanceled(true);
                return;
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
                if (mc.player != null && mc.player.isRidingJumpable() && mc.player.getVehicle() instanceof Pegasus pegasus) {
                    mc.player.jumpRidingTicks = -10;
                    if (mc.player.input.jumping && !wasJumping) {
                        // if starting to jump, set flag
                        wasJumping = true;
                    } else if (!mc.player.input.jumping && wasJumping) {
                        // if not jumping but was previously, send jump packet
                        wasJumping = false;
                        pegasus.flyingJump();
                    }
                }
            }
        }

        /**
         * This method enables or disabled autojump based on the level of Overstep enchantment
         * on the player feet item.
         *
         * @param event the player tick event (only handles TickEvent.Phase.START)
         **/
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.CLIENT
                    && GreekFantasy.CONFIG.isOverstepEnabled()
                    && event.player instanceof LocalPlayer player) {
                final Minecraft mc = Minecraft.getInstance();
                ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
                // check if the player is wearing an item with overstep enchantment in feet slot
                final boolean hasOverstep = !feet.isEmpty() && feet.isDamageableItem() && feet.getMaxDamage() - feet.getDamageValue() > WingedSandalsItem.BROKEN
                        && EnchantmentHelper.getItemEnchantmentLevel(GFRegistry.EnchantmentReg.OVERSTEP.get(), feet) > 0;
                // if the player has overstep and is not sneaking, disable autojump
                if (hasOverstep && !player.isCrouching() && player.isAutoJumpEnabled()) {
                    // use Access Transformers to use/modify this field directly
                    player.autoJumpEnabled = false;
                } else {
                    // restore autojump value from game options
                    player.autoJumpEnabled = mc.options.autoJump;
                }
            }
        }
    }

    public static final class ModHandler {

        private static final String MODID = GreekFantasy.MODID;

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(ModHandler::registerRenderLayers);
            event.enqueueWork(ModHandler::registerContainerRenders);
            event.enqueueWork(ModHandler::registerModelProperties);
        }

        @SubscribeEvent
        public static void registerPlayerLayers(final EntityRenderersEvent.AddLayers event) {
            // add layer renders to default model
            if (event.getSkin("default") instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new NemeanLionHideLayer<>(playerRenderer,
                        new NemeanArmorModel<>(event.getEntityModels().bakeLayer(NemeanArmorModel.NEMEAN_ARMOR_MODEL_RESOURCE)),
                        new NemeanArmorModel<>(event.getEntityModels().bakeLayer(NemeanArmorModel.NEMEAN_ARMOR_MODEL_RESOURCE))));
                playerRenderer.addLayer(new PlayerSoulFireLayer<>(playerRenderer));
            }
            // add layer renders to slim model
            if (event.getSkin("slim") instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new NemeanLionHideLayer<>(playerRenderer,
                        new NemeanArmorModel<>(event.getEntityModels().bakeLayer(NemeanArmorModel.NEMEAN_ARMOR_MODEL_RESOURCE)),
                        new NemeanArmorModel<>(event.getEntityModels().bakeLayer(NemeanArmorModel.NEMEAN_ARMOR_MODEL_RESOURCE))));
                playerRenderer.addLayer(new PlayerSoulFireLayer<>(playerRenderer));
            }
        }

        @SubscribeEvent
        public static void registerEntityLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            // register layer definitions
            // armor
            event.registerLayerDefinition(HellenicArmorModel.HELLENIC_ARMOR_MODEL_RESOURCE, HellenicArmorModel::createBodyLayer);
            event.registerLayerDefinition(WingedSandalsModel.WINGED_SANDALS_MODEL_RESOURCE, WingedSandalsModel::createBodyLayer);
            // creature
            event.registerLayerDefinition(AraModel.ARA_MODEL_RESOURCE, AraModel::createBodyLayer);
            event.registerLayerDefinition(ArachneModel.ARACHNE_MODEL_RESOURCE, ArachneModel::createBodyLayer);
            event.registerLayerDefinition(AutomatonModel.AUTOMATON_MODEL_RESOURCE, AutomatonModel::createBodyLayer);
            event.registerLayerDefinition(BabySpiderModel.BABY_SPIDER_MODEL_RESOURCE, BabySpiderModel::createBodyLayer);
            event.registerLayerDefinition(BronzeBullModel.BULL_MODEL_RESOURCE, BronzeBullModel::createBodyLayer);
            event.registerLayerDefinition(CentaurModel.CENTAUR_MODEL_RESOURCE, CentaurModel::createBodyLayer);
            event.registerLayerDefinition(CerastesModel.CERASTES_LAYER_LOCATION, CerastesModel::createBodyLayer);
            event.registerLayerDefinition(CerberusModel.CERBERUS_MODEL_RESOURCE, CerberusModel::createBodyLayer);
            event.registerLayerDefinition(CharybdisModel.CHARYBDIS_MODEL_RESOURCE, CharybdisModel::createBodyLayer);
            event.registerLayerDefinition(CirceModel.CIRCE_MODEL_RESOURCE, CirceModel::createBodyLayer);
            event.registerLayerDefinition(CyclopsModel.CYCLOPS_MODEL_RESOURCE, CyclopsModel::createBodyLayer);
            event.registerLayerDefinition(CyprianModel.CYPRIAN_MODEL_RESOURCE, CyprianModel::createBodyLayer);
            event.registerLayerDefinition(DrakainaModel.DRAKAINA_MODEL_RESOURCE, DrakainaModel::createBodyLayer);
            event.registerLayerDefinition(ElpisModel.ELPIS_MODEL_RESOURCE, ElpisModel::createBodyLayer);
            event.registerLayerDefinition(EmpusaModel.EMPUSA_MODEL_RESOURCE, EmpusaModel::createBodyLayer);
            event.registerLayerDefinition(FuryModel.FURY_MODEL_RESOURCE, FuryModel::createBodyLayer);
            event.registerLayerDefinition(GeryonModel.GERYON_MODEL_RESOURCE, () -> GeryonModel.createBodyLayer(CubeDeformation.NONE));
            event.registerLayerDefinition(GeryonModel.GERYON_ARMOR_MODEL_RESOURCE, () -> GeryonModel.createBodyLayer(new CubeDeformation(0.25F)));
            event.registerLayerDefinition(GiganteModel.GIGANTE_MODEL_RESOURCE, GiganteModel::createBodyLayer);
            event.registerLayerDefinition(GoldenRamModel.RAM_MODEL_RESOURCE, GoldenRamModel::createBodyLayer);
            event.registerLayerDefinition(GorgonModel.GORGON_MODEL_RESOURCE, GorgonModel::createBodyLayer);
            event.registerLayerDefinition(HalfHorseModel.HALF_HORSE_MODEL_RESOURCE, HalfHorseModel::createBodyLayer);
            event.registerLayerDefinition(HarpyModel.HARPY_MODEL_RESOURCE, HarpyModel::createBodyLayer);
            event.registerLayerDefinition(HydraBodyModel.HYDRA_BODY_MODEL_RESOURCE, HydraBodyModel::createBodyLayer);
            event.registerLayerDefinition(HydraHeadModel.HYDRA_HEAD_MODEL_RESOURCE, HydraHeadModel::createBodyLayer);
            event.registerLayerDefinition(MakhaiModel.MAKHAI_MODEL_RESOURCE, MakhaiModel::createBodyLayer);
            event.registerLayerDefinition(MinotaurModel.MINOTAUR_MODEL_RESOURCE, MinotaurModel::createBodyLayer);
            event.registerLayerDefinition(NemeanArmorModel.NEMEAN_ARMOR_MODEL_RESOURCE, NemeanArmorModel::createBodyLayer);
            event.registerLayerDefinition(NemeanLionModel.NEMEAN_LION_MODEL_RESOURCE, NemeanLionModel::createBodyLayer);
            event.registerLayerDefinition(NymphModel.NYMPH_LAYER_LOCATION, NymphModel::createBodyLayer);
            event.registerLayerDefinition(OrthusModel.ORTHUS_MODEL_RESOURCE, OrthusModel::createBodyLayer);
            event.registerLayerDefinition(PalladiumModel.PALLADIUM_MODEL_RESOURCE, PalladiumModel::createBodyLayer);
            event.registerLayerDefinition(PegasusModel.PEGASUS_MODEL_RESOURCE, PegasusModel::createBodyLayer);
            event.registerLayerDefinition(PythonModel.PYTHON_MODEL_RESOURCE, PythonModel::createBodyLayer);
            event.registerLayerDefinition(SatyrModel.SATYR_MODEL_RESOURCE, () -> SatyrModel.createBodyLayer(CubeDeformation.NONE));
            event.registerLayerDefinition(SatyrModel.SATYR_INNER_ARMOR_MODEL_RESOURCE, () -> SatyrModel.createBodyLayer(new CubeDeformation(0.25F)));
            event.registerLayerDefinition(ShadeModel.SHADE_MODEL_RESOURCE, ShadeModel::createBodyLayer);
            event.registerLayerDefinition(SirenModel.SIREN_MODEL_RESOURCE, SirenModel::createBodyLayer);
            event.registerLayerDefinition(StymphalianModel.STYMPHALIAN_MODEL_RESOURCE, StymphalianModel::createBodyLayer);
            event.registerLayerDefinition(UnicornModel.UNICORN_MODEL_RESOURCE, UnicornModel::createBodyLayer);
            // other
            event.registerLayerDefinition(CerberusHeadModel.CERBERUS_HEAD_MODEL_RESOURCE, CerberusHeadModel::createLayer);
            event.registerLayerDefinition(GiganteHeadModel.GIGANTE_HEAD_MODEL_RESOURCE, GiganteHeadModel::createLayer);
            event.registerLayerDefinition(OrthusHeadModel.ORTHUS_HEAD_MODEL_RESOURCE, OrthusHeadModel::createLayer);
            event.registerLayerDefinition(SpearModel.SPEAR_MODEL_RESOURCE, TridentModel::createLayer);
            event.registerLayerDefinition(SpellModel.SPELL_MODEL_RESOURCE, SpellModel::createLayer);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            // register entities
            // creature
            event.registerEntityRenderer(GFRegistry.EntityReg.AUTOMATON.get(), AutomatonRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.ARA.get(), AraRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.ARACHNE.get(), ArachneRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.ARION.get(), ArionRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.BABY_SPIDER.get(), BabySpiderRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.BRONZE_BULL.get(), BronzeBullRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CENTAUR.get(), CentaurRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CERASTES.get(), CerastesRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CERBERUS.get(), CerberusRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CHARYBDIS.get(), CharybdisRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CIRCE.get(), CirceRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CRETAN_MINOTAUR.get(), CretanMinotaurRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CYCLOPS.get(), CyclopsRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CYPRIAN.get(), CyprianRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.DRAKAINA.get(), DrakainaRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.DRYAD.get(), DryadRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.ELPIS.get(), ElpisRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.EMPUSA.get(), EmpusaRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.FURY.get(), FuryRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.GERYON.get(), GeryonRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.GIANT_BOAR.get(), GiantBoarRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.GIGANTE.get(), GiganteRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.GOLDEN_RAM.get(), GoldenRamRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.GORGON.get(), GorgonRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.HARPY.get(), HarpyRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.HYDRA.get(), HydraRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.HYDRA_HEAD.get(), HydraHeadRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.LAMPAD.get(), LampadRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.MAD_COW.get(), MadCowRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.MAKHAI.get(), MakhaiRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.MINOTAUR.get(), MinotaurRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.NAIAD.get(), NaiadRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.NEMEAN_LION.get(), NemeanLionRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.ORTHUS.get(), OrthusRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.PEGASUS.get(), PegasusRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.PYTHON.get(), PythonRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.SATYR.get(), SatyrRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.SPARTI.get(), SpartiRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.SHADE.get(), ShadeRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.SIREN.get(), SirenRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.STYMPHALIAN.get(), StymphalianRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.TALOS.get(), TalosRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.UNICORN.get(), UnicornRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.WHIRL.get(), WhirlRenderer::new);
            // other
            event.registerEntityRenderer(GFRegistry.EntityReg.BRONZE_FEATHER.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CURSE.get(), SpellRenderer.CurseRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.CURSE_OF_CIRCE.get(), SpellRenderer.CurseOfCirceRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.DISCUS.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.DRAGON_TOOTH.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.GREEK_FIRE.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.HEALING_SPELL.get(), SpellRenderer.HealingSpellRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.PALLADIUM.get(), PalladiumRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.POISON_SPIT.get(), SpellRenderer.PoisonSpitRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.SPEAR.get(), SpearRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.THROWING_AXE.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(GFRegistry.EntityReg.WEB_BALL.get(), ThrownItemRenderer::new);
            // register block entities
            event.registerBlockEntityRenderer(GFRegistry.BlockEntityReg.CERBERUS_HEAD.get(), CerberusHeadBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(GFRegistry.BlockEntityReg.GIGANTE_HEAD.get(), GiganteHeadBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(GFRegistry.BlockEntityReg.ORTHUS_HEAD.get(), OrthusHeadBlockEntityRenderer::new);
            event.registerBlockEntityRenderer(GFRegistry.BlockEntityReg.VASE.get(), VaseBlockEntityRenderer::new);
        }

        /**
         * Used to register block color handlers.
         * Currently used to color leaves.
         *
         * @param event the ColorHandlerEvent (Block)
         **/
        @SubscribeEvent
        public static void registerBlockColors(final ColorHandlerEvent.Block event) {
            event.getBlockColors().register(
                    (BlockState stateIn, BlockAndTintGetter level, BlockPos pos, int color) -> 0xD8E3D0,
                    RegistryObject.create(new ResourceLocation(GreekFantasy.MODID, "olive_leaves"), ForgeRegistries.BLOCKS).get());
            event.getBlockColors().register(
                    (BlockState stateIn, BlockAndTintGetter level, BlockPos pos, int color) -> 0x80f66b,
                    RegistryObject.create(new ResourceLocation(GreekFantasy.MODID, "golden_leaves"), ForgeRegistries.BLOCKS).get());
        }

        /**
         * Used to register item color handlers.
         * Currently used to color leaves.
         *
         * @param event the ColorHandlerEvent (Item)
         **/
        @SubscribeEvent
        public static void registerItemColors(final ColorHandlerEvent.Item event) {
            event.getItemColors().register((ItemStack item, int i) -> 0xD8E3D0,
                    RegistryObject.create(new ResourceLocation(GreekFantasy.MODID, "olive_leaves"), ForgeRegistries.ITEMS).get());
            event.getItemColors().register((ItemStack item, int i) -> 0x80f66b,
                    RegistryObject.create(new ResourceLocation(GreekFantasy.MODID, "golden_leaves"), ForgeRegistries.ITEMS).get());
        }

        @SubscribeEvent
        public static void registerParticleProviders(final ParticleFactoryRegisterEvent event) {
            Minecraft.getInstance().particleEngine.register(GFRegistry.ParticleReg.GORGON.get(), new GorgonParticle.Provider());
        }

        private static void registerRenderLayers() {
            // cutout mipped
            registerRenderLayer("olive_leaves", RenderType.cutoutMipped());
            registerRenderLayer("pomegranate_leaves", RenderType.cutoutMipped());
            registerRenderLayer("golden_leaves", RenderType.cutoutMipped());
            // cutout
            registerCutout("golden_sapling");
            registerCutout("golden_string");
            registerCutout("olive_door");
            registerCutout("olive_sapling");
            registerCutout("olive_oil");
            registerCutout("olive_trapdoor");
            registerCutout("pomegranate_door");
            registerCutout("pomegranate_sapling");
            registerCutout("pomegranate_trapdoor");
            registerCutout("reeds");
            registerCutout("wild_rose");
        }

        private static void registerCutout(final String blockName) {
            registerRenderLayer(blockName, RenderType.cutout());
        }

        private static void registerRenderLayer(final String blockName, RenderType renderType) {
            ItemBlockRenderTypes.setRenderLayer(RegistryObject.create(new ResourceLocation(MODID, blockName), ForgeRegistries.BLOCKS).get(), renderType);
        }

        private static void registerContainerRenders() {

        }

        private static void registerModelProperties() {
            // register instruments
            registerUsingProperties(GFRegistry.ItemReg.PANFLUTE.get(), "playing");
            registerUsingProperties(GFRegistry.ItemReg.WOODEN_LYRE.get(), "playing");
            registerUsingProperties(GFRegistry.ItemReg.GOLDEN_LYRE.get(), "playing");
            // register salve
            registerUsingProperties(GFRegistry.ItemReg.OLIVE_SALVE.get(), "using");
            // register bronze feather
            registerUsingProperties(GFRegistry.ItemReg.BRONZE_FEATHER.get(), "using");
            // register bows
            registerBowProperties(GFRegistry.ItemReg.AVERNAL_BOW.get());
            registerBowProperties(GFRegistry.ItemReg.APOLLO_BOW.get());
            registerBowProperties(GFRegistry.ItemReg.ARTEMIS_BOW.get());
            // register spears
            registerSpearProperties(GFRegistry.ItemReg.BIDENT.get());
            registerSpearProperties(GFRegistry.ItemReg.WOODEN_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.FLINT_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.STONE_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.GOLDEN_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.IRON_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.DIAMOND_SPEAR.get());
            registerSpearProperties(GFRegistry.ItemReg.NETHERITE_SPEAR.get());
        }

        private static void registerUsingProperties(final Item usingItem, final String propertyName) {
            ItemProperties.register(usingItem, new ResourceLocation(propertyName),
                    (item, world, entity, tintIndex) -> (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }

        private static void registerBowProperties(final Item bow) {
            ItemProperties.register(bow, new ResourceLocation("pull"),
                    (item, world, entity, tintIndex) -> {
                        if (entity == null) return 0.0F;
                        if (entity.getUseItem() != item) return 0.0F;
                        return (item.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
                    });
            ItemProperties.register(bow, new ResourceLocation("pulling"),
                    (item, world, entity, tintIndex) -> (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }

        private static void registerSpearProperties(final Item spear) {
            ItemProperties.register(spear, new ResourceLocation("throwing"), (item, world, entity, tintIndex) ->
                    (entity != null && entity.isUsingItem() && entity.getUseItem() == item) ? 1.0F : 0.0F);
        }

    }
}
