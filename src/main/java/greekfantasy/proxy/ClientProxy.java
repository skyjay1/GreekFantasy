package greekfantasy.proxy;

import greekfantasy.GFRegistry;
import greekfantasy.GreekFantasy;
import greekfantasy.client.gui.StatueScreen;
import greekfantasy.client.render.*;
import greekfantasy.client.render.tileentity.MobHeadTileEntityRenderer;
import greekfantasy.client.render.tileentity.StatueTileEntityRenderer;
import greekfantasy.client.render.tileentity.VaseTileEntityRenderer;
import greekfantasy.entity.DragonToothEntity;
import greekfantasy.events.ClientForgeEventHandler;
import greekfantasy.events.ClientModEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy extends Proxy {
  
  public void registerReloadListeners() {
    super.registerReloadListeners();
    GreekFantasy.LOGGER.debug("registerReloadListeners");
    IResourceManager manager = Minecraft.getInstance().getResourceManager();
    if (manager instanceof IReloadableResourceManager) {
      ((IReloadableResourceManager)manager).addReloadListener(PANFLUTE_SONGS);
    }
  }
  
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
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CENTAUR_ENTITY, CentaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CYPRIAN_ENTITY, CyprianRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CERASTES_ENTITY, CerastesRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CERBERUS_ENTITY, CerberusRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.CYCLOPES_ENTITY, CyclopesRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.DRAGON_TOOTH_ENTITY, m -> new SpriteRenderer<DragonToothEntity>(m, Minecraft.getInstance().getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.DRYAD_ENTITY, DryadRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ELPIS_ENTITY, ElpisRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.EMPUSA_ENTITY, EmpusaRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GERYON_ENTITY, GeryonRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GIGANTE_ENTITY, GiganteRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.GORGON_ENTITY, GorgonRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HARPY_ENTITY, HarpyRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.HEALING_SPELL_ENTITY, HealingSpellRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.MAD_COW_ENTITY, MadCowRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.MINOTAUR_ENTITY, MinotaurRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.NAIAD_ENTITY, NaiadRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ORTHUS_ENTITY, OrthusRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.ORTHUS_HEAD_ITEM_ENTITY, m -> new ItemRenderer(m, Minecraft.getInstance().getItemRenderer()));
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SATYR_ENTITY, SatyrRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SHADE_ENTITY, ShadeRenderer::new);  
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SIREN_ENTITY, SirenRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.SPARTI_ENTITY, SpartiRenderer::new);
    RenderingRegistry.registerEntityRenderingHandler(GFRegistry.UNICORN_ENTITY, UnicornRenderer::new);
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
  }
  
  @Override
  public void registerRenderLayers() {
    RenderTypeLookup.setRenderLayer(GFRegistry.OLIVE_SAPLING, RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(GFRegistry.REEDS, RenderType.getCutout());
    RenderTypeLookup.setRenderLayer(GFRegistry.WILD_ROSE, RenderType.getCutout());
  }
}
