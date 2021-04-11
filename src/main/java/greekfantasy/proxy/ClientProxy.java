package greekfantasy.proxy;

import java.util.Map;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.gui.DeityScreen;
import greekfantasy.client.gui.StatueScreen;
import greekfantasy.client.render.*;
import greekfantasy.client.render.layer.NemeanLionHideLayer;
import greekfantasy.client.render.layer.PlayerSkyjayLayer;
import greekfantasy.client.render.tileentity.MobHeadTileEntityRenderer;
import greekfantasy.client.render.tileentity.StatueTileEntityRenderer;
import greekfantasy.client.render.tileentity.VaseTileEntityRenderer;
import greekfantasy.entity.misc.DragonToothEntity;
import greekfantasy.entity.misc.DiscusEntity;
import greekfantasy.event.ClientForgeEventHandler;
import greekfantasy.event.ClientModEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
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
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ARION_ENTITY, ArionRenderer::new);
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
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HARPY_ENTITY, HarpyRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HEALING_SPELL_ENTITY, HealingSpellRenderer::new);
//    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HYDRA_ENTITY, HydraRenderer::new);
//    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HYDRA_HEAD_ENTITY, HydraHeadRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.MAD_COW_ENTITY, MadCowRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.MAKHAI_ENTITY, MakhaiRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.MINOTAUR_ENTITY, MinotaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.NEMEAN_LION_ENTITY, NemeanLionRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.NAIAD_ENTITY, NaiadRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ORTHUS_ENTITY, OrthusRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ORTHUS_HEAD_ITEM_ENTITY, m -> new ItemRenderer(m, Minecraft.getInstance().getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.PEGASUS_ENTITY, PegasusRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.POISON_SPIT_ENTITY, PoisonSpitRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.PYTHON_ENTITY, PythonRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SATYR_ENTITY, SatyrRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SHADE_ENTITY, ShadeRenderer::new);  
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SIREN_ENTITY, SirenRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SPARTI_ENTITY, SpartiRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SPEAR_ENTITY, SpearRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SWINE_SPELL_ENTITY, SwineSpellRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.UNICORN_ENTITY, UnicornRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.WHIRL_ENTITY, WhirlRenderer::new);
  }
  
  @Override
  public void registerTileEntityRenders() {
    GreekFantasy.LOGGER.debug("registerTileEntityRenders");
    ClientRegistry.bindTileEntityRenderer(GFRegistry.STATUE_TE, StatueTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GFRegistry.VASE_TE, VaseTileEntityRenderer::new);
    ClientRegistry.bindTileEntityRenderer(GFRegistry.BOSS_HEAD_TE, MobHeadTileEntityRenderer::new);
  }
  
  @Override
  public void registerContainerRenders() {
    GreekFantasy.LOGGER.debug("registerContainerRenders");
    ScreenManager.registerFactory(GFRegistry.STATUE_CONTAINER, StatueScreen::new);
    ScreenManager.registerFactory(GFRegistry.DEITY_CONTAINER, DeityScreen::new);
  }
  
  @Override
  public void registerRenderLayers() {
    GreekFantasy.LOGGER.debug("registerRenderLayers");
    RenderTypeLookup.setRenderLayer(GFRegistry.OLIVE_SAPLING, RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(GFRegistry.GOLDEN_APPLE_SAPLING, RenderType.getCutout());    
    RenderTypeLookup.setRenderLayer(GFRegistry.REEDS, RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(GFRegistry.WILD_ROSE, RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(GFRegistry.GOLDEN_STRING_BLOCK, RenderType.getCutout());
  }
  
  @Override
  public void registerPlayerLayers() {
    GreekFantasy.LOGGER.debug("registerPlayerLayers");
    final Minecraft mc = Minecraft.getInstance();
    final Map<String, PlayerRenderer> skinMap = mc.getRenderManager().getSkinMap();
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
    ItemModelsProperties.registerProperty(instrument, new ResourceLocation("playing"),
        (item, world, entity) -> (entity != null && entity.isHandActive() && entity.getActiveItemStack() == item) ? 1.0F : 0.0F);
  }
  
  private static void registerBowProperties(final Item bow) {
    ItemModelsProperties.registerProperty(bow, new ResourceLocation("pull"),
        (item, world, entity) -> {
          if (entity == null) return 0.0F;
          if (entity.getActiveItemStack() != item) return 0.0F;
          return (item.getUseDuration() - entity.getItemInUseCount()) / 20.0F;
        });
    ItemModelsProperties.registerProperty(bow, new ResourceLocation("pulling"),
        (item, world, entity) -> (entity != null && entity.isHandActive() && entity.getActiveItemStack() == item) ? 1.0F : 0.0F);
  }
  
  private static void registerSpearProperties(final Item spear) {
    ItemModelsProperties.registerProperty(spear, new ResourceLocation("throwing"), (item, world, entity) -> 
      (entity != null && entity.isHandActive() && entity.getActiveItemStack() == item) ? 1.0F : 0.0F);
  }
}
