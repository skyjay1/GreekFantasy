package greekfantasy.proxy;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
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
import greekfantasy.client.render.PoisonSpitRenderer;
import greekfantasy.client.render.PythonRenderer;
import greekfantasy.client.render.SatyrRenderer;
import greekfantasy.client.render.ShadeRenderer;
import greekfantasy.client.render.SirenRenderer;
import greekfantasy.client.render.SpartiRenderer;
import greekfantasy.client.render.SpearRenderer;
import greekfantasy.client.render.PigSpellRenderer;
import greekfantasy.client.render.TalosRenderer;
import greekfantasy.client.render.UnicornRenderer;
import greekfantasy.client.render.WhirlRenderer;
import greekfantasy.client.render.layer.NemeanLionHideLayer;
import greekfantasy.client.render.layer.PlayerSkyjayLayer;
import greekfantasy.client.render.tileentity.MobHeadTileEntityRenderer;
import greekfantasy.client.render.tileentity.VaseTileEntityRenderer;
import greekfantasy.entity.misc.DiscusEntity;
import greekfantasy.entity.misc.DragonToothEntity;
import greekfantasy.entity.misc.GreekFireEntity;
import greekfantasy.entity.misc.WebBallEntity;
import greekfantasy.event.ClientForgeEventHandler;
import greekfantasy.event.ClientModEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Map;

public class ClientProxy extends Proxy {

    @Override
    public void registerEventHandlers() {
        super.registerEventHandlers();
        GreekFantasy.LOGGER.debug("registerClientEventHandlers");
        FMLJavaModLoadingContext.get().getModEventBus().register(ClientModEventHandler.class);
        MinecraftForge.EVENT_BUS.register(ClientForgeEventHandler.class);
    }

    @Override
    public void registerEntityRenders() {
        GreekFantasy.LOGGER.debug("registerEntityRenders");
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ARA_ENTITY, AraRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ARACHNE_ENTITY, ArachneRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ARION_ENTITY, ArionRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.BABY_SPIDER_ENTITY, BabySpiderRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.BRONZE_BULL_ENTITY, BronzeBullRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.TALOS_ENTITY, TalosRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CENTAUR_ENTITY, CentaurRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CERASTES_ENTITY, CerastesRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CERBERUS_ENTITY, CerberusRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CHARYBDIS_ENTITY, CharybdisRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CIRCE_ENTITY, CirceRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CRETAN_MINOTAUR_ENTITY, CretanMinotaurRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CURSE_ENTITY, CurseRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CYCLOPES_ENTITY, CyclopesRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CYPRIAN_ENTITY, CyprianRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.DISCUS_ENTITY, m -> new SpriteRenderer<DiscusEntity>(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.DRAGON_TOOTH_ENTITY, m -> new SpriteRenderer<DragonToothEntity>(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.DRAKAINA_ENTITY, DrakainaRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.DRYAD_ENTITY, DryadRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ELPIS_ENTITY, ElpisRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EMPUSA_ENTITY, EmpusaRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.FURY_ENTITY, FuryRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GERYON_ENTITY, GeryonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GIANT_BOAR_ENTITY, GiantBoarRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GIGANTE_ENTITY, GiganteRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GOLDEN_RAM_ENTITY, GoldenRamRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GORGON_ENTITY, GorgonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GREEK_FIRE_ENTITY, m -> new SpriteRenderer<GreekFireEntity>(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HARPY_ENTITY, HarpyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HEALING_SPELL_ENTITY, HealingSpellRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HYDRA_ENTITY, HydraRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HYDRA_HEAD_ENTITY, HydraHeadRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.LAMPAD_ENTITY, LampadRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.MAD_COW_ENTITY, MadCowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.MAKHAI_ENTITY, MakhaiRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.MINOTAUR_ENTITY, MinotaurRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.NEMEAN_LION_ENTITY, NemeanLionRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.NAIAD_ENTITY, NaiadRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ORTHUS_ENTITY, OrthusRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ORTHUS_HEAD_ITEM_ENTITY, m -> new ItemRenderer(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.PALLADIUM_ENTITY, PalladiumRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.PEGASUS_ENTITY, PegasusRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.POISON_SPIT_ENTITY, PoisonSpitRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.PYTHON_ENTITY, PythonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SATYR_ENTITY, SatyrRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SHADE_ENTITY, ShadeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SIREN_ENTITY, SirenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SPARTI_ENTITY, SpartiRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SPEAR_ENTITY, SpearRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.PIG_SPELL_ENTITY, PigSpellRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.UNICORN_ENTITY, UnicornRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.WEB_BALL_ENTITY, m -> new SpriteRenderer<WebBallEntity>(m, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(GFRegistry.WHIRL_ENTITY, WhirlRenderer::new);
    }

    @Override
    public void registerTileEntityRenders() {
        GreekFantasy.LOGGER.debug("registerTileEntityRenders");
        ClientRegistry.bindTileEntityRenderer(GFRegistry.VASE_TE, VaseTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(GFRegistry.BOSS_HEAD_TE, MobHeadTileEntityRenderer::new);
    }

    @Override
    public void registerContainerRenders() {
        GreekFantasy.LOGGER.debug("registerContainerRenders");
    }

    @Override
    public void registerRenderLayers() {
        GreekFantasy.LOGGER.debug("registerRenderLayers");
        // cutout mipped
        RenderTypeLookup.setRenderLayer(GFRegistry.OLIVE_LEAVES, RenderType.cutoutMipped());
        RenderTypeLookup.setRenderLayer(GFRegistry.POMEGRANATE_LEAVES, RenderType.cutoutMipped());
        // cutout
        RenderTypeLookup.setRenderLayer(GFRegistry.OLIVE_DOOR, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.OLIVE_TRAPDOOR, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.OLIVE_SAPLING, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.POMEGRANATE_DOOR, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.POMEGRANATE_TRAPDOOR, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.POMEGRANATE_SAPLING, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.GOLDEN_APPLE_SAPLING, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.REEDS, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.WILD_ROSE, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.GOLDEN_STRING_BLOCK, RenderType.cutout());
        RenderTypeLookup.setRenderLayer(GFRegistry.OIL, RenderType.cutout());
    }

    @Override
    public void registerPlayerLayers() {
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

    @Override
    public void registerModelProperties() {
        GreekFantasy.LOGGER.debug("registerModelProperties");
        // Register instrument properties
        registerInstrumentProperties(GFRegistry.PANFLUTE);
        registerInstrumentProperties(GFRegistry.WOODEN_LYRE);
        registerInstrumentProperties(GFRegistry.GOLD_LYRE);
        // Register bow properties
        registerBowProperties(GFRegistry.APOLLO_BOW);
        registerBowProperties(GFRegistry.ARTEMIS_BOW);
        registerBowProperties(GFRegistry.CURSED_BOW);
        // Register spear properties
        registerSpearProperties(GFRegistry.BIDENT);
        registerSpearProperties(GFRegistry.WOODEN_SPEAR);
        registerSpearProperties(GFRegistry.STONE_SPEAR);
        registerSpearProperties(GFRegistry.IRON_SPEAR);
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
